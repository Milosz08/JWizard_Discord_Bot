/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.stereotype.RemotePropertySupplier
import kotlin.reflect.KClass

/**
 * Implementation of the [RemotePropertySupplier] interface that provides access to remote properties
 * from a database using JDBC.
 *
 * This class interacts with a database to fetch global properties and specific properties for a given guild.
 *
 * @property jdbcTemplateBean The bean responsible for JDBC operations.
 * @author Miłosz Gilga
 */
@Component
class RemotePropertySupplierBean(private val jdbcTemplateBean: JdbcTemplateBean) : RemotePropertySupplier {

	/**
	 * Retrieves all global properties from the database.
	 *
	 * This method executes a SQL query to fetch properties from the `global_configs` table.
	 * It maps the results into a mutable map where the key is the property key and the value is a
	 * pair consisting of the property value and its type.
	 *
	 * @return A mutable map of global properties where each entry consists of a key and a pair of
	 *         property value and type.
	 */
	override fun getGlobalProperties(): MutableMap<String, Pair<String, String>> {
		val sql = "SELECT prop_key, prop_value, type FROM global_configs"
		val properties = mutableMapOf<String, Pair<String, String>>()
		jdbcTemplateBean.query(sql) {
			properties[it.getString("prop_key")] = it.getString("prop_value") to it.getString("type")
		}
		return properties
	}

	/**
	 * Retrieves a specific property for a given guild from the database.
	 *
	 * This method constructs a SQL query to fetch a property value based on the provided column name
	 * and guild ID. The query is executed, and the result is cast to the specified type [T].
	 *
	 * @param T The type to which the property value should be cast.
	 * @param columnName The name of the column to fetch from the `guilds` table.
	 * @param guildId The ID of the guild for which the property is fetched.
	 * @param type The [KClass] representing the type to which the property value should be cast.
	 * @return The property value cast to type [T], or null if no value is found.
	 */
	override fun <T : Any> getProperty(columnName: String, guildId: String, type: KClass<T>): T? {
		val sql = jdbcTemplateBean.parse(
			"SELECT {{columnName}} FROM guilds WHERE discord_id = ?",
			mapOf("columnName" to columnName)
		)
		return jdbcTemplateBean.queryForNullableObject(sql, type, guildId)
	}
}