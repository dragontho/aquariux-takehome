package com.dragontho.aqtakehome.tasks;

import com.dragontho.aqtakehome.services.ExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetPricingTask {

    @Autowired
    private ExchangeService exchangeService;

    @Scheduled(cron = "*/10 * * * * *") // Execute every 10 seconds
    @Async("taskExecutor") // Execute in a separate thread
    public void getPricing() {
        exchangeService.aggregatePrices()
                .thenRun(() -> log.info("Price aggregation task completed."))
                .exceptionally(ex -> {
                    log.error("Error in price aggregation task", ex);
                    return null;
                });
    }
}
