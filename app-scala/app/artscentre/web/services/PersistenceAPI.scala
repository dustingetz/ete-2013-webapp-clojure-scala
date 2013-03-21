package artscentre.web.services


/**
 * Persistence API build on DB.
 */
class APIDB extends API
{
  import artscentre.web.ArtsCentreWeb.db
  import artscentre.web.{ProjectInfo,Project,Skill}
  import java.sql.Connection

  private val log = Log(this.getClass)
  private def logException [T](m: => String)(f: => T):T =
  {
    try f
    catch
      {
        case e:Throwable =>
          log.error(m, e)
          throw e
      }
  }
  override def login(username: String, password: String): Option[UserInfo] =
  {
    logException("login failed: "+username)
    {
      log.debug("login: " + username)
      db.connect { db.loginUser(_, username, password) }
    }
  }
  /**
   * Returns a JSON object indicating problems with the fields.
   */
  override def register(firstName: String, lastName: String, email: String, username: String, password: String)
  {
    logException("registration failed: "+firstName+", "+lastName+", "+email+", "+username)
    {
      log.debug ("register: " + username)
      db.connect { db.createUser(_, firstName, lastName, email, username, password); }
    }
  }
  override def whoami(userId: Int): Option[UserInfo] =
  {
    logException("whoami failed")
    {
      log.debug("whoami: "+userId)
      db.connect { db.getUserInfo(_, userId) }
    }
  }
  override def listAllSkills(): List[Skill] =
  {
    logException("list skills failed.")
    {
      log.debug("list skills")
      db.connect { db.getSkills(_) }
    }
  }
  override def listUserSkills(userId: Int): List[Skill] =
  {
    logException("list skills failed.")
    {
      log.debug("list skills")
      db.connect { db.getUserSkills(_, userId) }
    }
  }
  override def updateUserSkills(userId: Int, skillIds: Seq[Int])
  {
    logException("update user skills failed.")
    {
      log.debug("update user skills: "+userId+" "+skillIds)
      db.connect { db.updateUserSkills(_, userId, skillIds) }
    }
  }
  override def createProject(owner: Int, name: String, skills: Seq[Int])
  {
    logException("create project failed.")
    {
      log.debug("create project: "+owner+", "+name)
      db.connect
      { connection=> db.txn(connection)
      {
        if (db.getProjectByName(connection,name).isDefined)
          sys.error("Project '"+name+"' already exists.")
        db.createProject(connection, owner, name)
        val projectId = db.getProjectByName(connection, name).getOrElse(sys.error("Cannot find project named \""+name+"\".")).id
        skills.foreach(skillId => db.addProjectSkill(connection, projectId, skillId))
      }
      }
    }
  }
  private def checkProjectOwner(connection: Connection, userId: Int, projectId: Int)
  {
    val project = db.getProjectById(connection, projectId).getOrElse(sys.error("invalid project id: " + projectId))
    if (project.ownerId != userId)
      sys.error("Cannot access project '"+project.name+"' ["+projectId+"]: user ["+userId+"] is not the owner ["+project.ownerId+"].")
  }
  override def deleteProject(userId: Int, projectId: Int)
  {
    logException("project delete failed.")
    {
      log.debug("project: delete: "+projectId)
      db.connect
      { connection =>
        checkProjectOwner(connection, userId, projectId)
        db.deleteProject(connection, projectId)
      }
    }
  }
  override def projectAddMember(projectId: Int, memberId: Int)
  {
    logException("project add-member failed.")
    {
      log.debug("project: add-member: "+projectId+" <- "+memberId)
      db.connect
      { connection =>
        db.addProjectMember(connection, projectId, memberId)
      }
    }
  }
  override def projectRemoveMember(projectId: Int, memberId: Int)
  {
    logException("project remove-member failed.")
    {
      log.debug("project: remove-member: "+projectId+" <- "+memberId)
      db.connect
      { connection =>
        db.removeProjectMember(connection, projectId, memberId)
      }
    }
  }
  override def listProjects(): List[Project] =
  {
    logException("list projects")
    {
      log.debug("list projects")
      db.connect(db.listProjects(_))
    }
  }
  override def listProjectsByOwner(ownerId: Int): List[ProjectInfo] =
  {
    logException("list projects by owner")
    {
      log.debug("list projects by owner: "+ownerId)
      db.connect(db.listProjectsByOwner(_, ownerId))
    }
  }
  override def listProjectsByMember(memberId: Int): List[ProjectInfo] =
  {
    logException("list projects by member")
    {
      log.debug("list projects by member: "+memberId)
      db.connect(db.listProjectsByMember(_, memberId))
    }
  }
  override def listEligibleProjects(userId: Int): List[ProjectInfo] =
  {
    logException("list eligible projects: "+userId)
    {
      log.debug("list eligible projects by member: "+userId)
      db.connect(db.listEligibleProjects(_, userId))
    }
  }
}

