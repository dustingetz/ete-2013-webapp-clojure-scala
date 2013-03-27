package artscentre.orm.anorm

import anorm.SqlParser._
import anorm._
import artscentre.models.{Project, ProjectInfo, User, Skill}
import util.debug._
import anorm.~
import artscentre.models.User
import artscentre.models.Project
import artscentre.models.Skill
import artscentre.models.ProjectInfo
import scala.util.Try


object ProjectMapping {

  private val mapRawTuple =
    get[String]("projects.id") ~
    get[String]("projects.owner") ~
    get[String]("projects.name") ~
    get[java.util.Date]("projects.created") map(flatten)

  private val mapModel =
    get[String]("projects.owner") ~
    get[String]("projects.name") ~
    get[java.util.Date]("projects.created") map {
      case owner~name~created =>
        Project(name, owner, created)
    }

  private val mapProjectWithId =
    get[String]("projects.id") ~
    get[String]("projects.owner") ~
    get[String]("projects.name") ~
    get[java.util.Date]("projects.created") map {
      case id~owner~name~created =>
        id -> Project(name, owner, created)
    }


  def createProject(dbconn: java.sql.Connection, id: String, owner: String, name: String): Try[Project] = Try {

    val count = SQL("insert into projects (id, owner, name) values ({id}, {owner}, {name})")
      .on('id -> id, 'owner -> owner, 'name -> name)
      .executeUpdate()(dbconn)

    verify(count == 1, "insert expected 1 row, got: %s".format(count))

    // have to read back because created time is set in database
    val p: Try[Option[Project]] = getProjectByName(dbconn, name)
    p.get.get
  }

  def deleteProject(dbconn: java.sql.Connection, id: String): Try[Unit] = Try {

//    "DELETE FROM project_skills WHERE project_id = {projectId}"
//    "DELETE FROM project_members WHERE project_id = {projectId}"
//    "DELETE FROM projects WHERE id = {projectId}"

    val count = SQL("DELETE FROM projects WHERE id = {projectId} CASCADE CONSTRAINTS")
      .on('projectId -> id)
      .executeUpdate()(dbconn)

    verify(count == 1, "delete expected 1 row, got: %s".format(count))

  }


  def getProjectByName(dbconn: java.sql.Connection, projectName: String): Try[Option[Project]] = Try {

    SQL(
      """
        |SELECT id, name, owner, created FROM projects WHERE name = {projectName}
      """.stripMargin)
      .on('projectName -> projectName)
      .as(mapModel *)(dbconn).headOption

  }

  def read(dbconn: java.sql.Connection, id: String): Try[Option[Project]] = Try {

    SQL(
      """
        |SELECT id, name, owner, created FROM projects WHERE id = {id}
      """.stripMargin)
      .on('id -> id)
      .as(mapModel *)(dbconn).headOption

  }


  def addProjectSkill(dbconn: java.sql.Connection, projectId: String, skillId: String): Try[Unit] = Try {

    val count = SQL("insert into project_skills (project_id, skill_id) values ({projectId}, {skillId})")
      .on('projectId -> projectId, 'skillId -> skillId)
      .executeUpdate()(dbconn)

    verify(count == 1, "insert expected 1 row, got: %s".format(count))

  }

  private def queryMembers(dbconn: java.sql.Connection, projectId: String): Try[Set[User]] = Try {

    val mapUser =
      get[String]("u.id") ~
      get[String]("u.username") map {
        case id~username => User(username)
      }

    SQL(
      """
        |SELECT u.id, u.username FROM users u
        |INNER JOIN project_members m ON u.id = m.user_id
        |WHERE m.project_id = {projectId}
      """.stripMargin)
      .on('projectId -> projectId)
      .as(mapUser *)(dbconn)
      .view.toSet

  }

  def querySkills(dbconn: java.sql.Connection, projectId: String): Try[Set[Skill]] = Try {

    val mapSkill =
      get[String]("s.id") ~
      get[String]("s.name") map {
        case id~name => Skill(name)
      }

    SQL(
      """
        |SELECT s.id, s.name FROM skills s
        |INNER JOIN project_skills k ON s.id = k.skill_id
        |INNER JOIN projects p ON p.id = k.project_id
        |WHERE p.id = {projectId}
      """.stripMargin)
      .on('projectId -> projectId)
      .as(mapSkill *)(dbconn)
      .view.toSet

  }


  /**
   * note that this generates up to 2N+1 queries
   */
  def all(dbconn: java.sql.Connection): Try[Map[String, ProjectInfo]] = Try {

    val projects: Map[String, Project] =
      SQL("SELECT id, name, owner, created FROM projects")
        .as(mapProjectWithId *)(dbconn).toMap

    projects.map { case (projectId, project) =>

      val owner = User(project.owner)
      lazy val members: Set[User] = queryMembers(dbconn, projectId).get   // can throw outside a try if lazy - how to do this?
      lazy val skills: Set[Skill] = querySkills(dbconn, projectId).get

      projectId -> ProjectInfo(project.name, owner, project.created, members, skills)
    }.toMap

  }

  def forOwner(dbconn: java.sql.Connection, ownerId: String): Try[Map[String, ProjectInfo]] = Try {

    val projects: Map[String, Project] = SQL(
      """
        |SELECT id, name, owner, created
        |FROM projects
        |WHERE p.owner = {ownerId}
      """.stripMargin)
        .on('ownerId -> ownerId)
        .as(mapProjectWithId *)(dbconn).toMap

    projects.map { case (projectId, project) =>

      val owner = User(project.owner)
      lazy val members: Set[User] = queryMembers(dbconn, projectId).get   // can throw outside a try if lazy - how to do this?
      lazy val skills: Set[Skill] = querySkills(dbconn, projectId).get

      projectId -> ProjectInfo(project.name, owner, project.created, members, skills)
    }.toMap

  }

  def forMember(dbconn: java.sql.Connection, memberId: String): Try[Map[String, ProjectInfo]] = Try {

    val projects: Map[String, Project] = SQL(
      """
        |SELECT projects.id, projects.name, projects.owner, projects.created
        |FROM projects
        |INNER JOIN project_members ON projects.id = project_members.project_id
        |INNER JOIN users ON project_members.user_id = users.id
        |WHERE users.id = {memberId}
      """.stripMargin)
      .on('memberId -> memberId)
      .as(mapProjectWithId *)(dbconn).toMap

    projects.map { case (projectId, project) =>

      val owner = User(project.owner)
      lazy val members: Set[User] = queryMembers(dbconn, projectId).get   // can throw outside a try if lazy - how to do this?
      lazy val skills: Set[Skill] = querySkills(dbconn, projectId).get

      projectId -> ProjectInfo(project.name, owner, project.created, members, skills)
    }.toMap

  }


  /**
   * projects on which you are not a member but have matching skill requirements.
   */
  def elligibleForUser(dbconn: java.sql.Connection, userId: String): Try[Map[String, ProjectInfo]] = Try {

    val projects: Map[String, Project] = SQL(
      """
        |SELECT projects.id, projects.name, projects.owner, projects.created
        |FROM projects
        |INNER JOIN project_skills ON projects.id = project_skills.project_id
        |INNER JOIN
        |    (SELECT projects.id AS pid FROM projects WHERE pid NOT IN
        |        (SELECT p1.id FROM projects p1
        |         INNER JOIN project_members m ON p1.id = m.project_id
        |         INNER JOIN users u ON m.user_id = u.id WHERE u.id = {userId}))
        |ON p.id = np.pid
      """.stripMargin)
      .on('userId -> userId)
      .as(mapProjectWithId *)(dbconn).toMap

    projects.map { case (projectId, project) =>

      val owner = User(project.owner)
      lazy val members: Set[User] = queryMembers(dbconn, projectId).get   // can throw outside a try if lazy - how to do this?
      lazy val skills: Set[Skill] = querySkills(dbconn, projectId).get

      projectId -> ProjectInfo(project.name, owner, project.created, members, skills)
    }.toMap

  }

//  def addProjectMember(connection: Connection, projectId: Int, userId: Int)
//  {
//    prepareStatement(connection, "insert into "+qname("project_members")+" (project_id, user_id) values (?,?)",
//      Left(projectId), Left(userId))
//    { statement =>
//      if (1 != statement.executeUpdate)
//        sys.error("Cannot add user to project: no update.")
//    }
//  }
//  def removeProjectMember(connection: Connection, projectId: Int, userId: Int)
//  {
//    prepareStatement(connection, "delete from "+qname("project_members")+" where project_id = ? and user_id = ?",
//      Left(projectId), Left(userId))
//    { statement =>
//      if (1 != statement.executeUpdate)
//        sys.error("Wrong delete from project, project_id = "+projectId+", user_id = "+userId)
//    }
//  }
//  def removeProjectSkill(connection: Connection, projectId: Int, skillId: Int)
//  {
//    prepareStatement(connection, "delete from "+qname("project_skills")+" where project_id = ? and skill_id = ?",
//      Left(projectId), Left(skillId))
//    { statement =>
//      if (1 != statement.executeUpdate)
//        sys.error("Wrong delete from project, project_id = "+projectId+", skill_id = "+skillId)
//    }
//  }
}
