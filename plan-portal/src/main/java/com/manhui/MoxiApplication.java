package com.manhui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MoxiApplication extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(){
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				//设置超时时间
				container.setSessionTimeout(10000);//单位为S
			}
       };
	}
	
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {  
        container.setPort(8084);  
    }
	
	public static void main(String[] args) {
		SpringApplication.run(MoxiApplication.class, args);
	}
}
