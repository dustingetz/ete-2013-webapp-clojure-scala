package artscentre

import artscentre.models.Skill
import orm.anorm.SkillsMapping
import play.api.Play
import play.api.test.FakeApplication
import java.util.UUID.randomUUID
import play.api.db.DB


object dataloader {

  final val skills = List(
    "Web Designer",
    "Objective C Developer",
    "Android Developer",
    "Classical Guitar",
    "Jazz Guitar",
    "Classical Piano",
    "Jazz Piano",
    "Composer",
    "Conductor",
    "Photographer",
    "Graphic Designer",
    "Oboe",
    "Clarinet",
    "Trumpet",
    "Horn",
    "English Horn",
    "Viola",
    "Violin",
    "Cello",
    "Bass",
    "Video Editor",
    "Painter",
    "Sculptor",
    "Dancer",
    "Choreographer",
    "Producer",
    "Lighting Designer",
    "Director"
  )


  def main(args: Array[String])
  {
    implicit val app = FakeApplication()
    Play.start(app)
    try
    {
      DB.withTransaction
      { dbconn =>
        skills.foreach{ skillName => SkillsMapping.create(dbconn, randomUUID().toString, Skill(skillName, "dummy description for %s".format(skillName))) }
      }
    }
    finally Play.stop()
  }
}
