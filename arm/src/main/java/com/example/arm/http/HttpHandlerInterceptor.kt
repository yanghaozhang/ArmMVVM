package com.example.arm.http

import com.example.arm.http.log.RequestInterceptor
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class HttpHandlerInterceptor(var mHandler: GlobalHttpHandler) : RequestInterceptor() {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse: Response
        originalResponse = try {
            chain.proceed(mHandler.onHttpRequestBefore(chain, request))
        } catch (e: Exception) {
            Timber.w(e)
            throw e
        }
        val responseBody = originalResponse.body()
        //获取响应结果
        var bodyString: String? = null
        if (responseBody != null && isParseable(responseBody.contentType())) {
            bodyString = printResult(request, originalResponse, false)
        }

        //这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
        return mHandler.onHttpResultResponse(bodyString, chain, originalResponse)
    }
}