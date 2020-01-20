package models.daos.slick

import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.{ ForeignKeyQuery, PrimaryKey, ProvenShape }
import scala.language.implicitConversions

trait UserDef { self: HasDatabaseConfig[JdbcProfile] =>
  import profile.api._

  case class UserRow(
    id: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String],
    activated: Boolean)

  class Users(tag: Tag) extends Table[UserRow](tag, "USER") {
    def id = column[String]("USER_ID", O.PrimaryKey)
    def firstName = column[Option[String]]("FIRST_NAME")
    def lastName = column[Option[String]]("LAST_NAME")
    def fullName = column[Option[String]]("FULL_NAME")
    def email = column[Option[String]]("EMAIL")
    def avatarURL = column[Option[String]]("AVATAR_URL")
    def activated = column[Boolean]("ACTIVATED", O.Default(false))

    def * : ProvenShape[UserRow] = (id, firstName, lastName, fullName, email, avatarURL, activated) <> (UserRow.tupled, UserRow.unapply)
  }

  lazy val userTable = TableQuery[Users]
}

trait UserProfileDef extends UserDef { self: HasDatabaseConfig[JdbcProfile] =>
  import profile.api._

  case class UserProfileRow(userId: String, providerID: String, providerKey: String)

  class UserProfiles(tag: Tag) extends Table[UserProfileRow](tag, "USER_PROFILE") {
    def userId = column[String]("USER_ID")
    def providerID = column[String]("PROVIDER_ID")
    def providerKey = column[String]("PROVIDER_KEY")

    // Define a primary key
    def pk: PrimaryKey = primaryKey("profile_pk_id", (providerID, providerKey))

    // Define foreign key back to the user
    def user: ForeignKeyQuery[Users, UserRow] = foreignKey("profile_user_id_fk", userId, userTable)(_.id)

    def * : ProvenShape[UserProfileRow] = (userId, providerID, providerKey) <> (UserProfileRow.tupled, UserProfileRow.unapply)
  }

  lazy val profileTable = TableQuery[UserProfiles]
}

trait PasswordDef extends UserProfileDef { self: HasDatabaseConfig[JdbcProfile] =>
  import profile.api._

  case class Password(key: String, hasher: String, hash: String, salt: Option[String])

  class Passwords(tag: Tag) extends Table[Password](tag, "PASSWORD") {
    def key = column[String]("PROVIDER_KEY", O.PrimaryKey)
    def hasher = column[String]("HASHER")
    def hash = column[String]("HASH")
    def salt = column[Option[String]]("SALT")

    def profile = foreignKey("password_provider_key_fk", key, profileTable)(_.providerID)

    def * = (key, hasher, hash, salt) <> (Password.tupled, Password.unapply)
  }

  lazy val passwordTable = TableQuery[Passwords]
}
