package com.nads.nadsengine.Services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nads.nadsengine.Controllers.ApiController;

public class LoggingService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);
    private String logLevel;
    private String timeStamp;
    private String externalId;
    private String externalIp;
    private String internalIp;
    private String internalId;
    private String messageType;
    private String groupApplication;
    private String serviceApplication;
    private String processApplication;
    private String messageKey;
    private String messageCode;
    private String messageDescription;
    private String messageException;
    private String messageBody;

    // Add getters and setters for each attribute

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public String getInternalIp() {
        return internalIp;
    }

    public void setInternalIp(String internalIp) {
        this.internalIp = internalIp;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getGroupApplication() {
        return groupApplication;
    }

    public void setGroupApplication(String groupApplication) {
        this.groupApplication = groupApplication;
    }

    public String getServiceApplication() {
        return serviceApplication;
    }

    public void setServiceApplication(String serviceApplication) {
        this.serviceApplication = serviceApplication;
    }

    public String getProcessApplication() {
        return processApplication;
    }

    public void setProcessApplication(String processApplication) {
        this.processApplication = processApplication;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }

    public String getMessageException() {
        return messageException;
    }

    public void setMessageException(String messageException) {
        this.messageException = messageException;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String formatLog() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuilder logBuilder = new StringBuilder();

        // logBuilder.append("log_level [PIPE] -> ").append(logLevel).append(" |\n");
        logBuilder.append("\ntime_stamp [PIPE] -> ").append(dateFormat.format(new Date()));
        logBuilder.append("external_id [PIPE] -> ").append(externalId).append("\n");
        logBuilder.append("external_ip [PIPE] -> ").append(externalIp).append("\n");
        logBuilder.append("internal_ip [PIPE] -> ").append(internalIp).append("\n");
        logBuilder.append("internal_id [PIPE] -> ").append(internalId).append("\n");
        logBuilder.append("message_type [PIPE] -> ").append(messageType).append("\n");
        logBuilder.append("group_application [PIPE] -> ").append(groupApplication).append("\n");
        logBuilder.append("service_application [PIPE] -> ").append(serviceApplication).append("\n");
        logBuilder.append("proses_application [PIPE] -> ").append(processApplication).append("\n");
        logBuilder.append("message_key -> ").append(messageKey).append("\n");
        logBuilder.append("message_code [PIPE] -> ").append(messageCode).append(" |\n");
        logBuilder.append("message_descryption [PIPE] -> ").append(messageDescription).append("\n");
        logBuilder.append("message_exception [PIPE] -> ").append(messageException).append("\n");
        logBuilder.append("message_body [PIPE] -> ").append(messageBody).append("\n");

        return logBuilder.toString();
    }

    public void info(String isi) {
        logger.info(isi);
    }

    public void warning(String isi) {
        logger.warn(isi);
    }

}
