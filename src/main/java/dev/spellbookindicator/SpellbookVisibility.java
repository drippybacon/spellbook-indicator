package dev.spellbookindicator;

public enum SpellbookVisibility
{
	OFF("Off"),
	EVERYWHERE("Everywhere"),
	BANK_ONLY("Bank only"),
	OUTSIDE_BANK("Outside bank");

	private final String label;

	SpellbookVisibility(String label)
	{
		this.label = label;
	}

	boolean isVisible(boolean bankOpen)
	{
		switch (this)
		{
			case EVERYWHERE:
				return true;
			case BANK_ONLY:
				return bankOpen;
			case OUTSIDE_BANK:
				return !bankOpen;
			default:
				return false;
		}
	}

	@Override
	public String toString()
	{
		return label;
	}
}
