package at.kingcraft.OnevsOne_arena.Kits;

public enum KitSettings
{
	NO_FALL_DAMAGE (1 << 0,false,"Kein Fallschaden"),
	PLACE_BREAK_BLOCKS (1 << 1,true,"Blöcke (ab)bauen"),
	HUNGER (1 << 2,false,"Hunger"),
	REGENARATION(1 << 3,true,"Regeneration"),
	NO_EXPLOSION_DESTRUCTION(1 << 4,false,"Keine Zerstörung durch Explosionen"),
	INSTANT_TNT(1 << 5,false,"Sofort TNT"),
	NO_KNOCKBACK(1 << 6,false,"Kein Rückstoß"),
	FRIENDLY_FIRE(1 << 7,false,"Teamschaden"),
	NO_CRAFTING(1 << 8,false,"Kein Crafting"),
	DOUBLE_JUMP(1 << 9,false,"Doppelsprung"),
	SOUP(1 << 10,false,"Suppen-Heilung"),
	NO_SOUP_DROP(1 << 11,false,"Keine Suppen fallenlassen"),
	NO_HIT_DELAY(1 << 12,false,"Keine Schlagverzögerung"),
	NO_ARROW_COLLECT(1 << 13,false,"Keine Pfeile aufsammeln"),
	MORE_PARTICLES(1 << 14,false,"Mehr Partikel");
	
	private final int BIT;
	private final boolean D_A;
	private final String name;
	
	KitSettings(int bit,boolean da,String name)
	{
		BIT = bit;
		D_A = da;
		this.name = name;
	}
	
	public int getBit()
	{
		return BIT;
	}
	
	public boolean isDefaultActivate()
	{
		return D_A;
	}

	public String getName()
	{
		return name;
	}
}
