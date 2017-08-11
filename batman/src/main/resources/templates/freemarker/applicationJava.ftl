package ${project.packageName};

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.rayleigh.core.annotation.EnableCustomRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication(scanBasePackages = {"com.rayleigh","${project.packageName}"})
@EnableCustomRepository
public class ${module.name ?cap_first}Application{

public static void main(String[] args) {
    SpringApplication.run(${module.name ?cap_first}Application.class, args);
}

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
}