package artscentre.web.services


/**
 * Provides a validation layer on the API intended to be stacked with an implementation (a mock or a database).
 * This allows us to test our validations without a webservice up front.
 * NB this API returns JSON data only for error reporting and not for marshaling, which should be done in the WebService.
 * @param api The 'base' API with which all the calls are implemented.
 */
class APIValidated(api: API)
{

  def login(username: String, password: String): Option[UserInfo] = api.login(username,password)
  def whoami(userId: Int): Option[UserInfo] = api.whoami(userId)
  def register(firstName: String, lastName: String, email: String, username: String, password: String): Option[JSONObject] =
  {
    val errors = new JSONObject()
    def notBlank(s: String, f: String) { if (s == null || s.isEmpty) errors.append(f,"Cannot be blank.") }
    notBlank(firstName, "firstName")
    notBlank(lastName, "lastName")
    notBlank(email, "email")
    notBlank(username, "username")
    notBlank(password, "password")
    if (0 < errors.length())
    {
      log.info("BAD REGISTRATION "+errors.toString)
      Some(errors)
    }
    else
    {
      api.register(firstName,lastName,email,username,password)
      None
    }
  }


  def listAllSkills(): List[Skill] =
  {
    api.listAllSkills()
  }


  def listUserSkills(userId: Int): List[Skill] =
  {
    api.listUserSkills(userId)
  }


  def listUserSkillsPicker(userId: Int): List[(Skill,Boolean)] =
  {
    // fold the user's skills into a map which we'll use to decorate the full skills list with a boolean indicating whether it's currently selected.
    val us = api.listUserSkills(userId).foldLeft(Map[Int,Skill]())((m, s) => m + (s.id -> s))
    api.listAllSkills().foldLeft(List[(Skill,Boolean)]()) { (a, k) => (k, us contains k.id) :: a }
  }


  def updateUserSkills(userId: Int, skillIds: Seq[Int])
  {
    api.updateUserSkills(userId, skillIds)
  }


  def createProject(owner: Int, projectName: String, skills: Seq[Int]): Option[JSONObject] =
  {
    val errors = new JSONObject()
    if (projectName.isEmpty)
    {
      errors.append("projectName", "Project name cannot be blank.")
    }
    if (0 < errors.length)
      Some(errors)
    else
    {
      api.createProject(owner, projectName, skills)
      None
    }
  }


  def deleteProject(userId: Int, projectId: Int)
  {
    api.deleteProject(userId, projectId)
  }


  def projectAddMember(projectId: Int, memberId: Int)
  {
    api.projectAddMember(projectId, memberId)
  }


  def projectRemoveMember(projectId: Int, memberId: Int)
  {
    api.projectRemoveMember(projectId, memberId)
  }


  def listProjectsByOwner(ownerId: Int): List[ProjectInfo] =
  {
    api.listProjectsByOwner(ownerId)
  }


  def listProjectsByMember(memberId: Int): List[ProjectInfo] =
  {
    api.listProjectsByMember(memberId)
  }


  def listEligibleProjects(userId: Int): List[ProjectInfo] =
  {
    api.listEligibleProjects(userId)
  }
}

