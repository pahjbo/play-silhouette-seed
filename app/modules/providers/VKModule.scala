package modules.providers

import com.google.inject.Provides
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.impl.providers.{ OAuth2Settings, SocialStateHandler }
import com.mohiva.play.silhouette.impl.providers.oauth2.VKProvider
import play.api.Configuration
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader

class VKModule {

  /**
   * Provides the VK provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param socialStateHandler The social state handler implementation.
   * @param configuration The Play configuration.
   * @return The VK provider.
   */
  @Provides
  def provideVKProvider(
    httpLayer: HTTPLayer,
    socialStateHandler: SocialStateHandler,
    configuration: Configuration): VKProvider = {

    new VKProvider(httpLayer, socialStateHandler, configuration.underlying.as[OAuth2Settings]("silhouette.vk"))
  }

}
