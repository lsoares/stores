package store.restapi

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.http.staticfiles.Location

fun main() {
    WebApp(MockConfig).start(System.getenv("API_PORT")?.toInt() ?: 8080)
}

class WebApp(dependencies: Dependencies) : AutoCloseable {

    private val javalinApp by lazy {
        with(dependencies) {
            Javalin
                .create {
                    it.addStaticFiles("src/main/resources/public", Location.EXTERNAL)
                }
                .routes {
                    path("stores") {
                        get(ListStoresHandler(listStores))
                    }
                }
        }
    }

    fun start(port: Int): WebApp {
        javalinApp.start(port)
        return this
    }

    override fun close() {
        javalinApp.stop()
    }
}