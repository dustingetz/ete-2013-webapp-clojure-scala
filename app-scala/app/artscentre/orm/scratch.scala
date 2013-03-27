package artscentre.orm

import anorm.SqlParser._
import anorm._
import anorm.~
import scala.util.Try


object a {

  case class ProjectInfo(name: String,
                         owner: String,
                         created: java.util.Date)

  def readAllProjects(dbconn: java.sql.Connection): Try[List[ProjectInfo]] = Try {

    val query = "SELECT name, owner, created FROM projects"

    val mapping =
      get[String]("projects.owner") ~
      get[String]("projects.name") ~
      get[java.util.Date]("projects.created") map {
        case owner~name~created =>
          ProjectInfo(name, owner, created)
      }

    SQL(query).as(mapping *)(dbconn)
  }

}

object b {

  case class Skill(name: String, description: String)

  def readAllSkills(dbconn: java.sql.Connection): Try[List[Skill]] = Try {

    val query = "SELECT name, description FROM skills"

    val mapping =
      get[String]("skills.name") ~
        get[String]("skills.description") map {
        case owner~name =>
          Skill(name, owner)
      }

    SQL(query).as(mapping *)(dbconn)

  }

}


object c {

  case class Skill(name: String, description: String)

  case class ProjectInfo(name: String,
                         owner: String,
                         created: java.util.Date,
                         skill: Skill)

  def readAllProjects(dbconn: java.sql.Connection): Try[List[ProjectInfo]] = Try {

    val query =
      """
        SELECT projects.name, projects.owner, projects.created,
               skills.name, skills.description
        FROM projects
        INNER JOIN skills ON projects.skillId = skills.id
      """

    val mapping =
      get[String]("projects.name") ~
      get[String]("projects.owner") ~
      get[java.util.Date]("projects.created") ~
      get[String]("skills.name") ~
      get[String]("skills.description") map {
        case owner~name~created~skillName~skillDesc =>
          val skill = Skill(skillName, skillDesc)
          ProjectInfo(name, owner, created, skill)
      }

    SQL(query).as(mapping *)(dbconn)
  }

}

object d {

  case class Skill(name: String, description: String)

  case class ProjectInfo(name: String,
                         owner: String,
                         created: java.util.Date,
                         skills: Set[Skill])



  def readSkillsForProject(dbconn: java.sql.Connection, projectId: String): Try[Set[Skill]] = Try {

    val query =
      """
        SELECT skills.name, skills.description FROM skills
        INNER JOIN project_skills ON skills.id = project_skills.skill_id
        INNER JOIN projects ON projects.id = project_skills.project_id
        WHERE projects.id = {projectId}
      """

    val mapping =
      get[String]("skills.name") ~
      get[String]("skills.description") map {
        case name~desc =>
          Skill(name, desc)
      }

    SQL(query).on('projectId -> projectId).as(mapping *)(dbconn).toSet
  }

  def readAllProjects(dbconn: java.sql.Connection): Try[Set[ProjectInfo]] = Try {

    val query = "SELECT projcets.id, projects.name, projects.owner, projects.created FROM projects"

    val mapping =
      get[String]("projects.id") ~
      get[String]("projects.name") ~
      get[String]("projects.owner") ~
      get[java.util.Date]("projects.created") map {
        case id~name~owner~created =>
          id -> (name, owner, created)
      }


    val projects: Map[String, (String, String, java.util.Date)] = SQL(query).as(mapping *)(dbconn).toMap

    projects.map { case (projectId, project) =>
      lazy val skills: Set[Skill] = readSkillsForProject(dbconn, projectId).get
      ProjectInfo(project._1, project._2, project._3, skills)
    }.toSet
  }

}

