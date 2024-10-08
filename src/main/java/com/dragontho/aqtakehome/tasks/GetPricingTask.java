package com.dragontho.aqtakehome.tasks;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GetPricingTask {
    @Scheduled(cron = "*/10 * * * * *") // Execute every 1 second
    @Async // Execute in a separate thread
    public void getPricing() {
        // Task logic goes here
        System.out.println("Task 1 executed.");
    }
}
