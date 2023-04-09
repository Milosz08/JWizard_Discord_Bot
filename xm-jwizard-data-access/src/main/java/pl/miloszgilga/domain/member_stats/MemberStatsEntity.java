/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: MemberStats.java
 * Last modified: 07/04/2023, 01:09
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

package pl.miloszgilga.domain.member_stats;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

import pl.miloszgilga.domain.guild.GuildEntity;
import pl.miloszgilga.domain.member.MemberEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@NoArgsConstructor
@Table(name = "member_stats")
public class MemberStatsEntity extends AbstractAuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Column(name = "messages_sended")               private Long messagesSended;
    @Column(name = "messages_updated")              private Long messagesUpdated;
    @Column(name = "reactions_added")               private Long reactionsAdded;
    @Column(name = "level")                         private Integer level;

    @ManyToOne(cascade = { MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private MemberEntity member;

    @ManyToOne(cascade = { MERGE, REMOVE }, fetch = LAZY)
    @JoinColumn(name = "guild_id", referencedColumnName = "id")
    private GuildEntity guilds;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Long getMessagesSended() {
        return messagesSended;
    }

    void setMessagesSended(Long messagesSended) {
        this.messagesSended = messagesSended;
    }

    Long getMessagesUpdated() {
        return messagesUpdated;
    }

    void setMessagesUpdated(Long messagesUpdated) {
        this.messagesUpdated = messagesUpdated;
    }

    Long getReactionsAdded() {
        return reactionsAdded;
    }

    void setReactionsAdded(Long reactionsAdded) {
        this.reactionsAdded = reactionsAdded;
    }

    MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    GuildEntity getGuild() {
        return guild;
    }

    public void setGuild(GuildEntity guild) {
        this.guild = guild;
    }

    Integer getLevel() {
        return level;
    }

    void setLevel(Integer level) {
        this.level = level;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "messagesSended=" + messagesSended +
            ", messagesUpdated=" + messagesUpdated +
            ", reactionsAdded=" + reactionsAdded +
            ", level=" + level +
            '}';
    }
}