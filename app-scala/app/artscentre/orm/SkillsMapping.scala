package artscentre.orm

import anorm.SqlParser._
import anorm._
import artscentre.models.Skill


object SkillsMapping {



  type ID = String

  private val directMapping =
    get[ID]("skills.id") ~
    get[String]("skills.name") map(flatten)

  def all(dbconn: java.sql.Connection): Map[ID, Skill] = {
    val raw: List[(ID, String)] = SQL("SELECT skills.id, skills.name FROM skills").as(directMapping *)(dbconn)
    raw.view.map { t => t._1 -> Skill(t._2) }.toMap
  }

  def create(dbconn: java.sql.Connection, id: ID, obj: Skill) {
    val count = SQL("INSERT INTO skills VALUES ({id}, {name})")
      .on('id -> id,
          'name -> obj.name)
      .executeUpdate()(dbconn)
      // assert count==1
  }

  def read(dbconn: java.sql.Connection, id: ID): Option[Skill] = {
    val raw: List[(ID, String)] = SQL("SELECT skills.id, skills.name FROM skills WHERE users.id = {id}")
      .on('id -> id)
      .as(directMapping *)(dbconn)
    raw.view.map { t => Skill(t._2) }.headOption
  }

  def update(dbconn: java.sql.Connection, id: ID, obj: Skill) {
    val count = SQL("UPDATE skills SET name = {name} WHERE id = {id}")
      .on('id -> id,
          'name -> obj.name)
      .executeUpdate()(dbconn)
      // assert count == 1
  }

  def delete(dbconn: java.sql.Connection, id: ID) {
    val count = SQL("DELETE FROM users WHERE id = {id}")
      .on('id -> id)
      .executeUpdate()(dbconn)
      //assert count == 1
  }

  def forUser(dbconn: java.sql.Connection, userId: ID): Map[ID, Skill] = {
    val raw: List[(ID, String)] = SQL(
      """
        |SELECT skills.id, skills.name FROM skills
        |INNER JOIN skillsets
        |ON skills.id = skillsets.skill_id AND skillsets.user_id = {userId}
      """.stripMargin)
      .on('userId -> userId)
      .as(directMapping *)(dbconn)
    raw.view.map { t => t._1 -> Skill(t._2) }.toMap
  }
}
