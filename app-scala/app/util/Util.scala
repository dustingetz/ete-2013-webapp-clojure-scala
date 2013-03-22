package util


object debug {

  def verify(expr: Boolean, msg: => String) {
    if (!expr) sys.error(msg)
  }
}
