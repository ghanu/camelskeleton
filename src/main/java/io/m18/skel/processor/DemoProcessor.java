package io.m18.skel.processor;

import java.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.*;
import org.slf4j.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelExecutionException;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Put payload from file and timestamp in a map
 * @author Wolfram Huesken <woh@m18.io>
 */
@Component
@ConfigurationProperties(prefix = "routes.DemoRoute")
public class DemoProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DemoProcessor.class.getName());

    @Getter @Setter
    private String slack;

    @Override
    public void process(Exchange exchange) throws Exception {
        String payload = exchange.getIn().getBody(String.class);
        Map<String, String> data = new HashMap<>();

        data.put("content", payload);
        data.put("timestamp", new Date().toString());

        exchange.getIn().setBody(data, Map.class);

        try {
            final ProducerTemplate slackProducerTpl = exchange.getContext().createProducerTemplate();
            slackProducerTpl.sendBody(slack, payload);
        } catch (NullPointerException | CamelExecutionException ex) {
            logger.error("Slack notification failed", ex);
        }

    }

}
