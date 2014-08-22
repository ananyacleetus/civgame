package render;

import processing.core.PApplet;

//Renders terrain specifically with the P3D library

public class OpenGLTerrain extends PApplet {

	public Main main;
	public double[][] terrain;
	public int cutoff;

	public OpenGLTerrain(Main main)
	{
		this.main = main;
	}
	
	//Keys are w,a,s,d,q,e respectively
	public boolean[] keySet = new boolean[6];

	public void keyPressed()
	{
		if (key == 'w')
		{
			keySet[0] = true;
		}
		if (key == 'a')
		{
			keySet[1] = true;
		}
		if (key == 's')
		{
			keySet[2] = true;
		}
		if (key == 'd')
		{
			keySet[3] = true;
		}
		if (key == 'q')
		{
			keySet[4] = true;
		}
		if (key == 'e')
		{
			keySet[5] = true;
		}
		
		main.executeKey(key);
		redraw();
	}
	
	public void keyReleased()
	{
		if (key == 'w')
		{
			keySet[0] = false;
		}
		if (key == 'a')
		{
			keySet[1] = false;
		}
		if (key == 's')
		{
			keySet[2] = false;
		}
		if (key == 'd')
		{
			keySet[3] = false;
		}
		if (key == 'q')
		{
			keySet[4] = false;
		}
		if (key == 'e')
		{
			keySet[5] = false;
		}
	}

	public void setup()
	{
		size(1500,900,P3D);
		//camera(1500,1500,1500,0,0,0,0,-1,0);
		posX = 500;
		posY = 500;
		posZ = 500;
		tarX = 0;
		tarZ = 0;
		noLoop();
	}

	public float posX, posY, posZ;
	public float tarX, tarZ;
	public void draw()
	{
		background(135, 206, 235);
		fill(0,200,0);
		int width = 20; int con = 2;
		camera(posX,posY,posZ,tarX,0,tarZ,0,-1,0);
		for (int r = 0; r < terrain.length; r++)
		{
			for (int c = 0; c < terrain[0].length; c++)
			{
				pushMatrix();
				if ((int)terrain[r][c] > cutoff)
				{
					translate(r*width,(int)(terrain[r][c] - cutoff)/2*con,c*width);
					box(width,(int)(terrain[r][c] - cutoff)*con,width);
				}
				popMatrix();
			}
		}
		int dist = 15;
		if (keySet[0])
		{
			posX -= dist;
			tarX -= dist;
		}
		if (keySet[1])
		{
			posZ -= dist;
			tarZ -= dist;
		}
		if (keySet[2])
		{
			posX += dist;
			tarX += dist;
		}
		if (keySet[3])
		{
			posZ += dist;
			tarZ += dist;
		}
		if (keySet[4])
		{
			posY -= dist;
		}
		if (keySet[5])
		{
			posY += dist;
		}
	}

	public void setTerrain(double[][] terrain, int cutoff)
	{
		this.terrain = terrain;
		this.cutoff = cutoff;
	}

}
