package models


class Models {

}


class User(val id: Int, val username: String)

class UserInfo(val id: Int, val firstname: String, val lastname: String, val email: String, val username: String, val created: Long)

class Skill(val id: Int, val name: String)

class Project(val id: Int, val name: String, val ownerId: Int, val created: Long)

class ProjectInfo(val id: Int, val name: String, val owner: User, val created: Long, val members: List[User], val skills: List[Skill])