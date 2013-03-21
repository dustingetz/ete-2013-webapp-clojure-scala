package artscentre.orm

import anorm.SqlParser._
import anorm._
import artscentre.models.{UserInfo, Project}


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


  def getProjectByName(dbconn: java.sql.Connection, projectName: String): Option[Project] = {

    val raw: List[(String, String, String, java.util.Date)] = SQL(
      """
        |SELECT id, name, owner, created FROM projects WHERE name = {projectName}
      """.stripMargin)
      .on('projectName -> projectName)
      .as(directMapping *)(dbconn)

    raw.view.map { t => Project(t._2, t._3, t._4) }.headOption
  }


  def addProjectSkill(dbconn: java.sql.Connection, projectId: String, skillId: String) {

    val count = SQL("insert into project_skills (project_id, skill_id) values ({projectId}, {skillId})")
      .on('projectId -> projectId, 'skillId -> skillId)
      .executeUpdate()(dbconn)
    // assert count == 1

  }


  //  private def selectProjects(connection: Connection, by: String, params: Either[Any,Int]*) =
//  {
//    val sql = "select distinct p.id, p.name, p.owner, p.created from "+qname("projects")+" p"+(if (null == by || by.isEmpty) "" else (" "+by))+" order by name asc"
//    Util.decorate(sql)
//    {
//      prepareStatement(connection, sql, params: _*)
//      { statement =>
//        getResults(statement.executeQuery) (extractProject)
//      }
//    }
//  }
//
//  // create a project from the records set's fields according to the companion select.
//  private def extractProject(rs: ResultSet): Project = new Project(rs getInt 1, rs getString 2, rs getInt 3, (rs getTimestamp 4).getTime)






//  def deleteProject(connection: Connection, projectId: Int) = txn(connection)
//  {
//    prepareStatement(connection, "delete from "+qname("project_skills")+" where project_id = ?", Left(projectId)) { _.executeUpdate() }
//    prepareStatement(connection, "delete from "+qname("project_members")+" where project_id = ?", Left(projectId)) { _.executeUpdate() }
//    prepareStatement(connection, "delete from "+qname("projects")+" where id = ?", Left(projectId)) { _.executeUpdate() }
//  }
//  def listProjects(connection: Connection): List[Project] = selectProjects(connection, "")
//  /**
//   * Maps over a list of projects and uses the connection to fill in the rest of the project info.
//   */
//  private def toProjectInfo(connection: Connection, projects: List[Project]): List[ProjectInfo] = projects map
//    { project =>
//      new ProjectInfo(project.id, project.name,
//        getUserById(connection, project.ownerId).getOrElse(sys.error("No project owner! ["+project.ownerId+"]")),
//        project.created,
//        listProjectMembers(connection, project.id),
//        listProjectSkills(connection, project.id))
//    }
//  // projects you own
//  def listProjectsByOwner(connection: Connection, ownerId: Int): List[ProjectInfo] = toProjectInfo(connection, selectProjects(connection,
//    "where p.owner=?",
//    Left(ownerId)))
//  // projects on which you are participating
//  def listProjectsByMember(connection: Connection, memberId: Int): List[ProjectInfo] = toProjectInfo(connection, selectProjects(connection,
//    "inner join "+qname("project_members")+" m on p.id = m.project_id inner join "+qname("users")+" u on m.user_id = u.id where u.id=?",
//    Left(memberId)))
//  // projects on which you are not a member but have matching skill requirements.
//  def listEligibleProjects(connection: Connection, userId: Int): List[ProjectInfo] = toProjectInfo(connection, selectProjects(connection,
//    //		"left join "+qname("project_members")+" m on p.id = m.project_id and m.user_id = ? and user_id is null inner join "+qname("project_skills")+" ps on p.id = ps.project_id inner join "+qname("skillsets")+" s on s.user_id = ? and s.skill_id = ps.skill_id",
//    "inner join "+qname("project_skills")+" ps on p.id = ps.project_id "+
//      "inner join "+qname("skillsets")+" s on s.user_id = ? and s.skill_id = ps.skill_id "+
//      "inner join "+
//      "(select p.id as pid from "+qname("projects")+" p where p.id not in "+
//      "(select p1.id from "+qname("projects")+" p1 inner join "+qname("project_members")+" m on p1.id = m.project_id inner join "+qname("users")+" u on m.user_id = u.id where u.id=?)) np "+
//      "on p.id = np.pid",
//    Left(userId), Left(userId)))
//



//  def getProjectByName(connection: Connection, name: String): Option[Project] =
//  {
//    prepareStatement(connection, "select id, name, owner, created from "+qname("projects")+" where name = ?",
//      Left(name))
//    { statement =>
//      withUniqueRecord(statement.executeQuery, "Duplicate values for projectName: "+name) (extractProject)
//    }
//  }
//  def getProjectById(connection: Connection, id: Int): Option[Project] =
//  {
//    prepareStatement(connection, "select id, name, owner, created from "+qname("projects")+" where id = ?",
//      Left(id))
//    { statement =>
//      withUniqueRecord(statement.executeQuery, "Duplicate values for project id: "+id) (extractProject)
//    }
//  }




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
