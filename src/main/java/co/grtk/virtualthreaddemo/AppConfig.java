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
    ApplicationRunner multipleThreadsRunner() {
        return args -> {
            long start = System.currentTimeMillis();
            var observed = new ConcurrentSkipListSet<String>();
            var threads = new ArrayList<Thread>();
            for (var i = 0; i < 1000; i++) {
                var index = i;
                threads.add(Thread.ofVirtual().unstarted(() -> {
                    try {
                        Thread.sleep(100);
                        if (0 == index) {
                            observed.add(Thread.currentThread().toString());
                        }

                        Thread.sleep(100);
                        if (0 == index) {
                            observed.add(Thread.currentThread().toString());
                        }

                        Thread.sleep(100);
                        if (0 == index) {
                            observed.add(Thread.currentThread().toString());
                        }

                        Thread.sleep(100);
                        if (0 == index) {
                            observed.add(Thread.currentThread().toString());
                        }

                    } //
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }

            for (var t : threads) {
                t.start();
            }

            for (var t : threads) {
                t.join();
            }
            long elapsed = System.currentTimeMillis() - start;
            System.out.println(elapsed + " " +observed);

        };
    }

    @Bean
    TaskDecorator taskDecorator() {
        return runnable -> () -> {
            System.out.println("decorator: running before the thread "
                    + Thread.currentThread());
            runnable.run();
            System.out.println("decorator: running before the thread "
                    + Thread.currentThread());
        };
    }

    @Bean
    RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.baseUrl("https://httpbin.org/").build();
    }

}
