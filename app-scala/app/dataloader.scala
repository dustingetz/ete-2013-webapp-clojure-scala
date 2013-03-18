import play.api.Play
import play.api.test.FakeApplication

object dataloader {

  val skills = List(
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
    "Director",
  )


  def main(args: Array[String]) {
    Play.start(FakeApplication())

    //skills.foreach(create)

    Play.stop()
  }
}
