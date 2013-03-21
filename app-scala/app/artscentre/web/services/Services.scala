package artscentre.web.services

import scala.language.implicitConversions


/**
 * The application's web services are provided their implementations via an instance of this.
 * This allows us to swap implementations.  The web application context listener will create the real one
 * and unit tests will provide their own.
 */
trait ArtsCentreServiceDirectory
{
  val api:APIValidated
}
/**
 * This manages the singleton instance of the service directory.
 */
object ArtsCentreServices
{
  private var _serviceDirectory = None : Option [ArtsCentreServiceDirectory]
  def serviceDirectory = _serviceDirectory getOrElse (sys error "Service directory not set.")
  def serviceDirectory_= (sd : ArtsCentreServiceDirectory)
  {
    if (_serviceDirectory.isDefined) sys error ("Service directory already set.")
    else _serviceDirectory = Some (sd)
  }
}
/**
 * Hosts all the web service providers in this package.
 */
class ArtsCentreServices extends PackagesResourceConfig (classOf [ArtsCentreServices].getPackage.getName)

object WSError
{
  private def barf(status: Response.Status, mime: String, entity: Any): Nothing =
  {
    throw new WebApplicationException(Response.status(status).entity(entity).`type`(mime.toString).build())
  }
  def unauthorized(message: String) = barf(Status.UNAUTHORIZED, MediaType.TEXT_PLAIN, message)
  def badRequest(message: Any) = barf(Status.BAD_REQUEST, MediaType.APPLICATION_JSON, message)
  def internalError(message: String) = barf(Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_PLAIN, message)
}
/**
 * Handy methods for implementing Web Services.
 */
object WSUtil
{
  private val mimeTypes = new javax.activation.MimetypesFileTypeMap
  def mimeType (fileName : String) = mimeTypes getContentType fileName
  /**
   * Implementers of web services should wrap their calls in this to ensure a sensible response in the case of failure.
   */
  def call [T] (f : => Any) : Response =
  {
    // This allows callers to build their own responses if they care to.
    try f match
    {
      case r: Response => r
      // avoid building an entity from Unit.
      case _: Unit => Response.ok.build
      case u: Any => Response.ok.entity(u).build
    }
    catch
      {
        case e: WebApplicationException => throw e // pass it on; Jersey can handle it.
        case e: Throwable =>
          /log.error (e) ("AJAX: " + e.getMessage)
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage).`type`(MediaType.TEXT_PLAIN).build
      }
  }
  /**
   * Given a sequence of objects produce a JSON array of the objects.
   */
  implicit def toArray (xs : Seq [Any]) : JSONArray = xs.foldLeft (new JSONArray)
  { (array, x) => x match
  {
    case i : Int => array put i
    case i : Long => array put i
    case i : Double => array put i
    case i : Boolean => array put i
    case i : String => array put i
    case i : JSONObject => array put i
    case _ => sys.error ("Cannot put item of type " + x.getClass + " in array: " + array)
  }
  }
  implicit def toArray2[T](xs: Seq[T])(f: T => JSONObject) =
    xs.foldLeft(new JSONArray) { (array, x) => array put f(x) }
  /**
   * Converts json array to a sequence.
   */
  implicit def fromArray[T](a: JSONArray)(f: Any => T): Seq[T] =
  {
    val size = a.length
    var seq = List[T]()
    for (i <- 0 until size)
    {
      seq ::= f(a.get(i))
    }
    seq
  }
}
