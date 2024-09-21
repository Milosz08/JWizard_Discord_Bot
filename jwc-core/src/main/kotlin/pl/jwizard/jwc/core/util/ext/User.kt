/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

import net.dv8tion.jda.api.entities.User

/**
 * Extension property for the User class that retrieves the user's avatar URL.
 *
 * If the user has a custom avatar, this property returns the custom avatar URL. If the user does not have a custom
 * avatar, it returns the default avatar URL.
 *
 * @receiver User The user instance for which the avatar URL is being retrieved.
 * @return The URL of the user's avatar or the default avatar URL if none is set.
 * @author Miłosz Gilga
 */
val User.avatarOrDefaultUrl get() = avatarUrl ?: defaultAvatarUrl
