package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tech;
import game.Tile;

import java.util.ArrayList;

import data.EntityData;
import data.Improvement;
import processing.core.PApplet;
import processing.core.PFont;
import render.Button;
import render.CivGame;
import render.Menu;
import render.Game.PFrame;
import render.TextBox;
import units.City;
import units.Settler;
import units.Warrior;
import units.Worker;

public class MenuSystem extends BaseSystem {

	public ArrayList<Menu> menus;
	public ArrayList<TextBox> textboxes;

	private ArrayList<Click> clicks;

	public boolean minimap, info, loadout, loadoutDisplay, techMenu, continueMenu; //Access the menu's active property instead
	public int multiplier = 1;

	public Tile target;
	//public ArrayList<String> hintText;
	public Tile highlighted; //Under the player's crosshair
	private BaseEntity selected; //Selected by the player with the mouse explicitly
	public Tile[] settlerChoices;
	public String typeOfLastSelected = "";
	//public City citySelected;

	//public TextBox hintTextBox;
	//public TextBox selectedTextBox;

	private ArrayList<String> messages;

	public MenuSystem(CivGame civGame) {
		super(civGame);
		menus = new ArrayList<Menu>();
		textboxes = new ArrayList<TextBox>();
		clicks = new ArrayList<Click>();

		//hintText = new ArrayList<String>();
		messages = new ArrayList<String>();
		//highlighted = null;

		Menu menu0 = new Menu("MainMenu");
		menus.add(menu0);
		menu0.addButton("exitgame", "Exit", "", 0, 0, 100, 30);
		menu0.addButton("minimap", "Minimap", "", 0, 100, 100, 30);
		menu0.addButton("info", "Information", "", 0, 130, 100, 30);
		menu0.addButton("loadout", "Loadout", "", 0, 160, 100, 30, 3, 4);
		menu0.addButton("stats", "Statistics", "", 0, 190, 100, 30);
		menu0.addButton("techs", "Techs", "", 0, 220, 100, 30);

		Menu menu1 = new Menu("UnitMenu");
		menus.add(menu1);

		Menu menu2 = new Menu("CityMenu");
		menus.add(menu2);

		Menu menu3 = new Menu("LoadoutMenu");
		menus.add(menu3);
		String[] names = EntityData.allUnitNames();
		for (int i = 0; i < names.length; i++)
		{
			menu3.addButton("loadoutDisplay" + names[i], names[i], "", 100, 160 + 30*i, 200, 30);
		}

		Menu menu4 = new Menu("LoadoutDisplay");
		menus.add(menu4);

		Menu menu5 = new Menu("TechMenu");
		menus.add(menu5);

		Menu menu6 = new Menu("ContinueMenu"); //Menu when player loses the game
		menu6.addButton("continue", "You have lost the game. Continue?", "", main.width*2/6, 100, main.width*2/6, 200);
		menus.add(menu6);

		menu0.active = true;

		TextBox text0 = new TextBox("HintText",new ArrayList<String>(),"",main.width*5/6,0,200,150);
		textboxes.add(text0);

		TextBox text1 = new TextBox("SelectedText",new ArrayList<String>(),"",main.width*4/6,0,200,150);
		textboxes.add(text1);

		TextBox text2 = new TextBox("Messages",new ArrayList<String>(),"",main.width*5/6,200,main.width*1/6,100);
		textboxes.add(text2);

		TextBox text3 = new TextBox("PlayerStatus",new ArrayList<String>(),"",main.width/6,0,300,50);
		textboxes.add(text3);

		TextBox text4 = new TextBox("LedgerText",new ArrayList<String>(),"",100,190,500,250);
		textboxes.add(text4);
		text4.active = false;

		//arial = main.loadFont("ArialMT-48.vlw");
	}

	public PFont arial;

	public boolean menuActivated = false;
	public void tick()
	{
		//main.textFont(arial);
		//main.resetShader();
		main.hint(PApplet.DISABLE_DEPTH_TEST);
		//main.textSize(20);
		//main.background(255,255,255,0);
		main.camera();
		main.perspective();
		main.resetShader();
		main.noLights();
		main.noStroke();
		main.textSize(12);

		if (minimap)
		{
			//main.rect(0, 700, 50, 50);
			int con = 1;
			float sX = 0; float sY = 400; float widthX = 400; float widthY = 400; 
			for (int r = 0; r < main.grid.rows; r += con)
			{
				for (int c = 0; c < main.grid.cols; c += con)
				{
					Tile t = main.grid.getTile(r,c);
					if (t.height >= main.cutoff)
					{
						if (t.owner != null)
						{
							main.fill(t.owner.r,t.owner.g,t.owner.b);
						}
						else if (t.occupants.size() > 0)
						{
							GameEntity en = t.occupants.get(0);
							main.fill(en.owner.r, en.owner.g, en.owner.b);
						}
						else
						{
							main.fill(150);
						}
					}
					else
					{
						main.fill(150,225,255);
					}
					//System.out.println(sX + r/(float)main.grid.rows*widthX);
					main.rect(sX + (main.grid.rows-r)/(float)main.grid.rows*widthX,sY + c/(float)main.grid.cols*widthY,widthX*con/main.grid.rows,widthY*con/main.grid.cols);
				}
			}
		}

		if (info)
		{
			main.fill(0);
			main.rect(100,130,200,100);
			main.fill(255);
			main.textAlign(PApplet.LEFT);
			main.text("Work in progress", 115, 150);
		}

		menus.get(3).active = loadout;
		menus.get(4).active = loadoutDisplay;
		menus.get(5).active = techMenu;
		menus.get(6).active = continueMenu;
		if (textboxes.get(4).active)
		{
			updateCivStats();
		}
		//menus.get(6).active = ledgerMenu;
		//System.out.println(loadout + " " + loadoutDisplay);

		//Render the cursor
		int width = 6;
		main.stroke(255);
		main.fill(0);
		main.rect((main.width - width)/2, (main.height - width)/2, width, width);

		main.noStroke();

		ArrayList<String> hintText = textboxes.get(0).display;
		//hintText.clear();
		if (target != null)
		{
			hintText.add(target.row + " " + target.col);
			if (target.owner != null)
				hintText.add("Owner: " + target.owner.name);
			else
				hintText.add("Terra nullius");

			if (target.biome >= 4 && target.biome <= 6)
				if (target.forest)
					hintText.add(EntityData.getBiome(target.biome) + " (forested)");
				else
					hintText.add(EntityData.getBiome(target.biome) + " (unforested)");
			else
				hintText.add(EntityData.getBiome(target.biome));

			if (target.shape == 1)
			{
				hintText.add("Hill");
			}
			else if (target.shape == 2)
			{
				hintText.add("Mountain");
			}

			if (target.improvement != null)
			{
				hintText.add(target.improvement.name);
				hintText.add(target.improvement.id);
			}
			else
				hintText.add("Pristine");

			if (target.city != null)
			{
				if (target.city.owner != null)
				{
					double[] data = target.city.evaluate(target, null);
					hintText.add((int)data[0] + " F, " + (int)data[1] + " G, " + (int)data[2] + " M, " + (int)data[3] + " R");
				}
			}
			//Same check as above, really
			if (target.owner != null)
			{
				hintText.add("Relations: " + target.owner.opinions[0]);
			}

			if (main.grid.irrigated(target.row, target.col))
				hintText.add("Fresh Water");

			if (highlighted != null)
				if (highlighted.occupants.size() > 0)
				{
					String stringy = "";
					for (int i = 0; i < highlighted.occupants.size(); i++)
					{
						stringy += highlighted.occupants.get(i).name + "; ";
					}
					if (!stringy.equals(""))
						hintText.add(stringy.substring(0,stringy.length()-2));
				}
		}
		if (selected != null)
		{
			if (selected.owner != null && !(selected instanceof City))
			{
				//main.stroke(255);
				main.fill(0);
				//main.rect(main.width*4/6,0,200,150);
				main.fill(255);
				main.textSize(12);

				ArrayList<String> temp = textboxes.get(1).display;
				//temp.clear();
				temp.add(selected.name + " " + ((GameEntity)selected).action + "/" + ((GameEntity)selected).maxAction);
				temp.add(selected.offensiveStr + " offensive, " + selected.rangedStr + " ranged");
				temp.add(selected.defensiveStr + " defensive");

				if (!typeOfLastSelected.equals(selected.name))
				{
					updateUnitMenu(selected.name);
				}
				menus.get(1).active = true;
				//main.text("Test", main.width*5/6 + 15, main.height*5/6 + 15);
			}
			else
			{
				menus.get(1).active = false;
			}
		}
		else
		{
			menus.get(1).active = false;
		}

		menus.get(2).active = false;

		if (selected != null)
		{
			if (selected.owner != null)
				if (selected.owner.equals(main.grid.civs[0]) && selected instanceof City)
				{
					City citySelected = (City)selected; //to work with old code
					displayCity(citySelected);
				}
		}
		else if (highlighted != null)
		{
			if (highlighted.improvement != null)
				if (highlighted.improvement instanceof City)
				{
					City citySelected = (City)highlighted.improvement;
					displayCity(citySelected);
				}
		}

		if (messages.size() > 0)
		{
			int len = Math.min(4,messages.size());
			for (int i = 0; i < len; i++)
			{
				textboxes.get(2).display.add(messages.get(i));
			}
		}

		main.noStroke();
		Civilization c = main.grid.civs[0];
		textboxes.get(3).display.add(c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Metal: " + c.metal + "; Research: " + c.research);
		if (c.researchTech == null)
			textboxes.get(3).display.add("No research");
		else
			textboxes.get(3).display.add("Researching " + c.researchTech + " at " + c.researchProgress()*1000/1000);

		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (menus.get(menu).active)
			{
				//System.out.println(menu + " " + menus.get(menu).active);
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					main.fill(0);
					Button b = menus.get(menu).buttons.get(i);
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
					main.textAlign(PApplet.CENTER, PApplet.CENTER);
					main.fill(255);
					for (int j = 0; j < b.display.size(); j++)
						main.text(b.display.get(j), b.posX + b.sizeX/2, b.posY + b.sizeY/2 + j*15);
				}
			}
		}
		for (int i = 0; i < textboxes.size(); i++)
		{
			TextBox b = textboxes.get(i);
			if (b.active)
			{
				main.fill(0);
				main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
				main.textAlign(PApplet.LEFT, PApplet.UP);
				main.fill(255);
				for (int j = 0; j < b.display.size(); j++)
				{
					if (b.display.get(j) != null)
					{
						//System.out.println(b.text + " " + b.text.get(j) + " " + b.posX);
						main.text(b.display.get(j), b.posX + 15, b.posY + 15*(j+1));
					}
				}
				b.display.clear(); //Clear them to be refilled next frame
			}
		}
		main.hint(PApplet.ENABLE_DEPTH_TEST);

		menuActivated = false;
		for (int menu = 0; menu < menus.size(); menu++)
		{
			if (menus.get(menu).active)
			{
				for (int i = clicks.size() - 1; i >= 0; i--)
				{
					if (clicks.get(i).click)
					{
						String command = menus.get(menu).click(clicks.get(i).mouseX, clicks.get(i).mouseY);
						if (command != null && !command.equals(""))
						{
							menuActivated = true;
							if (command.equals("exitgame"))
							{
								System.exit(0);
								continue;
							}
							else if (
									command.equals("info") || 
									command.equals("minimap") || 
									command.equals("loadout") || 
									command.equals("loadoutDisplay") || 
									command.equals("stats") ||
									command.equals("continue") ||
									command.equals("techs")
									)
							{
								info = false;
								minimap = false;
								loadoutDisplay = false;
								loadout = false;
								textboxes.get(4).active = false;
								menus.get(5).active = false;
								if (command.equals("info"))
								{
									info = !info;
								}
								else if (command.equals("minimap"))
								{
									minimap = !minimap;
								}
								else if (command.equals("loadout"))
								{
									if (loadoutDisplay)
									{
										loadoutDisplay = false;
									}
									loadout = !loadout;
								}
								else if (command.contains("loadoutDisplay"))
								{
									//loadout = false;
									updateLoadoutDisplay(command.substring(14));
									loadoutDisplay = true;
								}
								else if (command.equals("stats"))
								{
									updateCivStats();
									//ledgerMenu = true;
									menus.get(0).findButtonByCommand("stats").lock = !textboxes.get(4).active;
									textboxes.get(4).active = !textboxes.get(4).active;
								}
								else if (command.equals("continue"))
								{
									main.grid.civs[0].observe = true;
									continueMenu = false;
								}
								else if (command.equals("techs"))
								{
									displayTechMenu(main.grid.civs[0]);
									techMenu = !techMenu;
									//menus.get(5).active = !menus.get(5).active;
								}
								resetAllButtons();
								continue;
							}

							else if (command.contains("/")) //if it is a entity-improvement command
							{
								int index = command.indexOf("/");
								String unit = command.substring(0,index);
								for (int j = 0; j < main.grid.civs[0].cities.size(); j++)
								{
									City city = main.grid.civs[0].cities.get(j);
									if (city.queue != null)
									{
										if (city.queue.equals(unit))
										{
											message("Cannot change production method of queued unit");
											return;
										}
									}
								}
								message("Changed production method of " + unit);
								main.grid.civs[0].unitImprovements.put(unit,EntityData.unitImprovementMap.get(command.substring(index+1)));
								loadoutDisplay = false; //Allow player to stay in menu?
								continue;
							}

							else if (command.equals("buildFarm"))
							{
								//Recycled code
								BaseEntity en = selected;
								if (en.location.resource == 1 || en.location.resource == 2)
								{
									en.queueTurns = 6;
									en.queue = "Farm";
								}
								else if (en.location.biome >= 3 && en.location.biome <= 6 && en.location.grid.irrigated(en.location.row, en.location.col))
								{
									en.queueTurns = 6;
									en.queue = "Farm";
								}
								en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
							}
							else if (command.equals("buildMine"))
							{
								BaseEntity en = selected;
								if (en.location.shape == 2)
								{
									en.queueTurns = 6;
									en.queue = "Mine";
								}
								else if (en.location.resource >= 20 && en.location.resource <= 22)
								{
									en.queueTurns = 6;
									en.queue = "Mine";
								}
								else if (en.location.shape == 1)
								{
									if (en.location.biome >= 0 && en.location.biome <= 3)
									{
										en.queueTurns = 6;
										en.queue = "Mine";
									}
								}
								en.queueTurns = Math.max(1,(int)(en.queueTurns*((Worker)en).workTime));
							}
							else if (command.equals("kill"))
							{
								main.grid.removeUnit(selected);
							}
							else if (command.equals("raze"))
							{
								((Warrior)selected).raze();
								((Warrior)selected).action = 0;
							}
							else if (command.equals("settle"))
							{
								((Settler)selected).settle();
							}

							else if (command.contains("queueBuilding"))
							{
								City city = ((City)selected);
								String impr = command.substring(13);
								//No need to check if the player's tech is appropriate
								System.out.println(impr);
								if (EntityData.queueCityImprovement(city,impr))
								{
									message("Succesfully queued " + impr);
								}
								else
								{
									message("Could not queue " + impr);
								}
							}
							else if (command.contains("queue"))
							{
								//if (EntityData.queue((City)selected, command.substring(5)))
								if (EntityData.queue((City)selected, command.substring(5)) != null)
								{
									message("Succesfully queued " + command.substring(5));
								}
								else
								{
									message("Cannot queue units in a city being recently captured or razed");
								}
							}
							else if (command.equals("razeCity"))
							{
								((City)selected).raze = true;
							}
							/*else if (command.equals("queueSettler"))
						{
							((City)selected).queue = "Settler";
							((City)selected).queueFood = 35;
						}
						else if (command.equals("queueWarrior"))
						{
							((City)selected).queue = "Warrior";
							((City)selected).queueFood = 5;
							((City)selected).queueMetal = 5;
						}
						else if (command.equals("queueWorker"))
						{
							((City)selected).queue = "Worker";
							((City)selected).queueFood = 25;
						}*/
							//Researching tech commands
							else if (command.contains("research"))
							{
								//Tech t = main.grid.civs[0].techTree.researched(command.substring(8));
								main.grid.civs[0].researchTech = command.substring(8);
								techMenu = false;
							}
							//The six commands below check to see if the number of idle people is more than the requested number of specialized workers 					
							else if (command.equals("addAdmin"))
							{
								City s = ((City)selected);
								if (s.adm + s.art + s.sci + 1 <= s.population - 1)
									s.adm++;
							}
							else if (command.equals("addArtist"))
							{
								City s = ((City)selected);
								if (s.adm + s.art + s.sci + 1 <= s.population - 1)
									s.art++;
							}
							else if (command.equals("addSci"))
							{
								City s = ((City)selected);
								if (s.adm + s.art + s.sci + 1 <= s.population - 1)
									s.sci++;
							}
							else if (command.equals("subAdmin"))
							{
								City s = ((City)selected);
								if (s.adm > 0)
									s.adm--;
							}
							else if (command.equals("subArtist"))
							{
								City s = ((City)selected);
								if (s.art > 0)
									s.art--;
							}
							else if (command.equals("subSci"))
							{
								City s = ((City)selected);
								if (s.sci > 0)
									s.sci--;
							}
							else
							{
								System.out.println("Invalid or non-functioning command: " + command);
							}
							main.menuSystem.select(null);
							//below was derived from the original expression to calculate rotY & rotVertical
							//main.centerX = main.mouseX/(1 - main.player.rotY/(float)Math.PI);
							//main.centerY = main.mouseY/(1 + 4*main.player.rotVertical/(float)Math.PI);
							main.resetCamera();
						}
					}
					else
					{
						if (menu == 0 || menu == 5)
						{	
							boolean[] activeMenus = new boolean[menus.size()];
							for (int j = 0; j < activeMenus.length; j++)
							{
								activeMenus[j] = menus.get(j).active;
							}
							menus.get(menu).pass(activeMenus, clicks.get(i).mouseX, clicks.get(i).mouseY);
						}
					}
				}
				menus.get(menu).origPosIfNoMouse();
				/*for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					main.fill(0);
					Button b = menus.get(menu).buttons.get(i);
					main.rect(b.posX, b.posY, b.sizeX, b.sizeY);
					main.textAlign(PApplet.CENTER, PApplet.CENTER);
					main.fill(255);
					main.text(b.display, b.posX + b.sizeX/2, b.posY + b.sizeY/2);
				}
				 */
				for (int i = 0; i < menus.get(menu).buttons.size(); i++)
				{
					Button b = menus.get(menu).buttons.get(i);
					b.tick();
				}
				for (int i = 0; i < textboxes.size(); i++)
				{
					textboxes.get(i).tick();
				}
			}
		}
		clicks.clear();
	}

	public class Click {float mouseX, mouseY; boolean click; Click(boolean click, float x, float y) {this.click = click; mouseX = x; mouseY = y;}}
	public void queueClick(float mouseX, float mouseY)
	{
		clicks.add(0, new Click(true,mouseX, mouseY));
	}

	public void queueMousePass(float mouseX, float mouseY)
	{
		clicks.add(0, new Click(false,mouseX, mouseY));
	}

	//Send a message, checking for repeats
	public void message(String... newMessages)
	{
		for (int i = 0; i < newMessages.length; i++)
		{
			String message = newMessages[i];
			if (messages.size() == 0) messages.add(message);
			if (!messages.get(0).equals(message))
				messages.add(0,message);
		}
		if (!main.grid.civs[0].observe) //Do not shake the GUI if player is not alive
		{
			textboxes.get(2).moveDis(0,-5,2);
			for (int i = 0; i < 10; i++)
				textboxes.get(2).moveDis(0,(10-i)*(int)Math.pow(-1,i),2);
			textboxes.get(2).moveDis(0,5,2);
			textboxes.get(2).orderOriginal(false);
		}
	}

	public void resetAllButtons()
	{
		for (int i = 0; i < menus.size(); i++)
		{
			for (int j = 0; j < menus.get(i).buttons.size(); j++)
			{
				menus.get(i).buttons.get(j).orderOriginal(true);
			}
		}
	}

	//Will always refer to the player's tech tree
	public void displayTechMenu(Civilization civ)
	{
		menus.get(5).active = true;
		menus.get(5).buttons.clear();

		ArrayList<String> techNames = civ.techTree.findCandidates();
		for (int i = 0; i < techNames.size(); i++)
		{
			String s = techNames.get(i);
			menus.get(5).addButton("research" + s, s, "", main.width/3F, (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	public void displayCity(City citySelected)
	{
		//Selection vs highlight
		if (citySelected.equals(selected))
		{
			menus.get(2).active = true;
		}

		ArrayList<String> temp = textboxes.get(1).display;
		temp.add(citySelected.name + "; Population: " + citySelected.population);
		if (citySelected.takeover > 0)
		{
			main.fill(255,0,0);
			if (citySelected.takeover == 1)
				temp.add("IN RESISTANCE FOR 1 TURN.");
			else
				temp.add("IN RESISTANCE FOR " + citySelected.takeover + " TURNS.");
			main.fill(255);
		}
		temp.add("Health: " + citySelected.health + ", Happiness: " + citySelected.happiness);
		temp.add("Culture: " + citySelected.culture);
		temp.add("Administrators: " + citySelected.adm + ", Artists: " + citySelected.art);
		temp.add("Scientists: " + citySelected.sci);
		String buildingString = "";
		if (citySelected.buildings.size() > 0)
		{
			for (int i = 0; i < citySelected.buildings.size(); i++)
				buildingString += citySelected.buildings.get(i).name + ", ";
		}
		else
		{
			buildingString = "No buildings.";
		}
		buildingString = buildingString.substring(0,buildingString.length()-2); //Remove a trailing comma
		temp.add(buildingString);
		if (citySelected.queueFood > 0 || citySelected.queueMetal > 0)
		{
			int[] t = citySelected.quickEval();
			//Division by zero errors
			if (t[0] == 0 && citySelected.queueFood > 0)
			{
				temp.add("No food production, will not finish.");
			}
			else if (t[2] == 0 && citySelected.queueMetal > 0)
			{
				temp.add("No metal production, will not finish.");
			}
			else if (t[0] == 0 && t[2] == 0)
			{
				temp.add("Neither food nor metal production");
				temp.add("will not finish.");
			}
			else
			{
				//System.out.println(t[0] + " " + t[2]);
				int turns;
				if (t[0] == 0)
				{
					turns = citySelected.queueMetal/(t[2]) + 1;
				}
				else if (t[2] == 0)
				{
					turns = citySelected.queueFood/(t[0]) + 1;
				}
				else
				{
					turns = Math.max(
							citySelected.queueFood/(t[0]) + 1,
							citySelected.queueMetal/(t[2]) + 1
							);
				}
				//English grammar...
				if (turns == 1)
					temp.add("Queued " + citySelected.queue + " for " + turns + " turn.");
				else
					temp.add("Queued " + citySelected.queue + " for " + turns + " turns.");
			}
		}
		else
		{
			temp.add("Nothing queued.");
		}
	}

	//Update the ledger
	public void updateCivStats()
	{
		textboxes.get(4).display.clear();
		textboxes.get(4).display.add("You:");
		Civilization c = main.grid.civs[0];
		String s = c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Metal: " + c.metal + "; Research: " + c.research;
		textboxes.get(4).display.add(s);
		textboxes.get(4).display.add("");
		
		textboxes.get(4).display.add("Civilizations:");
		for (int i = 1; i < main.grid.civs.length; i++)
		{
			c = main.grid.civs[i];
			s = c.name + "; Food: " + c.food + "; Gold: " + c.gold + "; Metal: " + c.metal + "; Research: " + c.research + "; Relations: " + main.grid.civs[0].opinions[i];
			textboxes.get(4).display.add(s);
		}
	}

	//Choose which buttons to show depending on unit (e.g. only settler can settle)
	public void updateUnitMenu(String name)
	{
		menus.get(1).buttons.clear();
		menus.get(1).addButton("kill", "Destroy", "", (float)main.width/3F, (float)main.height*5F/6F, 50, 50);
		if (name.equals("Settler"))
		{
			menus.get(1).addButton("settle", "Settle", "", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (name.equals("Warrior"))
		{
			menus.get(1).addButton("raze", "Attack", "", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		}
		else if (name.equals("Worker"))
		{
			ArrayList<String> units = main.grid.civs[0].techTree.allowedTileImprovements;
			for (int i = 0; i < units.size(); i++)
			{
				menus.get(1).addButton("build"+units.get(i), units.get(i), "", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
			}
			//menus.get(1).addButton("buildfarm", "Farm", (float)main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
			//menus.get(1).addButton("buildmine", "Mine", (float)main.width/3F + 120, (float)main.height*5F/6F, 50, 50);
		}
		//System.out.println(menus.get(1).buttons.size());
	}

	//Choose which builds to allow i.e. which can be queued up in the city (factor in techs later)
	public void updateCity(City c)
	{
		menus.get(2).buttons.clear();

		if (c.takeover > 0)
		{
			menus.get(2).addButton("razeCity", "Raze", "", main.width/3F, (float)main.height*5F/6F + 60, 50, 50);
		}

		ArrayList<String> units = c.owner.techTree.allowedUnits;
		for (int i = 0; i < units.size(); i++)
		{
			menus.get(2).addButton("queue" + units.get(i), units.get(i), "", main.width/3F + 60*i, (float)main.height*5F/6F, 50, 50);
		}

		ArrayList<String> buildings = c.owner.techTree.allowedCityImprovements;
		for (int i = 0; i < buildings.size(); i++)
		{
			menus.get(2).addButton("queueBuilding" + buildings.get(i), buildings.get(i), "", main.width/3F + 60*i, (float)main.height*5F/6F + 60, 50, 50);
		}
		//menus.get(2).addButton("queueSettler", "Settler", main.width/3F, (float)main.height*5F/6F, 50, 50);
		//menus.get(2).addButton("queueWorker", "Worker", main.width/3F + 60, (float)main.height*5F/6F, 50, 50);
		//menus.get(2).addButton("queueWarrior", "Warrior", main.width/3F + 120, (float)main.height*5F/6F, 50, 50);

		menus.get(2).addButton("addAdmin", "Admin+", "", main.width/6F, (float)main.height*5F/6F, 50, 50);
		menus.get(2).addButton("subAdmin", "Admin-", "", main.width/6F, (float)main.height*5F/6F + 60, 50, 50);
		menus.get(2).addButton("addArtist", "Artist+", "", main.width/6F + 60, (float)main.height*5F/6F, 50, 50);
		menus.get(2).addButton("subArtist", "Artist-", "", main.width/6F + 60, (float)main.height*5F/6F + 60, 50, 50);
		menus.get(2).addButton("addSci", "Sci+", "", main.width/6F + 120, (float)main.height*5F/6F, 50, 50);
		menus.get(2).addButton("subSci", "Sci-", "", main.width/6F + 120, (float)main.height*5F/6F + 60, 50, 50);
	}

	public void updateLoadoutDisplay(String name)
	{
		menus.get(4).buttons.clear();
		BaseEntity en = EntityData.get(name);
		ArrayList<Improvement> valid = EntityData.getValidImprovements(en);
		for (int i = 0; i < valid.size(); i++)
		{
			Improvement temp = valid.get(i);
			menus.get(4).addButton(en.name + "/" + temp.name, temp.name, "", main.width/3F, (float)main.height*2F/6F + 60*i, 200, 50);
		}
	}

	//Encapsulation for selected
	public BaseEntity getSelected()
	{
		return selected;
	}

	public void select(BaseEntity en)
	{
		selected = en;
		if (en != null)
		{
			if (en instanceof Settler)
			{
				settlerChoices = main.grid.returnBestCityScores(en.location.row, en.location.col);
			}
			else
			{
				settlerChoices = null;
			}
			if (en instanceof City)
			{
				updateCity((City)en);
			}
			textboxes.get(1).active = true;
			textboxes.get(1).move(main.width*4/6,-150);
			textboxes.get(1).moveTo(main.width*4/6,0,20);
		}
		else
		{
			textboxes.get(1).active = false;
			textboxes.get(1).move(main.width*4/6,-150);
		}
	}


}
