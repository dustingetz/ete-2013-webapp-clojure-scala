package controllers.auth

import jp.t2v.lab.play2.auth.AuthConfig
import scala.reflect.ClassTag
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results._
import scala.Some


trait PageAuthConfig extends ArtscentreAuthConfig {

  def loginSucceeded(request: RequestHeader): Result = Redirect("/artscentre")
  def logoutSucceeded(request: RequestHeader): Result = Redirect("/login")
  def authenticationFailed(request: RequestHeader): Result = Redirect("/login")
  def authorizationFailed(request: RequestHeader): Result = Forbidden("not authorized")

}