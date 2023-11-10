package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "ratatoskr@chatting.ba";
        String senderName = "Ratatoskr Chat-System";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                .concat("Please click the link below to verify your registration:<br>")
                .concat("<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>")
                .concat("Thank you,<br>")
                .concat("Ratatoskr Service");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFull_name());
        String verifyURL = siteURL.concat("/verify-email-token?code=").concat(user.getVerificationCode());

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
}
