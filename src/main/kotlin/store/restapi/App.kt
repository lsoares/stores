package store.restapi

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.http.staticfiles.Location
import java.lang.Exception

fun main() {
    val config = RealConfig
    // val config = StubbedConfig
    App(config)
        .start(System.getenv("API_PORT")?.toInt() ?: 8080)
}

class App(appConfig: AppConfig) : AutoCloseable {

    private val javalinApp by lazy {
        with(appConfig) {
            Javalin
                .create {
                    it.addStaticFiles("src/main/resources/public", Location.EXTERNAL)
                }
                .routes {
                    path("stores") {
                        get(ListStoresHandler(listStores))
                    }
                }
                .exception(Exception::class.java) { ex, _ ->
                    // TODO: use proper logger
                    println(ex.message)
                }
        }
    }

    fun start(port: Int): App {
        javalinApp.start(port)
        return this
    }

    override fun close() {
        javalinApp.stop()
    }
}