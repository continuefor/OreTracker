/*
 * Copyright (c) 2018, Continue For <continuefor@outlook.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.oretracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("oreTracker")
public interface OreTrackerConfig extends Config {

    @ConfigItem(
            keyName = "getSelectedOre",
            name = "Ore",
            description = "Select which ores to track.",
            position = 1
    )
    default OreType getSelectedOre() {
        return OreType.RUNITE;
    }

    @ConfigItem(
            keyName = "getMaxLineCount",
            name = "Max Line Count",
            description = "Maximum amount of lines to display in the overlay list.",
            position = 2
    )
    default int getMaxLineCount() {
        return 3;
    }

    @ConfigItem(
            keyName = "getNearlyReadySeconds",
            name = "Nearly Ready (In Seconds)",
            description = "How many seconds until ore is nearly ready for you.",
            position = 3
    )
    default int getNearlyReadySeconds() {
        return 30;
    }

    @ConfigItem(
            keyName = "getNearlyReadyColor",
            name = "Highlight Color (Nearly Ready)",
            description = "Highlight color to apply.",
            position = 4
    )
    default Color getNearlyReadyColor() {
        return Color.ORANGE;
    }

    @ConfigItem(
            keyName = "getReadyColor",
            name = "Highlight Color (Ready)",
            description = "Color for when ore is ready.",
            position = 5
    )
    default Color getReadyColor() {
        return Color.GREEN;
    }
}
