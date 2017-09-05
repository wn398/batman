package com.rayleigh.batman;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.rayleigh.core.dynamicDataSource.EnableDynamicDataSource;
import com.rayleigh.core.annotation.EnableCustomRepository;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication(scanBasePackages = {"com.rayleigh"})
@ServletComponentScan("com.rayleigh")
@EnableCustomRepository
@EnableDynamicDataSource
public class BatmanApplication  extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BatmanApplication.class);
	}

	public static void main(String[] args) {
		//SpringApplication.run(BatmanApplication.class, args);
		new SpringApplicationBuilder(BatmanApplication.class)
				.web(true)
				.run(args);

	}
	//使用自定义的MappingJackson2HttpMessageConverter覆盖默认的实现,防止加载懒加载的实体
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = jsonConverter.getObjectMapper();
		objectMapper.registerModule(new Hibernate5Module());
		//ObjectMapper objectMapper = new ObjectMapper();
		//objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}
	@Bean
	public DruidStatInterceptor druidStatInterceptor(){
		return new DruidStatInterceptor();
	}

	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator(){
		BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
		beanNameAutoProxyCreator.setProxyTargetClass(true);
		beanNameAutoProxyCreator.setBeanNames("*Controller","*Schedule");
		beanNameAutoProxyCreator.setInterceptorNames("druidStatInterceptor");
		return beanNameAutoProxyCreator;
	}
}
