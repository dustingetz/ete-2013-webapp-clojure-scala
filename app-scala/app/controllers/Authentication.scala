package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.data.Form
import play.api.data.Forms._

import jp.t2v.lab.play2.auth.{AuthConfig, LoginLogout}
import scala.reflect.ClassTag


object Authentication extends Controller with LoginLogout with AuthConfigImpl {

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


sealed trait Permission
case object Administrator extends Permission
case object NormalUser extends Permission


trait AuthConfigImpl extends AuthConfig {

  type Id = String
  type User = String
  type Authority = Permission

  val idTag: ClassTag[Id] = scala.reflect.classTag[Id]
  val sessionTimeoutInSeconds: Int = 3600

  def resolveUser(id: Id): Option[User] = Some(id) //Account.findById(id)

  def loginSucceeded(request: RequestHeader): Result = Redirect("/artscentre")
  def logoutSucceeded(request: RequestHeader): Result = Redirect("/login")
  def authenticationFailed(request: RequestHeader): Result = Redirect("/login")
  def authorizationFailed(request: RequestHeader): Result = Forbidden("no permission")

  def authorize(user: User, neededAuthority: Authority): Boolean = {
    val userPermission: Permission = NormalUser // user.permission
    (userPermission, neededAuthority) match {
      case (Administrator, _) => true         // Administrator can access everything
      case (NormalUser, NormalUser) => true   // Normal users are restricted to certain pages
      case _ => false
    }
  }

  override lazy val cookieSecureOption: Boolean = play.api.Play.current.configuration.getBoolean("auth.cookie.secure").getOrElse(false)
}