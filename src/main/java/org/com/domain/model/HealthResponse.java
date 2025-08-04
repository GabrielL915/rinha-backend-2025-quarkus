package org.com.domain.model;

public class HealthResponse {
    private boolean failing;
    private long minResponseTime;

    public HealthResponse() {
    }

    public HealthResponse(boolean failing, long minResponseTime) {
        this.failing = failing;
        this.minResponseTime = minResponseTime;
    }

    public boolean isFailing() {
        return failing;
    }

    public long getMinResponseTime() {
        return minResponseTime;
    }

}
