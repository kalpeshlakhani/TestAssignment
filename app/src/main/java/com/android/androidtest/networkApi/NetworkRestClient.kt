package com.android.androidtest.networkApi

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.androidtest.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkRestClient {
    private val TAG: String = NetworkRestClient::class.java.simpleName


    // Create a trust manager that does not validate certificate chains
    // Install the all-trusting trust manager
    // Create an ssl socket factory with our all-trusting manager
    private fun unsafeOkHttpClient(): OkHttpClient {
        try {

            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>, authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>, authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory


            val builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val logger = HttpLoggingInterceptor.Logger { message ->
                    Log.d("HSE-HTTP-LOGGER", message)
                }

                val logging = HttpLoggingInterceptor(logger)
                logging.level = HttpLoggingInterceptor.Level.BODY

                builder.addInterceptor(logging)
            }


            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ ->
                true
            }
            builder.connectTimeout(1, TimeUnit.HOURS)
            builder.readTimeout(1, TimeUnit.HOURS)
            builder.writeTimeout(1, TimeUnit.HOURS)

            return builder.build()
        } catch (e: Exception) {
            Log.e(TAG, "Http Exception : ${e.message}")
            throw RuntimeException(e)
        }

    }

    private fun getRetrofit(context: Context, endPoint: String): Retrofit {
        return Retrofit.Builder().baseUrl(endPoint)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create()).client(unsafeOkHttpClient())
            .build()
    }

    class NullOnEmptyConverterFactory : Converter.Factory() {

        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {
            val delegate = retrofit.nextResponseBodyConverter<Any>(this, type, annotations)
            return Converter<ResponseBody, Any> { body ->
                if (body.contentLength() == 0L) null else delegate.convert(
                    body
                )
            }
        }
    }

    fun getApiService(context: Context, baseURl: String): ApiService {
        return getRetrofit(context, baseURl).create(ApiService::class.java)
    }
}