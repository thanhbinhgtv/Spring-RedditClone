package com.example.springredditclone.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailContentBuilder {
	private final TemplateEngine templateEngine;

	//Nhận thông điệp từ MailService.sendMail
	//Thông điệp được truyền vào message để tạo email
	String build(String message) {
		Context context = new Context();
		context.setVariable("message", message); //message là key gửi đến mailTemplate.html
		return templateEngine.process("mailTemplate", context); //mailTemplate.html
	}
}
