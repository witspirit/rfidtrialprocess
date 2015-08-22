package be.witspirit.rfidtrialprocess.mail;

import javax.mail.Message;
import javax.mail.Session;

/**
 * Small abstraction for a Mail Message to be used in conjunction with the MailSender
 */
public interface MailMessage {

    Message toJavaMessage(Session mailSession);
}
