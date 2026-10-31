package dev.spellbookindicator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TextComponent;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class SpellbookNameOverlay extends Overlay
{
	private static final float MIN_FONT_SIZE = 8f;
	private static final float MAX_FONT_SIZE = 128f;

	private final SpellbookIndicatorConfig config;
	private final SpellbookIndicatorPlugin plugin;

	@Inject
	private SpellbookNameOverlay(SpellbookIndicatorConfig config, SpellbookIndicatorPlugin plugin)
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
			if (preferredSize != null)
			{
				Font font = textGraphics.getFont();
				FontMetrics metrics = textGraphics.getFontMetrics();
				double scale = Math.min(
					(double) Math.max(getMinimumSize(), preferredSize.width) / (metrics.stringWidth(name) + 1),
					(double) Math.max(getMinimumSize(), preferredSize.height) / (metrics.getHeight() + 1));
				float fontSize = (float) (font.getSize2D() * scale);
				textGraphics.setFont(font.deriveFont(Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, fontSize))));
			}

			FontMetrics metrics = textGraphics.getFontMetrics();
			TextComponent text = new TextComponent();
			text.setText(name);
			text.setColor(getColor(spellbook));
			text.setPosition(0, metrics.getAscent());
			text.render(textGraphics);

			int width = metrics.stringWidth(name) + 1;
			int height = metrics.getHeight() + 1;
			if (preferredSize != null)
			{
				width = Math.max(width, preferredSize.width);
				height = Math.max(height, preferredSize.height);
			}
			return new Dimension(width, height);
		}
		finally
		{
			textGraphics.dispose();
		}
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
