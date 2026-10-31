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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

public class SpellbookNameOverlay extends Overlay
{
	private static final float MIN_FONT_SIZE = 8f;
	private static final float MAX_FONT_SIZE = 128f;

	private final SpellbookIndicatorConfig config;
	private final SpellbookIndicatorPlugin plugin;

	@Inject
	SpellbookNameOverlay(SpellbookIndicatorConfig config, SpellbookIndicatorPlugin plugin)
	{
		super(plugin);
		this.config = config;
		this.plugin = plugin;

		setPosition(OverlayPosition.TOP_LEFT);
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
		if (!config.nameVisibility().isVisible(plugin.isBankOpen()))
		{
			return null;
		}

		int spellbook = plugin.getCurrentSpellbook();
		String name = getName(spellbook);
		if (name == null)
		{
			return null;
		}

		Graphics2D textGraphics = (Graphics2D) graphics.create();
		try
		{
			Dimension preferredSize = getPreferredSize();
			FontMetrics metrics = textGraphics.getFontMetrics();
			int width = metrics.stringWidth(name) + 1;
			int height = metrics.getHeight() + 1;
			if (preferredSize != null)
			{
				width = Math.max(getMinimumSize(), preferredSize.width);
				height = Math.max(getMinimumSize(), preferredSize.height);
				Font font = textGraphics.getFont();
				double scale = Math.min(
					(double) width / (metrics.stringWidth(name) + 1),
					(double) height / (metrics.getHeight() + 1));
				float fontSize = (float) (font.getSize2D() * scale);
				textGraphics.setFont(font.deriveFont(Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, fontSize))));
				metrics = textGraphics.getFontMetrics();
				name = fitName(name, metrics, width - 1);
			}

			textGraphics.clipRect(0, 0, width, height);
			TextComponent text = new TextComponent();
			text.setText(name);
			text.setColor(getColor(spellbook));
			text.setPosition(0, metrics.getAscent());
			text.render(textGraphics);

			return new Dimension(width, height);
		}
		finally
		{
			textGraphics.dispose();
		}
	}

	private static String fitName(String name, FontMetrics metrics, int width)
	{
		if (metrics.stringWidth(name) <= width)
		{
			return name;
		}
		String ellipsis = "...";
		if (metrics.stringWidth(ellipsis) > width)
		{
			return "";
		}
		int length = name.length();
		while (length > 0 && metrics.stringWidth(name.substring(0, length) + ellipsis) > width)
		{
			length--;
		}
		return name.substring(0, length) + ellipsis;
	}

	private static String getName(int spellbook)
	{
		switch (spellbook)
		{
			case 0:
				return "Standard";
			case 1:
				return "Ancient";
			case 2:
				return "Lunar";
			case 3:
				return "Arceuus";
			default:
				return null;
		}
	}

	private Color getColor(int spellbook)
	{
		switch (spellbook)
		{
			case 0:
				return config.standardNameColor();
			case 1:
				return config.ancientNameColor();
			case 2:
				return config.lunarNameColor();
			case 3:
				return config.arceuusNameColor();
			default:
				return Color.WHITE;
		}
	}
}
