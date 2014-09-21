package game;

import java.util.ArrayList;

import units.City;

public class Civilization {

	public String name;
	public float r,g,b;
	
	public ArrayList<City> cities;
	public ArrayList<GameEntity> units;
	public ArrayList<TileEntity> improvements;
	//public ArrayList<Tile> tiles;
	
	public int food, gold, metal, research;
	
	public Civilization(String name)
	{
		cities = new ArrayList<City>();
		units = new ArrayList<GameEntity>();
		improvements = new ArrayList<TileEntity>();
		//tiles = new ArrayList<Tile>();
		this.name = name;
		food = 10; gold = 0; metal = 0; research = 0;
	}
	
	public boolean equals(Civilization other)
	{
		if (other == null)
		{
			return true;
		}
		return name.equals(other.name);
	}
	
}
