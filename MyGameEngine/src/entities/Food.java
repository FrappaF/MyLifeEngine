package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import utils.Coordinate;
import utils.Utils;

//Food entity
public class Food extends Entity {

	private static final int size = Utils.getSquareDimension();
	
	public Food(int x, int y) {
		position = new Coordinate(x, y);
		live = true;
		targettedBy = null;
	}
	
	public Food(Coordinate c) {
		this(c.x, c.y);
	}
	
	//Function to generate random food
	public static ArrayList<Food> generateFood(int howMany, int maxWidth, int maxHeight) {
		Random rnd = new Random();
		ArrayList<Food> temp = new ArrayList<Food>();
		for(int i = 0; i < howMany; i++) {
			int tempX = rnd.nextInt(maxWidth);
			int tempY = rnd.nextInt(maxHeight);
			
			Food food = new Food(tempX, tempY);
			temp.add(food);
		}
		
		return temp;
	}
	
	//Draw the food
	public void draw(Graphics g) {
		g.setColor(new Color(0.35f, 1.0f, 0.5f));
		g.fillRect(position.x, position.y, size, size);
	}
	
}
