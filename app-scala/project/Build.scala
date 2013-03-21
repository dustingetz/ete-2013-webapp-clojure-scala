import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "app-scala"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "jp.t2v" %% "play2.auth" % "0.9"
    ,"postgresql" % "postgresql" % "9.1-901.jdbc4"
    ,jdbc
    ,anorm
    //,"joda-time" % "joda-time" % "2.1"
    //,"org.joda" % "joda-convert" % "1.2"
  )

  playAssetsDirectories += file(baseDirectory + "../webapp")

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
