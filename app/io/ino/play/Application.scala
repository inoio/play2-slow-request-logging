package io.ino.play

import play.api.mvc.{AnyContent, Action, Controller}

object Application extends Controller {

  def index() = Action { request =>
    Ok("hello play")
  }

}
