package org.campusforum.backend.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.campusforum.backend.utils.Const;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerAPI文档配置类
 * @author ChangxueDeng
 */
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "Bearer", name = Const.HEAD_TOKEN,
                in = SecuritySchemeIn.HEADER) //安全方案
@OpenAPIDefinition(security = @SecurityRequirement(name = Const.HEAD_TOKEN))
@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI myOpenApi(){
        return new OpenAPI().info(new Info()
                .title("CampusForum Api 文档")
                .description("校园论坛API开发文档"));
    }
}
