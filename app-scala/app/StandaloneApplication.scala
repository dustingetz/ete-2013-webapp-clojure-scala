package play.api.test

import play.api.{Application, WithDefaultConfiguration, WithDefaultGlobal, WithDefaultPlugins}


// this is copy pasted from play.api.test.FakeApplication, because
// we don't have access to FakeApplication from the app package - the play-test.jar isn't on the classpath
// just call `Play.start(FakeApplication())` from a class with a java-style main entry point, like 'dataloader',
// and invoke from the play shell with: `run-main dataloader`
//
// https://github.com/playframework/Play20/blob/6a340a2624f8b1a7ddfff269b67f8cc30dbf1467/framework/src/play-test/src/main/scala/play/api/test/Fakes.scala#L187

case class FakeApplication(
    override val path: java.io.File = new java.io.File("."),
    override val classloader: ClassLoader = classOf[FakeApplication].getClassLoader,
    val additionalPlugins: Seq[String] = Nil,
    val withoutPlugins: Seq[String] = Nil,
    val additionalConfiguration: Map[String, _ <: Any] = Map.empty,
    val withGlobal: Option[play.api.GlobalSettings] = None) extends {
  override val sources = None
  override val mode = play.api.Mode.Test
} with Application with WithDefaultConfiguration with WithDefaultGlobal with WithDefaultPlugins {
  override def pluginClasses = {
    additionalPlugins ++ super.pluginClasses.diff(withoutPlugins)
  }

  override def configuration = {
    super.configuration ++ play.api.Configuration.from(additionalConfiguration)
  }

  override lazy val global = withGlobal.getOrElse(super.global)
}