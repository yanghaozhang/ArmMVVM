package com.example.arm.http;

public interface ErrorListener {
    void handleResponseError(Throwable t);

    void handleNormalError(Throwable t);
}