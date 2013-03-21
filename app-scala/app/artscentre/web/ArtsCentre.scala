package artscentre.web

import javax.servlet.ServletContextListener
import artscentre.web.services.{ArtsCentreServiceDirectory, ArtsCentreServices}
import artscentre.web.ADB

object ArtsCentreWeb
{
  private def log = Log(ArtsCentreWeb)
  final val db = ADB createFromPropsResource "/db.props"
}




class ArtsCentreWeb extends ServletContextListener
{
  import ArtsCentreWeb._
  import javax.servlet.ServletContextEvent

  def contextInitialized(event: ServletContextEvent)
  {
    import artscentre.web.services.ArtsCentreServiceDirectory
    Log.init(Log.TRACE, "%d{yyyy-MMM-dd/HH:mm:ss,SSS} %c%n%p: %m%n")
    ADB.squelch();
    log info ("Context Initialized: "+event.toString)
    // Here, we hookup the services we'll need.
    // Webservice classes pull their implementations from here.


    ArtsCentreServices.serviceDirectory = new ArtsCentreServiceDirectory
    {
      import artscentre.web.services.{APIValidated, APIDB}
      // Annotate with interface types to ensure that we adhere to the abstract form.
      final val api: APIValidated = new APIValidated(new APIDB)
    }


  }



  def contextDestroyed(event: ServletContextEvent)
  {
    log.fatal("Context Destroyed: "+event.toString)
    db.close
  }
}

