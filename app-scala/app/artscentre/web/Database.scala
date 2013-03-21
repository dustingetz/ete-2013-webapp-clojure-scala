package artscentre.web

import com.mchange.v2.c3p0.ComboPooledDataSource
import artscentre.loggers.log4j.Log
import artscentre.legacy.Util

/**
 * Just a username and id.
 */
class User(val id: Int, val username: String)
/**
 * The full user info.
 */
class UserInfo(val id: Int, val firstname: String, val lastname: String, val email: String, val username: String, val created: Long)
class Skill(val id: Int, val name: String)
class Project(val id: Int, val name: String, val ownerId: Int, val created: Long)
class ProjectInfo(val id: Int, val name: String, val owner: User, val created: Long, val members: List[User], val skills: List[Skill])
/**
 * A simple wrapper for a postgres database. Contains a connection pool and loaners for connections and transactions.
 * TODO make connection pool configurable.
 * @param host host machine
 * @param port host port
 * @param ssl true for secure connection only
 * @param username username
 * @param password password
 * @param catalog catalog name (i.e. database)
 * @param schema schema name
 */
class DB(val host: String, val port: Int, val ssl: Boolean, val username: String, val password: String, val catalog: String, val schema: String)
{
  import java.sql.{ResultSet,Connection,PreparedStatement}

  //,Types => SQLTypes}
  import com.mchange.v2.c3p0.ComboPooledDataSource
  /**
   * NB It's much easier to use a connection pooler here than the JDBC DriverManager because you'll get nasty classpath issues.
   */
  private val dataSource = new ComboPooledDataSource
  dataSource setDriverClass classOf [org.postgresql.Driver].getCanonicalName
  dataSource setJdbcUrl "jdbc:postgresql://" + host + ":" + port + "/" + catalog
  dataSource setUser username
  dataSource setPassword password
  dataSource setMinPoolSize 5
  dataSource setAcquireIncrement 5
  dataSource setMaxPoolSize 20
  def close() { dataSource.close() }

  /**
   * Grants the use of a connection.
   */
  def connect[T](f: Connection => T): T =
  {
    val c = dataSource.getConnection
    try f (c)
    finally c.close()
  }
  /**
   * Manages a transaction and preserves whatever auto commit setting exists on the connection.
   */
  def txn[T](connection: Connection)(f: => T): T =
  {
    val ac = connection.getAutoCommit
    try
    {
      connection setAutoCommit false
      try
      {
        val r = f
        connection.commit()
        r
      }
      catch
        {
          case e:Throwable =>
            connection.rollback()
            throw e
        }
    }
    finally
    {
      connection setAutoCommit ac
    }
  }
  /**
   * Set a bunch of params on a statement.  A param is either a value in the Left position or, in the Right position, a java.sql.Types value for which a null will be set
   */
  def setParams(statement: PreparedStatement, params: Either[Any, Int] *)
  {
    var nth = 0
    def nextArg:Int = { nth += 1 ; nth }
    for (param <- params)
    {
      param match
      {
        case Right (x) => statement.setNull (nextArg, x)
        case Left (x) => x match
        {
          case x: Boolean => statement.setBoolean(nextArg, x)
          case x: Int => statement.setInt(nextArg, x)
          case x: Long => statement.setLong(nextArg, x)
          case x: Double => statement.setDouble(nextArg, x)
          case x: String => statement.setString(nextArg, x)
          case _ => sys error("Don't know how to set parameter "+nth+" of type "+x.getClass)
        }
      }
    }
  }
  def prepareStatement[T](connection: Connection, sql: String, params: Either[Any, Int] *) (f: PreparedStatement => T): T =
  {
    val statement = connection prepareStatement sql
    try
    {
      setParams(statement, params:_ *)
      f(statement)
    }
    finally statement.close()
  }
  /**
   * Qualifies a name with the schema
   */
  def qname(name: String) = schema+"."+name
  def getResults[T](rs: ResultSet)(f: ResultSet => T): List[T] =
  {
    var list = List[T]()
    while (rs.next)
    {
      list ::= f(rs)
    }
    list
  }
}
object ADB
{
  import artscentre.loggers.log4j.Log
  import java.util.Properties
  import java.io.{InputStream, File}
  import java.sql.ResultSet
  import artscentre.legacy.PropsUtil

  private final val log = Log(ADB)
  /**
   * Factory from properties.
   */
  def createFromProps (props: Properties):ADB =
  {
    def p (n:String) =
    {
      val v = props getProperty n
      if (null == v) sys.error("Missing required property: "+n)
      v
    }
    val host = p("host")
    val port = p("port").toInt
    val ssl = p("ssl").toBoolean
    val role = p("role")
    val password = p("password")
    val database = p("database")
    val schema = p("schema")
    new ADB(host, port, ssl, role, password, database, schema)
  }
  def createFromPropsResource (resourcePath: String):ADB = PropsUtil.fromResource(resourcePath)(createFromProps)
  def createFromPropsFile (propsFile: File): ADB = PropsUtil.fromFile(propsFile)(createFromProps)
  def createFromPropsStream (istream: InputStream): ADB = PropsUtil.fromStream (istream) (createFromProps)
  /**
   * Fails if there is more than one record.
   */
  def withUniqueRecord [T](r: ResultSet, e: =>String)(f: ResultSet => T): Option[T]=
  {
    if (! r.next) None
    else
    {
      val o = f(r)
      if (r.next) sys.error(e)
      Some(o)
    }
  }
  /**
   * Used to squelch noisy messages from the database subsystems.
   */
  def squelch()
  {
    Seq(
      "com.mchange.v2.resourcepool.BasicResourcePool",
      "com.mchange.v2.async.ThreadPoolAsynchronousRunner",
      "com.mchange.v2.c3p0.management.DynamicPooledDataSourceManagerMBean",
      "com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool",
      "com.mchange.v2.c3p0.impl.NewProxyPreparedStatement"
    ).foreach(Log.logger(_).setLevel(Log.INFO.level))
  }
}
class ADB(host: String, port: Int, ssl: Boolean, username: String, password: String, database: String, schema: String)
  extends DB(host, port, ssl, username, password, database, schema)
{
  import java.sql.{ResultSet,Connection}
  import ADB._
  log.info
  {
    // string * scalar multiplication!
    def f(n: String) = "\n"+(" " * (10 - n.size))+n+": "
    "database info"+f("host")+host+f("port")+port+f("database")+database+f("schema")+schema
  }

  // SQL HELPERS

  private def selectProjects(connection: Connection, by: String, params: Either[Any,Int]*) =
  {
    val sql = "select distinct p.id, p.name, p.owner, p.created from "+qname("projects")+" p"+(if (null == by || by.isEmpty) "" else (" "+by))+" order by name asc"
    log info sql
    Util.decorate(sql)
    {
      prepareStatement(connection, sql, params: _*)
      { statement =>
        getResults(statement.executeQuery) (extractProject)
      }
    }
  }
  // create a project from the records set's fields according to the companion select.
  private def extractProject(rs: ResultSet): Project = new Project(rs getInt 1, rs getString 2, rs getInt 3, (rs getTimestamp 4).getTime)
  // create a user from the record sets fields according to the companion select.
  private def extractUser(rs: ResultSet): User = new User(rs getInt 1, rs getString 2)

  def createUser(connection: Connection, firstName: String, lastName: String, email: String, username: String, password: String)
  {
    prepareStatement(connection,"insert into "+qname("users")+" (firstname, lastname, email, username, password) values (?, ?, ?, ?, ?)",
      Left(firstName),Left(lastName),Left(email),Left(username),Left(password))
    { statement =>
      if (1 != statement.executeUpdate) sys.error("Insert of new user failed to produce expected update.")
    }
  }
  def loginUser(connection: Connection, username: String, password: String): Option[UserInfo] =
  {
    prepareStatement(connection,"select id, password from "+qname("users")+" where username=?",
      Left(username))
    { statement =>
      val r = statement.executeQuery
      if (! r.next) None
      else
      {
        if (password != (r getString 2))
          sys.error("Invalid username or password.")
        else
        {
          val id = r getInt 1
          if (r.next) sys.error("Duplicate records.")
          getUserInfo(connection, id)
        }
      }
    }
  }
  def getUserByUserName(connection: Connection, username: String): Option[User] =
  {
    prepareStatement(connection,"select id from "+qname("users")+" where username=?",
      Left(username))
    { statement =>
      withUniqueRecord(statement.executeQuery, "Duplicate value for username: "+username)
      { r =>
        new User(r getInt 1, username)
      }
    }
  }
  def getUserById(connection: Connection, id: Int): Option[User] =
  {
    prepareStatement(connection,"select username from "+qname("users")+" where id=?",
      Left(id))
    { statement =>
      withUniqueRecord(statement.executeQuery, "Duplicate value for user id: "+id)
      { r =>
        new User(id, r getString 1)
      }
    }
  }
  def getUserInfo(connection: Connection, id: Int): Option[UserInfo] =
  {
    prepareStatement(connection, "select firstname, lastname, email, username, created from "+qname("users")+" where id=?",
      Left(id))
    { statement =>
      withUniqueRecord(statement.executeQuery, "Duplicate value for user id: "+id)
      { r =>
        new UserInfo(id, r getString 1, r getString 2, r getString 3, r getString 4, (r getTimestamp 5).getTime)
      }
    }
  }
  // nixed because the cookie now contains the id, not the name.
  // there seems to be and shouldn't be //	def getUserInfo(connection: Connection, username: String): Option[UserInfo] =
  //	{
  //		prepareStatement(connection, "select id, firstname, lastname, email, created from "+qname("users")+" where username=?",
  //			Left(username))
  //		{ statement =>
  //			withUniqueRecord(statement.executeQuery, "Duplicate value for username: "+username)
  //			{ r =>
  //				new UserInfo(r getInt 1, r getString 2, r getString 3, r getString 4, username, (r getTimestamp 5).getTime)
  //			}
  //		}
  //	}
  def deleteUser(connection: Connection, id: Int)
  {
    prepareStatement(connection, "delete from "+qname("users")+" where id=?",
      Left(id))
    { statement =>
      if (1 != statement.executeUpdate) sys.error("User "+id+" not deleted.")
    }
  }
  def changePassword(connection: Connection, userId: Int, password: String)
  {
    prepareStatement(connection, "update "+qname("users")+" set password = ? where id = ?",
      Left(password), Left(userId))
    { statement =>
      if (1 != statement.executeUpdate) sys.error("Change password failed.")
    }
  }
  def getSkills(connection: Connection): List[Skill] =
  {
    prepareStatement(connection, "select id, name from "+qname("skills")+" order by name asc")
    { statement =>
      getResults(statement.executeQuery) { rs => new Skill(rs getInt 1, rs getString 2) }
    }
  }
  def getUserSkills(connection: Connection, userId: Int): List[Skill] =
  {
    prepareStatement(connection, "select s.id, s.name from "+qname("skills")+" s inner join "+qname("skillsets")+" us on s.id = us.skill_id and us.user_id = ? order by name asc",
      Left(userId))
    { statement =>
      getResults (statement.executeQuery) { rs => new Skill(rs getInt 1, rs getString 2) }
    }
  }
  def updateUserSkills(connection: Connection, userId: Int, skillIds: Seq[Int])
  {
    txn(connection)
    {
      prepareStatement(connection, "delete from "+qname("skillsets")+" where user_id = ?",
        Left(userId))
      { statement =>
        statement.executeUpdate()
      }
      skillIds.foreach(addUserSkill(connection, userId, _))
    }
  }
  def addUserSkill(connection: Connection, userId: Int, skillId: Int)
  {
    prepareStatement(connection, "insert into "+qname("skillsets")+" (user_id, skill_id) values (?, ?)",
      Left(userId), Left(skillId))
    { statement =>
      if (1 != statement.executeUpdate)
        sys.error("Failed to update users skill.")
    }
  }
  def removeUserSkill(connection: Connection, userId: Int, skillId: Int)
  {
    prepareStatement(connection, "delete from "+qname("skillsets")+" where user_id = ? and skill_id = ?",
      Left(userId), Left(skillId))
    { statement =>
      if (1 != statement.executeUpdate)
        sys.error("Skill not deleted: user_id = "+userId+", skill_id="+skillId)
    }
  }
  def createProject(connection: Connection, owner: Int, name: String): Option[Project] =
  {
    getProjectByName(connection, name) match
    {
      case None =>
        prepareStatement(connection, "insert into "+qname("projects")+" (owner,name) values (?,?)",
          Left(owner), Left(name))
        { statement =>
          if (1 != statement.executeUpdate)
            sys.error("Cannot create project: no update.")
          getProjectByName(connection, name)
        }
      case Some(p) =>
        sys.error("Project '"+name+"' already exists.")
    }
  }
  def deleteProject(connection: Connection, projectId: Int) = txn(connection)
  {
    prepareStatement(connection, "delete from "+qname("project_skills")+" where project_id = ?", Left(projectId)) { _.executeUpdate() }
    prepareStatement(connection, "delete from "+qname("project_members")+" where project_id = ?", Left(projectId)) { _.executeUpdate() }
    prepareStatement(connection, "delete from "+qname("projects")+" where id = ?", Left(projectId)) { _.executeUpdate() }
  }
  def listProjects(connection: Connection): List[Project] = selectProjects(connection, "")
  /**
   * Maps over a list of projects and uses the connection to fill in the rest of the project info.
   */
  private def toProjectInfo(connection: Connection, projects: List[Project]): List[ProjectInfo] = projects map
    { project =>
      new ProjectInfo(project.id, project.name,
        getUserById(connection, project.ownerId).getOrElse(sys.error("No project owner! ["+project.ownerId+"]")),
        project.created,
        listProjectMembers(connection, project.id),
        listProjectSkills(connection, project.id))
    }
  // projects you own
  def listProjectsByOwner(connection: Connection, ownerId: Int): List[ProjectInfo] = toProjectInfo(connection, selectProjects(connection,
    "where p.owner=?",
    Left(ownerId)))
  // projects on which you are participating
  def listProjectsByMember(connection: Connection, memberId: Int): List[ProjectInfo] = toProjectInfo(connection, selectProjects(connection,
    "inner join "+qname("project_members")+" m on p.id = m.project_id inner join "+qname("users")+" u on m.user_id = u.id where u.id=?",
    Left(memberId)))
  // projects on which you are not a member but have matching skill requirements.
  def listEligibleProjects(connection: Connection, userId: Int): List[ProjectInfo] = toProjectInfo(connection, selectProjects(connection,
    //		"left join "+qname("project_members")+" m on p.id = m.project_id and m.user_id = ? and user_id is null inner join "+qname("project_skills")+" ps on p.id = ps.project_id inner join "+qname("skillsets")+" s on s.user_id = ? and s.skill_id = ps.skill_id",
    "inner join "+qname("project_skills")+" ps on p.id = ps.project_id "+
      "inner join "+qname("skillsets")+" s on s.user_id = ? and s.skill_id = ps.skill_id "+
      "inner join "+
      "(select p.id as pid from "+qname("projects")+" p where p.id not in "+
      "(select p1.id from "+qname("projects")+" p1 inner join "+qname("project_members")+" m on p1.id = m.project_id inner join "+qname("users")+" u on m.user_id = u.id where u.id=?)) np "+
      "on p.id = np.pid",
    Left(userId), Left(userId)))

  def getProjectByName(connection: Connection, name: String): Option[Project] =
  {
    prepareStatement(connection, "select id, name, owner, created from "+qname("projects")+" where name = ?",
      Left(name))
    { statement =>
      withUniqueRecord(statement.executeQuery, "Duplicate values for projectName: "+name) (extractProject)
    }
  }
  def getProjectById(connection: Connection, id: Int): Option[Project] =
  {
    prepareStatement(connection, "select id, name, owner, created from "+qname("projects")+" where id = ?",
      Left(id))
    { statement =>
      withUniqueRecord(statement.executeQuery, "Duplicate values for project id: "+id) (extractProject)
    }
  }
  def addProjectSkill(connection: Connection, projectId: Int, skillId: Int)
  {
    prepareStatement(connection, "insert into "+qname("project_skills")+" (project_id, skill_id) values (?, ?)",
      Left(projectId), Left(skillId))
    { statement =>
      if (1 != statement.executeUpdate)
        sys.error("Duplicate skill values for (projectId="+projectId+", skillId="+skillId+")")
    }
  }
  def addProjectMember(connection: Connection, projectId: Int, userId: Int)
  {
    prepareStatement(connection, "insert into "+qname("project_members")+" (project_id, user_id) values (?,?)",
      Left(projectId), Left(userId))
    { statement =>
      if (1 != statement.executeUpdate)
        sys.error("Cannot add user to project: no update.")
    }
  }
  def removeProjectMember(connection: Connection, projectId: Int, userId: Int)
  {
    prepareStatement(connection, "delete from "+qname("project_members")+" where project_id = ? and user_id = ?",
      Left(projectId), Left(userId))
    { statement =>
      if (1 != statement.executeUpdate)
        sys.error("Wrong delete from project, project_id = "+projectId+", user_id = "+userId)
    }
  }
  def removeProjectSkill(connection: Connection, projectId: Int, skillId: Int)
  {
    prepareStatement(connection, "delete from "+qname("project_skills")+" where project_id = ? and skill_id = ?",
      Left(projectId), Left(skillId))
    { statement =>
      if (1 != statement.executeUpdate)
        sys.error("Wrong delete from project, project_id = "+projectId+", skill_id = "+skillId)
    }
  }
  def listProjectMembers(connection: Connection, projectId: Int): List[User] =
  {
    prepareStatement(connection, "select u.id, u.username from "+qname("users")+" u inner join "+qname("project_members")+" m on u.id = m.user_id where m.project_id = ?",
      Left(projectId))
    { statement =>
      getResults(statement.executeQuery) (extractUser)
    }
  }
  def listProjectSkills(connection: Connection, projectId: Int): List[Skill] =
  {
    prepareStatement(connection, "select s.id, s.name from "+qname("skills")+" s inner join "+qname("project_skills")+" k on s.id=k.skill_id inner join "+qname("projects")+" p on p.id = k.project_id where p.id=? order by name asc",
      Left(projectId))
    { statement =>
      getResults(statement.executeQuery) { rs => new Skill(rs getInt 1, rs getString 2) }
    }
  }
}