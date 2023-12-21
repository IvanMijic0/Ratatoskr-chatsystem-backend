package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
public class EmailSenderServiceTest {

    @Mock
    private JavaMailSender mockMailSender;

    @Value("${website.frontend.url}")
    private String frontendUrl;

    @Test
    public void testSendVerificationEmail() throws MessagingException, IOException {
        EmailSenderService emailSenderService = new EmailSenderService(mockMailSender);
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setVerificationCode("testVerificationCode");

        emailSenderService.sendVerificationEmail(user, frontendUrl);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mockMailSender).send(mimeMessageCaptor.capture());

        MimeMessage sentMimeMessage = mimeMessageCaptor.getValue();

        assertEquals("ratatoskr@chatting.ba", sentMimeMessage.getFrom()[0].toString());
        assertEquals("test@example.com", sentMimeMessage.getAllRecipients()[0].toString());
        assertEquals("Please verify your registration", sentMimeMessage.getSubject());

        String content = (String) sentMimeMessage.getContent();
        assertEquals("Dear testUser,<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"/verify-email-token?code=testVerificationCode\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Ratatoskr Service", content);
    }
}
