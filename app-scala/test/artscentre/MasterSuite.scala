package artscentre

import org.scalatest._
import org.scalatest.Suite
import org.scalatest.Suites
import artscentre.TestDB

object TestMasterSuite
{
  def main(args: Array[String])
  {
    execute [TestMasterSuite]
  }
  def execute [T <: Suite] (implicit suite : scala.reflect.ClassTag [T])
  {
    suite.runtimeClass.newInstance().asInstanceOf[Suite].execute(stats = true)
    //		tools.Runner.main (Array ("-s", suite.runtimeClass.getCanonicalName))
  }
}

class TestMasterSuite extends Suites(
  new TestDB())
