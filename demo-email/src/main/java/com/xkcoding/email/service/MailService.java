package com.xkcoding.email.service;

import javax.mail.MessagingException;

/**
 * <p>
 * Mail interface
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-21 11:16
 */
public interface MailService {
    /**
     * Send text messages
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param cc cc to the address
     */
    void sendSimpleMail(String to, String subject, String content, String... cc);

    /**
     * Send HTML emails
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException;

    /**
     * Send messages with attachments
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param filePath attachment address
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException;

    /**
     * Send messages with static resources in the body
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param rscPath static resource address
     * @param rscId static resource ID
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException;

}
