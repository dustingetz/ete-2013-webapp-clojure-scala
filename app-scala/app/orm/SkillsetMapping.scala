package orm

import play.api.db.DB
import anorm._
import models.UserInfo


object SkillsetMapping {

  import play.api.Play.current


  def updateUserSkills(userId: String, skillIds: List[String]) {
    DB.withTransaction { implicit connection =>

      // delete all, then add back what we have.

      val count = SQL("DELETE FROM skillsets WHERE user_id = {userId}").on('userId -> userId).executeUpdate()
      //assert count == 1

      val createSkill: String => Unit = SkillsetMapping.create(userId)
      skillIds.foreach(createSkill)

    }
  }



  def create(userId: String)(skillId: String) {
    DB.withTransaction { implicit connection =>
      val count = SQL("INSERT INTO skillsets (user_id, skill_id) VALUES ({user_id}, {skill_id})")
        .on('user_id -> userId, 'skill_id -> skillId)
        .executeUpdate()
      // assert count==1
    }
  }


}
