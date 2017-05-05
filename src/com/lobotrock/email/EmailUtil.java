package com.lobotrock.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.Logger;

import com.lobotrock.automationTest.TestCase1;
import com.lobotrock.logging.LoggingUtil;



public class EmailUtil {

	private static Logger logger = Logger.getLogger(EmailUtil.class);

	public static String getVerificationCode(String gmail, String password){
		logger.debug("getVerificationCode called.");
		String verificationCode = "";
		int emailCheckAttempts = Integer.valueOf(TestCase1.prop.getProperty("EMAIL_CHECK_ATTEMPTS"));
		

		Date timeRequested = new Date();
		for(int attempts = 0; attempts < emailCheckAttempts; attempts++){
			logger.info("Finding verification email attempt: " + attempts);
			verificationCode = getVerificationCode(gmail, password, timeRequested);
			if(verificationCode.length() > 0){
				break;
			}

			try {
				//Waiting for email to send
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				LoggingUtil.writeErrorStack(logger, e);
			}

		}
		return verificationCode;
	}

	private static String getVerificationCode(String gmail, String password, Date timeRequested){
		logger.debug("getVerificationCode called.");
		String verificationCode = "";

		logger.info("Attempting to read email.");
		//create properties field
		Properties properties = new Properties();

		properties.put("mail.pop3.host",  "pop.gmail.com");
		properties.put("mail.pop3.port", "995");
		properties.put("mail.pop3.starttls.enable", "true");
		Session emailSession = Session.getDefaultInstance(properties);

		try{
			//create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3s");

			store.connect("pop.gmail.com", gmail, password);

			//create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();

			//Find the most current email from salesforce
			String messageContent = "";

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				if(message.getFrom()[0].toString().contains("<noreply@salesforce.com>") &&
						"Verify your identity in Salesforce".equals(message.getSubject())){

					logger.debug("---------------------------------");
					logger.debug("Email Number " + (i + 1));
					logger.debug("Subject: " + message.getSubject());
					logger.debug("From: " + message.getFrom()[0]);
					logger.debug("Text: " + message.getContent().toString());
					logger.debug(message.getSentDate());

					if(message.getContent().toString().contains("Verification Code:")){
						messageContent = message.getContent().toString();
					}
				}
			}

			for(String line : messageContent.split(System.getProperty("line.separator"))){
				//Finding line with verification code
				if(line.contains("Verification Code:")){
					verificationCode = line.substring("Verification Code: ".length());
				}
			}

			//close the store and folder objects
			emailFolder.close(false);
			store.close();
		} catch (NoSuchProviderException e) {
			LoggingUtil.writeErrorStack(logger, e);
		} catch (MessagingException e) {
			LoggingUtil.writeErrorStack(logger, e);
		} catch (Exception e) {
			LoggingUtil.writeErrorStack(logger, e);
		}

		return verificationCode;
	}
}
