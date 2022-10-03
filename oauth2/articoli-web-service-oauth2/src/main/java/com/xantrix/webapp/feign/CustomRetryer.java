package com.xantrix.webapp.feign;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRetryer implements Retryer {

    private final int maxAttempts;
    private final long backoff;
    int attempt;

    public CustomRetryer() {
        this(2000, 5);
    }

    public CustomRetryer(long backoff, int maxAttempts) {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

    public void continueOrPropagate(RetryableException e) {
        if (attempt++ > maxAttempts) {
            throw e;
        } else {
            log.warn("Tentativo di connessione {}", attempt - 1);
        }

        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Retryer clone() {
        return new CustomRetryer(backoff, maxAttempts);
    }
}
