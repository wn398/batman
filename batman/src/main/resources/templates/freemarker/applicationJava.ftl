<#include "CopyRight.ftl">
package ${project.packageName};

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.WebApplicationType;
import com.rayleigh.core.application.BaseApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = {"com.rayleigh","${project.packageName}"})
@ServletComponentScan(basePackages = {"com.rayleigh","${project.packageName}"})
public class ${module.name ?cap_first}Application extends BaseApplication{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(${module.name ?cap_first}Application.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(${module.name ?cap_first}Application.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
    }

}