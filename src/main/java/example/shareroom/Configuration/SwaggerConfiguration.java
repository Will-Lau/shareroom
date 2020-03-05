package example.shareroom.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("api document")
                .description("调用登录接口获得token\n\r" +
                             "非管理员接口userId填token，管理员接口userId就填userId\n\r"+
                             "接口不是所有的参数都要填，不知道的参数就不填\n\r"+
                             "几个时间的格式说明一下：\n\r" +
                             "date_id 和 date 举例:2020-01-01\n\r"+
                             "startTime和endTime  举例:6\n\r"+
                             "actionTime和doneTime 举例:2020-01-01 00:00:00")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket createRestApi() {
        /*ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token").description("access token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数*/

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描basePackage包下面的“/rest/”路径下的内容作为接口文档构建的目标
                .apis(RequestHandlerSelectors.basePackage("example.shareroom"))
                .paths(PathSelectors.regex("/.*"))
                .build();
                //.globalOperationParameters(pars);
    }
}
