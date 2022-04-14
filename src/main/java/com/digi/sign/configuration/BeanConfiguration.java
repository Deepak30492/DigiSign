package com.digi.sign.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Configuration
public class BeanConfiguration {

	@Bean("xmlMapper")
	public ObjectMapper xmlMapper() {
		return new XmlMapper();
	}

	@Bean("objectMapper")
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
