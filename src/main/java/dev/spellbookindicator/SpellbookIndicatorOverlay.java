package dev.spellbookindicator;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class SpellbookIndicatorOverlay extends OverlayPanel
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
		setMovable(true);
		getMenuEntries().add(new OverlayMenuEntry(
			RUNELITE_OVERLAY_CONFIG,
			OPTION_CONFIGURE,
			"Spellbook Indicator"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showIcon())
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

		int size = config.iconSize();
		panelComponent.getChildren().add(new SpellbookIconComponent(icon, size));
		return super.render(graphics);
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

	private static final class SpellbookIconComponent implements LayoutableRenderableEntity
	{
		private final BufferedImage icon;
		private final int size;
		private final Rectangle bounds = new Rectangle();
		private Point preferredLocation = new Point();

		private SpellbookIconComponent(BufferedImage icon, int size)
		{
			this.icon = icon;
			this.size = size;
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			graphics.drawImage(icon, preferredLocation.x, preferredLocation.y, size, size, null);
			bounds.setBounds(preferredLocation.x, preferredLocation.y, size, size);
			return new Dimension(size, size);
		}

		@Override
		public Rectangle getBounds()
		{
			return bounds;
		}

		@Override
		public void setPreferredLocation(Point preferredLocation)
		{
			this.preferredLocation = preferredLocation;
		}

		@Override
		public void setPreferredSize(Dimension preferredSize)
		{
			// The icon uses its configured size.
		}
	}
}
