/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

import org.springframework.beans.factory.DisposableBean
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import pl.jwizard.jwc.core.i18n.spi.I18nPropertyFilesSupplier
import pl.jwizard.jwc.core.util.logger
import java.nio.charset.Charset
import java.text.MessageFormat
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * A [ReloadableResourceBundleMessageSource] implementation that combines local and remote message sources. It
 * integrates with [I18nPropertyFilesSupplier] to fetch remote properties and uses caching to improve performance.
 *
 * @property i18nPropertyFilesSupplier The remote property supplier used to fetch remote property files.
 * @property languageTags A set of language tags supported by this message source.
 * @property charset The character set used for encoding messages.
 * @author Miłosz Gilga
 * @see CacheableRemotePropertiesLoader
 */
class CombinedMessageSource(
	private val i18nPropertyFilesSupplier: I18nPropertyFilesSupplier,
	private val languageTags: Set<String>,
	private val charset: Charset,
) : ReloadableResourceBundleMessageSource(), DisposableBean {

	companion object {
		private val log = logger<CombinedMessageSource>()
	}

	/**
	 * A loader instance responsible for caching and loading remote properties.
	 */
	private lateinit var loader: CacheableRemotePropertiesLoader

	init {
		defaultEncoding = charset.name()
	}

	/**
	 * Configures the remote base names for message properties. This method initializes the
	 * [CacheableRemotePropertiesLoader] with the provided base names and starts cache revalidation if the cacheMillis
	 * property is set.
	 *
	 * @param basenames The base names of the remote message bundles to be loaded.
	 */
	fun setRemoteBasenames(vararg basenames: String) {
		loader = CacheableRemotePropertiesLoader(i18nPropertyFilesSupplier, languageTags, basenames.toList(), charset)
		if (cacheMillis > 0) {
			val durationSec = cacheMillis.toDuration(DurationUnit.MILLISECONDS).inWholeSeconds
			loader.start(durationSec)
			log.info("Start revalidation i18n remote messages cache with interval: {}s.", durationSec)
		} else {
			log.info("I18n remote sources cache revalidation is disabled.")
		}
	}

	/**
	 * Resolves a message with the specified code and locale. It first attempts to retrieve the message from the remote
	 * properties. If not found, it falls back to the parent implementation.
	 *
	 * @param code The message key to resolve.
	 * @param locale The locale to use for resolving the message.
	 * @return A [MessageFormat] object for the resolved message, or `null` if the message could not be resolved.
	 */
	override fun resolveCode(code: String, locale: Locale): MessageFormat? {
		val properties = loader.remoteProperties[locale.language]
		if (properties != null) {
			val message = properties.getProperty(code) ?: return super.resolveCode(code, locale)
			return MessageFormat(message, locale)
		}
		return super.resolveCode(code, locale)
	}

	/**
	 * Resolves a message with the specified code and locale without arguments. It first attempts to retrieve the message
	 * from the remote properties. If not found, it falls back to the parent implementation.
	 *
	 * @param code The message key to resolve.
	 * @param locale The locale to use for resolving the message.
	 * @return The resolved message string, or `null` if the message could not be resolved.
	 */
	override fun resolveCodeWithoutArguments(code: String, locale: Locale): String? {
		val properties = loader.remoteProperties[locale.language]
		if (properties != null) {
			return properties.getProperty(code) ?: super.resolveCodeWithoutArguments(code, locale)
		}
		return super.resolveCodeWithoutArguments(code, locale)
	}

	/**
	 * Cleans up resources by calling the `destroy` method on the [CacheableRemotePropertiesLoader] instance.
	 * This method is called automatically by Spring when the bean is disposed.
	 */
	override fun destroy() = loader.destroy()
}