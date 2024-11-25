/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import pl.jwizard.jwc.audio.client.DistributedAudioClientBean
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * An IoC component that aggregates various environment-related beans used in the command processing system.
 *
 * @property environment Access to environment-related properties and configurations.
 * @property i18n Manages internationalization settings for localized messages.
 * @property jdaColorStore Handles color settings for JDA interactions.
 * @property eventQueue Manages event queue for asynchronous processing of JDA events.
 * @property musicManagers Supplies music manager instances for voice channel management.
 * @property audioClient Supplies distributed audio client instance for audio streaming.
 * @property jdaShardManager Manages multiple shards of the JDA bot, responsible for handling Discord API interactions.
 * @property guildSettingsEventAction Handles guild settings change events.
 * @property exceptionTrackerHandler The store used to track and log exceptions.
 * @property looselyTransportHandler Handles loosely-typed transport operations between services.
 * @author Miłosz Gilga
 */
@SingletonComponent
class CommandEnvironmentBean(
	val environment: EnvironmentBean,
	val i18n: I18nBean,
	val jdaColorStore: JdaColorStoreBean,
	val eventQueue: EventQueueBean,
	val musicManagers: MusicManagersBean,
	val audioClient: DistributedAudioClientBean,
	val jdaShardManager: JdaShardManagerBean,
	val guildSettingsEventAction: GuildSettingsEventAction,
	val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
)