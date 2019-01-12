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
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class OreTracker3DOverlay extends Overlay {

    private final Client client;
    private final OreTrackerPlugin plugin;
    private final OreTrackerConfig config;

    @Inject
    private OreTracker3DOverlay(Client client, OreTrackerPlugin plugin, OreTrackerConfig config) {
        super();
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g) {
        final long now = System.currentTimeMillis();
        final int worldId = client.getWorld();
        plugin.oreEvents.forEach((ore, oreEvent) -> {
            if (ore != null && oreEvent != null) {
                if (ore.getWorldId() == worldId) { // rock is on current world
                    if (now < oreEvent.getWhenRespawn()) { // rock is still depleted
                        if (ore.getWorldPoint().isInScene(client)) { // depleted rock is in current scene
                            drawRock(g, now, ore, oreEvent);
                        }
                    }
                }
            }
        });
        return null;
    }

    void drawRock(Graphics2D g, long now, Ore ore, OreEvent oreEvent) {
        LocalPoint localPoint = LocalPoint.fromWorld(client, ore.getWorldPoint());
        Polygon polygon = Perspective.getCanvasTilePoly(client, localPoint);
        String timeLeftStr = plugin.formatTime(oreEvent.getWhenRespawn() - now);
        // render ore 3D
        OverlayUtil.renderPolygon(g, polygon, ore.getType().getColor());
        // render respawn time over ore
        renderTextLocation(g, polygon, timeLeftStr, Color.WHITE);
    }

    void renderTextLocation(Graphics2D g, Polygon polygon, String text, Color color) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        Rectangle rectangle = polygon.getBounds();
        double x = rectangle.getCenterX() - (textWidth / 2);
        double y = rectangle.getCenterY();
        net.runelite.api.Point point = new net.runelite.api.Point((int) x, (int) y);
        OverlayUtil.renderTextLocation(g, point, text, color);
    }
}
