package models.slick

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.{ OAuth1Info, OAuth2Info, OpenIDInfo }
import models.daos.{ AuthTokenDAOImpl, UserDAOImpl }
import models.{ AuthTokenDAO, UserDAO }
import net.codingwell.scalaguice.ScalaModule

class SlickModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[UserDAO].to[UserDAOImpl]

    // Replace this with the bindings to your concrete DAOs
    bind[DelegableAuthInfoDAO[GoogleTotpInfo]].toInstance(new InMemoryAuthInfoDAO[GoogleTotpInfo])
    bind[DelegableAuthInfoDAO[PasswordInfo]].toInstance(new InMemoryAuthInfoDAO[PasswordInfo])
    bind[DelegableAuthInfoDAO[OAuth1Info]].toInstance(new InMemoryAuthInfoDAO[OAuth1Info])
    bind[DelegableAuthInfoDAO[OAuth2Info]].toInstance(new InMemoryAuthInfoDAO[OAuth2Info])
    bind[DelegableAuthInfoDAO[OpenIDInfo]].toInstance(new InMemoryAuthInfoDAO[OpenIDInfo])
  }
}
