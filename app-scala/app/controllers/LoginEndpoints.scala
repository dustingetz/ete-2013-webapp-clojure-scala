package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import jp.t2v.lab.play2.auth.LoginLogout
import controllers.auth.PageAuthConfig


object LoginEndpoints extends Controller with LoginLogout with PageAuthConfig {

  private def authenticate(username: String, password: String): Boolean = true

  private val loginForm = Form(
    tuple("username" -> text, "password" -> text)
      .verifying("Invalid username or password", result => authenticate(result._1, result._2)))


  def loginPage = Action { Ok(views.html.login()) }

  def doLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => authenticationFailed(request),
      form => {
        val username = form._1
        gotoLoginSucceeded(username)
      }
    )
  }
}


