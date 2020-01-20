package modules

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.{ GoogleTotpInfo, OAuth1Info, OAuth2Info, OpenIDInfo }
import com.mohiva.play.silhouette.persistence.daos.{ DelegableAuthInfoDAO, InMemoryAuthInfoDAO }
import models.daos.{ AuthTokenDAO, SlickAuthTokenDAO, UserDAO }
import models.daos.slick.SlickUserDAO
import net.codingwell.scalaguice.ScalaModule

class SlickModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AuthTokenDAO].to[SlickAuthTokenDAO]
    bind[UserDAO].to[SlickUserDAO]

    // Replace this with the bindings to your concrete DAOs
    bind[DelegableAuthInfoDAO[GoogleTotpInfo]].toInstance(new InMemoryAuthInfoDAO[GoogleTotpInfo])
    bind[DelegableAuthInfoDAO[PasswordInfo]].toInstance(new InMemoryAuthInfoDAO[PasswordInfo])
    bind[DelegableAuthInfoDAO[OAuth1Info]].toInstance(new InMemoryAuthInfoDAO[OAuth1Info])
    bind[DelegableAuthInfoDAO[OAuth2Info]].toInstance(new InMemoryAuthInfoDAO[OAuth2Info])
    bind[DelegableAuthInfoDAO[OpenIDInfo]].toInstance(new InMemoryAuthInfoDAO[OpenIDInfo])
  }
}
