package artscentre.web

/**
 * Some simple and general utilities.
 */
object Util
{
  // This makes explicit the use of the structural typing.
  import scala.language.reflectiveCalls

  def fail (s : String) : Nothing =
  {
    cerr println s
    System exit 1
    sys error "This is never thrown; it is here for static type checking purposes."
  }

  def closer[T, C <: {def close () : Unit}] (c: C)(f: C => T): T =
    try f (c)
    finally c.close

  def wrap [T] (e : Throwable => Throwable)(f : => T) : T =
    try f
    catch
      {
        case x : Throwable => throw e (x)
      }
  def decorate [T](m: => String)(f: => T): T =
    try f
    catch
      {
        case x : Throwable => throw new Exception (m, x)
      }
  val cout = System.out
  val cerr = System.err
}
