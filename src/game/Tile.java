package game;

import java.util.ArrayList;

public class Tile {

	public TileEntity improvement;
	public ArrayList<GameEntity> occupants;
	
	public int height;
	public int row, col;

	public String type;
	
	public Tile(String type, int height, int row, int col)
	{
		this.type = type;
		occupants = new ArrayList<GameEntity>();
		this.height = height;
		this.row = row;
		this.col = col;
	}
	
}
