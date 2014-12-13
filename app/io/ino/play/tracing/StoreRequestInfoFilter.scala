package io.ino.play.tracing

import kamon.trace.TraceRecorder
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

/**
 * Stores request info in kamon's trace local storage, so that it's available for logging (SlowRequestLogger).
 */
object StoreRequestInfoFilter extends Filter {

  import TraceMetadata._

  override def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    val ctxt = TraceRecorder.currentContext
    ctxt.addMetadata(RequestMethod, rh.method)
    ctxt.addMetadata(RequestUri, rh.uri)

    next(rh)
  }
}
