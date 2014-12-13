import io.ino.play.tracing.{SlowRequestLogger, StoreRequestInfoFilter}
import play.api.Application
import play.api.libs.concurrent.Akka
import play.api.mvc.WithFilters

object Global extends WithFilters(StoreRequestInfoFilter) {

  override def onStart(app: Application): Unit = {
    SlowRequestLogger.start(Akka.system(app))
  }

}
