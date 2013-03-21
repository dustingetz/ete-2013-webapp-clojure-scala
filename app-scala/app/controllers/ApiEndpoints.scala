package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play2.auth.Auth
import controllers.auth.{NormalUser, ServiceAuthConfig}
import artscentre.orm._
import artscentre.models._
import artscentre.models.Implicits._
import play.api.db.DB
import java.util.UUID.randomUUID


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

    val userInfo = DB.withConnection { implicit dbconn =>
      UserInfoMapping.read(dbconn, user)
    }

    Ok(Json.toJson(userInfo))
  }


  private case class UserSkillPickerPayload(id: String, name: String, enabled: Boolean)
  private implicit val skillPayloadFmt = Json.format[UserSkillPickerPayload]


  // express with type system: what resources is this endpoint allowed to use?
  // user, request, database.
  // this helps us keep dependencies on a request or database isolated, so as much of our code
  // as possible can run in contexts without these.
  def listSkillsUserPicker = authorizedAction(NormalUser) { user => implicit request =>

    val (allSkills, userSkills) = DB.withConnection { implicit dbconn =>

      val allSkills: Map[String, Skill] = SkillsMapping.all(dbconn)
      val userSkills: Map[String, Skill] = SkillsMapping.forUser(dbconn, user)

      (allSkills, userSkills)
    }

    val payload: Iterable[UserSkillPickerPayload] = allSkills.view.map { case (id, skill) =>
      val enabled = userSkills.contains(id)
      UserSkillPickerPayload(id, skill.name, enabled)
    }

    Ok(Json.toJson(payload))
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

        val project: Option[Project] = ProjectMapping.read(dbconn, projectId)

        // this error handling sucks
        project.filter(_.owner == user).map { _ =>
          ProjectMapping.deleteProject(dbconn, projectId)
        }

      }
    } match {
      case Some(_) => Ok
      case None => BadRequest("malformed request params")
    }

  }

  def listOwnedProjects = authorizedAction(NormalUser) { user => implicit request =>

    val ownedProjects: List[ProjectInfo] = DB.withConnection { implicit dbconn =>
      ProjectMapping.forOwner(dbconn, user)
    }

    Ok(Json.toJson(ownedProjects))
  }

  def listJoinedProjects = authorizedAction(NormalUser) { user => implicit request =>

    val joinedProjects: List[ProjectInfo] = DB.withConnection { implicit dbconn =>
      ProjectMapping.forMember(dbconn, user)
    }

    Ok(Json.toJson(joinedProjects))
  }

  def listElligibleProjects = authorizedAction(NormalUser) { user => implicit request =>

    val elligibleProjects: List[ProjectInfo] = DB.withConnection { implicit dbconn =>
      ProjectMapping.elligibleForUser(dbconn, user)
    }

    Ok(Json.toJson(elligibleProjects))
  }



}