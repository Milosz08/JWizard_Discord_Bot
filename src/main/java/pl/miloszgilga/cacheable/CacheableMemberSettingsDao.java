/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CacheableSettingsDao.java
 * Last modified: 25/04/2023, 03:43
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.cacheable;

import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.CachePut;

import java.util.function.Consumer;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.core.configuration.BotConfiguration;

import pl.miloszgilga.domain.member_settings.MemberSettingsEntity;
import pl.miloszgilga.domain.member_settings.IMemberSettingsRepository;

import static pl.miloszgilga.exception.StatsException.MemberHasNoStatsYetInGuildException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class CacheableMemberSettingsDao extends AbstractCacheableDao<MemberSettingsEntity, IMemberSettingsRepository> {

    CacheableMemberSettingsDao(BotConfiguration config, IMemberSettingsRepository settingsRepository) {
        super(config, settingsRepository);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "MemberSettingsCache", key = "#p0.member.id.concat(#p0.guild.id)")
    public MemberSettingsEntity toggleStatsVisibility(CommandEventWrapper event, boolean isPrivate, Consumer<Boolean> exec) {
        final MemberSettingsEntity settings = cacheableRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .orElseThrow(() -> new MemberHasNoStatsYetInGuildException(config, event, event.getMember().getUser()));

        exec.accept(settings.getStatsPrivate());
        settings.setStatsPrivate(isPrivate);
        return settings;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @CachePut(cacheNames = "MemberSettingsCache", key = "#p0.member.id.concat(#p0.guild.id)")
    public MemberSettingsEntity toggleMemberStatsDisabled(CommandEventWrapper event, boolean isDisabled, Consumer<Boolean> exec) {
        final MemberSettingsEntity settings = cacheableRepository
            .findByMember_DiscordIdAndGuild_DiscordId(event.getMemberId(), event.getGuildId())
            .orElseThrow(() -> new MemberHasNoStatsYetInGuildException(config, event, event.getMember().getUser()));

        exec.accept(settings.getStatsDisabled());
        settings.setStatsDisabled(isDisabled);
        return settings;
    }
}