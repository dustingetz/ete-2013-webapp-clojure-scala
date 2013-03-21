package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play2.auth.Auth
import controllers.auth.{NormalUser, ServiceAuthConfig}
import artscentre.orm.{SkillsetMapping, SkillsMapping}
import artscentre.models._
import artscentre.models.Implicits._
import play.api.db.DB


object ApiEndpoints extends Controller with Auth with ServiceAuthConfig {

  // this singleton gives you access to a running play context
  // includes config, database connection pool, etc.
  // if this implicit is in scope, the code is now coupled to a Play context.
  // Structuring code to keep this dependency isolated means we can drop
  // our code into a context without play - e.g. to port our stuff to use Jersey services instead.
  import play.api.Play.current



  def whoami = authorizedAction(NormalUser) { user => implicit request =>
    val resp = ???
    Ok(Json.toJson(resp))
  }


  private case class UserSkillPickerPayload(id: String, name: String, enabled: Boolean)
  private implicit val skillPayloadFmt = Json.format[UserSkillPickerPayload]

  // express with type system: what resources is this endpoint allowed to use?
  // user, request, database.
  // this helps us keep dependencies on a request or database isolated, so as much of our code
  // as possible can run in contexts without these.
  def listSkillsUserPicker = authorizedAction(NormalUser) { user => implicit request =>
    DB.withConnection { implicit dbconn =>

      val allSkills: Map[String, Skill] = SkillsMapping.all(dbconn)
      val userSkills: Map[String, Skill] = SkillsMapping.forUser(dbconn, user)

      val payload: Iterable[UserSkillPickerPayload] = allSkills.view.map { case (id, skill) =>
        val enabled = userSkills.contains(id)
        UserSkillPickerPayload(id, skill.name, enabled)
      }

      Ok(Json.toJson(payload))
    }
  }


  def updateUserSkills = authorizedAction(parse.json, NormalUser) { user => implicit request =>
    DB.withConnection { implicit dbconn =>
      val b: JsValue = request.body
      b.asOpt[List[String]].map { SkillsetMapping.updateUserSkills(dbconn, user, _) }.map { _ => Ok } getOrElse (BadRequest)
    }
  }


  def listAllSkills = authorizedAction(NormalUser) { user => implicit request =>
    DB.withConnection { implicit dbconn =>

      val allSkills: Map[String, Skill] = SkillsMapping.all(dbconn)

      val payload = ???

      Ok(Json.toJson(payload))
    }
  }

  def listUserSkills = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def createProject = authorizedAction(parse.json, NormalUser) { user => implicit request =>
    ???
  }

  def projectAddMember = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def projectRemoveMember = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def deleteProject = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def listOwnedProjects = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def listJoindProjects = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def listElligibleProjects = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }



}