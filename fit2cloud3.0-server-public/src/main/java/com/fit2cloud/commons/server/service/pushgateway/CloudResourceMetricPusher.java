package com.fit2cloud.commons.server.service.pushgateway;

import com.fit2cloud.commons.server.service.pushgateway.domain.MetricEntity;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

public class CloudResourceMetricPusher {
    private final static String DEFAULT_HELP = "no_help";
    @Resource
    private PushGateway pushGateway;
    @Resource
    private CheckPushGatewayService checkPushGatewayService;

    public void registry(MetricEntity metric, CollectorRegistry registry) {
        metric.getLabelMap().put("resourceId", metric.getResourceId());
        Gauge gauge = Gauge.build().name(metric.getMetricName())
                .help(!StringUtils.isEmpty(metric.getHelp()) ? metric.getHelp() : DEFAULT_HELP)
                .labelNames(metric.getLabelMap().keySet().stream().filter(key -> metric.getLabelMap().get(key) != null).toArray(String[]::new))
                .register(registry);
        gauge.labels(metric.getLabelMap().values().stream().filter(Objects::nonNull).toArray(String[]::new)).set(metric.getValue());
    }

    public void push(String resourceId, CollectorRegistry registry) throws IOException {
        pushGateway.pushAdd(registry, resourceId);
        checkPushGatewayService.removePushGatewayJob(resourceId);
    }
}
