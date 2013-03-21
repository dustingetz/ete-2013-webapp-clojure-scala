package artscentre.orm

import anorm.SqlParser._
import anorm._
import artscentre.models.{Project, ProjectInfo, User, Skill}


object ProjectMapping {

  private val directMapping =
    get[String]("projects.id") ~
    get[String]("projects.owner") ~
    get[String]("projects.name") ~
    get[java.util.Date]("projects.created") map(flatten)


  def createProject(dbconn: java.sql.Connection, id: String, owner: String, name: String): Option[Project] = {

    val count = SQL("insert into projects (id, owner, name) values ({id}, {owner}, {name})")
      .on('id -> id, 'owner -> owner, 'name -> name)
      .executeUpdate()(dbconn)
    // assert count == 1

    // have to read back because created time is set in database
    getProjectByName(dbconn, name)
  }

  def deleteProject(dbconn: java.sql.Connection, id: String) {

//    "DELETE FROM project_skills WHERE project_id = {projectId}"
//    "DELETE FROM project_members WHERE project_id = {projectId}"
//    "DELETE FROM projects WHERE id = {projectId}"

    val count = SQL("DELETE FROM projects WHERE id = {projectId} CASCADE CONSTRAINTS")
      .on('projectId -> id)
      .executeUpdate()(dbconn)
    // assert count == 1

  }


  def getProjectByName(dbconn: java.sql.Connection, projectName: String): Option[Project] = {

    val raw: List[(String, String, String, java.util.Date)] = SQL(
      """
        |SELECT id, name, owner, created FROM projects WHERE name = {projectName}
      """.stripMargin)
      .on('projectName -> projectName)
      .as(directMapping *)(dbconn)

    raw.view.map { t => Project(t._2, t._3, t._4) }.headOption
  }

  def read(dbconn: java.sql.Connection, id: String): Option[Project] = {

    val raw: List[(String, String, String, java.util.Date)] = SQL(
      """
        |SELECT id, name, owner, created FROM projects WHERE id = {id}
      """.stripMargin)
      .on('id -> id)
      .as(directMapping *)(dbconn)

    raw.view.map { t => Project(t._2, t._3, t._4) }.headOption
  }


  def addProjectSkill(dbconn: java.sql.Connection, projectId: String, skillId: String) {

    val count = SQL("insert into project_skills (project_id, skill_id) values ({projectId}, {skillId})")
      .on('projectId -> projectId, 'skillId -> skillId)
      .executeUpdate()(dbconn)
    // assert count == 1

  }


  /**
   * unused?
   */
  def all(dbconn: java.sql.Connection): List[ProjectInfo] = {

    val raw: List[(String, String, String, java.util.Date)] =
      SQL("SELECT id, name, owner, created FROM projects")
        .as(directMapping *)(dbconn)

    val members: List[User] = ???
    val skills: List[Skill] = ???
    val owner: User = User(???)

    raw.view.map { t => ProjectInfo(t._2, owner, t._4, members, skills) }.toList

  }

  def forOwner(dbconn: java.sql.Connection, ownerId: String): List[ProjectInfo] = {

    val raw: List[(String, String, String, java.util.Date)] =
      SQL(
        """
          |SELECT id, name, owner, created
          |FROM projects
          |WHERE p.owner = {ownerId}
        """.stripMargin)
        .on('ownerId -> ownerId)
        .as(directMapping *)(dbconn)

    val members: List[User] = ???
    val skills: List[Skill] = ???
    val owner: User = User(???)

    raw.view.map { t => ProjectInfo(t._2, owner, t._4, members, skills) }.toList

  }

  def forMember(dbconn: java.sql.Connection, memberId: String): List[ProjectInfo] = {

    val raw: List[(String, String, String, java.util.Date)] =
      SQL(
        """
          |SELECT projects.id, projects.name, projects.owner, projects.created
          |FROM projects
          |INNER JOIN project_members ON projects.id = project_members.project_id
          |INNER JOIN users ON project_members.user_id = users.id
          |WHERE users.id = {memberId}
        """.stripMargin)
        .on('memberId -> memberId)
        .as(directMapping *)(dbconn)

    val members: List[User] = ???
    val skills: List[Skill] = ???
    val owner: User = User(???)

    raw.view.map { t => ProjectInfo(t._2, owner, t._4, members, skills) }.toList

  }


  /**
   * projects on which you are not a member but have matching skill requirements.
   */
  def elligibleForUser(dbconn: java.sql.Connection, userId: String): List[ProjectInfo] = {

    val raw: List[(String, String, String, java.util.Date)] =
      SQL(
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
        .as(directMapping *)(dbconn)

    val members: List[User] = ???
    val skills: List[Skill] = ???
    val owner: User = User(???)

    raw.view.map { t => ProjectInfo(t._2, owner, t._4, members, skills) }.toList

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
//  def listProjectMembers(connection: Connection, projectId: Int): List[User] =
//  {
//    prepareStatement(connection, "select u.id, u.username from "+qname("users")+" u inner join "+qname("project_members")+" m on u.id = m.user_id where m.project_id = ?",
//      Left(projectId))
//    { statement =>
//      getResults(statement.executeQuery) (extractUser)
//    }
//  }
//  def listProjectSkills(connection: Connection, projectId: Int): List[Skill] =
//  {
//    prepareStatement(connection, "select s.id, s.name from "+qname("skills")+" s inner join "+qname("project_skills")+" k on s.id=k.skill_id inner join "+qname("projects")+" p on p.id = k.project_id where p.id=? order by name asc",
//      Left(projectId))
//    { statement =>
//      getResults(statement.executeQuery) { rs => new Skill(rs getInt 1, rs getString 2) }
//    }
//  }
}
