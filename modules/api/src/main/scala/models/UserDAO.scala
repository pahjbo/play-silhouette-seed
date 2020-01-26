package models

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
//trait MyUserDAO {
//
//  def lookup(id: String): Future[Option[User]]
//
//  def all: Future[Seq[User]]
//
//  def update(user: User): Future[Int]
//
//  def delete(id: String): Future[Int]
//
//  def create(user: User): Future[Int]
//
//  def close(): Future[Unit]
//}

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