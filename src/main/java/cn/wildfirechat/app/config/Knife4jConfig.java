package cn.wildfirechat.app.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

	@Bean(value = "dockerBean")
	public Docket dockerBean() {
		//指定使用Swagger2规范
		Docket docket=new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
//				//分组名称
//				.groupName("用户服务")
				.select()
				//这里指定Controller扫描包路径
//				.apis(RequestHandlerSelectors.basePackage("cn.wildfirechat.app"))
//				.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}

	private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
        		// 标题
                .title("IM app-server服務接口文挡")
                // 描述
                .description("IM app-server服務接口文挡<br>")
//                .description("IM app-server服務接口文挡<br>" +
//                        "<b>备注：<br>" +
//                        "(1)在页面上的\"文挡管理\"要配置\"全局参数设置\"(为了模拟已登入状态),配置后<a href=\"#\">重刷页面</a>即可生效<br></b>" +
//                        "a.参数名称:BestPay-Session<br>" +
//                        "参数值:eyJtZW1iZXJOYW1lIjoiY2hyaXMifQ==<br>" +
//                        "b.参数名称:Authorization<br>" +
//                        "参数值:Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjaHJpcyIsImlhdCI6MTY0MTIwMTAwNywiZXhwIjoxNjQxMjg3NDA3fQ.XAW0kSkw0CWxreJJLNPerHUFQyxYmvIsay7-KmoRCn30MV3SvSh2MtqnerESeZTKn7fIMXVXb3hll_CdzMjB2g<br>" +
//                        "(或可以使用<a href=\"#/default/内部测试接口/createSessionUsingGET\">内部测试接口-产生BestPay-Session</a>)<br>" +
//                        "<b><font color=\"red\">(2)生產環境要屏蔽swagger相關資源，要在properties添加以下参数<br></font></b>" +
//                        "knife4j.production=true<br>")
				// 服务Url
				.termsOfServiceUrl("https://localhost:8888/")
				// 作者
				.contact("jyintgroup")
				// 版本
                .version("1.0") 
                .build();
    }
	

}
