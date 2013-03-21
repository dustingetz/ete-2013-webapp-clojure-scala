import play.api._

// http://scala.playframework.org/documentation/2.0.4/ScalaGlobal

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.debug("Play has started")

    if (app.mode != Mode.Test) {

    }
  }


  override def onStop(app: Application) {
    Logger.debug("Application shutdown...")
  }

}