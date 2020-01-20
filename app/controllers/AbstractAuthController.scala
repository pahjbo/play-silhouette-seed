package controllers

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import models.User
import play.api.mvc._
import utils.route.Calls

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Success

/**
 * `AbstractAuthController` base with support methods to authenticate an user.
 *
 * @param scc The Silhouette stack.
 * @param ex The execution context.
 */
abstract class AbstractAuthController(
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Performs user authentication
   *
   * @param rememberMe Remember me flag
   * @param request Initial request
   * @return The result to display.
   */
  protected def authenticateUser(loginInfo: LoginInfo, rememberMe: Boolean)(implicit request: RequestHeader): Future[AuthenticatorResult] = {
    val result = Redirect(Calls.home)
    authenticatorService.create(loginInfo).map {
      case authenticator if rememberMe =>
        authenticator.copy(
          expirationDateTime = clock.now + scc.rememberMeConfig.expiry,
          idleTimeout = scc.rememberMeConfig.idleTimeout,
          cookieMaxAge = scc.rememberMeConfig.cookieMaxAge
        )
      case authenticator => authenticator
    }.flatMap { authenticator =>
      authenticatorService.init(authenticator).flatMap { v =>
        authenticatorService.embed(v, result)
      }
    }.andThen {
      case Success(_) =>
        userService.retrieve(loginInfo).foreach { maybeUser =>
          maybeUser.foreach(user =>
            eventBus.publish(LoginEvent(user, request))
          )
        }
    }
  }
}
