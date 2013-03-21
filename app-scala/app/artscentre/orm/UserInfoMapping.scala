package artscentre.orm

import anorm.SqlParser._
import play.api.db.DB
import anorm._
import artscentre.models.{User, UserInfo}
import java.sql.{Connection, ResultSet}


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
          SELECT users.id, users.username, users.email, users.firstname, users.lastname, users.created
          FROM users
        """).as(directMapping *)
      raw.view.map { t => t._1 -> UserInfo(t._2, t._3, t._4, t._5, t._6) }.toMap
    }
  }

  def create(id: ID, obj: UserInfo) {
    DB.withTransaction { implicit connection =>
    val count = SQL("INSERT INTO users VALUES ({id}, {username}, {email}, {firstname}, {lastname}, {created})")
      .on('id -> id,
      'username -> obj.username,
      'email -> obj.email,
      'firstname -> obj.firstName,
      'lastname -> obj.lastName,
      'created -> obj.created)
      .executeUpdate()
      // assert count==1
    }
  }

  def read(dbconn: java.sql.Connection, id: ID): Option[UserInfo] = {

    val raw: List[(ID, String, String, String, String, java.util.Date)] = SQL(
      """
        SELECT users.id, users.username, users.email, users.firstname, users.lastname, users.created
        FROM users
        WHERE users.id = {id}
      """)
      .on('id -> id)
      .as(directMapping *)(dbconn)
    raw.view.map { t => UserInfo(t._2, t._3, t._4, t._5, t._6) }.headOption

  }

  def update(id: ID, obj: UserInfo) {
    DB.withTransaction { implicit connection =>
    val count = SQL(
      """
        UPDATE users
        SET username = {username}, email = {email}, firstname = {firstname}, lastname = {lastname}, created = {created}
        WHERE id = {id}
      """)
      .on('id -> id,
      'username -> obj.username,
      'email -> obj.email,
      'firstname -> obj.firstName,
      'lastname -> obj.lastName,
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






//  private def extractUser(rs: ResultSet): User = new User(rs getInt 1, rs getString 2)
//
//  def createUser(connection: Connection, firstName: String, lastName: String, email: String, username: String, password: String)
//  {
//    prepareStatement(connection,"insert into "+qname("users")+" (firstname, lastname, email, username, password) values (?, ?, ?, ?, ?)",
//      Left(firstName),Left(lastName),Left(email),Left(username),Left(password))
//    { statement =>
//      if (1 != statement.executeUpdate) sys.error("Insert of new user failed to produce expected update.")
//    }
//  }
//  def loginUser(connection: Connection, username: String, password: String): Option[UserInfo] =
//  {
//    prepareStatement(connection,"select id, password from "+qname("users")+" where username=?",
//      Left(username))
//    { statement =>
//      val r = statement.executeQuery
//      if (! r.next) None
//      else
//      {
//        if (password != (r getString 2))
//          sys.error("Invalid username or password.")
//        else
//        {
//          val id = r getInt 1
//          if (r.next) sys.error("Duplicate records.")
//          getUserInfo(connection, id)
//        }
//      }
//    }
//  }
//  def getUserByUserName(connection: Connection, username: String): Option[User] =
//  {
//    prepareStatement(connection,"select id from "+qname("users")+" where username=?",
//      Left(username))
//    { statement =>
//      withUniqueRecord(statement.executeQuery, "Duplicate value for username: "+username)
//      { r =>
//        new User(r getInt 1, username)
//      }
//    }
//  }
//  def getUserById(connection: Connection, id: Int): Option[User] =
//  {
//    prepareStatement(connection,"select username from "+qname("users")+" where id=?",
//      Left(id))
//    { statement =>
//      withUniqueRecord(statement.executeQuery, "Duplicate value for user id: "+id)
//      { r =>
//        new User(id, r getString 1)
//      }
//    }
//  }
//  def getUserInfo(connection: Connection, id: Int): Option[UserInfo] =
//  {
//    prepareStatement(connection, "select firstname, lastname, email, username, created from "+qname("users")+" where id=?",
//      Left(id))
//    { statement =>
//      withUniqueRecord(statement.executeQuery, "Duplicate value for user id: "+id)
//      { r =>
//        new UserInfo(id, r getString 1, r getString 2, r getString 3, r getString 4, (r getTimestamp 5).getTime)
//      }
//    }
//  }
//  // nixed because the cookie now contains the id, not the name.
//  // there seems to be and shouldn't be //	def getUserInfo(connection: Connection, username: String): Option[UserInfo] =
//  //	{
//  //		prepareStatement(connection, "select id, firstname, lastname, email, created from "+qname("users")+" where username=?",
//  //			Left(username))
//  //		{ statement =>
//  //			withUniqueRecord(statement.executeQuery, "Duplicate value for username: "+username)
//  //			{ r =>
//  //				new UserInfo(r getInt 1, r getString 2, r getString 3, r getString 4, username, (r getTimestamp 5).getTime)
//  //			}
//  //		}
//  //	}
//  def deleteUser(connection: Connection, id: Int)
//  {
//    prepareStatement(connection, "delete from "+qname("users")+" where id=?",
//      Left(id))
//    { statement =>
//      if (1 != statement.executeUpdate) sys.error("User "+id+" not deleted.")
//    }
//  }
//  def changePassword(connection: Connection, userId: Int, password: String)
//  {
//    prepareStatement(connection, "update "+qname("users")+" set password = ? where id = ?",
//      Left(password), Left(userId))
//    { statement =>
//      if (1 != statement.executeUpdate) sys.error("Change password failed.")
//    }
//  }




}