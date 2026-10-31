package dev.spellbookindicator;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Spellbook Indicator",
	description = "Shows the active spellbook icon and optional name in separate overlays"
)
public class SpellbookIndicatorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SpellbookIndicatorOverlay overlay;

	@Inject
	private SpellbookNameOverlay nameOverlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(nameOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(nameOverlay);
	}

	@Provides
	SpellbookIndicatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SpellbookIndicatorConfig.class);
	}

	int getCurrentSpellbook()
	{
		return client.getVarbitValue(VarbitID.SPELLBOOK);
	}
}
