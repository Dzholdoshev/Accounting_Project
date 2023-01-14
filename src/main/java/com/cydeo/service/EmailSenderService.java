package com.cydeo.service;

import com.cydeo.EmailContext;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailSenderService {
    void sendEmail(String subject, String body);
   // void sendEmailAttach1(String subject, String body,String pathToAttachment) throws MessagingException;
  // void sendEmailAttach(String subject, String text, String pathToAttachment) throws MessagingException, IOException;
   void sendEmailAttach(String subject, String text, String pathToAttachment)throws MessagingException, IOException;
    void sendMail(EmailContext email) throws MessagingException;

}
