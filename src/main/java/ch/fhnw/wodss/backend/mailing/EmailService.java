package ch.fhnw.wodss.backend.mailing;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  
    @Autowired
    public JavaMailSender mailSender;
 
    public void sendMessage(String to, String subject, String text) {
    	try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mail, true);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true);
            mailSender.send(mail);
        } catch (MailException | MessagingException e) {
            e.printStackTrace();
        }
    }
}