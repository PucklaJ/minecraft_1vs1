package at.kingcraft.OnevsOne_setup.Maps;

import org.bukkit.Material;

public class SaveBlock {
	public double x;
	public double y;
	public double z;
	public byte data;
 	public Material mat;
	
	public SaveBlock(double x,double y,double z,byte data,Material mat)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
		this.mat = mat;
	}
}
