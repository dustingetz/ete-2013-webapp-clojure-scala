package artscentre.web.services

import artscentre.web._
import artscentre.web.ArtsCentreWeb._
import scala._


//@Path("/")
class APIWS
{
  /**
   * Here's the magic where we hook up the implementation, e.g. 'mock' or 'real'.
   */
  private final val api = ArtsCentreServices.serviceDirectory.api

//  /**
//   * Lists all the skills available to be picked.
//   */
//  @GET
//  @Path("/list-all-skills")
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def listAllSkills(): Response = WSUtil.call
//  {
//    log.trace("list skills all")
//    api.listAllSkills().reverse.foldLeft(new JSONArray())
//    { (a,k) =>
//      val o = new JSONObject()
//      o.put("id", k.id)
//      o.put("name", k.name)
//      a put o
//    }
//  }

//  /**
//   * Lists a user's skills.
//   * This would be used for non-editing displays.
//   */
//  @GET
//  @Path("/list-user-skills")
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def listUserSkills(@QueryParam("userId") userId: Int): Response = WSUtil.call
//  {
//    log.trace("list skills user: "+userId)
//    api.listUserSkills(userId).foldLeft(new JSONArray())
//    { (a,k) =>
//      val o = new JSONObject()
//      o.put("id", k.id)
//      o.put("name", k.name)
//      a put o
//    }
//  }




//  /**
//   * Lists all the skills that can be picked along with a boolean indicating whether the user has picked each one.
//   * This is tied to the cookie because only the logged in user can edit skills (and only for that user).
//   */
//  @GET
//  @Path("/list-skills-user-picker")
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def listUserSkillsPicker(@CookieParam(COOKIE) cookie: String): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    log.trace("list skills user picker: "+userId)
//    api.listUserSkillsPicker(userId).foldLeft(new JSONArray())
//    { (a, k) =>
//      val o = new JSONObject()
//      o.put("id", k._1.id)
//      o.put("name", k._1.name)
//      o.put("enabled", k._2)
//      a put o
//    }
//  }





//  @POST
//  @Path("/update-user-skills")
//  @Consumes(Array(MediaType.APPLICATION_JSON))
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def updateUserSkills(@CookieParam(COOKIE) cookie: String, ids: JSONArray): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    log.trace("update user skills: "+userId+" "+ids)
//    api.updateUserSkills(userId, WSUtil.fromArray(ids)(x => x.asInstanceOf[Int]))
//  }


//  @POST
//  @Path("/create-project")
//  @Consumes(Array(MediaType.APPLICATION_JSON))
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def createProject(@CookieParam(COOKIE) cookie: String, data: JSONObject): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    val (name, skills) = try
//    {
//      val projectName = data.get("name").asInstanceOf[String]
//      val skillsArray = data.get("skills").asInstanceOf[JSONArray]
//      val skills = WSUtil.fromArray(skillsArray)(_.asInstanceOf[Int])
//      (projectName, skills)
//    }
//    catch
//      {
//        case e: Throwable => throw new Exception ("Cannot process JSONObject " + data.toString, e)
//      }
//    log.trace("create project: "+name)
//    api.createProject(userId, name, skills) match
//    {
//      case None => Response.ok.build
//      case Some(e) => WSError.badRequest(e)
//    }
//  }
//
//
//
//
//  @GET
//  @Path("/project-add-member")
//  def projectAddMember(@CookieParam(COOKIE) cookie: String, @QueryParam("projectId") projectId: Int): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    log.trace("project: add member: "+projectId+" <- "+userId)
//    api.projectAddMember(projectId, userId)
//  }
//
//
//
//  @GET
//  @Path("/project-remove-member")
//  def projectRemoveMember(@CookieParam(COOKIE) cookie: String, @QueryParam("projectId") projectId: Int): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    log.trace("project: add member: "+projectId+" <- "+userId)
//    api.projectRemoveMember(projectId, userId)
//  }
//
//
//
//  @GET
//  @Path("/delete-project")
//  def deleteProject(@CookieParam(COOKIE) cookie: String, @QueryParam("projectId") projectId: Int): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    log.trace("project: add member: "+projectId+" <- "+userId)
//    api.deleteProject(userId, projectId)
//  }
//
//
//
//  @GET
//  @Path("/list-owned-projects")
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def listOwnedProjects(@CookieParam(COOKIE) cookie: String): Response = WSUtil.call
//  {
//    val ownerId = fromCookie(cookie)
//    WSUtil.toArray2(api.listProjectsByOwner(ownerId))(projectToJSON)
//  }
//
//
//
//  @GET
//  @Path("/list-joined-projects")
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def listJoinedProjects(@CookieParam(COOKIE) cookie: String): Response = WSUtil.call
//  {
//    val memberId = fromCookie(cookie)
//    WSUtil.toArray2(api.listProjectsByMember(memberId))(projectToJSON)
//  }
//
//
//
//  @GET
//  @Path("/list-eligible-projects")
//  @Produces(Array(MediaType.APPLICATION_JSON))
//  def listEligibleProjects(@CookieParam(COOKIE) cookie: String): Response = WSUtil.call
//  {
//    val userId = fromCookie(cookie)
//    WSUtil.toArray2(api.listEligibleProjects(userId))(projectToJSON)
//  }


//  private def userToJSON(user: User): JSONObject =
//  {
//    val o = new JSONObject()
//    o.put("id", user.id)
//    o.put("name", user.username)
//    o
//  }
//
//
//
//  private def skillToJSON(skill: Skill): JSONObject =
//  {
//    val o = new JSONObject()
//    o.put("id", skill.id)
//    o.put("name", skill.name)
//    o
//  }
//
//
//
//  private def projectToJSON(project: ProjectInfo): JSONObject =
//  {
//    val o = new JSONObject()
//    o.put("id", project.id)
//    o.put("name", project.name)
//    o.put("owner", userToJSON(project.owner))
//    o.put("created", project.created)
//    o.put("members", WSUtil.toArray2(project.members)(userToJSON))
//    o.put("skills", WSUtil.toArray2(project.skills)(skillToJSON))
//  }
}







