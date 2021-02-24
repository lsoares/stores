package restapi

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    WebApp(MockConfig, System.getenv("API_PORT")?.toInt() ?: 8080).start()
}

class WebApp(dependencies: Dependencies, private val port: Int) : AutoCloseable {

    private val javalinApp by lazy {
        with(dependencies) {
            Javalin.create().routes {
                get { it.result("check health") }
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