package com.example.armmvvm.base

import com.example.arm.http.ErrorListener
import com.example.arm.http.HttpResultErrorException
import com.example.arm.util.ArmsUtils
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 2:02
 *  description :
 */
class MyErrorListener:ErrorListener {
    override fun handleResponseError(t: Throwable?) {
        Timber.tag("MyErrorListener").e(t)
        //根据不同的错误做出不同的逻辑处理
        //这里只是对几个常用错误进行简单的处理, 展示这个类的用法, 在实际开发中请您自行对更多错误进行更严谨的处理
        var msg: String? = "未知错误"
        when (t) {
            is UnknownHostException -> {
                msg = "网络不可用"
            }
            is SocketTimeoutException -> {
                msg = "请求网络超时"
            }
            is ConnectException -> {
                msg = "请求连接失败"
            }
            is HttpException -> {
                msg = convertStatusCode(t)
            }
            is JsonParseException, is ParseException, is JSONException -> {
                msg = "数据解析错误"
            }
            is HttpResultErrorException -> {
                msg = t.message
            }
        }
        ArmsUtils.makeText(msg)
    }

    /**
     * 根据项目需要添加Exception
     */
    override fun handleNormalError(t: Throwable?) {
        Timber.tag("MyErrorListener").e(t)
        var msg = "未知错误"
        when (t) {
            is NullPointerException, is IndexOutOfBoundsException -> {
                msg = "数据异常"
            }
            is IOException -> {
                msg = "文件处理异常"
            }
            is JsonParseException, is ParseException, is JSONException -> {
                msg = "数据解析错误"
            }
        }
        ArmsUtils.makeText(msg)
    }

    private fun convertStatusCode(httpException: HttpException): String? {
        val msg: String
        msg = if (httpException.code() == 500) {
            "服务器发生错误"
        } else if (httpException.code() == 404) {
            "请求地址不存在"
        } else if (httpException.code() == 403) {
            "请求被服务器拒绝"
        } else if (httpException.code() == 307) {
            "请求被重定向到其他页面"
        } else {
            httpException.message()
        }
        return msg
    }
}