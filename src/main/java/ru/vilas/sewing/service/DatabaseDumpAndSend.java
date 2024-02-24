package ru.vilas.sewing.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.stereotype.Service;


import java.util.Properties;

@Service
public class DatabaseDumpAndSend {

    // Настройки SMTP
    private static final String SMTP_HOST_NAME = "smtp.yandex.ru";
    private static final String SMTP_AUTH_USER = "webkonditer@yandex.ru";
    private static final String SMTP_AUTH_PWD  = "iesdsuzaketixwbl";

    // Получатель
    private static final String EMAIL_TO = "otlichnik_fzo@mail.ru";

    // Настройки SSH и Docker
    private static final String SSH_USER = "root";
    private static final String SSH_HOST = "77.232.139.95";
    private static final int SSH_PORT = 22;
    private static final String SSH_PASSWORD = "nWo61j8+1snbZD";
    private static final String CONTAINER_NAME = "sewing_db";
    private static final String DATABASE_NAME = "sewing";

    public static void main(String[] args) {
        Session session = null;
        Channel channel = null;
        try {
//            JSch jsch = new JSch();
//            session = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
//            session.setPassword(SSH_PASSWORD);
//            session.setConfig("StrictHostKeyChecking", "no");
//            session.connect();
//
//            // Команда для дампа базы данных
//            String dumpCommand = String.format("docker exec %s pg_dump -U postgres %s", CONTAINER_NAME, DATABASE_NAME);
//            channel = session.openChannel("exec");
//            ((ChannelExec) channel).setCommand(dumpCommand);
//            channel.setInputStream(null);
//
//            InputStream in = channel.getInputStream();
//            channel.connect();
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            try (GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(baos)) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = in.read(buffer)) != -1) {
//                    gzOut.write(buffer, 0, bytesRead);
//                }
//            }

            byte[] byteArray = new byte[] {(byte)72, (byte)101, (byte)108, (byte)108, (byte)111, (byte)32, (byte)87, (byte)111, (byte)114, (byte)108, (byte)100};


            // Отправка дампа по электронной почте
//            sendEmail(baos.toByteArray(), "database_dump.gz");
            sendEmail();
//            sendEmail(byteArray, "database_dump.gz");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }

    private static void sendEmail() {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.yandex.ru"); // Адрес SMTP сервера
        props.put("mail.smtp.socketFactory.port", "465"); // Порт SMTP сервера
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Класс SocketFactory для SSL
        props.put("mail.smtp.auth", "true"); // Необходимость аутентификации
        props.put("mail.smtp.port", "465"); // Порт для установления соединения

        jakarta.mail.Session session = jakarta.mail.Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD); // Ваши учетные данные для входа в SMTP сервер
            }
        });


        try {
            // Создаем объект MimeMessage
            MimeMessage message = new MimeMessage(session);

            // Устанавливаем отправителя
            message.setFrom(new InternetAddress(SMTP_AUTH_USER));

            // Устанавливаем получателя
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));

            // Устанавливаем тему письма
            message.setSubject("Бэкап базы данных");

            // Создаем Multipart объект
            Multipart multipart = new MimeMultipart();

            // Добавляем текстовую часть письма (необязательно)
            BodyPart textPart = new MimeBodyPart();
            textPart.setText("Вложение: Бэкап базы данных");
            multipart.addBodyPart(textPart);

            // Добавляем вложение
//            BodyPart attachmentPart = new MimeBodyPart();
//            attachmentPart.setDataHandler(new DataHandler(new FileDataSource(backupFile)));
//            attachmentPart.setFileName(backupFile.getName());
//            multipart.addBodyPart(attachmentPart);

            // Устанавливаем содержимое письма
            message.setContent(multipart);

            // Отправляем сообщение
            Transport.send(message);

            System.out.println("Бэкап успешно отправлен по электронной почте.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
