package io.ino.play.tracing

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import kamon.Kamon
import kamon.trace.{SegmentInfo, Trace, TraceInfo}
import play.api.Logger
import scala.concurrent.duration._

/**
 * Kamon trace subscriber that logs slow requests. Relies on request method + uri stored
 * under the keys TraceMetadata.{RequestMethod, RequestUri} in the trace context's metadata.
 */
class SlowRequestLogger extends Actor with ActorLogging {

  private val logger = Logger("SlowRequestLog")

  import TraceMetadata._

  override def receive: Receive = {
    case trace: TraceInfo =>
      val requestTime = NANOSECONDS.toMillis(trace.elapsedTime.nanos)
      val segmentsMsg = formatSegmentsInfo(trace.segments)
      val msg = s""""${trace.metadata(RequestMethod)} ${trace.metadata(RequestUri)}" ($requestTime ms)$segmentsMsg"""
      logger.info(msg)
  }

  /**
   * Describes the given segments like this "segments: segment1 (23 ms), segment2 (42 ms, meta21: value21)
   */
  private def formatSegmentsInfo(segments: Seq[SegmentInfo]): String = {
    if (segments.isEmpty) {
      ""
    } else {
      ", segments: " + segments.map { segment =>
        val detailsMsg = if (segment.metadata.isEmpty) {
          ""
        } else {
          ", " + segment.metadata.map { case (key, value) => s"$key: $value"}.mkString(", ")
        }
        segment.name + " (" + NANOSECONDS.toMillis(segment.elapsedTime.nanos) + " ms" + detailsMsg + ")"
      }.mkString(", ")
    }
  }

}

object SlowRequestLogger {

  def props: Props = Props(new SlowRequestLogger)

  def start(implicit system: ActorSystem): Unit = {
    val slowRequestLogger = system.actorOf(props)
    Kamon(Trace).subscribe(slowRequestLogger)
  }

}

object TraceMetadata {

  val RequestMethod = "requestMethod"
  val RequestUri = "requestUri"

}