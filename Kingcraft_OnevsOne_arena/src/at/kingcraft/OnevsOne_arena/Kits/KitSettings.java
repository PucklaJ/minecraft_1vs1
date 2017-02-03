package at.kingcraft.OnevsOne_arena.Kits;

public enum KitSettings
{
	NO_FALL_DAMAGE (1,false,"Kein Fallschaden"),
	PLACE_BREAK_BLOCKS (2,true,"Blöcke (ab)bauen"),
	HUNGER (4,false,"Hunger"),
	REGENARATION(8,true,"Regeneration"),
	NO_EXPLOSION_DESTRUCTION(16,false,"Keine Zerstörung durch Explosionen"),
	INSTANT_TNT(32,false,"Sofort TNT"),
	NO_KNOCKBACK(64,false,"Kein Rückstoß"),
	FRIENDLY_FIRE(128,false,"Teamschaden"),
	NO_CRAFTING(256,false,"Kein Crafting"),
	DOUBLE_JUMP(512,false,"Doppelsprung"),
	SOUP(1024,false,"Suppen-Heilung"),
	NO_SOUP_DROP(2048,false,"Keine Suppen fallenlassen"),
	NO_HIT_DELAY(4096,false,"Keine Schlagverzögerung"),
	NO_ARROW_COLLECT(8192,false,"Keine Pfeile aufsammeln");
	
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
