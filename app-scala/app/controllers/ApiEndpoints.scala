package controllers

import anorm.SqlParser._
import anorm._
import anorm.~
import play.api._
import play.api.db.DB
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play2.auth.Auth
import controllers.auth.{NormalUser, ServiceAuthConfig}
import java.util.UUID.randomUUID
import scala.util.{Try, Failure, Success}

import artscentre.models._
import artscentre.models.Implicits._
import artscentre.orm.anorm._


object ApiEndpoints extends Controller with Auth with ServiceAuthConfig {

  // this singleton gives you access to a running play context
  // includes config, database connection pool, etc.
  // if this implicit is in scope, the code is now coupled to a Play context.
  // Structuring code to keep this dependency isolated means we can drop
  // our code into a context without play - e.g. to port our stuff to use Jersey services instead.
  import play.api.Play.current


  /**
   * {"username":"binky","firstName":"binky","lastName":"rabbit"}
   */
  def whoami = authorizedAction(NormalUser) { user => implicit request =>

    val userInfo: Try[Option[UserInfo]] = DB.withConnection { implicit dbconn =>
      UserInfoMapping.read(dbconn, user)
    }

    userInfo match {
      case Failure(err) => InternalServerError(err.toString)
      case Success(Some(obj)) => Ok(Json.toJson(obj))
      case Success(None) => InternalServerError("couldn't read userinfo for user `%s`".format(user))
    }
  }


  private implicit val skillPayloadFmt = Json.format[UserSkillPicker]

  case class UserSkillPicker(id: String,
                             name: String,
                             enabled: Boolean)




  def listSkillsUserPicker = authorizedAction(NormalUser) { user => implicit request =>

    val x: Try[List[UserSkillPicker]] = DB.withConnection { implicit dbconn =>
      for {
        allSkills: Map[String, Skill] <- SkillsMapping.all(dbconn)
        userSkills: Map[String, Skill] <- SkillsMapping.forUser(dbconn, user)
      }  yield {
        allSkills.map { case (id, skill) =>
          val enabled = userSkills.contains(id)
          UserSkillPicker(id, skill.name, enabled)
        }
      }.toList
    }

    x match {
      case Failure(e) => InternalServerError(e.toString)
      case Success(obj) => Ok(Json.toJson(obj))
    }

  }


  object SkillsMapping {

    val mappingWithId =
      get[String]("skills.id") ~
      get[String]("skills.name") ~
      get[String]("skills.description") map {
        case id~name~desc => id -> Skill(name, desc)
      }

    def all(dbconn: java.sql.Connection): Try[Map[String, Skill]] = Try {

      SQL("SELECT skills.id, skills.name FROM skills")
        .as(mappingWithId *)(dbconn)
        .toMap

    }

    def forUser(dbconn: java.sql.Connection, userId: String): Try[Map[String, Skill]] = Try {

      SQL(
        """
          SELECT skills.id, skills.name, skills.description FROM skills
          INNER JOIN skillsets
          ON skills.id = skillsets.skill_id AND skillsets.user_id = {userId}
        """)
        .on('userId -> userId)
        .as(mappingWithId *)(dbconn)
        .toMap

    }
  }





  def updateUserSkills = authorizedAction(parse.json, NormalUser) { user => implicit request =>

      val b: JsValue = request.body
      b.asOpt[List[String]].map { skillIds =>

        DB.withTransaction { implicit dbconn =>
          SkillsetMapping.updateUserSkills(dbconn, user, skillIds)
        }

      } match {
        case Some(_) => Ok
        case None => BadRequest("malformed request body")
      }
  }


  def createProject = authorizedAction(parse.json, NormalUser) { user => implicit request =>

    // owner: Int, name: String, skills: Seq[Int]
    // {"name":"dustin's project","skills":[3,19,13]}

    val owner: String = ???
    val projectName: String = ???
    val skills: Seq[String] = ???

    DB.withTransaction { implicit dbconn =>

      val projectId = randomUUID().toString
      ProjectMapping.createProject(dbconn, projectId, owner, projectName)

      skills.foreach(skillId => ProjectMapping.addProjectSkill(dbconn, projectId, skillId))

    }

    Ok
  }

  def projectAddMember = authorizedAction(NormalUser) { user => implicit request =>
    DB.withTransaction { implicit dbconn =>
    }
    ???
  }

  def projectRemoveMember = authorizedAction(NormalUser) { user => implicit request =>
    DB.withTransaction { implicit dbconn =>
    }
    ???
  }

  /**
   * /api/delete-project?projectId=4
   * must be project owner to delete it
   */
  def deleteProject = authorizedAction(NormalUser) { user => implicit request =>

    val projectId: Option[String] = request.getQueryString("projectId")

    projectId.map { projectId =>
      DB.withTransaction { implicit dbconn =>

        val project: Try[Option[Project]] = ProjectMapping.read(dbconn, projectId)

        // this error handling sucks
        project.map(_.filter(_.owner == user).map { _ =>
          ProjectMapping.deleteProject(dbconn, projectId)
        })

      }
    } match {
      case Some(_) => Ok
      case None => BadRequest("malformed request params")
    }

  }

  def listOwnedProjects = authorizedAction(NormalUser) { user => implicit request =>

    val ownedProjects: Try[Map[String, ProjectInfo]] = DB.withConnection { implicit dbconn =>
      ProjectMapping.forOwner(dbconn, user)
    }

    ownedProjects match {
      case Failure(err) => InternalServerError(err.toString)
      case Success(obj) => Ok(Json.toJson(obj))
    }

  }

  def listJoinedProjects = authorizedAction(NormalUser) { user => implicit request =>

    val joinedProjects: Try[Map[String, ProjectInfo]] = DB.withConnection { implicit dbconn =>
      ProjectMapping.forMember(dbconn, user)
    }

    joinedProjects match {
      case Failure(err) => InternalServerError(err.toString)
      case Success(obj) => Ok(Json.toJson(obj))
    }
  }

  def listElligibleProjects = authorizedAction(NormalUser) { user => implicit request =>

    val elligibleProjects: Try[Map[String, ProjectInfo]] = DB.withConnection { implicit dbconn =>
      ProjectMapping.elligibleForUser(dbconn, user)
    }

    elligibleProjects match {
      case Failure(err) => InternalServerError(err.toString)
      case Success(obj) => Ok(Json.toJson(obj))
    }
  }



}