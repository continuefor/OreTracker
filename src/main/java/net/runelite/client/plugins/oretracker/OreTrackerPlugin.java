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

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginDescriptor(
        name = "Ore Tracker",
        description = "Keeps track of ore spawnings across worlds.",
        tags = { "ore", "tracker" }
)
@Singleton
public class OreTrackerPlugin extends Plugin {

    // injected from API
    @Inject Client client;
    @Inject ItemManager itemManager;
    @Inject OverlayManager overlayManager;

    // injected from plugin
    @Inject OreTrackerConfig config;
    @Inject OreTracker3DOverlay oreTracker3DOverlay;
    @Inject OreTrackerListOverlay oreTrackerOverlayList;

    DateTimeFormatter timeFormatter;
    Map<Ore, OreEvent> oreEvents;

    @Provides
    OreTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OreTrackerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        Stream.of(OreType.values()).forEach(this::loadImage);
        timeFormatter = DateTimeFormatter.ofPattern("mm:ss");
        oreEvents = new HashMap<>();
        overlayManager.add(oreTracker3DOverlay);
        overlayManager.add(oreTrackerOverlayList);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(oreTracker3DOverlay);
        overlayManager.remove(oreTrackerOverlayList);
        oreEvents.clear();
        oreEvents = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("oreTracker")) {
            if (e.getKey().equals("getSelectedOre")) {
                oreEvents.clear();
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned e) {
        final long now = System.currentTimeMillis();
        final OreType selectedOre = config.getSelectedOre();
        GameObject gameObject = e.getGameObject();
        OreType oreType = OreType.valueOf(gameObject.getId());
        Ore ore;
        OreEvent oreEvent;
        if (oreType != null && oreType == selectedOre) {
            // see if we have already recorded the rock, or record it if we haven't
            ore = getRegisteredOre(oreType, gameObject.getWorldLocation(), client.getWorld());
            if (ore == null) {
                ore = new Ore(oreType, gameObject.getWorldLocation(), client.getWorld());
            }
            // see if we have already recorded an event against the rock, or create a new one if we haven't
            oreEvent = oreEvents.get(ore);
            if (oreEvent == null) {
                oreEvent = new OreEvent(ore, now);
                oreEvents.put(ore, oreEvent);
            }
        }
    }

    @Schedule(period = 1, unit = ChronoUnit.SECONDS)
    public void removeEventsThatHaveExpired() {
        final long now = System.currentTimeMillis();
        final int worldId = client.getWorld();
        if (oreEvents != null && !oreEvents.isEmpty()) {
            oreEvents.entrySet().removeIf(e -> (now > e.getValue().getWhenRespawn()) && (e.getKey().getWorldId() == worldId));
        }
    }

    /*
     * Helper functions
     */

    void loadImage(OreType oreType) {
        oreType.setImage(itemManager.getImage(oreType.getItemId()));
    }

    protected String formatTime(long milliseconds) {
        return LocalTime.ofSecondOfDay(milliseconds / 1000L).format(timeFormatter);
    }

    Ore getRegisteredOre(OreType oreType, WorldPoint worldPoint, int worldId) {
        return oreEvents.keySet().stream()
                .filter(ore -> ore.getWorldId() == worldId)
                .filter(ore -> ore.getType() == oreType)
                .filter(ore -> ore.getWorldPoint().equals(worldPoint))
                .findFirst()
                .orElse(null);
    }

    Stream<Map.Entry<Integer, OreEvent>> getSummary() {
        return oreEvents.entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey().getWorldId(),
                    e -> e.getValue(),
                    (e1, e2) -> e1.getWhenRespawn() < e2.getWhenRespawn() ? e1 : e2))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, OreEvent>comparingByValue());
    }
}
