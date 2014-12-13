package io.ino.play

import io.ino.play.tracing.Tracing
import play.api.libs.concurrent.Promise
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Main application controller.
 */
object Application extends Controller {

  import Tracing._

  def index() = Action.async { request =>
    // trace some dummy async calls...
    for {
      // simple tracing, in your application there be a good/central place to intercept operations of a specific type
      res1 <- traceSegmentAsync("op1", "db-cassandra", "datastax") {
        longRunningOp1()
      }
      // additionaly record some metadata based on the result of the operation
      res2 <- traceSegmentAsyncWithMetadata("op2", "db-postgresql", "postgresql-async") {
        longRunningOp2()
      }((segment, res) => segment.addMetadata("numItems", res.length.toString))
    } yield Ok(s"Results: $res1, $res2")
  }

  private def longRunningOp1(): Future[String] = {
    Promise.timeout("op1result", 50 millis)
  }

  private def longRunningOp2(): Future[Seq[String]] = {
    Promise.timeout(Seq("op2result1", "op2result2"), 50 millis)
  }


}
