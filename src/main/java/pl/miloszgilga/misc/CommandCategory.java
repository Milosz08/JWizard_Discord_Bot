/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandCategory.java
 * Last modified: 16/05/2023, 19:23
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga.misc;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Guild;

import pl.miloszgilga.locale.CategoryLocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@RequiredArgsConstructor
public enum CommandCategory {
    MUSIC               (CategoryLocaleSet.COMMAND_CATEGORY_MUSIC),
    DJ                  (CategoryLocaleSet.COMMAND_CATEGORY_DJ_ROLE),
    STATS               (CategoryLocaleSet.COMMAND_CATEGORY_STATISTICS),
    OWNER               (CategoryLocaleSet.COMMAND_CATEGORY_OWNER),
    OWNER_MANAGER       (CategoryLocaleSet.COMMAND_CATEGORY_OWNER_AND_MANAGER),
    VOTE                (CategoryLocaleSet.COMMAND_CATEGORY_VOTE),
    OTHERS              (CategoryLocaleSet.COMMAND_CATEGORY_OTHERS);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final CategoryLocaleSet localeSet;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getHolder(BotConfiguration config, Guild guild) {
        return config.getLocaleText(localeSet, guild);
    }
}
