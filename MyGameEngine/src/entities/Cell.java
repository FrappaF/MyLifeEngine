package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import arms.DamageArm;
import arms.EatArm;
import arms.ReproduceArm;
import utils.Coordinate;
import utils.Utils;
import world.World;

//Cell class
public class Cell extends Entity{
	
	//Used to paint the cell ant its arms
	private final int squareLength = Utils.getSquareDimension();
	
	//Color for painting the arms
	private final Color EAT_COLOR = Color.GREEN, DAMAGE_COLOR = Color.RED, REPRODUCE_COLOR = Color.BLUE;
	
	
	private Entity target; //The current target the cell is moving to
	private Dna dna; //Cell DNA
	private float hungry; //How hungry the cell is
	private Cell partner; //the current partner of the cell
	
	public int epochs; //the 'age' of the cell
	public World world; //The world the cell live
	
	//Default constructor
	public Cell(World world, Coordinate initialPosition, Cell father, Cell mother, Dna dna) {
		position = initialPosition;
		epochs = 0;
		hungry = 3.0f;
		target = null;
		if (mother != null && father != null) this.dna = new Dna(father, mother);
		else if(dna != null) this.dna = dna;
		else this.dna = new Dna();
		live = true;
		partner = null;
		targettedBy= null;
		this.world = world;
	}
	
	//Initialize the cell without a father and a mother (Used to create initial cells)
	public Cell(World world, Coordinate initialPosition) {
		this(world, initialPosition, null, null, null);
	}
	
	//Initialize the cell with a given DNA
	public Cell(World world, Coordinate initialPosition, Dna dna) {
		this(world, initialPosition, null, null, dna);
	}
	
	//Draw the cell
	public void draw(Graphics g) {
		 //Kernel square
		 g.setColor(new Color(dna.bias.awarness, dna.bias.food, dna.bias.reproduce));
		 g.fillRect(position.x, position.y, squareLength, squareLength);
		
		 for(int i = 0; i < 4; i++) {
			 drawArm(g, i);
		 }
		 
		 Graphics2D g2 = (Graphics2D)g;
		 g2.setColor(new Color(234, 234, 234));
		
		
		 if(isTargetted()) {
			 //If it's targetted by another cell
			 g2.drawString("X", position.x - 2, position.y - 10);
			 
		 } else if(partner != null) {
			 //If it's going to reproduce
			 g2.drawString("<3", position.x - 6, position.y - 10);
		 }
	}
	
	//Draw the cell bigger (Used to draw most common cell in the GUI)
	public void drawBig(Graphics g) {
	
		 //Kernel square
		 g.setColor(new Color(dna.bias.awarness, dna.bias.food, dna.bias.reproduce));
		 g.fillRect(position.x, position.y, squareLength * 3, squareLength * 3);
		
		 for(int i = 0; i < 4; i++) {
			 drawBigArm(g, i);
		 }	 
	}
	
	//Move the cell in the given world
	public void move(World world) {
		//System.out.println("------------------------@---------------------");
		decrementHungry(); //Decrement the hungry of the cell every time it moves
		happyBirthday(); //Increment epochs
		
		//If the targettedBy cell is not more in the world 
		if ( !world.cells.contains(getTargettedBy()) ) setTargetted(null); 
		
		//If the partner is not more in the world
		if ( !world.cells.contains(partner) ) partner = null;
		
		//If the cell or the partner cannot reproduce
		if ( partner != null && (hungry < 2.5f || dna.fertility == 0) ) { partner.setPartner(null); partner = null; }
		
		//If both the cell and the partner can reproduce
		if( partner != null && dna.fertility > 0 && partner.isAlive() && world.cells.contains(partner)) {
			tryToReproduce(world);
			return;
		}
		
		//if(target != null) System.out.println("DISTANCE TO TARGET: " + Utils.euclideanDistance(target.getPosition(), position));
		
		//If the target is not more in the world
		if( (!world.food.contains(target) && !world.cells.contains(target)) && !(target instanceof Point) ) target = null;
		
		//If the cell has not a target find it
		if (target == null || Utils.euclideanDistance(target.getPosition(), position) > dna.bias.getMaxRange()) {
			if(target != null) target.setTargetted(null);
			target = findNearestEntity(world);
			
		} else {
			float dist = Utils.euclideanDistance(target.getPosition(), position);
			
			//If the cell is near the target
			if (dist < Utils.getSquareDimension()) {
				//System.out.println("NEAR THE TARGET");
				nearTarget(world);
				
			}
			else {
				Entity nearest = findNearestEntity(world);
				
				//If the target too far from the cell and there is another possible target nearest
				if (nearest != null && dist > Utils.euclideanDistance(nearest.getPosition(), position)) {
					target.setTargetted(null);
					target = nearest;
				}
			}
			//return;
		}
	
		//if the cell does not find any possible target
		if (target == null) {
			//Set the target to a random Point
			target = lookFor();
		}
		
		target.setTargetted(this);
		moveToTarget(target);
		
		//Check if the cell has to die
		checkIfDie();
		
	}

	//Every time a cell reproduce decrement hungry and fertility
	private void giveBirth() {
		hungry -= 1f;
		dna.fertility -= 1;
	}
	
	//Set the partner of the cell
	private void setPartner(Cell other) {
		//System.out.println("SET PARTNER TO " + other);
		partner = other;
	}
	
	//Delete partner of the cell
	private void deletePartner() {
		partner = null;
	}
	
	//Decrement hungry
	private void decrementHungry() {
		hungry -= .03f;
	}
	
	private void tryToReproduce(World world) {
		//If it's near the partner
		if (Utils.euclideanDistance(partner.getPosition(), position) < 6f) {
			//Create a new cell
			createNextGen(world);
			
			partner.giveBirth();
			giveBirth();
			
			//Set the partners of the two cells to null
			partner.deletePartner();
			partner = null;
			
			
		}
		else moveToTarget(partner); //Else move to
	}
	
	//Create a new cell with parents
	private void createNextGen(World world) {
		Random rng = new Random();
		Cell newCell  = new Cell(world, new Coordinate((position.x + rng.nextInt(20) - 10) % world.winWidth, (position.y + rng.nextInt(20) - 10) % world.winHeight), this, partner, null);
		
		world.cellsToAdd.add(newCell);
	}
	
	//Action when the cell is near the target
	private void nearTarget(World world) {
		//If it's a food
		if (target instanceof Food) { 
			world.food.remove(target); 
			eatFood(); 
			
		}
		//If it's a cell
		else if (target instanceof Cell) { 
			//If the target is stronger than the cell the cell die
			if(((Cell) target).getTotalDamage() > getTotalDamage()) {
				
				die();
			} else { //Else the target dies and the cell eat it
				target.die();
				eatCell();
			}
		}
		else target = null; //If it is a Point do nothing
	}
	
	//Return a random Point in the max range of the cell
	private Point lookFor() {
		
		int max = (int) dna.bias.getMaxRange();
		
		int randX = (int) (new Random().nextInt( 2 * max )) - max;
		int randY = (int) (new Random().nextInt( 2 * max )) - max;
		
		//Check if out of bounds
		if(position.x + randX > world.winWidth || position.x + randX < 0) randX = -randX;
		if(position.y + randY > world.winHeight || position.y + randY < 0) randY = -randY;

		return new Point(position.x + randX, position.y + randY);
	}
	
	//Move the cell closer to the target
	private void moveToTarget(Entity target) {
		
		if (target.getPosition().x < position.x - 1) {
			if( position.x - dna.speed > 0 );
			position.x -= dna.speed;
			
		} else if (target.getPosition().x > position.x + 1) {
			if( position.x + dna.speed < world.winWidth );
			position.x += dna.speed;
		}
		
		if(target.getPosition().y < position.y - 1) {
			if( position.y - dna.speed > 0 );
			position.y -= dna.speed;
			
		} else if (target.getPosition().y > position.y + 1) {
			if( position.y + dna.speed < world.winHeight );
			position.y += dna.speed;
			
		}
	}
	
	private void happyBirthday() {
		epochs += 1;
	}

	private void eat() {
		target.setTargetted(null);
		target = null;
	}
	
	private void eatFood() {
		eat();
		hungry += 1f * dna.getNumberOfEatArm() + 1f;
	}
	
	private void eatCell() {
		eat();
		hungry += 2f * dna.getNumberOfDamageArm() + 1f;
	}
	
	private void checkIfDie() {
		if(hungry < .1f || epochs > 2000) die();
	}
	
	//The total damage of a cell is the number of damage arms it has
	private int getTotalDamage() {
		int res = 0;
		
		for(int i = 0; i < dna.arms.length; i++) {
			if (dna.arms[i] instanceof DamageArm) res ++;
		}
		
		return res;
	}
	
	//Return true if the cell can reproduce, false otherwise
	private boolean canReproduce() { return hungry > 5 && partner == null && epochs > 150 && dna.fertility > 0;}
	
	private Entity findNearestEntity(World world) {
		float best = 0xFFFFFF;
		Entity res = null;
		
		
		for(int i = 0; i < world.cells.size(); i++) {
			Cell c = world.cells.get(i);
			if(!this.equals(c)) { //If it's not the same cell
				float dist = Utils.euclideanDistance(position, c.getPosition());
				if ( !c.isTargetted()) { //If the cell is not targetted by another cell
					
					//If the cell is evil enough to eat another cell
					if( dist < (dna.bias.getAwarnessRange()) && dist < best && hungry < dna.evilness && hungry <= 10f) {
						best = dist;
						res = c;
					} else if( canReproduce() && c.canReproduce() && dist < (dna.bias.getReproduceRange()) ) { //If it can be a partner
					
						partner = c;
						c.setPartner(this);
					}
			
				}	
			}
		}
		
		//Look for food
		best = 0xFFFFFF;
		for(int i = 0; i < world.food.size(); i++) {
			Food f = world.food.get(i);
			float dist = Utils.euclideanDistance(position, f.getPosition());
			
			if( dist < (dna.bias.getFoodRange()) && dist < best  && !f.isTargetted() && hungry <= 10f) {
				best = dist;
				res = f;
			}
			
		}
		
		return res;
	}
	
	//Draw the 4 arms of the cell
	private void drawArm(Graphics g, int whichArm) {
		
			if(this.dna.arms[whichArm] instanceof DamageArm) g.setColor(DAMAGE_COLOR);
			else if(this.dna.arms[whichArm] instanceof EatArm) g.setColor(EAT_COLOR);
			else if(this.dna.arms[whichArm] instanceof ReproduceArm) g.setColor(REPRODUCE_COLOR);
			
			switch(whichArm) {
			case 0:
				g.fillRect(position.x - squareLength, position.y - squareLength, squareLength, squareLength);
				break;
			case 1:
				g.fillRect(position.x + squareLength, position.y - squareLength, squareLength, squareLength);
				 break;
			case 2:
				g.fillRect(position.x - squareLength, position.y + squareLength, squareLength, squareLength);
				break;
			case 3:
				g.fillRect(position.x + squareLength, position.y + squareLength, squareLength, squareLength);
				break;
			default:
				break;
			}
			
	}
	
	//Draw the 4 arms of the cell bigger
	private void drawBigArm(Graphics g, int whichArm) {
		
		if(this.dna.arms[whichArm] instanceof DamageArm) g.setColor(DAMAGE_COLOR);
		else if(this.dna.arms[whichArm] instanceof EatArm) g.setColor(EAT_COLOR);
		else if(this.dna.arms[whichArm] instanceof ReproduceArm) g.setColor(REPRODUCE_COLOR);
		
		switch(whichArm) {
		case 0:
			g.fillRect(position.x - squareLength * 3, position.y - squareLength * 3, squareLength * 3, squareLength * 3);
			break;
		case 1:
			g.fillRect(position.x + squareLength * 3, position.y - squareLength * 3, squareLength * 3, squareLength * 3);
			 break;
		case 2:
			g.fillRect(position.x - squareLength * 3, position.y + squareLength * 3, squareLength * 3, squareLength * 3);
			break;
		case 3:
			g.fillRect(position.x + squareLength * 3, position.y + squareLength * 3, squareLength * 3, squareLength * 3);
			break;
		default:
			break;
		}
		
}
	
	public Dna getDna() {
		return dna;
	}

	public float getHungry() { return hungry; }

	public boolean equals(Cell other) {
		return dna.equals(other.dna);
	}
	
}
