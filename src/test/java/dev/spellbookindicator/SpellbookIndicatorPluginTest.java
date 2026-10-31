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

import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.OverlayManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SpellbookIndicatorPluginTest
{
	@Mock
	private Client client;

	@Mock
	private OverlayManager overlayManager;

	@Mock
	private SpellbookIndicatorOverlay iconOverlay;

	@Mock
	private SpellbookNameOverlay nameOverlay;

	@InjectMocks
	private SpellbookIndicatorPlugin plugin;

	@Test
	public void registersAndRemovesBothOverlays()
	{
		plugin.startUp();
		plugin.shutDown();

		InOrder calls = inOrder(overlayManager);
		calls.verify(overlayManager).add(iconOverlay);
		calls.verify(overlayManager).add(nameOverlay);
		calls.verify(overlayManager).remove(iconOverlay);
		calls.verify(overlayManager).remove(nameOverlay);
		verifyNoMoreInteractions(overlayManager);
	}

	@Test
	public void readsTheActiveSpellbookVarbit()
	{
		for (int spellbook : new int[]{0, 1, 2, 3, -1, 4})
		{
			when(client.getVarbitValue(VarbitID.SPELLBOOK)).thenReturn(spellbook);
			assertEquals(spellbook, plugin.getCurrentSpellbook());
		}
	}

	@Test
	public void bankIsClosedWhenItsWidgetIsMissing()
	{
		assertFalse(plugin.isBankOpen());
	}

	@Test
	public void bankIsClosedWhenItsWidgetIsHidden()
	{
		Widget bank = mock(Widget.class);
		when(client.getWidget(InterfaceID.Bankmain.UNIVERSE)).thenReturn(bank);
		when(bank.isHidden()).thenReturn(true);

		assertFalse(plugin.isBankOpen());
	}

	@Test
	public void bankIsOpenWhenItsWidgetIsVisible()
	{
		Widget bank = mock(Widget.class);
		when(client.getWidget(InterfaceID.Bankmain.UNIVERSE)).thenReturn(bank);
		when(bank.isHidden()).thenReturn(false);

		assertTrue(plugin.isBankOpen());
	}
}
