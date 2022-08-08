package com.spring.integration.mailintegration.listener;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.spring.integration.mailintegration.domain.ImapSetting;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class MailListenerService implements ApplicationListener<ApplicationReadyEvent> {

  public static Logger LOGGER = LoggerFactory.getLogger(MailListenerService.class);

  private final IntegrationFlowContext integrationFlowContext;
  private final HeaderMapper<MimeMessage> mailHeaderMapper;
  private final MessageChannel imapMailChannel;
  private final ThreadPoolTaskExecutor taskExecutor;
  private final ApplicationContext applicationContext;

  @Autowired
  public MailListenerService(IntegrationFlowContext integrationFlowContext, HeaderMapper<MimeMessage> mailHeaderMapper, @Qualifier("imapMailChannel") MessageChannel imapMailChannel,
    @Qualifier("mailTaskExecutor") ThreadPoolTaskExecutor taskExecutor, ApplicationContext applicationContext) {
    this.integrationFlowContext = integrationFlowContext;
    this.mailHeaderMapper = mailHeaderMapper;
    this.imapMailChannel = imapMailChannel;
    this.taskExecutor = taskExecutor;
    this.applicationContext = applicationContext;
  }

  public void registerImapFlows() {
    ImapSetting imapSetting = new ImapSetting();
    imapSetting.setConcernId(100l);
    imapSetting.setConcernCode("rcn");
    imapSetting.setEnableDeleteMessage(false);
    imapSetting.setEnableMessageAsRead(true);
    imapSetting.setEnableMessageImport(true);
    imapSetting.setImapSettingId(1234l);
    imapSetting.setImapUrl("imaps://%s:%s@outlook.office365.com/INBOX");
    imapSetting.setIsSSL(true);
    imapSetting.setPort(993l);
    imapSetting.setUsername("<username>");
    imapSetting.setPassword("<Application/Login Password>");
    imapSetting.setArchivedFrom(null);
    this.registerImapFlow(imapSetting);
  }

  public void registerImapFlow(ImapSetting imapSetting) {
    ImapMailReceiver mailReceiver = createImapMailReceiver(imapSetting);

    // create the flow for an email process
    //@formatter:off
    StandardIntegrationFlow flow = IntegrationFlows
      .from(Mail.imapInboundAdapter(mailReceiver),
        consumer -> consumer.autoStartup(true)
                            .poller(Pollers.fixedDelay(Duration.ofSeconds(5))
                                           .taskExecutor(taskExecutor)
                                           .errorHandler(t -> LOGGER.error("Error while polling emails for address " + imapSetting.getUsername(), t))
                                           .maxMessagesPerPoll(10)))
      .channel(imapMailChannel).get();
    //@formatter:on

    // give the bean a unique name to avoid clashes with multiple imap settings
    String flowId = imapSetting.getConcernCode() + "-" + imapSetting.getImapSettingId();
    IntegrationFlowContext.IntegrationFlowRegistration existingFlow = integrationFlowContext.getRegistrationById(flowId);
    if (existingFlow != null) {
      // destroy the previous beans
      existingFlow.destroy();
    }
    // register the new flow
    if (imapSetting.getArchivedFrom() == null && imapSetting.getEnableMessageImport()) {
      integrationFlowContext.registration(flow).id(flowId).useFlowIdAsPrefix().register();
    }
  }

  /**
   * Stops listening for given IMAP
   *
   * @param imapSettingId - The IMAP setting ID that needs to be de-registered
   */
  public void unregisterImapFlow(Long imapSettingId, String concernCode) {
    String flowId = concernCode + "-" + imapSettingId;
    IntegrationFlowContext.IntegrationFlowRegistration existingFlow = integrationFlowContext.getRegistrationById(flowId);
    if (existingFlow != null) {
      existingFlow.destroy();
    }
  }

  private ImapMailReceiver createImapMailReceiver(ImapSetting imapSettings) {
    String url = String.format(imapSettings.getImapUrl(), URLEncoder.encode(imapSettings.getUsername(), UTF_8), URLEncoder.encode(imapSettings.getPassword(), UTF_8));
    ImapMailReceiver receiver = new ImapMailReceiver(url);
    receiver.setSimpleContent(true);

    Properties mailProperties = new Properties();
    mailProperties.put("mail.debug", "false");

    mailProperties.put("mail.imap.connectionpoolsize", "5");
    mailProperties.put("mail.imap.fetchsize", 4194304);
    mailProperties.put("mail.imap.connectiontimeout", 15000);
    mailProperties.put("mail.imap.timeout", 30000);

    mailProperties.put("mail.imaps.connectionpoolsize", "5");
    mailProperties.put("mail.imaps.fetchsize", 4194304);
    mailProperties.put("mail.imaps.connectiontimeout", 15000);
    mailProperties.put("mail.imaps.timeout", 30000);

    receiver.setJavaMailProperties(mailProperties);
    receiver.setSearchTermStrategy(this::notSeenTerm);
    receiver.setAutoCloseFolder(false);
    receiver.setShouldDeleteMessages(false);
    receiver.setShouldMarkMessagesAsRead(true);
    receiver.setHeaderMapper(mailHeaderMapper);
    receiver.setEmbeddedPartsAsBytes(false);
    return receiver;
  }

  private SearchTerm notSeenTerm(Flags supportedFlags, Folder folder) {
    return new FlagTerm(new Flags(Flags.Flag.SEEN), false);
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (event.getApplicationContext().equals(this.applicationContext)) {
      registerImapFlows();
    }
  }
}
