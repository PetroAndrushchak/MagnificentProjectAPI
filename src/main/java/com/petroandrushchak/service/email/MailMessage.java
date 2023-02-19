package com.petroandrushchak.service.email;

import com.petroandrushchak.fut.exeptions.EmailClientException;
import com.petroandrushchak.helper.StringHelper;
import lombok.Builder;
import lombok.Getter;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Builder
@Getter
public class MailMessage {

    String subject;
    LocalDateTime sentDate;
    String stringContent;
    String htmlContent;

    Object content;

    static MailMessage fromMessage(Message message){
        try {
            return new MailMessageBuilder()
                    .stringContent(MailMessageConverter.toString(message))
                    .htmlContent(MailMessageConverter.toHtmlString(message))
                    .sentDate(message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .subject(message.getSubject())
                    .build();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new EmailClientException("Error during converting Message to the custom MailMessage");
        }
    }

    @Override
    public String toString() {
        return "{subject='" + subject + '\'' +
                ", sentDate=" + sentDate +
                ", stringContent='" + StringHelper.removeUselessWhiteSpaces(stringContent) + '\'';
    }
}
