package com.fit2cloud.autoconfigure;


import com.fit2cloud.commons.server.service.pushgateway.CheckPushGatewayService;
import com.fit2cloud.commons.server.service.pushgateway.CloudResourceMetricPusher;
import io.prometheus.client.exporter.PushGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {
    @Value("${prometheus.push-gateway.host}")
    private String address;

    @Bean
    public PushGateway pushGateway() {
        return new PushGateway(address);
    }

    @Bean
    public CloudResourceMetricPusher cloudResourceMetricPusher() {
        return new CloudResourceMetricPusher();
    }

    @Bean
    public CheckPushGatewayService checkPushGatewayService() {
        return new CheckPushGatewayService();
    }
}
