import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "app-scala"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "jp.t2v" %% "play2.auth"      % "0.9",
    jdbc,
    anorm
  )

  playAssetsDirectories += file(baseDirectory + "../webapp")

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
