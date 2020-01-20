package models.daos.slick

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import play.api.Logging
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.jdbc.JdbcProfile
import scala.language.implicitConversions

import scala.concurrent.{ ExecutionContext, Future }

class SlickUserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends models.daos.UserDAO
  with Logging with HasDatabaseConfigProvider[JdbcProfile] with UserDef with UserProfileDef {
  import profile.api._

  override def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val usersQuery = profileTable
      .filter(p => p.providerID === loginInfo.providerID && p.providerKey === loginInfo.providerKey)
      .flatMap(_.user)
    db.run(usersQuery.result.headOption).map(_.map(fromRow))
  }

  /**
   * Returns the user and login info matching providerId if there is a matching loginInfo found.
   *
   * @param userID the user id.
   * @return the loginInfos belonging to this user.
   */
  override def findLoginInfo(userID: UUID, providerId: String): Future[Option[(User, LoginInfo)]] = {
    lazy val query = for {
      profile <- profileTable if (profile.providerID === providerId) && (profile.userId === userID.toString)
      user <- profile.user
    } yield (profile, user)
    db.run(query.result.headOption).map { maybeTuple =>
      maybeTuple.map {
        case (profileRow, userRow) =>
          (userRow, LoginInfo(profileRow.providerID, profileRow.providerKey))
      }
    }
  }

  override def findInfos(userID: UUID): Future[Seq[LoginInfo]] = {
    lazy val loginInfoFromUserIdQuery = for {
      profile <- profileTable if profile.userId === userID.toString
    } yield profile
    db.run(loginInfoFromUserIdQuery.result).map(_.map(p => LoginInfo(p.providerID, p.providerKey)))
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  override def find(userID: UUID): Future[Option[User]] = db.run {
    userTable.filter(f => f.id === userID.toString()).result.headOption.map(_.map(fromRow))
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = {
    db.run(userTable += user).map(_ => user)
  }

  private implicit def toRow(user: User): UserRow = {
    UserRow(user.userID.toString, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL, user.activated)
  }

  private implicit def fromRow(row: UserRow): User = {
    User(UUID.fromString(row.id), row.firstName, row.lastName, row.fullName, row.email, row.avatarURL, row.activated)
  }

}
