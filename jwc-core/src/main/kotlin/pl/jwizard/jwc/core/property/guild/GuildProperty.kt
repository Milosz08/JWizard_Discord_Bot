/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.guild

import pl.jwizard.jwc.core.i18n.source.I18nSystemSource
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.guild.GuildProperty.*
import pl.jwizard.jwc.core.property.guild.GuildPropertyConverter.*
import pl.jwizard.jwl.property.Property
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * Enum class representing configuration properties specific to guilds.
 *
 * Defining following properties:
 * - [DB_ID]: Represents a unique identifier for a database guild entry.
 * - [LANGUAGE_TAG]: Represents the language tag associated with guild.
 * - [VOTING_PERCENTAGE_RATIO]: Ratio of voting percentage for guilds.
 * - [MAX_VOTING_TIME_SEC]: Maximum voting time for guilds in seconds.
 * - [MUSIC_TEXT_CHANNEL_ID]: ID of the music text channel in the guild. Stored as a string.
 * - [DJ_ROLE_NAME]: Name of the DJ role in the guild.
 * - [MIN_REPEATS_OF_TRACK]: Minimum number of repeats allowed for a track in the guild.
 * - [MAX_REPEATS_OF_TRACK]: Maximum number of repeats allowed for a track in the guild.
 * - [LEAVE_EMPTY_CHANNEL_SEC]: Time in seconds after which the bot leaves an empty channel in the guild.
 * - [LEAVE_NO_TRACKS_SEC]: Time in seconds after which the bot leaves a channel with no tracks in the guild.
 * - [PLAYER_VOLUME]: Default volume level for the guild.
 * - [RANDOM_AUTO_CHOOSE_TRACK]: Indicates whether to randomly auto-choose tracks in the guild.
 * - [TIME_AFTER_AUTO_CHOOSE_SEC]: Time in seconds after which the bot automatically chooses a track in the guild.
 * - [MAX_TRACKS_TO_CHOOSE]: Maximum number of tracks to choose from in the guild.
 * - [LEGACY_PREFIX]: The legacy command prefix used by the bot in guilds.
 * - [SLASH_ENABLED]: Indicates whether slash commands are enabled for the guild.
 *
 * @property key The name of the database column that corresponds to this property.
 * @property i18nSourceKey The source key used for internationalization (i18n) of this property.
 * @property converter The converter used for convert property to more readable form via [GuildPropertyConverter].
 * @property nonDefaultType The type of the property value. By default, guild property refer to [BotProperty] as
 *           default value. If default value not exist, this field must not be null.
 * @author Miłosz Gilga
 * @see BotProperty
 */
enum class GuildProperty(
	override val key: String,
	val i18nSourceKey: I18nSystemSource? = null,
	val converter: GuildPropertyConverter? = null,
	val nonDefaultType: KClass<*>? = null,
) : Property {

	/**
	 * Represents a unique identifier for a database guild entry.
	 */
	DB_ID("id", null, null, BigInteger::class),

	/**
	 * Represents the language tag associated with guild.
	 */
	LANGUAGE_TAG("language", I18nSystemSource.LANGUAGE_TAG, BASE, String::class),

	/**
	 * Ratio of voting percentage for guilds.
	 */
	VOTING_PERCENTAGE_RATIO("voting_percentage_ratio", I18nSystemSource.VOTING_PERCENTAGE_RATIO, TO_PERCENTAGE),

	/**
	 * Maximum voting time for guilds in seconds.
	 */
	MAX_VOTING_TIME_SEC("time_to_finish_voting_sec", I18nSystemSource.VOTE_MAX_WAITING_TIME, TO_DTF_SEC),

	/**
	 * ID of the music text channel in the guild. Stored as a number.
	 */
	MUSIC_TEXT_CHANNEL_ID("music_text_channel_id", I18nSystemSource.MUSIC_TEXT_CHANNEL, BASE, Long::class),

	/**
	 * Name of the DJ role in the guild.
	 */
	DJ_ROLE_NAME("dj_role_name", I18nSystemSource.DJ_ROLE_NAME, BASE),

	/**
	 * Minimum number of repeats allowed for a track in the guild.
	 */
	MIN_REPEATS_OF_TRACK("min_repeats_of_track", I18nSystemSource.MIN_REPEATS_OF_TRACK, BASE),

	/**
	 * Maximum number of repeats allowed for a track in the guild.
	 */
	MAX_REPEATS_OF_TRACK("max_repeats_of_track", I18nSystemSource.MAX_REPEATS_OF_TRACK, BASE),

	/**
	 * Time in seconds after which the bot leaves an empty channel in the guild.
	 */
	LEAVE_EMPTY_CHANNEL_SEC("leave_empty_channel_sec", I18nSystemSource.LEAVE_EMPTY_CHANNEL_SEC, TO_DTF_SEC),

	/**
	 * Time in seconds after which the bot leaves a channel with no tracks in the guild.
	 */
	LEAVE_NO_TRACKS_SEC("leave_no_tracks_channel_sec", I18nSystemSource.LEAVE_NO_TRACKS_SEC, TO_DTF_SEC),

	/**
	 * Default volume level for the guild.
	 */
	PLAYER_VOLUME("player_volume", I18nSystemSource.PLAYER_VOLUME, TO_PERCENTAGE),

	/**
	 * Indicates whether to randomly auto-choose tracks in the guild.
	 */
	RANDOM_AUTO_CHOOSE_TRACK("random_auto_choose_track", I18nSystemSource.RANDOM_AUTO_CHOOSE_TRACK, TO_BOOL),

	/**
	 * Time in seconds after which the bot automatically chooses a track in the guild.
	 */
	TIME_AFTER_AUTO_CHOOSE_SEC("time_after_auto_choose_sec", I18nSystemSource.TIME_AFTER_AUTO_CHOOSE_SEC, TO_DTF_SEC),

	/**
	 * Maximum number of tracks to choose from in the guild.
	 */
	MAX_TRACKS_TO_CHOOSE("tracks_to_choose_max", I18nSystemSource.MAX_TRACKS_TO_CHOOSE, BASE),

	/**
	 * The legacy command prefix used by the bot in guilds.
	 */
	LEGACY_PREFIX("legacy_prefix", I18nSystemSource.LEGACY_PREFIX, BASE),

	/**
	 * Indicates whether slash commands are enabled for the guild.
	 */
	SLASH_ENABLED("slash_enabled", I18nSystemSource.SLASH_ENABLED, TO_BOOL),
	;
}