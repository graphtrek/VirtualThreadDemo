package co.grtk.virtualthreaddemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HttpBinController {

    final RestClient restClient;
    long sum;
    AtomicInteger counter = new AtomicInteger();

    @GetMapping("/block/{seconds}")
    public String delay(@PathVariable int seconds) {
        long start = System.currentTimeMillis();
        ResponseEntity<Void> result = restClient.get()
                .uri("/delay/" + seconds)
                .retrieve()
                .toBodilessEntity();
        long elapsed = System.currentTimeMillis() - start;
        sum += elapsed;
        log.info("# {} {} on {} blocked {} sec elapsed {} ms sum {} ms", counter.incrementAndGet(), result.getStatusCode(), Thread.currentThread(), seconds, elapsed, sum);
        return Thread.currentThread().toString();
    }

    @GetMapping("/block1/{seconds}")
    public String delay1(@PathVariable int seconds) {
        long start = System.currentTimeMillis();

        CompletableFuture<HttpStatusCode> future = CompletableFuture.supplyAsync(restClient.get()
                .uri("/delay/" + seconds)
                .retrieve()::toBodilessEntity).thenApply(ResponseEntity::getStatusCode);

        HttpStatusCode result = future.join();

        long elapsed = System.currentTimeMillis() - start;
        sum += elapsed;
        log.info("# {} {} on {} blocked {} sec elapsed {} ms sum {} ms", counter.incrementAndGet(), result, Thread.currentThread(), seconds, elapsed, sum);
        return Thread.currentThread().toString();
    }

}
