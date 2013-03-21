package artscentre.models

/**
 * Models don't have IDs - that is a database implementation detail.
 * We can still correlate models using ID as their identity by preferring
 * to store our model instances inside of maps keyed by ID. (think about the term
 * "primary key" for a moment for why this makes tons of sense)
 */

case class User(username: String)

case class UserInfo(firstname: String, lastname: String, email: String, username: String, created: java.util.Date)

case class Skill(name: String)

case class Project(name: String, ownercreated: Long)

case class ProjectInfo(name: String, owner: User, created: Long, members: List[User], skills: List[Skill])



//class User(val id: Int, val username: String)
//class UserInfo(val id: Int, val firstname: String, val lastname: String, val email: String, val username: String, val created: Long)
//class Skill(val id: Int, val name: String)
//class Project(val id: Int, val name: String, val ownerId: Int, val created: Long)
//class ProjectInfo(val id: Int, val name: String, val owner: User, val created: Long, val members: List[User], val skills: List[Skill])


object Implicits {
  import play.api.libs.json._

  implicit val userFmt = Json.format[User]
  implicit val userInfoFmt = Json.format[UserInfo]
  implicit val projectFmt = Json.format[Project]
  implicit val projectInfoFmt = Json.format[ProjectInfo]
  implicit val skillFmt = Json.format[Skill]
}