package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.SocialProfile
import models.User

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Returns the login infos belonging to this user.
   *
   * @param userID the user id.
   * @return the loginInfos belonging to this user.
   */
  def findInfos(userID: UUID): Future[Seq[LoginInfo]]

  /**
   * Returns the user and login info matching providerId if there is a matching loginInfo found.
   *
   * @param userID the user id.
   * @return the loginInfos belonging to this user.
   */
  def findLoginInfo(userID: UUID, providerId: String): Future[Option[(User, LoginInfo)]]

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]
}
