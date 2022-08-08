package com.spring.integration.mailintegration.domain;

import java.time.LocalDateTime;

public class ImapSetting {

  private Long imapSettingId;

  private String imapUrl;

  private Boolean isSSL;

  private String username;

  private String password;

  private Boolean enableDeleteMessage;

  private Boolean enableMessageAsRead;

  private Boolean enableMessageImport;

  private Long port;

  private Long concernId;

  private Long templateId;

  private Long languageId;

  private LocalDateTime archivedFrom;

  private String concernCode;

  public Long getImapSettingId() {
    return imapSettingId;
  }

  public void setImapSettingId(Long imapSettingId) {
    this.imapSettingId = imapSettingId;
  }

  public String getImapUrl() {
    return imapUrl;
  }

  public void setImapUrl(String imapUrl) {
    this.imapUrl = imapUrl;
  }

  public Boolean getIsSSL() {
    return isSSL;
  }

  public void setIsSSL(Boolean isSSL) {
    this.isSSL = isSSL;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getEnableDeleteMessage() {
    return enableDeleteMessage;
  }

  public void setEnableDeleteMessage(Boolean enableDeleteMessage) {
    this.enableDeleteMessage = enableDeleteMessage;
  }

  public Boolean getEnableMessageAsRead() {
    return enableMessageAsRead;
  }

  public void setEnableMessageAsRead(Boolean enableMessageAsRead) {
    this.enableMessageAsRead = enableMessageAsRead;
  }

  public Boolean getEnableMessageImport() {
    return enableMessageImport;
  }

  public void setEnableMessageImport(Boolean enableMessageImport) {
    this.enableMessageImport = enableMessageImport;
  }

  public Long getPort() {
    return port;
  }

  public void setPort(Long port) {
    this.port = port;
  }

  public Long getConcernId() {
    return concernId;
  }

  public void setConcernId(Long concernId) {
    this.concernId = concernId;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public Long getLanguageId() {
    return languageId;
  }

  public void setLanguageId(Long languageId) {
    this.languageId = languageId;
  }

  public LocalDateTime getArchivedFrom() {
    return archivedFrom;
  }

  public void setArchivedFrom(LocalDateTime archivedFrom) {
    this.archivedFrom = archivedFrom;
  }

  public String getConcernCode() {
    return concernCode;
  }

  public void setConcernCode(String concernCode) {
    this.concernCode = concernCode;
  }

}
