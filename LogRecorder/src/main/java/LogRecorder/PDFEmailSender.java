package LogRecorder;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class PDFEmailSender {
	
	public static void main(String[] args) {
		
		System.out.println("SimpleEmail Start");
        String smtpHostServer = "gmail-smtp-in.l.google.com";
        String toEmail = "";
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        Session session = Session.getDefaultInstance(props);
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress("n91eply@gmail.com", "NoReply-JD"));
            msg.setRecipients(Message.RecipientType.TO, toEmail);
            msg.setReplyTo(InternetAddress.parse("n91eply@gmail.com", false));
            msg.setSubject("SimpleEmail Testing Subject", "UTF-8");
            msg.setText("SimpleEmail Testing Body", "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);
            System.out.println("EMail Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
}
