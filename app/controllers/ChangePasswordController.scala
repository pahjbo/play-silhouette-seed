package controllers

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ChangePasswordForm
import javax.inject.Inject
import play.api.i18n.Messages
import utils.auth.WithProvider

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Change Password` controller.
 */
class ChangePasswordController @Inject() (
  scc: SilhouetteControllerComponents,
  changePassword: views.html.changePassword
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Views the `Change Password` page.
   *
   * @return The result to display.
   */
  def view = SecuredAction(WithProvider(CredentialsProvider.ID)) {
    implicit request =>
      Ok(changePassword(ChangePasswordForm.form, request.identity))
  }

  /**
   * Changes the password.
   *
   * @return The result to display.
   */
  def submit = SecuredAction(WithProvider(CredentialsProvider.ID)).async {
    implicit request =>
      ChangePasswordForm.form.bindFromRequest.fold(
        form => Future.successful(BadRequest(changePassword(form, request.identity))),
        password => {
          val (currentPassword, newPassword) = password
          val credentials = Credentials(request.identity.email.getOrElse(""), currentPassword)
          credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
            val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
              Redirect(routes.ChangePasswordController.view()).flashing("success" -> Messages("password.changed"))
            }
          }.recover {
            case _: ProviderException =>
              Redirect(routes.ChangePasswordController.view()).flashing("error" -> Messages("current.password.invalid"))
          }
        }
      )
  }
}
