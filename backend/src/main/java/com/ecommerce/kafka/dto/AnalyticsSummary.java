package com.ecommerce.kafka.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AnalyticsSummary {

    private LocalDate date;
    private BigDecimal revenue;
    private long paidOrders;

    public AnalyticsSummary() {
    }

    public AnalyticsSummary(LocalDate date, BigDecimal revenue, long paidOrders) {
        this.date = date;
        this.revenue = revenue;
        this.paidOrders = paidOrders;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public long getPaidOrders() {
        return paidOrders;
    }

    public void setPaidOrders(long paidOrders) {
        this.paidOrders = paidOrders;
    }
}
