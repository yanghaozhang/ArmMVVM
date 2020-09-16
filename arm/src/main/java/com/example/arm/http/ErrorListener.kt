package com.example.arm.http

interface ErrorListener {
    fun handleResponseError(t: Throwable?)
    fun handleNormalError(t: Throwable?)
}