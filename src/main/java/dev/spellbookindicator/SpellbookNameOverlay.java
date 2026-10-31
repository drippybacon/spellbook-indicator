package dev.spellbookindicator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class SpellbookNameOverlay extends OverlayPanel
{
	private final SpellbookIndicatorConfig config;
	private final SpellbookIndicatorPlugin plugin;

	@Inject
	private SpellbookNameOverlay(SpellbookIndicatorConfig config, SpellbookIndicatorPlugin plugin)
	{
		super(plugin);
		this.config = config;
		this.plugin = plugin;

		// Keep the name separate from the icon by default. Both overlays can be moved independently.
		setPosition(OverlayPosition.TOP_LEFT);
		setMovable(true);
		getMenuEntries().add(new OverlayMenuEntry(
			RUNELITE_OVERLAY_CONFIG,
			OPTION_CONFIGURE,
			"Spellbook Indicator"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showName())
		{
			return null;
		}

		int spellbook = plugin.getCurrentSpellbook();
		panelComponent.getChildren().add(new SpellbookNameComponent(
			getName(spellbook),
			getColor(spellbook)));
		return super.render(graphics);
	}

	private static final class SpellbookNameComponent implements LayoutableRenderableEntity
	{
		private final String text;
		private final Color color;
		private final Rectangle bounds = new Rectangle();
		private Point preferredLocation = new Point();

		private SpellbookNameComponent(String text, Color color)
		{
			this.text = text;
			this.color = color;
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			java.awt.FontMetrics metrics = graphics.getFontMetrics();
			TextComponent textComponent = new TextComponent();
			textComponent.setPosition(
				preferredLocation.x,
				preferredLocation.y + metrics.getAscent());
			textComponent.setText(text);
			textComponent.setColor(color);
			textComponent.render(graphics);

			Dimension dimension = new Dimension(metrics.stringWidth(text), metrics.getHeight());
			bounds.setBounds(preferredLocation.x, preferredLocation.y, dimension.width, dimension.height);
			return dimension;
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
			// The text component sizes itself to the spellbook name.
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
				return "Unknown";
		}
	}

	private static Color getColor(int spellbook)
	{
		switch (spellbook)
		{
			case 0:
				return new Color(196, 169, 132);
			case 1:
				return new Color(169, 132, 196);
			case 2:
				return Color.WHITE;
			case 3:
				return Color.CYAN;
			default:
				return Color.RED;
		}
	}
}
