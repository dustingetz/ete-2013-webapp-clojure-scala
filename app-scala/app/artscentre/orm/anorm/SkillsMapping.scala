package artscentre.orm.anorm

import anorm.SqlParser._
import anorm._
import artscentre.models.Skill
import scala.util.Try


object SkillsMapping {

  val mappingWithId =
    get[String]("skills.id") ~
    get[String]("skills.name") ~
    get[String]("skills.description") map {
      case id~name~desc => id -> Skill(name, desc)
    }

  val mapping =
    get[String]("skills.name") ~
    get[String]("skills.description") map {
      case name~desc => Skill(name, desc)
    }

  def all(dbconn: java.sql.Connection): Try[Map[String, Skill]] = Try {

    SQL("SELECT skills.id, skills.name FROM skills")
      .as(mappingWithId *)(dbconn)
      .toMap

  }

  def create(dbconn: java.sql.Connection, id: String, obj: Skill): Try[Unit] = Try {
    val count = SQL("INSERT INTO skills VALUES ({id}, {name})")
      .on('id -> id,
          'name -> obj.name)
      .executeUpdate()(dbconn)
      // assert count==1
  }


  def read(dbconn: java.sql.Connection, id: String): Try[Option[Skill]] = Try {
    SQL("SELECT skills.name, skills.description FROM skills WHERE skills.id = {id}")
      .on('id -> id)
      .as(mapping *)(dbconn)
      .headOption
  }

  def update(dbconn: java.sql.Connection, id: String, obj: Skill): Try[Unit] = Try {
    val count = SQL("UPDATE skills SET name = {name} WHERE id = {id}")
      .on('id -> id,
          'name -> obj.name)
      .executeUpdate()(dbconn)
      // assert count == 1
  }

  def delete(dbconn: java.sql.Connection, id: String): Try[Unit] = Try {
    val count = SQL("DELETE FROM users WHERE id = {id}")
      .on('id -> id)
      .executeUpdate()(dbconn)
      //assert count == 1
  }

  def forUser(dbconn: java.sql.Connection, userId: String): Try[Map[String, Skill]] = Try {
    SQL(
      """
        |SELECT skills.id, skills.name, skills.description FROM skills
        |INNER JOIN skillsets
        |ON skills.id = skillsets.skill_id AND skillsets.user_id = {userId}
      """.stripMargin)
      .on('userId -> userId)
      .as(mappingWithId *)(dbconn)
      .toMap
  }
}
