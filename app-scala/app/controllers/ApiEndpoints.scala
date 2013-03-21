package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play2.auth.Auth
import controllers.auth.{NormalUser, ServiceAuthConfig}
import orm.{SkillsetMapping, SkillsMapping}
import artscentre.{Implicits, Skill}


object ApiEndpoints extends Controller with Auth with ServiceAuthConfig {

  import Implicits._

  def whoami = authorizedAction(NormalUser) { user => implicit request =>
    val resp = Map(
      "username" -> user,
      "firstName" -> "First",
      "lastName" -> "Last")
    Ok(Json.toJson(resp))
  }


  private case class UserSkillPickerPayload(id: String, name: String, enabled: Boolean)
  private implicit val skillPayloadFmt = Json.format[UserSkillPickerPayload]

  def listSkillsUserPicker = authorizedAction(NormalUser) { user => implicit request =>
    val userSkills: Map[String, Skill] = SkillsMapping.forUser(user)
    val allSkills: Map[String, Skill] = SkillsMapping.all()

    val payload: Iterable[UserSkillPickerPayload] = allSkills.view.map { case (id, skill) =>
      val enabled = userSkills.contains(id)
      UserSkillPickerPayload(id, skill.name, enabled)
    }

    Ok(Json.toJson(payload))
  }


  def updateUserSkills = authorizedAction(parse.json, NormalUser) { user => implicit request =>
    val b: JsValue = request.body
    b.asOpt[List[String]].map { SkillsetMapping.updateUserSkills(user, _) }.map { _ => Ok } getOrElse (BadRequest)
  }


  def listAllSkills = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def listUserSkills = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def updateUserSAkills = authorizedAction(parse.json, NormalUser) { user => implicit request =>
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

  def listJoiendProjects = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }

  def listElligibleProjects = authorizedAction(NormalUser) { user => implicit request =>
    ???
  }



}