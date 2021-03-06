package it.pagopa.ecommerce.payment.instruments.task;

import it.pagopa.ecommerce.payment.instruments.application.PspService;
import it.pagopa.ecommerce.payment.instruments.client.ApiConfigClient;
import it.pagopa.generated.ecommerce.apiconfig.v1.dto.ServicesDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class ScheduledPSPUpdate {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Autowired
    private ApiConfigClient apiConfigClient;
    @Autowired
    private PspService pspService;
    @Scheduled(cron = "${apiConfig.psp.update.cronString}")
    public void updatePSPs(){
        AtomicReference<Integer> currentPage = new AtomicReference<>(0);
        log.info("Starting PSPs scheduled update. Time: {}", dateFormat.format(new Date()));;

        apiConfigClient.getPSPs(0, 50, null).expand(
                servicesDto -> {
                    if (servicesDto.getPageInfo().getTotalPages().equals(currentPage.get())) {
                        return Mono.empty();
                    }
                    return apiConfigClient.getPSPs(currentPage.updateAndGet(v -> v + 1), 50, null);
                }
        ).collectList().subscribe(
                instruments -> {
                    for(ServicesDto servicesDto: instruments) {
                        pspService.updatePSPs(servicesDto);
                    }
                },
                error -> log.error("[ScheduledPSPUpdate] Error: " + error)
        );
    }
}
