package cn.wildfirechat.app.controller;

import cn.wildfirechat.app.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "客服中心接口")
@RestController
public class CustomerController {

    @Value("${customer.live800.url}")
    private String customerUrl;

    @Value("${customer.live800.company_id}")
    private String customerCompanyId;

    @Value("${customer.live800.config_id}")
    private String customerConfigId;

    @Value("${customer.live800.jid}")
    private String customerJid;

    @Value("${customer.live800.s}")
    private String customerS;

    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    @ApiOperation(value = "查询客服网址")
    @GetMapping(value ="/customer/url")
    public RestResult url() {
        StringBuilder builder = new StringBuilder();
        builder.append(customerUrl).append("?")
                .append("companyID=").append(customerCompanyId).append("&")
                .append("configID=").append(customerConfigId).append("&")
                .append("jid=").append(customerJid).append("&")
                .append("s=").append(customerS);

        LOG.info("customerUrl: {}", builder);

        return RestResult.ok(builder.toString());
    }
}
