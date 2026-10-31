/*
 * Copyright (c) 2026, Spellbook Indicator contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dev.spellbookindicator;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;

public class SpellbookIndicatorOverlay extends Overlay
{
	private final SpellbookIndicatorConfig config;
	private final SpellbookIndicatorPlugin plugin;
	private final SpriteManager spriteManager;

	@Inject
	SpellbookIndicatorOverlay(
		SpellbookIndicatorConfig config,
		SpellbookIndicatorPlugin plugin,
		SpriteManager spriteManager)
	{
		super(plugin);
		this.config = config;
		this.plugin = plugin;
		this.spriteManager = spriteManager;

		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setMovable(true);
		setResizable(true);
		setMinimumSize(16);
		getMenuEntries().add(new OverlayMenuEntry(
			RUNELITE_OVERLAY_CONFIG,
			OPTION_CONFIGURE,
			"Spellbook Indicator"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.iconVisibility().isVisible(plugin.isBankOpen()))
		{
			return null;
		}

		int spriteId = getSpriteId(plugin.getCurrentSpellbook());
		if (spriteId < 0)
		{
			return null;
		}

		BufferedImage icon = spriteManager.getSprite(spriteId, 0);
		if (icon == null)
		{
			return null;
		}

		Dimension preferredSize = getPreferredSize();
		int defaultSize = Math.max(getMinimumSize(), Math.min(40, config.legacyIconSize()));
		int width = preferredSize == null ? defaultSize : Math.max(getMinimumSize(), preferredSize.width);
		int height = preferredSize == null ? defaultSize : Math.max(getMinimumSize(), preferredSize.height);
		int size = Math.min(width, height);
		graphics.drawImage(icon, (width - size) / 2, (height - size) / 2, size, size, null);
		return new Dimension(width, height);
	}

	private static int getSpriteId(int spellbook)
	{
		switch (spellbook)
		{
			case 0:
				return SpriteID.SideiconsInterface.MAGIC;
			case 1:
				return SpriteID.SideiconsInterface.SPELLBOOK_ANCIENT_MAGICKS;
			case 2:
				return SpriteID.SideiconsInterface.SPELLBOOK_LUNAR;
			case 3:
				return SpriteID.SideiconsInterface.SPELLBOOK_ARCEUUS;
			default:
				return -1;
		}
	}
}
