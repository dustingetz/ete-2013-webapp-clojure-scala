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


object Implicits {
  import play.api.libs.json._

  implicit val skillFmt = Json.format[Skill]
}