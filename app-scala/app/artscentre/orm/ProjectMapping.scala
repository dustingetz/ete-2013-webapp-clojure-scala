package artscentre.orm

import java.sql.{ResultSet, Connection}
import artscentre.legacy.Util
import artscentre.models.{Skill, User, ProjectInfo, Project}

object ProjectMapping {

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







//  def createProject(connection: Connection, owner: Int, name: String): Option[Project] =
//  {
//    getProjectByName(connection, name) match
//    {
//      case None =>
//        prepareStatement(connection, "insert into "+qname("projects")+" (owner,name) values (?,?)",
//          Left(owner), Left(name))
//        { statement =>
//          if (1 != statement.executeUpdate)
//            sys.error("Cannot create project: no update.")
//          getProjectByName(connection, name)
//        }
//      case Some(p) =>
//        sys.error("Project '"+name+"' already exists.")
//    }
//  }
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
//  def addProjectSkill(connection: Connection, projectId: Int, skillId: Int)
//  {
//    prepareStatement(connection, "insert into "+qname("project_skills")+" (project_id, skill_id) values (?, ?)",
//      Left(projectId), Left(skillId))
//    { statement =>
//      if (1 != statement.executeUpdate)
//        sys.error("Duplicate skill values for (projectId="+projectId+", skillId="+skillId+")")
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
