package org.com.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Payment {
    private UUID id;
    private UUID correlationId;
    private BigDecimal amount;
    private Instant requestAt;
    private boolean isDefault;

    public Payment() {
    }

    public Payment(UUID correlationId, BigDecimal amount, boolean isDefault) {
        this.id = UUID.randomUUID();
        this.correlationId = correlationId;
        this.amount = amount;
        this.requestAt = Instant.now();
        this.isDefault = isDefault;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getRequestAt() {
        return requestAt;
    }

    public void setRequestAt(Instant requestAt) {
        this.requestAt = requestAt;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
