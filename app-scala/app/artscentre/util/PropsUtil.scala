package artscentre.util

/**
 * Utilities for loading properties files.
 */
object PropsUtil
{
  import java.io.{File,InputStream,FileInputStream}
  import java.util.Properties

  /**
   * Read properties from a stream.
   * @param istream The input stream.
   * @param createFromProps A function mapping the properties to an instance of T.
   * @tparam T The type of the object created from the properties.
   * @return An instance of T.
   */
  def fromStream [T] (istream : InputStream) (createFromProps : Properties => T) : T =
  {
    val props = new java.util.Properties
    props load istream
    createFromProps (props)
  }
  /**
   * Read properties from a resource path.
   * @param resourcePath
   * @param createFromProps A function mapping the properties to an instance of T.
   * @tparam T The type of the object created from the properties.
   * @return An instance of T.
   */
  def fromResource [T] (resourcePath : String) (createFromProps : Properties => T) : T =
  {
    val s = getClass getResourceAsStream resourcePath
    if (null == s) sys error ("Cannot load properties resource: " + resourcePath)
    try fromStream (s) (createFromProps)
    finally s.close
  }
  /**
   * Read properties from a file.
   * @param propsFile The properties file.
   * @param createFromProps A function mapping the properties to an instance of T.
   * @tparam T The type of the object created from the properties.
   * @return An instance of T.
   */
  def fromFile [T] (propsFile : File) (createFromProps : Properties => T) : T =
  {
    val s = new FileInputStream(propsFile)
    try fromStream(s)(createFromProps)
    catch
      {
        case e : Throwable => throw new Exception("Failed to load properties from file: " + propsFile, e)
      }
    finally s.close
  }
}