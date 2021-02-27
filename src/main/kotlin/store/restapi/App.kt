package store.restapi

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.core.JavalinConfig
import io.javalin.http.staticfiles.Location
import store.AppConfig
import store.RealConfig

fun main() {
    App(RealConfig).start(apiPort)
}

private val apiPort get() = System.getenv("API_PORT")?.toInt() ?: 8080

class App(appConfig: AppConfig) : AutoCloseable {

    private val javalinApp by lazy {
        with(appConfig) {
            Javalin
                .create {
                    it.setupWebpage()
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

    private fun JavalinConfig.setupWebpage() {
        runCatching {
            addStaticFiles("src/main/resources/public", Location.EXTERNAL)
        }.onFailure {
            addStaticFiles("/public", Location.CLASSPATH)
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