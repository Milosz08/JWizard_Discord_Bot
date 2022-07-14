/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AvailableCommands.java
 * Last modified: 11/07/2022, 22:02
 * Project name: franek-bot
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

package pl.miloszgilga;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;


public enum AvailableCommands {
    MUSIC_PLAY("play"),
    MUSIC_SKIP("skip"),
    HELP("help");

    private final String commandName;

    AvailableCommands(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public static List<String> getAllCommands() {
        return Arrays
                .stream(AvailableCommands.values()).map(AvailableCommands::getCommandName)
                .collect(Collectors.toList());
    }
}