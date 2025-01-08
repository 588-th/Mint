package com.example.mint.models;

public enum OperationType {
    Income,
    Expense;

    public static OperationType getEnumFromString(String string) {
        for (OperationType operationType : values()) {
            if (operationType.name().equalsIgnoreCase(string)) {
                return operationType;
            }
        }

        throw new IllegalArgumentException("No enum constant for string: " + string);
    }
}
