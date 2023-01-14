package com.cydeo.service.impl;

import com.cydeo.EmailContext;
import com.cydeo.service.EmailSenderService;
import com.cydeo.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Service
public class EmailSenderServiceImpl implements EmailSenderService {
private final SecurityService securityService;


    @Autowired
    private JavaMailSender mailSender;

    public EmailSenderServiceImpl(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void sendEmail(String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        String toEmail= securityService.getLoggedInUser().getUsername();

//        SimpleMailMessage sms = new SimpleMailMessage();
//        String phone = securityService.getLoggedInUser().getPhone();
//        String emailToSmsVerizon=phone + "@vtext.com";
//        String emailToSmsTT = phone + "number@txt.att.net"
//        sms.setFrom("javadevelopertest2000@googlemail.com");
//        sms.setTo(emailToSmsVerizon);
//        sms.setText(body);
//        sms.setSubject(subject);
//        mailSender.send(sms);


        message.setFrom("javadevelopertest2000@googlemail.com");
       // message.setTo(toEmail);
        message.setTo("marklen86@gmail.com");
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);

    }




    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendMail(EmailContext email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getContext());
        String emailContent = templateEngine.process(email.getTemplateLocation(), context);

        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setText(emailContent, true);
        mailSender.send(message);
    }

    @Override
    public void sendEmailAttach(String subject, String text, String pathToAttachment) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setContent("invoice_print.html","text/html");
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom("javadevelopertest2000@googlemail.com");
        helper.setTo("marklen86@gmail.com");
        helper.setSubject(subject);
        helper.setText(text,true);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("Invoice", file, "text/html" );
        mailSender.send(message);


    }

//    @Override
//    public void sendEmailAttach(String from, Collection<String> recipients, String subject, String text,
//                                List<InputStream> attachments, List<String> fileNames, List<String> mimeTypes) throws IOException, MessagingException {
//
//        Objects.requireNonNull(from);
//        Objects.requireNonNull(recipients);
//
//        // a message with attachments consists of several parts in a multipart
//        MimeMultipart multipart = new MimeMultipart();
//
//        // create text part
//        MimeBodyPart textPart = new MimeBodyPart();
//        textPart.setText(text, "utf-8", "html");
//
//        // add the text part to the multipart
//        multipart.addBodyPart(textPart);
//
//        // create attachment parts if required
//        if (attachments != null) {
//            // check that attachment and fileNames arrays sizes match
//            if (attachments.size() != fileNames.size() || attachments.size() != mimeTypes.size()) {
//                throw new IllegalArgumentException(
//                        "Attachments, file names, and mime types array sizes must match");
//            }
//
//            // create parts and add them to the multipart
//            for (int i = 0; i < attachments.size(); i++) {
//                // create a data source to wrap the attachment and its mime type
//                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachments.get(i), mimeTypes.get(i));
//
//                // create a dataHandler wrapping the data source
//                DataHandler dataHandler = new DataHandler(dataSource);
//
//                // create a body part for the attachment and set its data handler and file name
//                MimeBodyPart attachmentPart = new MimeBodyPart();
//                attachmentPart.setDataHandler(dataHandler);
//                attachmentPart.setFileName(fileNames.get(i));
//
//                // add the body part to the multipart
//                multipart.addBodyPart(attachmentPart);
//            }
        }










