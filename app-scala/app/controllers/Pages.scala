package controllers

import play.api._
import play.api.mvc._
import jp.t2v.lab.play2.auth.Auth
import java.io.File


object Pages extends Controller with Auth with AuthConfigImpl {

  // should be externalized to a setting; this constant is
  // also inlined in the routes file for global assets
  val magicExternalAssetDir = "../webapp/"

  /**
   * this snippet is derived from play 2.1 source code of: controllers.ExternalAssets.at,
   * because the play provided version doesn't work with play2auth. This is probably a valid
   * issue in play and should be reported.
   */
  def externalAsset(rootPath: String, file: String)(implicit request: Request[AnyContent]): Result = {
    import play.api.Play.current
    val fileToServe = new File(Play.application.getFile(magicExternalAssetDir + rootPath), file)

    if (fileToServe.exists) {
      Ok.sendFile(fileToServe, inline = true).withHeaders(CACHE_CONTROL -> "max-age=3600")
    } else {
      NotFound
    }
  }


  def artscentre = authorizedAction(NormalUser) { user => implicit request =>
    externalAsset("artscentre", "home.html")
  }

}
