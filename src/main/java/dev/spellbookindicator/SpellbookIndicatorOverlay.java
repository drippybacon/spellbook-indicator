package dev.spellbookindicator;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class SpellbookIndicatorOverlay extends Overlay
{
	private final SpellbookIndicatorConfig config;
	private final SpellbookIndicatorPlugin plugin;
	private final SpriteManager spriteManager;

	@Inject
	private SpellbookIndicatorOverlay(
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
