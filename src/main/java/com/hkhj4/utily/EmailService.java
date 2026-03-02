package com.hkhj4.utily;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailService {
    @Resource
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送验证码邮件
     *
     * @param toEmail 收件人邮箱
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    public void sendVerificationCode(String toEmail, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 设置发件人（名称 + 邮箱）
            helper.setFrom("好看韩剧4<" + fromEmail + ">");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content);
            mailSender.send(mimeMessage);
            log.info("验证码邮件发送成功！");
        } catch (Exception e) {
            log.error("邮箱验证码发送失败:{}", e.getMessage());
            throw new RuntimeException("邮件发送失败,请稍后重试");
        }
    }
}
