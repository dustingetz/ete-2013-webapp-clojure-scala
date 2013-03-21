package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import jp.t2v.lab.play2.auth.Auth
import controllers.auth.{NormalUser, ServiceAuthConfig}
import orm.{SkillsetMapping, SkillsMapping}
import models.Skill


object Api extends Controller with Auth with ServiceAuthConfig {

  import models.Implicits._

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

}