package github.com.matcwa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
private JavaMailSender javaMailSender;
    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendRegistrationEmailTo(String email,String token){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("PollSystem");
        message.setText("Click the link to confirm the email" +"\n"
                +"http://localhost:8080/pollsystem/user/activate/"+token);
        javaMailSender.send(message);
    }
}
