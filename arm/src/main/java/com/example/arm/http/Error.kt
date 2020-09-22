package com.example.arm.http

/**
 *  author : yanghaozhang
 *  date : 2020/9/22 11:37
 *  description :
 */
class HttpResultErrorException(val code: Int = -1, msg: String?) : IllegalArgumentException(msg ?: "数据异常")

class HttpRequestFailException : IllegalArgumentException("数据异常")
