package cn.usr.middleware.config;

import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket createRestApi() {


        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
//                Class<?> declaringClass = input.declaringClass();
//                if (declaringClass == BasicErrorController.class)// 排除
//                    return false;
//                if(declaringClass.isAnnotationPresent(ApiOperation.class)) // 被注解的类
//                    return true;
//                if(input.isAnnotatedWith(ResponseBody.class)) // 被注解的方法
//                    return true;
                //只有添加了ApiOperation注解的method才在API中显示
                if (input.isAnnotatedWith(ApiOperation.class)) {
                    return true;
                }
                return false;
            }
        };


        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(predicate)
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("USRCloud_ConnectMiddleware Operate API")
                .description("【有人透传云NB-IOT服务调试API页面 】")
                .termsOfServiceUrl("http://cloud.usr.cn/")
                .version("2.0")
                .build();
    }

}
