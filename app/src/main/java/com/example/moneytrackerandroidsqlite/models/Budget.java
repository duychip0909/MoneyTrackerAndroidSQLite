package com.example.moneytrackerandroidsqlite.models;

import java.util.Date;

public class Budget {
    public enum PeriodType {
        WEEKLY,
        MONTHLY,
        YEARLY
    }
    private long id;
    private long userId;
    private long categoryId;
    private double amount;
    private PeriodType periodType;
    private Long startDate;
    private Long endDate;
    private Date createdAt;
    private Date updatedAt;

    private String categoryName;
    private double spentAmount;
    private double remainingAmount;
    private double progressPercentage;

    public Budget() {}

    public Budget(long userId, long categoryId, double amount, PeriodType periodType, long startDate, long endDate, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
    private void calculateDerivedValue() {
        this.remainingAmount = this.amount - this.spentAmount;
        this.progressPercentage = (this.spentAmount / this.amount) * 100;
    }

}
