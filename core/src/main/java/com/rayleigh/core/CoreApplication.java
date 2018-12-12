package com.rayleigh.core;

import com.rayleigh.core.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

//@SpringBootApplication(scanBasePackages = {"com.rayleigh"})
//@ServletComponentScan("com.rayleigh")
public class CoreApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(CoreApplication.class, args);
//	}
//	//使用自定义的MappingJackson2HttpMessageConverter覆盖默认的实现,防止加载懒加载的实体
//	@Bean
//	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
//		ObjectMapper objectMapper = jsonConverter.getObjectMapper();
//		objectMapper.registerModule(new Hibernate5Module());
//		//ObjectMapper objectMapper = new ObjectMapper();
//		//objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		//jsonConverter.setObjectMapper(objectMapper);
//		return jsonConverter;
//	}
public static void main(String[] args) {
    Date date = StringUtil.stringToDate("2017-11-23 00:00:00");
    System.out.println(StringUtil.dateToDbString(date));
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.println(simpleDateFormat.format(date));
}
}
