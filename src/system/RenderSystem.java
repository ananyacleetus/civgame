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
import game.TileEntity;
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
		//main.smooth(4);
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
		for (int r = 0; r < main.terrain.length; r++)
		{
			for (int c = 0; c < main.terrain[0].length; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist == -1F) continue;
				//TODO: The center of the player's view is the right bound of the viewing angle
				if (main.player.posY > 150 
						&& dist < dist2 && dist != -1F 
						&& angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) 
						&& main.chunkSystem.angle[chunk] != -10)
				{
					renderBlock(dist,r,c,true,true);
				}
				else if ((main.player.posY <= 150 && dist < dist0 && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10) ||
						(dist < dist1))
				{
					/*if (!main.grid.civs[0].revealed[r][c] || main.showAll)
					{
						continue;
					}*/

					if (main.grid.civs[0].revealed[r][c] || main.showAll)
					{
						renderBlock(dist,r,c,false,false);
					}
					else
					{
						renderBlock(dist,r,c,true,false);
						continue;
					}

					Tile t = main.grid.getTile(r,c);
					if (t.improvement != null)
					{
						renderGameEntity(t.improvement,dist,r,c);
					}
					if (r < main.terrain.length - 1)
					{
						if (main.horizontalRivers[r][c]) renderRiver(r+1,c,r,c);
					}
					if (c < main.terrain[0].length - 1)
					{
						if (main.verticalRivers[r][c]) renderRiver(r,c,r,c+1);
					}
					/*if (!main.grid.civs[0].revealed[r][c] || main.showAll)
					{
						continue;
					}*/
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
		/*main.strokeWeight(5);
		for (int r = 0; r < main.terrain.length; r++)
		{
			for (int c = 0; c < main.terrain[0].length; c++)
			{
				main.pushMatrix();
				main.translate(r*widthBlock, (float)main.terrain[r][c]*con/2F, c*widthBlock);
				main.translate(-widthBlock/2F, 0, -widthBlock/2F);
				float m = 3;
				System.out.println("*");
				for (int nr = r; nr < r + m; nr++)
				{
					for (int nc = c; nc < c + m; nc++)
					{
						main.pushMatrix();
						main.translate((float)(nr - nr%3)*-widthBlock/3F, 0, (float)(nc - nc%3)*-widthBlock/3F);
						main.point((float)nr/m*widthBlock,(float)vertices[nr][nc],(float)nc/m*widthBlock);
						main.point((float)nr/m*widthBlock,(float)vertices[nr][nc+1],(float)(nc+1)/m*widthBlock);
						main.point((float)(nr+1)/m*widthBlock,(float)vertices[nr][nc+1],(float)nc/m*widthBlock);
						main.popMatrix();
					}
				}
				main.popMatrix();
			}
		}*/
		/*for (int r = 0; r < main.verticalRivers.length; r++)
		{
			for (int c = 0; c < main.verticalRivers[0].length; c++)
			{
				if (main.verticalRivers[r][c]) renderRiver(r,c,r,c+1);
			}
		}
		for (int r = 0; r < main.horizontalRivers.length; r++)
		{
			for (int c = 0; c < main.horizontalRivers[0].length; c++)
			{
				if (main.horizontalRivers[r][c]) renderRiver(r+1,c,r+1,c);
			}
		}*/
		/*for (int r = 0; r < main.grid.rows; r++)
		{
			for (int c = 0; c < main.grid.cols; c++)
			{
				int chunk = main.chunkSystem.chunkFromLocation(r*(int)widthBlock,c*(int)widthBlock);
				float dist = main.chunkSystem.dist[chunk];
				if (dist < dist1 && dist != -1F && angle(main.chunkSystem.angle[chunk]+Math.PI, main.chunkSystem.playerAngle+Math.PI) && main.chunkSystem.angle[chunk] != -10)
				{

				}
			}
		}*/
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
	private final int dist0 = 500;
	private final int dist1 = 800; private final int dist2 = 1250;
	private double viewAngle = Math.PI/2 + Math.PI/12;
	private double[][] vertices;
	public void renderBlock(float dist, int r, int c, boolean hidden, boolean lazy)
	{
		//if (dist < 1000 && en.sizeY >= cutoff)
		//if (main.terrain[r][c] >= 0)
		//if (main.grid.getTile(r,c).biome)
		{
			float sampleSize = 1;
			Color color = EntityData.brickColorMap.get(EntityData.groundColorMap.get(main.grid.getTile(r, c).biome));
			if (!hidden)
				main.fill((float)color.r*255F,(float)color.g*255F,(float)color.b*255F);
			else if (hidden || lazy)
				main.fill((float)color.r*150F,(float)color.g*150F,(float)color.b*150F);
			main.noStroke();
			Tile t = main.grid.getTile(r,c);

			Entity temp = new Entity();
			temp.size(widthBlock*sampleSize, (float)main.terrain[r][c]*con + 1, widthBlock*sampleSize);
			temp.moveTo(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			if (main.player.lookingAtEntity(temp))
			{
				main.menuSystem.target = main.grid.getTile(r, c);
				//main.fill(0);
				main.stroke(0,0,255);
				main.strokeWeight(8);
				if (main.grid.getTile(r,c) != null)
				{
					main.menuSystem.highlighted = main.grid.getTile(r,c);
				}
				else
				{
					main.menuSystem.highlighted = null;
				}
			}
			else
			{
				if (main.grid.getTile(r,c).owner != null && !hidden && !lazy)
				{
					Civilization civ = t.owner;
					main.stroke(civ.r, civ.g, civ.b);
					if (t.harvest)
					{
						main.strokeWeight(5);
					}
					else
					{
						main.strokeWeight(1);
					}
				}
				else if (main.menuSystem.settlerChoices != null)
				{
					for (int i = 0; i < main.menuSystem.settlerChoices.length; i++)
					{
						Tile tile = main.menuSystem.settlerChoices[i];
						if (tile.equals(t))
						{
							main.stroke(200,0,255);
							main.strokeWeight(5);
							break;
						}
					}
				}
			}

			main.pushMatrix();
			//main.translate(en.posX + widthBlock, en.posY*con, en.posZ + widthBlock);
			//main.translate(en.posX, en.posY*con, en.posZ);
			main.translate(r*widthBlock*sampleSize, (float)main.terrain[r][c]*con/2F, c*widthBlock*sampleSize);
			//main.box(widthBlock*sampleSize, (float)main.terrain[r][c]*con, widthBlock*sampleSize);

			if (main.grid.getTile(r, c).biome == -1)
			{
				main.box(widthBlock*sampleSize, (float)main.terrain[r][c]*con, widthBlock*sampleSize);
				main.popMatrix();
				return;
			}

			main.pushMatrix();
			main.translate(-widthBlock/2F, 0, -widthBlock/2F);
			float m = 3;
			//System.out.println("*");

			for (int nr = r*3; nr < r*3 + m; nr++)
			{
				for (int nc = c*3; nc < c*3 + m; nc++)
				{
					main.pushMatrix();
					main.translate((float)(nr - nr%3)*-widthBlock/3F, 0, (float)(nc - nc%3)*-widthBlock/3F);
					main.beginShape(main.TRIANGLES);
					main.vertex((float)nr/m*widthBlock,(float)vertices[nr][nc],(float)nc/m*widthBlock);
					main.vertex((float)nr/m*widthBlock,(float)vertices[nr][nc+1],(float)(nc+1)/m*widthBlock);
					main.vertex((float)(nr+1)/m*widthBlock,(float)vertices[nr+1][nc+1],(float)(nc+1)/m*widthBlock);
					//System.out.println(nr + " " + nc);
					//System.out.println(nr + " " + nc + " " + (float)vertices[nr][nc] + " " + (float)vertices[nr][nc+1] + " " + (float)vertices[nr+1][nc+1]);
					main.vertex((float)nr/m*widthBlock,(float)vertices[nr][nc],(float)nc/m*widthBlock);
					main.vertex((float)(nr+1)/m*widthBlock,(float)vertices[nr+1][nc],(float)nc/m*widthBlock);
					main.vertex((float)(nr+1)/m*widthBlock,(float)vertices[nr+1][nc+1],(float)(nc+1)/m*widthBlock);
					main.endShape();
					main.popMatrix();
				}
			}
			main.popMatrix();

			//Render a hill or mountain
			if (!lazy)
			{
				if (sampleSize == 1)
				{
					if (t.shape == 1)
					{
						main.pushMatrix();
						main.translate(0, (float)main.terrain[r][c]*con/2, 0);
						main.box(widthBlock/2*sampleSize);
						main.popMatrix();
					}
					else if (t.shape == 2)
					{
						main.pushMatrix();
						main.translate(0, (float)main.terrain[r][c]*con/2, 0);
						main.translate(0, widthBlock*sampleSize/4, 0);
						main.box(widthBlock/2*sampleSize, widthBlock*sampleSize*1.5F, widthBlock/2*sampleSize);
						main.popMatrix();
					}
					int res = t.resource;
					if (res != 0)
					{
						main.pushMatrix();
						main.fill(EntityData.get(res));
						main.translate(0, 15, 0);
						main.box(5);
						main.popMatrix();
					}
					if (t.forest)
					{
						renderModel("Forest",0,0,0);
					}
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

		if (en.location.harvest)
		{
			main.strokeWeight(5);
			main.stroke(en.owner.r,en.owner.g,en.owner.b);
		}
		else
		{
			main.strokeWeight(1);
		}

		if (main.menuSystem.getSelected() != null)
			if (en.equals(main.menuSystem.getSelected()))
			{
				main.stroke(0,0,255);
				main.strokeWeight(5);
				if (en instanceof GameEntity)
				{
					GameEntity gameEn = (GameEntity)en;
					if (gameEn.queueTiles.size() > 0)
					{
						//System.out.println(gameEn.queueTiles.size());
						for (int i = gameEn.queueTiles.size() - 1; i >= 0; i--)
						{
							Tile t = gameEn.queueTiles.get(i);
							main.pushMatrix();
							main.translate(t.row*widthBlock, 25, t.col*widthBlock);
							main.fill(((float)(i+1)/(float)gameEn.queueTiles.size())*255F);
							main.box(5,5,5);
							main.popMatrix();
						}
					}
				}
			}
		/*if (en.name.equals("City"))
			{
				main.fill(0);
				main.stroke(en.owner.r,en.owner.g,en.owner.b);
				main.translate(r*widthBlock, (float)(main.terrain[r][c]-cutoff)*con + sizeY/2, c*widthBlock);
				main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
			}*/
		//System.out.println(en.name);
		//System.out.println(EntityData.getModel(en.name));
		renderModel(en.getName(),r,c,en.owner.r,en.owner.g,en.owner.b);
		main.noStroke();
		/*else
		{
			main.fill(0);
			main.stroke(en.owner.r,en.owner.g,en.owner.b);
			main.translate(r*widthBlock, (float)(main.terrain[r][c])*con, c*widthBlock);
			main.box(widthBlock*0.4F,sizeY,widthBlock*0.4F);
		}*/
		/*else
		{
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
		}*/
		main.popMatrix();
	}

	//Generate vertices to be shown in the world
	//this is terrible math
	public void generateRoughTerrain(double[][] terrain, int multiply)
	{
		vertices = new double[terrain.length*multiply + 1][terrain.length*multiply + 1];
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				Tile t = main.grid.getTile(r,c);
				if (t.shape == 2)
				{

				}
				else if (t.shape == 1)
				{

				}
				else
				{
					for (int nr = r*multiply; nr < r*multiply + multiply; nr++)
					{
						for (int nc = c*multiply; nc < c*multiply + multiply; nc++)
						{
							double height = 4;
							//vertices[nr][nc] = terrain[r][c] + Math.random()*height*2 - height;
							vertices[nr][nc] = Math.random()*height;
						}
					}
				}
			}
		}
		//Make the top & left border zero
		for (int i = 0; i < vertices.length; i++)
		{
			vertices[i][0] = 0;
			vertices[0][i] = 0;
		}
		/*for (int r = 0; r < vertices.length; r++)
		{
			for (int c = 0; c < vertices[0].length; c++)
			{
				System.out.print((int)vertices[r][c] + " ");
			}
			System.out.println();
		}*/
	}

	public void renderModel(String name, float red, float green, float blue)
	{
		main.pushMatrix();
		float[][] model = EntityData.getModel(name);
		if (model != null)
		{
			for (int i = 0; i < model.length; i++)
			{
				main.pushMatrix();
				float[] t = model[i];
				if ((int)t[0] == 0)
				{
					main.fill(150);
				}
				else if ((int)t[0] == 1)
				{
					main.fill(red,green,blue);
				}
				main.translate(t[1],t[2],t[3]);
				main.rotateY(t[5]);
				main.box(t[7],t[8],t[9]);
				main.popMatrix();
			}
		}
		main.popMatrix();
	}

	public void renderModel(String name, int r, int c, float red, float green, float blue)
	{
		main.pushMatrix();
		main.translate(r*widthBlock, (float)(main.terrain[r][c])*con, c*widthBlock);
		renderModel(name, red, green, blue);
		main.popMatrix();
	}

	public void renderRiver(int r1, int c1, int r2, int c2)
	{
		main.fill(0,0,150);
		if (r1 == r2) //"Vertical"
		{
			main.pushMatrix();
			main.translate(r1*widthBlock,0,(c1+0.5F)*widthBlock);
			main.box(widthBlock,5,5);
			main.popMatrix();
		}
		else if (c1 == c2) //"Horizontal"
		{
			main.pushMatrix();
			main.translate((r1-0.5F)*widthBlock,0,c1*widthBlock);
			main.box(5,5,widthBlock);
			main.popMatrix();
		}
		else
		{
			System.err.println("Invalid river");
		}
	}

	public void setCamera()
	{
		main.camera(player.posX,player.posY,player.posZ,player.tarX,player.tarY,player.tarZ,0,-1,0);
	}

	//Make a model of entities with a height map
	public static float widthBlock = 21;
	/*public void addTerrain(double[][] t, float con, float cutoff)
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
				en.moveTo(r*widthBlock, (float)(h-cutoff)/2F, c*widthBlock);
				en.size(widthBlock, (float)(h-cutoff), widthBlock);
				terrain.add(en,r,c);
				this.con = con;
				this.cutoff = cutoff;
			}
		}
	}*/

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
