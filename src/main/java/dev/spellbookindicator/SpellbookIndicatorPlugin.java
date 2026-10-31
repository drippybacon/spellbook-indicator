package dev.spellbookindicator;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Spellbook Indicator",
	description = "Shows the active spellbook icon and name with custom colors and bank visibility settings"
)
public class SpellbookIndicatorPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "spellbookindicator";

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
		migrateVisibility(configManager, "showIcon", "iconVisibility");
		migrateVisibility(configManager, "showName", "nameVisibility");
		return configManager.getConfig(SpellbookIndicatorConfig.class);
	}

	private static void migrateVisibility(ConfigManager configManager, String oldKey, String newKey)
	{
		String legacyValue = configManager.getConfiguration(CONFIG_GROUP, oldKey);
		if (legacyValue == null)
		{
			return;
		}
		if (configManager.getConfiguration(CONFIG_GROUP, newKey) == null
			&& ("true".equals(legacyValue) || "false".equals(legacyValue)))
		{
			SpellbookVisibility visibility = Boolean.parseBoolean(legacyValue)
				? SpellbookVisibility.EVERYWHERE : SpellbookVisibility.OFF;
			configManager.setConfiguration(CONFIG_GROUP, newKey, visibility);
		}
		configManager.unsetConfiguration(CONFIG_GROUP, oldKey);
	}

	int getCurrentSpellbook()
	{
		return client.getVarbitValue(VarbitID.SPELLBOOK);
	}

	boolean isBankOpen()
	{
		Widget bank = client.getWidget(InterfaceID.Bankmain.UNIVERSE);
		return bank != null && !bank.isHidden();
	}
}
