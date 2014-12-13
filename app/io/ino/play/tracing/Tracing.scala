package io.ino.play.tracing

import kamon.trace.{TraceRecorder, Segment}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}

/**
 * Tracing support.
 */
object Tracing {

  /**
   * Trace the given block as segment using the provided segment name/category/library.
   */
  def traceSegmentAsync[T](segmentName: String, category: String, library: String)
                          (block: => Future[T])
                          (implicit ec: ExecutionContext): Future[T] = TraceRecorder.withTraceContextAndSystem { (ctx, _) =>
    val segment = ctx.startSegment(segmentName, category, library)
    val res = block
    block.onComplete(_ => segment.finish())
    res
  }.getOrElse(block)

  /**
   * Trace the given block as segment using the provided segment name/category/library.
   * The segment is passed to the given callback block so that it can record additional metadata.
   */
  def traceSegmentAsyncWithMetadata[T](segmentName: String, category: String, library: String)
                                      (block: => Future[T])
                                      (recordMetadata: (Segment, T) => Unit)
                                      (implicit ec: ExecutionContext): Future[T] = TraceRecorder.withTraceContextAndSystem { (ctx, _) =>
    val segment = ctx.startSegment(segmentName, category, library)
    val res = block
    block.onComplete {
      case Success(value) =>
        recordMetadata(segment, value)
        segment.finish()
      case Failure(e) =>
        segment.finish()
    }
    res
  }.getOrElse(block)

}
