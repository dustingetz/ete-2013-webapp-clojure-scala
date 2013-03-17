package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._


object Test extends Controller {

  def hello = Action { request =>
    val resp = Map("status" -> "OK", "message" -> ("Hello world"))
    Ok(Json.toJson(resp))
  }

  def sayHello = Action(parse.json) { request =>
    (request.body \ "name").asOpt[String].map { name =>
      Ok(Json.toJson(
        Map("status" -> "OK", "message" -> ("Hello " + name))
      ))
    }.getOrElse {
      BadRequest(Json.toJson(
        Map("status" -> "KO", "message" -> "Missing parameter [name]")
      ))
    }
  }

}