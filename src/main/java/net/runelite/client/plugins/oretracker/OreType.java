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

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.client.game.AsyncBufferedImage;

import java.awt.*;
import java.util.Set;
import java.util.stream.Stream;

public enum OreType {

    TIN("Tin", "tinOre", Color.decode("#ffedeb"), ItemID.TIN_ORE, ImmutableSet.of(ObjectID.ROCKS_7485, ObjectID.ROCKS_7486), 2000L),
    COPPER("Copper", "copperOre", Color.decode("#ffb375"), ItemID.COPPER_ORE, ImmutableSet.of(ObjectID.ROCKS_7453, ObjectID.ROCKS_7484), 2000L),
    CLAY("Clay", "clayOre", Color.decode("#ffd791"), ItemID.CLAY, ImmutableSet.of(ObjectID.ROCKS_7454), 600L),
    IRON("Iron", "ironOre", Color.decode("#80564a"), ItemID.IRON_ORE, ImmutableSet.of(ObjectID.ROCKS_7455, ObjectID.ROCKS_7488), 5400L),
    SILVER("Silver", "silverOre", Color.decode("#f0edff"), ItemID.SILVER_ORE, ImmutableSet.of(ObjectID.ROCKS_7490), 60000L),
    COAL("Coal", "coalOre", Color.decode("#383626"), ItemID.COAL, ImmutableSet.of(ObjectID.ROCKS_7456, ObjectID.ROCKS_7489), 30000L),
    GOLD("Gold", "goldOre", Color.decode("#ffcc26"), ItemID.GOLD_ORE, ImmutableSet.of(ObjectID.ROCKS_7458, ObjectID.ROCKS_7491), 61000L),
    GEM("Gem", "gemOre", Color.decode("#b12bff"), ItemID.ENCHANTED_GEM, ImmutableSet.of(ObjectID.ROCKS_7463, ObjectID.ROCKS_7464), 105000L),
    MITHRIL("Mithril", "mithrilOre", Color.decode("#b5baff"), ItemID.MITHRIL_ORE, ImmutableSet.of(ObjectID.ROCKS_7459, ObjectID.ROCKS_7492), 120000L),
    ADAMANTITE("Adamantite", "adamantiteOre", Color.decode("#ccffcf"), ItemID.ADAMANTITE_ORE, ImmutableSet.of(ObjectID.ROCKS_7493), 240000L),
    RUNITE("Runite", "runiteOre", Color.decode("#ade5ff"), ItemID.RUNITE_ORE, ImmutableSet.of(ObjectID.ROCKS_7461), 720000L),
    ;

    public static final Set<Integer> DEPLETED_ROCK_ID = ImmutableSet.of(ObjectID.ROCKS_7468);

    @Getter
    private final String name;

    @Getter
    private final String keyName;

    @Getter
    private final Color color;

    @Getter
    private final int itemId;

    @Getter
    private final Set<Integer> objectIds;

    @Getter
    private final long respawnDuration;

    @Getter
    @Setter
    private AsyncBufferedImage image;

    private OreType(String name, String keyName, Color color, int itemId, Set<Integer> objectIds, long respawnDuration) {
        this.name = name;
        this.keyName = keyName;
        this.color = color;
        this.itemId = itemId;
        this.objectIds = objectIds;
        this.respawnDuration = respawnDuration;
    }

    @Override
    public String toString() {
        return name;
    }

    public static OreType valueOf(int objectId) {
        return Stream.of(values())
                .filter(ore -> ore.objectIds.contains(objectId))
                .findFirst()
                .orElse(null);
    }

}
