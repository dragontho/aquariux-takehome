package com.dragontho.aqtakehome.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum ThreadPool {

    ACCOUNT(Executors.newFixedThreadPool(5)),
    ;

    private final ExecutorService executorService;

    ThreadPool(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
