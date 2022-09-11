package world;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import entities.Cell;
import entities.Dna;
import entities.Food;
import utils.Coordinate;
import utils.Utils;

public class World {
	
	public int winWidth = Utils.getScreenWidth();
	public int winHeight = Utils.getScreenHeight();
	private int year; //Current year
	
	public ArrayList<Food> food; //All the food
	public ArrayList<Cell> cells; //All the cells
	public ArrayList<Cell> cellsToAdd; //All the cells to add in the next update
	
	public boolean paintGui; //Tell if to draw the GUI or not
	
	//Default constructor
	public World() {
		year = 0;
		cells = new ArrayList<Cell>();
		food = new ArrayList<Food>();
		cellsToAdd = new ArrayList<Cell>();
		paintGui = true;
	}
	
	//Generate random cells and random food
	public void randomWorld() {
		Random rnd = new Random(System.currentTimeMillis());
		year = 0;
		cells = new ArrayList<Cell>();
		cellsToAdd = new ArrayList<Cell>();
		paintGui = true;
		
		for(int i = 0; i < 200; i++) {			
			
			int randX = rnd.nextInt(winWidth);
			int randY = rnd.nextInt(winHeight);
			
			Coordinate temp = new Coordinate(randX, randY);
			
			cells.add(new Cell(this, temp));
		
		}
		
		food = Food.generateFood(500, winWidth, winHeight);
	}
	
	public void paint(Graphics g) {
	
		int mem[] = new int[Dna.getDnaLength()]; //Array to get the most common DNA
		
		//Draw every food
		food.forEach((f) -> f.draw(g));
		
		//Draw every cell and calculate every DNA hashcode
		for(int i = 0; i < cells.size(); i++) {
			Cell c = cells.get(i);
			mem[c.getDna().getHashCode()]++;
			c.draw(g);
		}
		
		//Find the most common DNA
		int max = 0, best = 0;
		for(int i = 0; i < Dna.getDnaLength(); i++) {
			if(mem[i] > max) {
				max = mem[i];
				best = i;
			}
		}
		
		//Check if to and draw the GUI
		if(paintGui) paintGui(g, best);
		
	}

	//Update the world status
	public void update() {
		year++; //Increment the current year
		
		checkWhoDie(); 
		checkWhoBorn(); 
		
		cells.forEach((c) -> c.move(this)); //Move every cell
		
		//Check if some food has lost the targeted by cell
		food.forEach((food) -> {
			if(food.isTargetted() && !cells.contains(food.getTargettedBy())) {
				food.setTargetted(null);
			}
		});
		
		//Every 15 year add random food
		if(year % 15 == 0) addFood();
	}
	
	public void addCell(Cell toAdd) { cellsToAdd.add(toAdd); }
	
	private void addFood() {
		food.addAll(Food.generateFood(10, winWidth, winHeight));
	}
	
	//Draw the GUI
	private void paintGui(Graphics g, int best) {
		//System.out.println(String.valueOf(best & 7) + "  " + String.valueOf((best & 56) / 8) + "  " + String.valueOf((best & 448) / 64));		
		//Create most common DNA and cell
		Dna commonDna = new Dna(best & 7, (best & 56) / 8, (best & 448) / 64, 125, 125, 125, 1);
		Cell commonCell = new Cell(this, new Coordinate(64, 110), commonDna);
		
		Graphics2D g2 = (Graphics2D)g;
		
		g.setColor(new Color(230, 230, 230, 100));
		g.fillRect(4, 4, 150, 150);
		g.setColor(Color.WHITE);
		g2.drawString("Year: " + String.valueOf(year), 10, 20);
		g2.drawString("Population: " + String.valueOf(cells.size()), 10, 40);
		g2.drawString("Food: " + String.valueOf(food.size()), 10, 60);
		g2.drawString("Most common cell:", 10, 80);
		commonCell.drawBig(g);
	}
	
	//Check if some cell are not more alive
	private void checkWhoDie() {
		
		cells.forEach((cell) -> {
			if ( !cell.isAlive() ) {
				food.add(new Food(cell.getPosition()));
			}
		});
		
		cells.removeIf((cell) -> ( !cell.isAlive() ));
		
	}
	
	//Check if there's any cells in cellsToAdd array and empty that
	private void checkWhoBorn() {
		cellsToAdd.forEach((cell) -> cells.add(cell));
		cellsToAdd = new ArrayList<Cell>();
	}
	
	public String toString() {
		return "Cells: " + cells.size() + " Food: " + food.size();
	}
}
