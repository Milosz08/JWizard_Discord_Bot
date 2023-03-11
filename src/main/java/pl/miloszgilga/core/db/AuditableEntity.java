/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AuditableEntity.java
 * Last modified: 24/02/2023, 04:12
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.core.db;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@MappedSuperclass
@NoArgsConstructor
@ScannedHibernateEntity
public abstract class AuditableEntity implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at") private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at") private ZonedDateTime updatedAt;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}