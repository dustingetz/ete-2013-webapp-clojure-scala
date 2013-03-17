package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._


object Api extends Controller {

  def whoami = Action {
    val resp = Map("status" -> "OK", "message" -> ("Hello world"))
    Ok(Json.toJson(resp))
  }

}