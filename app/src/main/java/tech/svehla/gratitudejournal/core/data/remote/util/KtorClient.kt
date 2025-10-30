package tech.svehla.gratitudejournal.core.data.remote.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIME_OUT = 10_000L

//val ktorHttpClient = HttpClient(Android) {
//    install(JsonFeature) {
//        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
//            prettyPrint = true
//            isLenient = true
//            ignoreUnknownKeys = true
//        })
//
//        engine {
//            connectTimeout = TIME_OUT
//            socketTimeout = TIME_OUT
//        }
//    }
//
//    install(Logging) {
//        logger = object : Logger {
//            override fun log(message: String) {
//                Timber.v("Logger Ktor => $message")
//            }
//
//        }
//        level = LogLevel.ALL
//    }
//
//    install(ResponseObserver) {
//        onResponse { response ->
//            Timber.d("HTTP status: ${response.status.value}")
//        }
//    }
//
//    install(DefaultRequest) {
//        header(HttpHeaders.ContentType, ContentType.Application.Json)
//    }
//}

val ktorHttpClient = HttpClient(CIO) {
    engine {
        requestTimeout = TIME_OUT
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
        filter { request ->
            request.url.host.contains("ktor.io")
        }
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
}