package render;

import java.util.ArrayList;

public class TextBox extends Button {
	
	public String name;
	//public float posX, posY;
	//public float sizeX, sizeY;
	public boolean active = true;
	
	public TextBox(String name, ArrayList<String> display, float a, float b,
			float c, float d) {
		super("", display, a, b, c, d);
		this.name = name;
	}
	
	/*public TextBox(String name, ArrayList<String> text, float x, float y, float sX, float sY)
	{
		super("", text, x, y, sX, sY);
		this.name = name;
		text = new ArrayList<String>();
		posX = x; posY = y;
		sizeX = sX; sizeY = sY;
	}*/
	
}
