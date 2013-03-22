package artscentre.models

import java.text.SimpleDateFormat

/**
 * Models don't have IDs - that is a database implementation detail.
 * We can still correlate models using ID as their identity by preferring
 * to store our model instances inside of maps keyed by ID. (think about the term
 * "primary key" for a moment for why this makes tons of sense)
 */

/**
 * can be case sensitive because in some cases we reuse the models as json payloads
 */

case class User(username: String)   // is the userId part of the public model? is it used on the client?
                                    // probably shouldnt be, as username has unique constraint

case class UserInfo(firstName: String, lastName: String, email: String, username: String, created: java.util.Date)

case class Skill(name: String)

case class Project(name: String, owner: String, created: java.util.Date)

case class ProjectInfo(name: String, owner: User, created: java.util.Date, members: Set[User], skills: Set[Skill])



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
  implicit val skillFmt = Json.format[Skill]


  // ProjectInfo has object members; default formatters aren't enough
  implicit object StudyEventHistoryInfoFormat extends Format[ProjectInfo] {

    // hack - will throw if parse error
    def reads(json: JsValue) = JsSuccess(ProjectInfo(
      (json \ "name").as[String],
      (json \ "owner").as[User],
      (json \ "created").as[Option[java.util.Date]].get,
      (json \ "members").as[Set[User]],
      (json \ "skills").as[Set[Skill]]))

    def writes(u: ProjectInfo): JsValue = JsObject(List(
      "name" -> JsString(u.name),
      "owner" -> JsString(u.owner.username),
      "created" -> Json.toJson(u.created),
      "members" -> JsArray(u.members.map(Json.toJson(_)).toSeq),
      "skills" -> JsArray(u.skills.map(Json.toJson(_)).toSeq)))
  }


  implicit object JsonDateFormatter extends Format[Option[java.util.Date]] {

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

    def writes(date: Option[java.util.Date]): JsValue = Json.toJson(date.map(date => dateFormat.format(date)).getOrElse(""))

    // hack - throws for parse errors
    def reads(j: JsValue) = JsSuccess(Some(dateFormat.parse(j.as[String])))

  }
}