package controllers.auth

import jp.t2v.lab.play2.auth.AuthConfig
import scala.reflect.ClassTag
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results._
import scala.Some


trait ArtscentreAuthConfig extends AuthConfig {

  type Id = String
  type User = String
  type Authority = Permission

  val idTag: ClassTag[Id] = scala.reflect.classTag[Id]
  val sessionTimeoutInSeconds: Int = 3600

  def resolveUser(id: Id): Option[User] = Some(id) //Account.findById(id)

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