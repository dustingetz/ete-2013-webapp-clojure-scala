package artscentre.web.services

import artscentre.models._

/**
 * API for persistence that can be mocked.
 */
trait API {

  // login and register are no longer part of the artscentre api - they are external concerns.
  //def login(username: String, password: String): Option[UserInfo]
  //def register(firstName: String, lastName: String, email: String, username: String, password: String)

  def whoami(userId: Int): Option[UserInfo]
  def listAllSkills(): List[Skill]
  def listUserSkills(userId: Int): List[Skill]
  def updateUserSkills(userId: Int, skillIds: Seq[Int])
  def createProject(owner: Int, name: String, skills: Seq[Int])
  def deleteProject(userId: Int, projectId: Int)
  def projectAddMember(projectId: Int, memberId: Int)
  def projectRemoveMember(projectId: Int, memberId: Int)
  def listProjects(): List [Project]
  def listProjectsByOwner(ownerId: Int): List[ProjectInfo]
  def listProjectsByMember(memberId: Int): List[ProjectInfo]
  def listEligibleProjects(userId: Int): List[ProjectInfo]
}