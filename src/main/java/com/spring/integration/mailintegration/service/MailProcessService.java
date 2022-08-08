package com.spring.integration.mailintegration.service;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MailProcessService {

  public static Logger LOGGER = LoggerFactory.getLogger(MailProcessService.class);

  @ServiceActivator(inputChannel = "imapMailChannel")
  public void processMessage(Message<?> message) {
    LOGGER.warn("New incoming email received");
    String logMessage = null;
    try {
      Object payload = message.getPayload();
      if (payload instanceof MimeMultipart) {
        logMessage = getLogMimeMultipartMessage((MimeMultipart) payload);
        LOGGER.warn("Received {}", logMessage);
      }
      else if (payload instanceof String) {
        logMessage = getLogStringMessage(message);
        LOGGER.warn("Received {}", logMessage);
      }
      LOGGER.warn("Processed {}", logMessage);
    }
    catch (Exception e) {
      LOGGER.error("Exception in processMessage()", e);
    }
  }

  private String getLogMimeMultipartMessage(MimeMultipart payload) {
    try {
      MimeMultipart multipart = payload;
      if (multipart.getParent() instanceof MimeMessage) {
        MimeMessageParser parser = new MimeMessageParser((MimeMessage) multipart.getParent()).parse();
        return "email from " + parser.getFrom() + " with subject " + parser.getSubject();
      }
    }
    catch (Exception e) {
      LOGGER.error("Exception in logMimeMultipartMessage", e);
    }
    return "ERROR";
  }

  private String getLogStringMessage(Message<?> message) {
    try {
      Object sender = message.getHeaders().get(MailHeaders.FROM);
      Object subject = message.getHeaders().get(MailHeaders.SUBJECT);
      return "email from " + sender + " with subject " + subject;
    }
    catch (Exception e) {
      LOGGER.error("Exception in logStringMessage", e);
    }
    return "ERROR";
  }

}
