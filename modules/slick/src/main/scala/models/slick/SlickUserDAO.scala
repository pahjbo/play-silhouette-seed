package models.slick

import java.time.Instant
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{ Inject, Singleton }
import models.{ User, UserDAO }
import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class SlickUserDAO @Inject() (db: Database)(implicit ec: ExecutionContext) extends UserDAO with Tables {

  override val profile: JdbcProfile = _root_.slick.jdbc.H2Profile

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[UUID]) => Users.filter(_.userId === id))

  def find(id: UUID): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(UserRowToUser))
  }

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def find(loginInfo: LoginInfo): Future[Option[User]] = ???

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = ???

  def all: Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(UserRowToUser))
  }

  def update(user: User): Future[Int] = {
    db.run(queryById(user.userID).update(userToUserRow(user)))
  }

  def delete(id: UUID): Future[Int] = {
    db.run(queryById(id).delete)
  }
  //
  //  def create(user: User): Future[Int] = {
  //    db.run(User += userToUserRow(user))
  //  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUserRow(user: User): UsersRow = {
    UsersRow(user.userID, user.firstName, user.lastName, user.email, user.avatarURL)
  }

  private def UserRowToUser(row: UsersRow): User = {
    User(
      row.userId,
      row.firstName, row.lastName,
      Some(row.firstName + " " + row.lastName),
      row.email, row.avatarUrl, activated = row.activated)
  }

}
