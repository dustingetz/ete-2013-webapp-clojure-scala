package orm

import play.api.db.DB
import anorm._
import java.sql.Connection
import artscentre.models.Skill


object SkillsetMapping {

  def updateUserSkills(dbconn: java.sql.Connection, userId: String, skillIds: List[String]) {

      // delete all, then add back what we have.

      val count = SQL("DELETE FROM skillsets WHERE user_id = {userId}").on('userId -> userId).executeUpdate()
      //assert count == 1

      val createSkill: String => Unit = SkillsetMapping.create(userId)
      skillIds.foreach(createSkill)
  }



  def create(userId: String)(skillId: String) {
    DB.withTransaction { implicit connection =>
      val count = SQL("INSERT INTO skillsets (user_id, skill_id) VALUES ({user_id}, {skill_id})")
        .on('user_id -> userId, 'skill_id -> skillId)
        .executeUpdate()
      // assert count==1
    }
  }







//  def getSkills(connection: Connection): List[Skill] =
//  {
//    prepareStatement(connection, "select id, name from "+qname("skills")+" order by name asc")
//    { statement =>
//      getResults(statement.executeQuery) { rs => new Skill(rs getInt 1, rs getString 2) }
//    }
//  }
//  def getUserSkills(connection: Connection, userId: Int): List[Skill] =
//  {
//    prepareStatement(connection, "select s.id, s.name from "+qname("skills")+" s inner join "+qname("skillsets")+" us on s.id = us.skill_id and us.user_id = ? order by name asc",
//      Left(userId))
//    { statement =>
//      getResults (statement.executeQuery) { rs => new Skill(rs getInt 1, rs getString 2) }
//    }
//  }
//  def updateUserSkills(connection: Connection, userId: Int, skillIds: Seq[Int])
//  {
//    txn(connection)
//    {
//      prepareStatement(connection, "delete from "+qname("skillsets")+" where user_id = ?",
//        Left(userId))
//      { statement =>
//        statement.executeUpdate()
//      }
//      skillIds.foreach(addUserSkill(connection, userId, _))
//    }
//  }
//  def addUserSkill(connection: Connection, userId: Int, skillId: Int)
//  {
//    prepareStatement(connection, "insert into "+qname("skillsets")+" (user_id, skill_id) values (?, ?)",
//      Left(userId), Left(skillId))
//    { statement =>
//      if (1 != statement.executeUpdate)
//        sys.error("Failed to update users skill.")
//    }
//  }
//  def removeUserSkill(connection: Connection, userId: Int, skillId: Int)
//  {
//    prepareStatement(connection, "delete from "+qname("skillsets")+" where user_id = ? and skill_id = ?",
//      Left(userId), Left(skillId))
//    { statement =>
//      if (1 != statement.executeUpdate)
//        sys.error("Skill not deleted: user_id = "+userId+", skill_id="+skillId)
//    }
//  }


}
