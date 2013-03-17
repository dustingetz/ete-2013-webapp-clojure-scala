package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play2.auth.Auth
import controllers.auth.{NormalUser, ServiceAuthConfig}


object Api extends Controller with Auth with ServiceAuthConfig {

  def whoami = authorizedAction(NormalUser) { user => implicit request =>
    val resp = Map(
      "username" -> user,
      "firstName" -> "First",
      "lastName" -> "Last")
    Ok(Json.toJson(resp))
  }

}