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

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class OreTrackerListOverlay extends Overlay {

    private final Client client;
    private final OreTrackerPlugin plugin;
    private final OreTrackerConfig config;
    private final PanelComponent panelComponent;

    @Inject
    public OreTrackerListOverlay(Client client, OreTrackerPlugin plugin, OreTrackerConfig config) {
        super();
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.panelComponent = new PanelComponent();
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D g) {

        final int maxCount = config.getMaxLineCount();

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .color(Color.YELLOW)
                .text("Ore Tracker").build());

        /*panelComponent.getChildren().add(LineComponent.builder()
            .leftColor(Color.YELLOW)
            .left("World")
            .rightColor(Color.YELLOW)
            .right("Time")
            .build());*/

        if (maxCount > 0) {
            plugin.getSummary().limit(maxCount).forEach(this::addLine);
        } else {
            plugin.getSummary().forEach(this::addLine);
        }

        return panelComponent.render(g);
    }

    void addLine(Map.Entry<Integer, OreEvent> entry) {
        final long now = System.currentTimeMillis();
        int world = entry.getKey();
        OreEvent oreEvent = entry.getValue();
        long timeLeft = (oreEvent.getWhenRespawn() - now);

        Color color;

        if (timeLeft <= 0L) {
            color = config.getReadyColor();
            timeLeft = 0L;
        } else if (timeLeft <= (config.getNearlyReadySeconds() * 1000L)) {
            color = config.getNearlyReadyColor();
        } else {
            color = Color.WHITE;
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .leftColor(color)
                .left(String.valueOf(world))
                .rightColor(color)
                .right(plugin.formatTime(timeLeft))
                .build());
    }
}
