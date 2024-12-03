package co.grtk.virtualthreaddemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Slf4j
@Component
public class EventListenerExampleBean implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    public static int counter;
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    long threadCount = threadMXBean.getThreadCount();
    long peakThreadCount = threadMXBean.getPeakThreadCount();

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Application started jvm threads # {}/{}", threadCount,peakThreadCount);
        for(long threadId : threadMXBean.getAllThreadIds()) {
            log.info("Thread #{} {}",threadId, threadMXBean.getThreadInfo(threadId));
        }
        counter++;
    }


    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        TomcatWebServer webServer = (TomcatWebServer)factory.getWebServer();
        int utilityThreads = webServer.getTomcat().getServer().getUtilityThreads();
        log.info("Application started utilityThreads # {}", utilityThreads);
    }
}