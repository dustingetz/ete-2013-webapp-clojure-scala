package controllers.auth

import jp.t2v.lab.play2.auth.AuthConfig
import scala.reflect.ClassTag
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results._
import scala.Some


trait ServiceAuthConfig extends ArtscentreAuthConfig {

  // web service calls are not issuing login/logout; these are page endpoints.
  def loginSucceeded(request: RequestHeader): Result = ???
  def logoutSucceeded(request: RequestHeader): Result = ???

  // if a webservice fails in auth, we can't redirect since it's a service not a page.
  // so send down a status code.
  def authenticationFailed(request: RequestHeader): Result = Forbidden("not authenticated")
  def authorizationFailed(request: RequestHeader): Result = Forbidden("not authorized")

}
