package restapi

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path

fun main() {
    WebApp(MockConfig, System.getenv("API_PORT")?.toInt() ?: 8080).start()
}

class WebApp(dependencies: Dependencies, private val port: Int) : AutoCloseable {

    private val javalinApp by lazy {
        with(dependencies) {
            Javalin
                .create {
                    it.addStaticFiles("/public")
                }
                .routes {
                    path("stores") {
                        get(ListStoresHandler(listStores))
                    }
                }
        }
    }

    fun start(): WebApp {
        javalinApp.start(port)
        return this
    }

    override fun close() {
        javalinApp.stop()
    }
}