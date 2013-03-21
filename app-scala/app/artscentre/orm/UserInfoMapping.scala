package orm

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import artscentre.UserInfo


object UserInfoMapping {

  import play.api.Play.current

  private val directMapping =
    get[ID]("users.id") ~
    get[String]("users.username") ~
    get[String]("users.email") ~
    get[String]("users.firstname") ~
    get[String]("users.lastname") ~
    get[java.util.Date]("users.created") map(flatten)


  type ID = String

  def list(): Map[ID, UserInfo] = {
    DB.withConnection { implicit connection =>
      val raw: List[(ID, String, String, String, String, java.util.Date)] = SQL(
        """
          |SELECT users.id, users.username, users.email, users.firstname, users.lastname, users.created
          |FROM users
        """.stripMargin).as(directMapping *)
      raw.view.map { t => t._1 -> UserInfo(t._2, t._3, t._4, t._5, t._6) }.toMap
    }
  }

  def create(id: ID, obj: UserInfo) {
    DB.withTransaction { implicit connection =>
    val count = SQL("INSERT INTO users VALUES ({id}, {username}, {email}, {firstname}, {lastname}, {created})")
      .on('id -> id,
      'username -> obj.username,
      'email -> obj.email,
      'firstname -> obj.firstname,
      'lastname -> obj.lastname,
      'created -> obj.created)
      .executeUpdate()
      // assert count==1
    }
  }

  def read(id: ID): Option[UserInfo] = {
    DB.withConnection { implicit connection =>
      val raw: List[(ID, String, String, String, String, java.util.Date)] = SQL(
        """
          |SELECT users.id, users.username, users.email, users.firstname, users.lastname, users.created
          |FROM users
          |WHERE users.id = {id}
        """.stripMargin)
        .on('id -> id)
        .as(directMapping *)
      raw.view.map { t => UserInfo(t._2, t._3, t._4, t._5, t._6) }.headOption
    }
  }

  def update(id: ID, obj: UserInfo) {
    DB.withTransaction { implicit connection =>
    val count = SQL(
      """
        |UPDATE users
        |SET username = {username}, email = {email}, firstname = {firstname}, lastname = {lastname}, created = {created}
        |WHERE id = {id}
      """.stripMargin)
      .on('id -> id,
      'username -> obj.username,
      'email -> obj.email,
      'firstname -> obj.firstname,
      'lastname -> obj.lastname,
      'created -> obj.created)
      .executeUpdate()
      // assert count == 1
    }
  }

  def delete(id: ID) {
    DB.withTransaction { implicit connection =>
    val count = SQL("DELETE FROM users WHERE id = {id}")
      .on('id -> id)
      .executeUpdate()
      //assert count == 1
    }
  }

}