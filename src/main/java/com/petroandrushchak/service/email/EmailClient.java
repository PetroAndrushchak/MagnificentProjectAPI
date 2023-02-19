package com.petroandrushchak.service.email;

import com.petroandrushchak.fut.exeptions.EmailClientException;
import com.petroandrushchak.fut.exeptions.NotFoundSwitchException;
import com.petroandrushchak.helper.DateTimeHelper;
import com.petroandrushchak.helper.StringHelper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailClient implements AutoCloseable {

    private static final String ERROR_MSG = "Error during processing inbox messages. Error: \n";

    private Store store;

    public EmailClient(String email, String password) {
        login(email, password);
    }

    @SneakyThrows
    public EmailClient login(String email, String password) {

        log.info(String.format("Log in as email email = %s, password = %s", email, password));
        //create properties field
        Properties properties = EmailClientConfig.getPropertiesBaseOnTheEmailDomain(email);
        Session emailSession = Session.getInstance(properties);
        try {
            //create the POP3 store object and connect with the pop server
            store = emailSession.getStore("imap");
            store.connect(email, password);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new EmailClientException(ERROR_MSG + e);
        }
        return this;
    }

    public List<MailMessage> getInboxMessagesBySubjectAndAfterDateTime(String subject, LocalDateTime expectedDateTime) {
        log.info("Searching inbox messages with subject containing '" + subject + "'");
        List<MailMessage> mailMessages = searchInboxMessagesBySubject(subject, expectedDateTime);
        log.info("Found '" + mailMessages.size() + "' inbox emails with subject containing '" + subject + "'" + ", and after dateTime: " + expectedDateTime);
        return mailMessages;
    }

    @SneakyThrows
    private List<MailMessage> searchInboxMessagesBySubject(String subject, LocalDateTime expectedDateTime) {
        Folder emailFolder = null;
        try {
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.search(messageContainsSubjectAndNotLaterThanTerm(subject, expectedDateTime));

            List<MailMessage> mailsMessages = Stream.of(messages)
                                                    .map(MailMessage::fromMessage)
                                                    .toList();
            emailFolder.close(false);
            return mailsMessages;
        } catch (MessagingException e) {
            throw new EmailClientException(ERROR_MSG + e);
        } finally {
            if (emailFolder != null && emailFolder.isOpen()) {
                emailFolder.close(false);
            }
        }
    }

    private SearchTerm messageContainsSubjectAndNotLaterThanTerm(String subject, LocalDateTime dateTime) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        return new SearchTerm() {
            @SneakyThrows
            @Override
            public boolean match(Message message) {
                log.info("Checking email for match: " + atomicInteger.addAndGet(1) +
                        ", subject: " + message.getSubject() +
                        ", sent at:" + message.getSentDate() +
                        ", received at: " + message.getReceivedDate());
                try {
                    LocalDateTime sentDateTime = DateTimeHelper.toLocalDateTime(message.getSentDate());
                    log.info("");
                    if (sentDateTime.isBefore(dateTime)) {
                        log.info("Sent date is before than expected: " + dateTime);
                        return false;
                    } else {
                        log.info("Sent date is after, sent date: " + sentDateTime + " expected date: " + dateTime);
                    }

                    if (StringHelper.removeUselessWhiteSpaces(message.getSubject()).contains(subject)) {
                        return true;
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };
    }

    @Override
    public void close() throws Exception {
        logout();
    }

    public void logout() {
        if (store.isConnected()) {
            log.info("Closing gmail connection");
            try {
                store.close();
            } catch (MessagingException e) {
                log.error("Error while closing the store connection");
                e.printStackTrace();
            }
        }
    }

    private class EmailClientConfig {

        public static Properties getPropertiesBaseOnTheEmailDomain(String email) {
            String domain = StringHelper.getEmailDomain(email);

            Properties properties = new Properties();
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

            properties.setProperty("mail.imap.socketFactory.fallback", "false");
            properties.setProperty("mail.imap.socketFactory.port", "993");
            properties.put("mail.imap.port", "993");

            switch (domain) {
                case "gmail.com": {
                    properties.put("mail.imap.host", "imap.gmail.com");
                    return properties;
                }
                case "yahoo.com": {
                    properties.put("mail.imap.host", "imap.mail.yahoo.com");
                    return properties;
                }
                default:
                    throw new NotFoundSwitchException(domain);
            }
        }
    }
}
