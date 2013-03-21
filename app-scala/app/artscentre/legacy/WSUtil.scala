//package artscentre.legacy
//
///**
// * Handy methods for implementing Web Services.
// */
//object WSUtil
//{
//  private val mimeTypes = new javax.activation.MimetypesFileTypeMap
//  def mimeType (fileName : String) = mimeTypes getContentType fileName
//  /**
//   * Implementers of web services should wrap their calls in this to ensure a sensible response in the case of failure.
//   */
//  def call [T] (f : => Any) : Response =
//  {
//    // This allows callers to build their own responses if they care to.
//    try f match
//    {
//      case r: Response => r
//      // avoid building an entity from Unit.
//      case _: Unit => Response.ok.build
//      case u: Any => Response.ok.entity(u).build
//    }
//    catch
//      {
//        case e: WebApplicationException => throw e // pass it on; Jersey can handle it.
//        case e: Throwable =>
//          /log.error (e) ("AJAX: " + e.getMessage)
//        Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage).`type`(MediaType.TEXT_PLAIN).build
//      }
//  }
//  /**
//   * Given a sequence of objects produce a JSON array of the objects.
//   */
//  implicit def toArray (xs : Seq [Any]) : JSONArray = xs.foldLeft (new JSONArray)
//  { (array, x) => x match
//  {
//    case i : Int => array put i
//    case i : Long => array put i
//    case i : Double => array put i
//    case i : Boolean => array put i
//    case i : String => array put i
//    case i : JSONObject => array put i
//    case _ => sys.error ("Cannot put item of type " + x.getClass + " in array: " + array)
//  }
//  }
//  implicit def toArray2[T](xs: Seq[T])(f: T => JSONObject) =
//    xs.foldLeft(new JSONArray) { (array, x) => array put f(x) }
//  /**
//   * Converts json array to a sequence.
//   */
//  implicit def fromArray[T](a: JSONArray)(f: Any => T): Seq[T] =
//  {
//    val size = a.length
//    var seq = List[T]()
//    for (i <- 0 until size)
//    {
//      seq ::= f(a.get(i))
//    }
//    seq
//  }
//}
