package ${project.packageName};

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.rayleigh.core.DynamicDataSource.EnableDynamicDataSource;
import com.rayleigh.core.annotation.EnableCustomRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication(scanBasePackages = {"com.rayleigh","${project.packageName}"})
@EnableCustomRepository
@EnableDynamicDataSource
public class ${module.name ?cap_first}Application extends SpringBootServletInitializer{

@Override
protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(${module.name ?cap_first}Application.class);
}

public static void main(String[] args) {
    new SpringApplicationBuilder(${module.name ?cap_first}Application.class)
    .web(true)
    .run(args);
}

@Bean
public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = jsonConverter.getObjectMapper();
    objectMapper.registerModule(new Hibernate5Module());
    return jsonConverter;
}
}