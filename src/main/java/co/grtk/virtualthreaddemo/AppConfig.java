package co.grtk.virtualthreaddemo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

@Configuration
public class AppConfig {
    @Bean
    RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

}
