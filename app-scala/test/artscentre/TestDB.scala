package artscentre

import org.scalatest._
import org.scalatest.FunSuite
import artscentre.loggers.log4j.Log

/**
 * Test the DB api.
 */
object TestDB
{
  import artscentre.web.ADB

  def main(args: Array[String])
  {
    TestMasterSuite.execute[TestDB]
  }
  val binky = UserDef("bongo", "rabbit", "bongo@rabbit.com", "baba", "ghanoush")
  val bongo = UserDef("binky", "rabbit", "binky@rabbit.com", "musto", "khiar")
  val skillz = Seq("Scala", "Haskell", "Groovy", "Clojure")
  /**
   * Takes a not necessarily clean DB and prepares it for testing.
   */
  def initDB(db : ADB)
  {
    cleanDB(db)
    db.connect
    { connection =>
      skillz.foreach
      { skillName =>
        println(skillName)
        // "insert into "+qname("users")+" (firstname, lastname, email, username, password) values (?,?,?,?,?)"
        db.prepareStatement(connection, "insert into "+db.qname("skills")+" (name) values (?)", Left(skillName))
        { s =>
          if (1 != s.executeUpdate()) sys.error("No update for skill.")
        }
      }
    }
  }
  /**
   * Cleans all the data out of a DB.
   */
  def cleanDB(db : ADB)
  {
    db.connect
    { connection =>
      db.prepareStatement(connection, "delete from "+db.qname("users"))(_.execute())
      db.prepareStatement(connection, "delete from "+db.qname("skills"))(_.execute())
    }
  }
  /**
   * Initializes the DB from the standard props file, prepares it for test.
   * @param f
   * @return
   */
  def withDB(f: ADB => Unit)
  {
    val db = ADB.createFromPropsResource("/db.test.props")
    initDB(db)
    try f(db)
    finally cleanDB(db)
  }
}
case class UserDef(firstName: String, lastName: String, email: String, username: String, password: String)
class TestDB extends FunSuite
{
  import artscentre.web.ADB
  import artscentre.loggers.log4j.Log
  import TestDB._

  val pattern = "%d{yyyy-MMM-dd/HH:mm:ss,SSS} %c%n%p: %m%n"
  Log.init(Log.INFO, pattern)
  ADB.squelch

  test("db")
  {
    withDB
    { db =>
      db.connect
      { connection =>
        val userDef = bongo
        db.createUser(connection, userDef.firstName, userDef.lastName, userDef.email, userDef.username, userDef.password)
        val uid = db.loginUser(connection, userDef.username, userDef.password) match
        {
          case None => fail("User not found.")
          case Some(u) =>
            expectResult(userDef.firstName) { u.firstname }
            expectResult(userDef.lastName) { u.lastname }
            expectResult(userDef.email) { u.email }
            db.getUserById(connection, u.id) match
            {
              case None => fail("User not found.")
              case Some(u) =>
                expectResult(userDef.username) { u.username }
            }
            db.getUserInfo(connection, u.id) match
            {
              case None => fail("User not found.")
              case Some(u) =>
                expectResult(userDef.firstName) { u.firstname }
                expectResult(userDef.lastName) { u.lastname }
                expectResult(userDef.email) { u.email }
            }
            db.getUserByUserName(connection, userDef.username) match
            {
              case None => fail("User not found.")
              case Some(x) =>
                expectResult(u.id) { x.id }
            }
            u.id
        }
        db.changePassword(connection, uid, "newpassword")
        db.loginUser(connection, userDef.username, "newpassword").getOrElse(fail("Change password failed."))

        val skills = db.getSkills(connection)
        expectResult(TestDB.skillz.size)(skills.size)
        skills.foreach(println(_))
      }
    }
  }
}
