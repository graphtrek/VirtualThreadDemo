package co.grtk.virtualthreaddemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
    @Bean
    RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

}
