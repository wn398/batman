package ${project.packageName};

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import com.rayleigh.core.application.BaseApplication;

@SpringBootApplication(scanBasePackages = {"com.rayleigh","${project.packageName}"})
public class ${module.name ?cap_first}Application extends BaseApplication{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(${module.name ?cap_first}Application.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(${module.name ?cap_first}Application.class)
        .web(true)
        .run(args);
    }

}