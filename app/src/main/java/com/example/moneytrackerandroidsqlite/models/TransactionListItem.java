package com.example.moneytrackerandroidsqlite.models;

public class TransactionListItem {
    public enum ItemType {
        DATE_HEADER,
        TRANSACTION
    }

    private ItemType type;
    private String dateText;
    private Transaction transaction;

    // Constructor for date header
    public TransactionListItem(String dateText) {
        this.type = ItemType.DATE_HEADER;
        this.dateText = dateText;
    }

    // Constructor for transaction
    public TransactionListItem(Transaction transaction) {
        this.type = ItemType.TRANSACTION;
        this.transaction = transaction;
    }

    public ItemType getType() {
        return type;
    }

    public String getDateText() {
        return dateText;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
