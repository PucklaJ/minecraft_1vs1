package at.Kingcraft.OnevsOne_lobby.Special;

import org.bukkit.Material;

public class MapSymbol {
	private String name;
	private Material mat;
	private short data;
	private String builder;
	private int x;
	private int y;
	
	public MapSymbol(String name,String symbol,short data,String builder,int x,int y)
	{
		this.name = name;
		mat = Material.getMaterial(symbol);
		if(mat == null)
		{
			mat = Material.PAPER;
		}
		this.data = data;
		this.builder = builder;
		this.x = x;
		this.y = y;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Material getSymbol()
	{
		return mat;
	}
	
	public short getData()
	{
		return data;
	}
	
	public String getBuilder()
	{
		return builder;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
