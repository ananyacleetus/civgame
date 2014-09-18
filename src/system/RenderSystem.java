package system;

import java.util.ArrayList;

import processing.core.PApplet;
import render.Button;
import render.CivGame;
import entity.*;
import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tile;
import data.Color;
import data.EntityData;

public class RenderSystem extends BaseSystem {

	public GridModel terrain;
	public Player player;

	public RenderSystem(CivGame civGame)
	{
		super(civGame);
		player = main.player;
	}

	public void tick()
	{
		main.background(150,225,255);
		main.smooth(4);
		//background(background);
		main.noStroke();
		main.lights();
		//stroke(0);
		main.fill(135, 206, 235);
		main.perspective(3.14F/2,15F/9F,1,10000);
		//System.out.println(player);
		setCamera();
		/*for (int i = 0; i < terrain.entities.size(); i++)
		{
			renderBlock(terrain.entities.get(i));
		}*/
		//Look to see if the entity is both within the player's vision and is a close enough distance
		for (int r = 0; r < terrain.entities.length; r++)
		{
			for (int c = 0; c < terrain.entities[0].length; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk]; 
				//TODO: The center of the player's view is the right bound of the viewing angle
				if (dist < dist2 && dist != -1F && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10)
				{
					renderBlock(terrain.entities[r][c],dist,r,c);
				}
			}
		}
		for (int r = 0; r < main.grid.rows; r++)
		{
			for (int c = 0; c < main.grid.cols; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist < dist1 && dist != -1F && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10)
				{
					Tile t = main.grid.getTile(r,c);
					if (t.improvement != null)
					{
						renderGameEntity(t.improvement,dist,r,c);
					}
					if (t.occupants.size() > 0)
					{
						for (int i = 0; i < t.occupants.size(); i++)
						{
							GameEntity en = t.occupants.get(i);
							renderGameEntity(en,dist,r,c);
						}
					}
				}
			}
		}
		/*main.hint(PApplet.DISABLE_DEPTH_TEST);
		main.camera();
		main.perspective();
		main.rect(500, 500, 500, 500);
		main.hint(PApplet.ENABLE_DEPTH_TEST);*/
		//main.perspective();
		//main.ortho();
		//main.stroke(255);
		//float lineWidth = 20;
		//main.line(main.width/2 - lineWidth/2, main.height/2 - lineWidth/2, main.width/2 + lineWidth/2, main.height/2 + lineWidth/2);
	}

	//Render a block by accessing main's P3D abilities
	public float con; public float cutoff;
	private final int dist1 = 1000; private final int dist2 = 1350;
	private double viewAngle = Math.PI/2 + Math.PI/12;
	public void renderBlock(Entity en, float dist, int r, int c)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		if (en.sizeY >= cutoff)
		{
			float sampleSize;
			//float dist = (float)Math.sqrt(Math.pow(player.posX - en.posX, 2) + Math.pow(player.posY - en.posY, 2) + Math.pow(player.posZ - en.posZ, 2));
			//main.fill(135, 206, 235);
			Color color = EntityData.brickColorMap.get(EntityData.groundColorMap.get(main.grid.getTile(r, c).biome));
			main.fill((float)color.r*255F,(float)color.g*255F,(float)color.b*255F);
			main.noStroke();
			if (dist > dist2)
			{
				sampleSize = 4;
				if (!((r) % sampleSize == 0 && (c) % sampleSize == 0))
				{
					return;
				}
			}
			else if (dist > dist1)
			{	
				sampleSize = 2;
				if (!((r+1) % sampleSize == 0 && (c+1) % sampleSize == 0))
				{
					return;
				}
			}
			else
			{
				if (main.grid.getTile(r,c).owner != null)
				{
					Civilization civ = main.grid.getTile(r,c).owner;
					main.stroke(civ.r, civ.g, civ.b);
					if (main.grid.getTile(r,c).harvest)
					{
						main.strokeWeight(5);
					}
					else
					{
						main.strokeWeight(1);
					}
				}
				sampleSize = 1;
			}
			main.pushMatrix();
			//main.translate(en.posX + widthBlock, en.posY*con, en.posZ + widthBlock);
			main.translate(en.posX, en.posY*con, en.posZ);
			main.box(en.sizeX*sampleSize, (en.sizeY - cutoff)*con, en.sizeZ*sampleSize);
			//Render a hill or mountain
			if (sampleSize == 1)
			{
				if (main.grid.getTile(r,c).shape == 1)
				{
					main.translate(0, (en.sizeY - cutoff)*con/2, 0);
					main.box(en.sizeX/2*sampleSize);
				}
				else if (main.grid.getTile(r,c).shape == 2)
				{
					main.translate(0, (en.sizeY - cutoff)*con/2, 0);
					main.translate(0, en.sizeX*sampleSize/4, 0);
					main.box(en.sizeX/2*sampleSize, en.sizeX*sampleSize*1.5F, en.sizeX/2*sampleSize);
				}
				if (main.grid.getTile(r,c).resource != 0)
				{
					
				}
			}
			main.popMatrix();
		}
	}

	//Render a game entity
	public void renderGameEntity(BaseEntity en, float dist, int r, int c)
	{
		main.fill(en.owner.r,en.owner.g,en.owner.b);
		//float dist = (float)Math.sqrt(Math.pow(player.posX - r*widthBlock, 2) + Math.pow(player.posY - main.terrain[r][c], 2) + Math.pow(player.posZ - c*widthBlock, 2));
		main.noStroke();
		float sizeY = widthBlock*3F;
		main.pushMatrix();
		main.translate(r*widthBlock, (float)(main.terrain[r][c]-cutoff)*con + sizeY/2, c*widthBlock);
		main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
		if (en.name.equals("Settler"))
		{
			main.translate(0,sizeY/2 + widthBlock*0.4F,0);
			main.fill(150,225,255);
			main.box(widthBlock*0.4F*2);
		}
		else if (en.name.equals("Worker"))
		{
			main.translate(0,sizeY/2 + widthBlock*0.4F/2,0);
			main.fill(150,225,255);
			main.box(widthBlock*0.4F);
		}
		else if (en.name.equals("Warrior"))
		{
			main.translate(0,sizeY/2 + widthBlock*0.4F/2,0);
			main.fill(255,0,0);
			main.box(widthBlock*0.4F);
		}
		main.popMatrix();
	}

	public void setCamera()
	{
		main.camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
	}

	//Make a model of entities with a height map
	public float widthBlock = 20;
	public void addTerrain(double[][] t, float con, float cutoff)
	{
		terrain = new GridModel(t.length, t[0].length);
		for (int r = 0; r < t.length; r++)
		{
			for (int c = 0; c < t[0].length; c++)
			{
				double h = t[r][c];
				//float con = (1F/10F)*widthBlock;
				Entity en = new Entity();
				//en.moveTo(r*widthBlock, (float)h/2F*con, c*widthBlock);
				//en.size(widthBlock, (float)h*con, widthBlock);
				en.moveTo(r*widthBlock, (float)h/2F, c*widthBlock);
				en.size(widthBlock, (float)h, widthBlock);
				terrain.add(en,r,c);
				this.con = con;
				this.cutoff = cutoff;
			}
		}
	}

	//Compares two angles between 0 and 6.28 (2*Math.PI)
	public boolean angle(double a1, double a2)
	{
		if (a2 > a1)
		{
			return (2*Math.PI - a2) + a1 <= viewAngle || a2 - a1 <= viewAngle;
		}
		else
		{
			return (2*Math.PI - a1) + a2 <= viewAngle || a1 - a2 <= viewAngle;
		}
	}

}
