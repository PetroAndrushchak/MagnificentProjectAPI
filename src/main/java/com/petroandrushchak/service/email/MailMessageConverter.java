package com.petroandrushchak.service.email;

import com.petroandrushchak.fut.exeptions.EmailClientException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

public class MailMessageConverter {

    private MailMessageConverter() {
    }

    public static String toHtmlString(Message message) {
        return toString(message, 1);
    }

    public static String toString(Message message) {
        return toString(message, 0);
    }

    private static String toString(Message message, Integer bodyPartIndex) {
        try {
            Object content = message.getContent();
            if (content instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) content;
                if (multipart.getCount() > bodyPartIndex) {
                    BodyPart part = multipart.getBodyPart(bodyPartIndex);
                    content = part.getContent();
                } else if (multipart.getCount() == 1) {
                    BodyPart part = multipart.getBodyPart(0);
                    content = part.getContent();
                }
            }
            if (content != null) {
                return content.toString();
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new EmailClientException("Error during converting message \n" + e);
        }
        return null;
    }
}
