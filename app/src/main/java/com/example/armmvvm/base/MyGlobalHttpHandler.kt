package com.example.armmvvm.base

import com.example.arm.http.GlobalHttpHandler
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


/**
 *  author : yanghaozhang
 *  date : 2020/9/21 23:19
 *  description :
 */
class MyGlobalHttpHandler : GlobalHttpHandler {
    override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
        if (request.body() is FormBody) {
            // 构造新的请求表单
            val builder = getNewBuilder()
            val body = request.body() as FormBody?
            //将以前的参数添加
            for (i in 0 until body!!.size()) {
                builder.add(body!!.encodedName(i), body!!.encodedValue(i))
            }
            return request.newBuilder().post(builder.build()).build() //构造新的请求体
        } else if (request.body() == null) {
            val builder = getNewBuilder()
            return request.newBuilder().post(builder.build()).build()
        }
        return request
    }

    private fun getNewBuilder(): FormBody.Builder {
        val builder = FormBody.Builder()
        builder.add("key", "e1fb7e102aa2f6976161e78598632648")
        return builder
    }

    override fun onHttpResultResponse(httpResult: String?, chain: Interceptor.Chain, response: Response): Response {
        return response
    }
}