package co.grtk.virtualthreaddemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
@RestController
public class HttpBinController {

    final RestClient restClient;
    long sum;
    AtomicInteger counter = new AtomicInteger();
    AtomicInteger threadCounter = new AtomicInteger();
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @GetMapping("/clear")
    public String clear() {
        counter.set(0);
        threadCounter.set(0);
        return Thread.currentThread().toString();
    }

    @GetMapping("/block/{seconds}")
    public String block(@PathVariable int seconds) {
        waitForRest(counter.incrementAndGet(), 1, seconds);
        return Thread.currentThread().toString();
    }

    private String waitForRest(int requestNr, int threadNr, int seconds) {
        long start = System.currentTimeMillis();
        ResponseEntity<Void> result = restClient.get()
                .uri("/delay/" + seconds)
                .retrieve()
                .toBodilessEntity();
        long elapsed = System.currentTimeMillis() - start;
        return "REST Call finished requestNr:" + requestNr + " threadNr:" + threadNr + " nrOfFinishedThreads: " + threadCounter.incrementAndGet() + " result:" + result.getStatusCode() + " elapsed: " + elapsed;
    }

    public String waitForThread(int requestNr, int threadNr, int millis) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long elapsed = System.currentTimeMillis() - start;
        return "Thread sleep finished requestNr:" + requestNr + " threadNr:" + threadNr + " nrOfFinishedThreads: " + threadCounter.incrementAndGet() + " elapsed: " + elapsed + " ms";
    }

    private int getRequestThreadsCount() {
        int requestThreads = 0;
        for(long threadId : threadMXBean.getAllThreadIds()) {
            if(threadMXBean.getThreadInfo(threadId).getThreadName().startsWith("http-nio-8080-exec-"))
                requestThreads++;
        }
        return requestThreads;
    }

    @GetMapping("/partialReactiveBlocks/{seconds}/threads/{nrOfThreads}")
    public String partialReactiveBlocks(@PathVariable int seconds, @PathVariable int nrOfThreads) throws ExecutionException, InterruptedException, TimeoutException {

        long start = System.currentTimeMillis();
        final int requestCounter = counter.incrementAndGet();
        ArrayList<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 1; i <= nrOfThreads; i++) {
            final int threadNr;
            threadNr = i;
            CompletableFuture<String> future
                    = CompletableFuture.supplyAsync(() -> waitForThread(requestCounter, threadNr, seconds));
           futures.add(future);
        }

       // CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
       // combinedFuture.get(5L, TimeUnit.SECONDS);

        String combined = Stream.of(futures.toArray(new CompletableFuture[0]))
                .map(CompletableFuture::join)
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        long threadCount = getRequestThreadsCount();
        long peakThreadCount = threadMXBean.getPeakThreadCount();
        long elapsed = System.currentTimeMillis() - start;
        sum += elapsed;
        log.info("\n{}\nrequest # {} paralellThreads # {} blocked # {} ms elapsed {} ms requestThreads/allThreadsPeak # {}/{}", combined, requestCounter, nrOfThreads, seconds, elapsed, threadCount,peakThreadCount);
        return Thread.currentThread().toString();
    }

}
