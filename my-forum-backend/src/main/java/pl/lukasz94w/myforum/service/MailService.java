package pl.lukasz94w.myforum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendActivateAccountEmail(String to, String activationLink) throws MessagingException, UnsupportedEncodingException, MailSendException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom("myforumspringangular@gmail.com", "My forum support");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("Activate account");
        String emailContent = "<p>Hello,</p>"
                + "<p>Thank you for creating account on our forum.</p>"
                + "<p>Click the link below to activate it:</p>"
                + "<p><a href=\"" + activationLink + "\">Confirm email</a></p>"
                + "<p>Link will be active for 24 hours.</p>"
                + "<br>"
                + "<p>If you have any problems with the registration,"
                + "<p>you can contact us on: myforumspringangular@gmail.com</p>";
        mimeMessageHelper.setText(emailContent, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendResetPasswordEmail(String to, String resetPasswordLink) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom("myforumspringangular@gmail.com", "My forum support");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("Link to reset your password");
        String emailContent = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change it:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Change my password</a></p>"
                + "<p>Link will be active for 24 hours.</p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
        mimeMessageHelper.setText(emailContent, true);

        javaMailSender.send(mimeMessage);
    }

}
