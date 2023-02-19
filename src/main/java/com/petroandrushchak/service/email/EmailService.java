package com.petroandrushchak.service.email;

import com.petroandrushchak.fut.exeptions.NotFoundException;
import com.petroandrushchak.helper.StringHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
public class EmailService {

    private static final Duration EMAIL_RECEIVE_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration EMAIL_RECEIVE_POLL_INTERVAL_TIMEOUT = Duration.ofSeconds(10);

    private final String email;
    private final String password;

    public static EmailService forUser(String email, String emailPassword) {
        return new EmailService(email, emailPassword);
    }

    private EmailService(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEASecurityCode(LocalDateTime localDateTime) {
        String subject = "Your EA Security Code is:";
        var mails = findAllEmails(subject, localDateTime);

        if (mails.size() != 1) {
            log.info("Found " + mails.size() + " emails");
            throw new NotFoundException("There is not only one email with subject: " + subject + " Content body has email: " + email + " and sent data after " + localDateTime);
        } else {
            log.info("Found only one email: ");
            log.info(mails.get(0).toString());
        }

        var message = mails.get(0);

        String messageContent = message.getSubject();
        String regex = "[0-9]{6}";

        return StringHelper.findValueByRegexInString(messageContent, regex);

    }


        @SneakyThrows
    private List<MailMessage> findAllEmails(String subject, LocalDateTime localDateTime) {
        try (EmailClient mailClient = new EmailClient(email, password)) {

            List<MailMessage> messages = Awaitility.await()
                                                   .pollInSameThread()
                                                   .atMost(EMAIL_RECEIVE_TIMEOUT)
                                                   .pollInterval(EMAIL_RECEIVE_POLL_INTERVAL_TIMEOUT)
                                                   .pollDelay(Duration.ofSeconds(0))
                                                   .until(
                                                           () -> mailClient.getInboxMessagesBySubjectAndAfterDateTime(subject, localDateTime),
                                                           foundMessages -> foundMessages.size() > 0);

            log.info("Found emails with subject and sent Date after : " + localDateTime);
            messages.forEach(message -> log.info(message.toString()));
            return messages;
        } catch (ConditionTimeoutException e) {
            e.printStackTrace();
            log.info("Timeout finding emails, found 0 emails");
            return Collections.emptyList();
        }
    }
}
