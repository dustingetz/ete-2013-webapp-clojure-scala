package artscentre

import artscentre.models._


object PersistenceAPI {

  type Connection = java.sql.Connection



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

