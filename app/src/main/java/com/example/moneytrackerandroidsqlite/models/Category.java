package com.example.moneytrackerandroidsqlite.models;

import androidx.annotation.NonNull;

public class Category {
    public enum Type {
        EXPENSE,
        INCOME
    }

    private long id;
    private Long userId;
    private String name;
    private Type type;
    private boolean isDefault;
    public Category() {}

    public Category(Long userId, String name, Type type, boolean isDefault) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.isDefault = isDefault;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
