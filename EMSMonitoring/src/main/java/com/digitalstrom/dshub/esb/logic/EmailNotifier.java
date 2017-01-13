package com.digitalstrom.dshub.esb.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.digitalstrom.dshub.esb.contract.INotifier;
import com.digitalstrom.dshub.esb.util.ReadConfigs;

public class EmailNotifier implements INotifier {

	final static Logger logger = Logger.getLogger("EmailNotifier");
	private String emailusername;
	private String emailpassword;
	private String smtphost;
	private String smtpport;
	private String receiverslist;
	private String fromaddress;
	private Map<String, String> map = null;
	private String emailbodytemplate;

	public EmailNotifier() {
		super();
		this.map = ReadConfigs.getInstance();
		this.emailusername = map.get("emailusername");
		this.emailpassword = map.get("emailpassword");
		this.smtphost = map.get("smtphost");
		this.smtpport = map.get("smtpport");
		this.receiverslist = map.get("sendingto");
		this.fromaddress = map.get("sendingfrom");
		this.emailbodytemplate = map.get("emailbodytemplate");
	}

	public void SendNotification(Map notificationBacklog, boolean isDeltaBacklog, String title, String serverName, String env) {

		final String emailusername = this.emailusername;
		final String emailpassword = this.emailpassword;
		String emailBody;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", this.smtphost);
		props.put("mail.smtp.port", this.smtpport);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailusername, emailpassword);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.emailusername));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.receiverslist));
			message.setSubject(title + " from the server " + serverName + " in the environement " + env);

			emailBody = this.prepareEmailBody(notificationBacklog, emailbodytemplate);
			message.setText(emailBody);

			Transport.send(message);
			logger.info("Notiication email sent!");
			logger.debug("Notiication email sent to: " + this.receiverslist + " from: " + this.fromaddress
					+ " with body: " + this.emailbodytemplate);
		} catch (MessagingException e) {
			logger.error("Error while sending notification email: " + e);
			throw new RuntimeException(e);
		}
	}

	private String prepareEmailBody(Map<String, Long> notificationBacklog, String emailbodytemplate) {
		String emailBody = emailbodytemplate;
		StringBuilder deltaDest = new StringBuilder();
		StringBuilder backlogDest = new StringBuilder();

		logger.debug("Destination(s) backlog: ");
		for (Entry<String, Long> entry : notificationBacklog.entrySet()) {
			backlogDest.append(entry.getKey() + " : " + entry.getValue());
			backlogDest.append("; ");
			logger.debug(entry.getKey() + " : " + entry.getValue());
		}

		String replaceDelta = emailBody.replace("<<delta>>", deltaDest);
		String replaceBacklog = replaceDelta.replace("<<backlog>>", backlogDest);
		return replaceBacklog;
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("C:\\Personal\\Projects\\EMSMonitoring\\EMSMonitoring\\"
				+ "src\\main\\resources\\log4j.properties");
		EmailNotifier sn = new EmailNotifier();
		Map<String, Long> map = new HashMap<String, Long>();
		Map<String, Long> mapDelta = new HashMap<String, Long>();
		String emailbodytemplate = "These destinations are either new or their backlog is increased by at least 1+ threshold(s) since the last check: <<delta>>. "
				+ "These destinations have backlog over the limit: <<backlog>>";
		String result;
		for (int i=0; i<10;i++)
			map.put("queue"+i, (long) i);
		for (int i=0; i<10;i++)
			mapDelta.put("queue-delta"+i, (long) i);
		result = sn.prepareEmailBody(map, emailbodytemplate);
		System.out.println(result);
		//sn.SendNotification(null, null, "title", "dev");
	}
	
}
