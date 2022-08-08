package com.spring.integration.mailintegration.configuration;

import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.ExecutorChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mail.support.DefaultMailHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableIntegration
public class EmailIntegrationConfiguration {

  @Bean("mailTaskExecutor")
  public ThreadPoolTaskExecutor mailTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setMaxPoolSize(1000);
    taskExecutor.setCorePoolSize(100);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setAwaitTerminationSeconds(Integer.MAX_VALUE);
    return taskExecutor;
  }

  @Bean("imapMailChannel")
  public ExecutorChannelSpec imapMailChannel() {
    return MessageChannels.executor(mailTaskExecutor());
  }

  @Bean
  public HeaderMapper<MimeMessage> mailHeaderMapper() {
    return new DefaultMailHeaderMapper();
  }
}
