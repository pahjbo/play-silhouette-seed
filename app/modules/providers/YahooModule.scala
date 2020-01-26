package modules.providers

import com.google.inject.Provides
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.impl.providers.OpenIDSettings
import com.mohiva.play.silhouette.impl.providers.openid.YahooProvider
import com.mohiva.play.silhouette.impl.providers.openid.services.PlayOpenIDService
import play.api.Configuration
import play.api.libs.openid.OpenIdClient
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader

class YahooModule {

  /**
   * Provides the Yahoo provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param client The OpenID client implementation.
   * @param configuration The Play configuration.
   * @return The Yahoo provider.
   */
  @Provides
  def provideYahooProvider(
    httpLayer: HTTPLayer,
    client: OpenIdClient,
    configuration: Configuration): YahooProvider = {

    val settings = configuration.underlying.as[OpenIDSettings]("silhouette.yahoo")
    new YahooProvider(httpLayer, new PlayOpenIDService(client, settings), settings)
  }
}
