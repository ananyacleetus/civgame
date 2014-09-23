package game;

import java.util.ArrayList;

import units.City;
import data.EntityData;

//An entity that moves around the map i.e. a unit

public abstract class GameEntity extends BaseEntity {

	public ArrayList<Tile> queueTiles = new ArrayList<Tile>();

	public GameEntity(String name)
	{
		super(name);
	}

	public GameEntity(GameEntity other)
	{
		super(other);
	}

	//public abstract String getName();

	public abstract void tick();

	public void waddle()
	{
		if (queueTiles.size() > 0)
		{
			location.grid.moveTo(this, queueTiles.get(0).row, queueTiles.get(0).col);
			queueTiles.remove(0);
		}
		else
		{
			GameEntity en = this;
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
			if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
			{
				if (location.grid.getTile(en.location.row+r,en.location.col+c).biome != -1)
				{
					GameEntity enemy = location.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
					if (enemy == null)
					{
						location.grid.move(this, r, c);
					}
				}
			}
		}
	}

	public void waddleTo(int r, int c)
	{
		if (location.grid.getTile(location.row+r,location.col+c) == null)
		{
			ArrayList<Tile> tiles = location.grid.pathFinder.findAdjustedPath(location.row,location.col,location.row+r,location.col+c);
			if (tiles != null)
			{
				if (tiles.size() > 0)
				{
					queueTiles = tiles;
				}
			}
		}
	}

	//public void tick()
	{
		/*GameEntity en = this;
		if (name.equals("Worker") && queue != null)
		{
			queueTurns--;
			if (queueTurns <= 0)
			{
				location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
				queueTurns = 0; //just to be sure
				queue = null;
			}
			return;
		}
		else if (name.equals("Worker"))
		{
			if (en.name.equals("Worker") && en.queue == null)
			{
				if (en.location.city != null && en.location.improvement == null)
				{
					City city = en.location.city;
					//Factor in the city later
					//if (city.location.owner.equals(owner)) //just in case
					{
						if (en.location.resource == 1 || en.location.resource == 2)
						{
							en.queueTurns = 6;
							en.queue = "Farm";
						}
						else if (en.location.resource == 10 || en.location.resource == 11)
						{

						}
						else if (en.location.resource >= 20 && en.location.resource <= 22)
						{
							en.queueTurns = 6;
							en.queue = "Mine";
						}
						else if (en.location.resource >= 30 && en.location.resource <= 30)
						{

						}
						if (en.location.biome >= 3 && en.location.biome <= 6)
						{
							if (location.grid.irrigated(location.row, location.col))
							{
								en.queueTurns = 6;
								en.queue = "Farm";
							}
						}
						else if (en.location.shape == 1)
						{
							en.queueTurns = 6;
							en.queue = "Mine";
						}
						else if (en.location.shape == 2)
						{
							if (en.location.biome >= 0 && en.location.biome <= 3)
							{
								en.queueTurns = 6;
								en.queue = "Mine";
							}
						}
					}
				}
			}
		}
		else if (en.name.equals("Settler"))
		{
			if (en.location.owner == null && Math.random() < 0.2)
			{
				//Make the city and set its surrounding tiles to the civilization's territory
				City city = (City)EntityData.get("City");
				city.owner = en.owner;
				city.owner.cities.add(city);
				location.grid.addUnit(city, en.owner, en.location.row, en.location.col);
				if (owner.cities.size() == 1)
				{
					city.capital = true;
				}
				for (int i = en.location.row - 2; i <= en.location.row + 2; i++)
				{
					for (int j = en.location.col - 2; j <= en.location.col + 2; j++)
					{
						if (i >= 0 && i < location.grid.rows && j >= 0 && j < location.grid.cols)
						{
							Tile t = location.grid.getTile(i,j);
							if (t != null)
							{
								if (t.owner == null)
								{
									t.city = city;
									city.land.add(t);
									location.grid.addTile(en.owner, t);
								}
								if (t.owner == city.owner && t.city == null)
								{
									t.city = city;
									city.land.add(t);
								}
							}
						}
					}
				}
				//Remove the settler
				location.grid.removeUnit(en);
				return;
			}
		}
		else if (name.equals("Work Boat") || name.equals("Galley"))
		{
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
			if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
			{
				if (location.grid.getTile(en.location.row+r,en.location.col+c).biome == -1)
				{
					//if (location.grid.getTile(en.location.row+r,en.location.col+c).improvement.name.equals("City"))
					if (name.equals("Galley"))
					{
						if (queue == null)
						{
							location.grid.move(en,r,c);
							//System.out.println(location.grid.getTile(en.location.row+r,en.location.col+c));
							//if (location == null) return;
							if (location.resource == 10 ||
									location.resource == 11)
							{
								en.queueTurns = 6;
								en.queue = "Fishing Boats";
							}
						}
						else
						{
							en.queueTurns--;
							if (queueTurns <= 0)
							{
								location.grid.addUnit(EntityData.get(queue), owner, location.row, location.col);
								queueTurns = 0;
								queue = null;
							}
						}
					}
					else
					{
						GameEntity enemy = location.grid.hasEnemy(this, location.row, location.col);
						if (enemy != null)
						{
							if (Math.random() < 0.6)
							{
								location.grid.removeUnit(enemy);
								location.grid.move(en,r,c);
							}
							else
							{
								location.grid.removeUnit(en);
								return;
							}
						}
						else
						{
							location.grid.move(en,r,c);
						}
					}
				}
			}
			return;
		}
		//if (!name.equals("Worker") || (name.equals("Worker") && queue == null))
		if (!name.equals("Work Boat"))
		{
			int r = (int)(Math.random()*3) - 1;
			int c = (int)(Math.random()*3) - 1;
			if (location.grid.getTile(en.location.row+r,en.location.col+c) != null)
			{
				//if (main.grid.getTile(en.location.row+r,en.location.col+c).owner == en.owner ||
				//main.grid.getTile(en.location.row+r,en.location.col+c).owner == null)
				if (location.grid.getTile(en.location.row+r,en.location.col+c).biome != -1)
				{
					GameEntity enemy = location.grid.hasEnemy(en,en.location.row+r,en.location.col+c);
					if (enemy != null)
					{
						if (en.name.equals("Warrior"))
						{
							if (enemy.name.equals("Warrior"))
							{
								if (Math.random() < 0.5)
								{
									location.grid.removeUnit(enemy);
									location.grid.move(en,r,c);
								}
								else
								{
									location.grid.removeUnit(en);
									return;
								}
							}
							else
							{
								location.grid.removeUnit(enemy);
								location.grid.move(en,r,c);
							}
						}
					}
					else
					{
						//en.tick();
						if ((name.equals("Settler") || name.equals("Worker")) && !owner.equals(location.grid.getTile(en.location.row+r,en.location.col+c).owner))
						{
							return;
						}
						if (en.queue == null)
							location.grid.move(en,r,c);
						if (en.location.improvement != null)
						{
							if (en.location.improvement.name.equals("City") && !en.owner.equals(en.location.improvement.owner) && en.name.equals("Warrior"))
							{
								//System.out.println("Destroyed");
								City city = (City)en.location.improvement;
								for (int k = city.land.size() - 1; k >= 0; k--)
								{
									Tile t = city.land.get(k);
									//if (t.equals(city.location)) continue;
									if (t.improvement != null)
									{
										if (!t.improvement.name.equals("City"))
											location.grid.removeUnit(t.improvement);
										t.improvement = null;
									}
									//city.owner.tiles.remove(t);
									t.owner = null;
									t.city = null;
									city.land.remove(k);
									//System.out.println("Destroyed");
									//en.owner.
									//t.owner = en.owner;
								}
								city.owner.cities.remove(city);
								location.grid.removeUnit(city);
								en.location.improvement = null;
								//city = null;
							}
						}
					}
				}
			}
		}*/
	}

}
