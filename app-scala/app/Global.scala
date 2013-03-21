import artscentre.web.database.ADB
import javax.servlet.ServletContextEvent
import play.api._

// http://scala.playframework.org/documentation/2.0.4/ScalaGlobal

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.debug("Play has started")

    if (app.mode != Mode.Test) {

    }
  }

//  def contextInitialized(event: ServletContextEvent)
//  {
//    import artscentre.web.services.ArtsCentreServiceDirectory
//    Log.init(Log.TRACE, "%d{yyyy-MMM-dd/HH:mm:ss,SSS} %c%n%p: %m%n")
//    ADB.squelch();
//    log info ("Context Initialized: "+event.toString)
//    // Here, we hookup the services we'll need.
//    // Webservice classes pull their implementations from here.
//
//    ArtsCentreServices.serviceDirectory = new ArtsCentreServiceDirectory
//    {
//      import artscentre.web.services.{APIValidated, APIDB}
//      // Annotate with interface types to ensure that we adhere to the abstract form.
//      final val api: APIValidated = new APIValidated(new APIDB)
//    }
//  }

  override def onStop(app: Application) {
    Logger.debug("Application shutdown...")
  }

}