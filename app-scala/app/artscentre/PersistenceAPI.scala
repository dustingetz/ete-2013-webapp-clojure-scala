package artscentre

import artscentre.models._


object PersistenceAPI {

  type Connection = java.sql.Connection


  def createProject(owner: Int, name: String, skills: Seq[Int])
  {
//    log.debug("create project: "+owner+", "+name)
//    db.connect
//    { connection=> db.txn(connection)
//      {
//        if (db.getProjectByName(connection,name).isDefined)
//          sys.error("Project '"+name+"' already exists.")
//        db.createProject(connection, owner, name)
//        val projectId = db.getProjectByName(connection, name).getOrElse(sys.error("Cannot find project named \""+name+"\".")).id
//        skills.foreach(skillId => db.addProjectSkill(connection, projectId, skillId))
//      }
//    }
    ???
  }


//  private def checkProjectOwner(connection: Connection, userId: Int, projectId: Int)
//  {
////    val project = db.getProjectById(connection, projectId).getOrElse(sys.error("invalid project id: " + projectId))
////    if (project.ownerId != userId)
////      sys.error("Cannot access project '"+project.name+"' ["+projectId+"]: user ["+uaserId+"] is not the owner ["+project.ownerId+"].")
//    ???
//  }


  def deleteProject(userId: Int, projectId: Int)
  {
//    log.debug("project: delete: "+projectId)
//    db.connect
//    { connection =>
//      checkProjectOwner(connection, userId, projectId)
//      db.deleteProject(connection, projectId)
//    }
    ???
  }


  def projectAddMember(projectId: Int, memberId: Int)
  {
//    log.debug("project: add-member: "+projectId+" <- "+memberId)
//    db.connect
//    { connection =>
//      db.addProjectMember(connection, projectId, memberId)
//    }
    ???
  }


  def projectRemoveMember(projectId: Int, memberId: Int)
  {
//    log.debug("project: remove-member: "+projectId+" <- "+memberId)
//    db.connect
//    { connection =>
//      db.removeProjectMember(connection, projectId, memberId)
//    }
    ???
  }


  def listProjects(): List[Project] =
  {
//    log.debug("list projects")
//    db.connect(db.listProjects(_))
    ???
  }


  def listProjectsByOwner(ownerId: Int): List[ProjectInfo] =
  {
//    log.debug("list projects by owner: "+ownerId)
//    db.connect(db.listProjectsByOwner(_, ownerId))
    ???
  }


  def listProjectsByMember(memberId: Int): List[ProjectInfo] =
  {
//    log.debug("list projects by member: "+memberId)
//    db.connect(db.listProjectsByMember(_, memberId))
    ???
  }


  def listEligibleProjects(userId: Int): List[ProjectInfo] =
  {
//    log.debug("list eligible projects by member: "+userId)
//    db.connect(db.listEligibleProjects(_, userId))
    ???
  }
}

