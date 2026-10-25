package com.TradeP.SmartTrade.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " not found with id: " + identifier);
    }
}
