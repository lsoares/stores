package store.restapi

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.JavalinConfig
import io.javalin.http.staticfiles.Location
import store.AppConfig
import store.RealConfig

fun main() {
    App(RealConfig).start(apiPort)
}

private val apiPort get() = System.getenv("API_PORT")?.toInt() ?: 8081

class App(appConfig: AppConfig) : AutoCloseable {

    private val javalinApp by lazy {
        with(appConfig) {
            Javalin
                .create { config ->
                    runCatching {
                        config.withHotReload("src/main/resources/public")
                    }.onFailure {
                        config.addStaticFiles("/public")
                    }
                }
                .routes {
                    path("stores") {
                        get(ListStoresHandler(listStores))
                        patch(":id", SetStoreNameHandler(setStoreName))
                        get("export.csv", GenerateCsvHandler(listStores))
                    }
                }
                .exception(Exception::class.java) { ex, _ ->
                    // TODO: use proper logger
                    ex.printStackTrace()
                }
        }
    }

    private fun JavalinConfig.withHotReload(path: String) =
        addStaticFiles(path, Location.EXTERNAL)

    fun start(port: Int): App {
        javalinApp.start(port)
        return this
    }

    override fun close() {
        javalinApp.stop()
    }
}