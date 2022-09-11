package entities;

import java.util.Random;

import arms.Arm;
import arms.DamageArm;
import arms.EatArm;
import arms.ReproduceArm;


//Contains all the information for the cell
public class Dna {
	
	public Arm arms[]; //The 4 arms of the cell
	public Bias bias; //The Bias
	public int speed; //Velocity
	
	int fertility; //How many cells it can produce
	float evilness; 
	
	//Default constructor
	public Dna(Arm tl, Arm tr, Arm bl, Arm br, int f, int r, int a, int speed) {
		arms = new Arm[4];
		arms[0] = tl;
		arms[1] = tr;
		arms[2] = bl;
		arms[3] = br;
		
		bias = new Bias(f, r ,a);
		
		this.speed = speed;
		this.evilness = calcEvilness();
		this.fertility = calcFertility();
	}
	
	//Initialize the DNA with a number of the specific arms it has
	public Dna(int n_of_damage, int n_of_reproduce, int n_of_eat, int f, int r, int a, int speed) {
		arms = new Arm[4];
		
		while( n_of_damage + n_of_eat + n_of_reproduce > 4) {
			int max = Math.max(n_of_damage, n_of_eat);
			max = Math.max(n_of_reproduce, max);
			if(max == n_of_eat) n_of_eat--;
			else if(max == n_of_damage) n_of_damage--;
			else n_of_reproduce--;
		}
		
		int index = 0;
		for(int i = 0; i < n_of_damage; i++) {
			arms[i] = new DamageArm();
		}
		index += n_of_damage;
		
		for(int i = index; i < index + n_of_eat; i++) {
			arms[i] = new EatArm();
		}
		index += n_of_eat;
		
		for(int i = index; i < index + n_of_reproduce; i++) {
			arms[i] = new ReproduceArm();
		}
		
		bias = new Bias(f, r ,a);
		
		this.speed = speed;
		this.evilness = calcEvilness();
		this.fertility = calcFertility();
	}
	
	//Initialize the values from a mother and a father
	public Dna(Cell father, Cell mother) {
		Random rng = new Random(System.currentTimeMillis());
		float r = rng.nextFloat();
		
		if( r < 0.5f ) arms = father.getDna().arms;
		else arms = mother.getDna().arms;
		
		r = rng.nextFloat();
		
		if( r < 0.5f ) bias = father.getDna().bias;
		else bias = mother.getDna().bias;
		
		r = rng.nextFloat();
		
		if( r < 0.5f ) speed = father.getDna().speed;
		else speed = mother.getDna().speed;
		
		evilness = rng.nextFloat(2 * getNumberOfDamageArm() + .1f);
		fertility = calcFertility();
		tryToMutate();
	}

	//Initialize all field with random values
	public Dna() {
		
	Random rng = new Random();
		
		int tl = rng.nextInt(3), tr = rng.nextInt(3), bl = rng.nextInt(3), br = rng.nextInt(3);
		Arm TL, TR, BL, BR;
		
		switch(tl) {
		case 0:
			TL = new EatArm();
			break;
		case 1:
			TL = new DamageArm();
			break;
		case 2:
			TL = new ReproduceArm();
			break;
		default:
			TL = new EatArm();
			break;
		}
		
		switch(tr) {
		case 0:
			TR = new EatArm();
			break;
		case 1:
			TR = new DamageArm();
			break;
		case 2:
			TR = new ReproduceArm();
			break;
		default:
			TR = new EatArm();
			break;
		}
		
		switch(bl) {
		case 0:
			BL = new EatArm();
			break;
		case 1:
			BL = new DamageArm();
			break;
		case 2:
			BL = new ReproduceArm();
			break;
		default:
			BL = new EatArm();
			break;
		}
		
		switch(br) {
		case 0:
			BR = new EatArm();
			break;
		case 1:
			BR = new DamageArm();
			break;
		case 2:
			BR = new ReproduceArm();
			break;
		default:
			BR = new EatArm();
			break;
		}
		
		arms = new Arm[4];
		arms[0] = TL;
		arms[1] = TR;
		arms[2] = BL;
		arms[3] = BR;
		
		int f = 0, r = 0, a = 0;
		
		for(int i = 0; i < 4; i++) {
			if(arms[i] instanceof DamageArm) a += 63;
			if(arms[i] instanceof ReproduceArm) r += 63;
			if(arms[i] instanceof EatArm) f += 63;
		}
		
		bias = new Bias(f, r, a);
		
		this.speed = rng.nextInt(3) + 1;
		this.evilness = calcEvilness();
		this.fertility = calcFertility();
	}
	
	//Calculate the hashCode
	//It is a binary value where:
	//The first 3 bits represents the number of damage arms
	//The other 3 bits represents the number of reproduce arms
	//The last 3 bits represents the number of eat arms
	//Example: 001000011 -> 001 eat arms and 011 damage arms
	public int getHashCode() {
		int res = 0;
		
		res += getNumberOfDamageArm(); 
		res += 8 * getNumberOfReproduceArm();
		res += 64 * getNumberOfEatArm();
		
		return res;
	}
	
	//Return the max number the hashcode could be -> 100000000 = 267 (4 eat arms)
	public static int getDnaLength() { return 267; }
	
	//Return the evilness of the cell
	private float calcEvilness() {
		return new Random().nextFloat(2 * bias.awarness + .1f);
	}

	//Return the fertility of the cell
	private int calcFertility() {
		return (2 * getNumberOfReproduceArm()) + 1;
	}

	//There's a 10% chance to mutate one of the DNA values
	private void tryToMutate() {
		Random rng = new Random();
		float r = rng.nextFloat();
		
		if(r <= .1f) {
			
			int w = rng.nextInt(4);
			
			switch(w) {
			case 0:
				mutateArm();
				break;
			case 1:
				mutateBias();
				break;
			case 2:
				mutateSpeed();
				break;
			case 3:
				mutateEvilness();
				break;
			
			default:
				break;
			}
		}
	}
	
	//Add to evilness a random float between [-1, +1]
	private void mutateEvilness() {
		evilness += new Random().nextFloat(2) - 1f;
		
	}
	
	//Add to speed a random int between [-1, +1]
	private void mutateSpeed() {
		speed += new Random().nextInt(2) - 1;
		
	}

	//Mutate a random arm
	private void mutateArm() {
		Random rng = new Random();
		int i = rng.nextInt(3);
		int a = rng.nextInt(3);
		
		if(a == 0) arms[i] = new DamageArm();
		else if(a == 1) arms[i] = new EatArm();
		else { 
			arms[i] = new ReproduceArm();
			fertility = calcFertility();
		}
		
	}

	//Mutate a random bias field
	private void mutateBias() {
		Random rng = new Random();
		int a = rng.nextInt(3);
		
		if(a == 0) {
			bias.food += (rng.nextInt(3) - 1);
			if(bias.food > 255) bias.food = 255;
			if(bias.food < 0) bias.food = 0;
		}
		else if(a == 1) {
			bias.reproduce += (rng.nextInt(3) - 1);
			if(bias.reproduce > 255) bias.reproduce = 255;
			if(bias.reproduce < 0) bias.reproduce = 0;
		}
		else {
			bias.awarness += (rng.nextInt(3) - 1);
			if(bias.awarness > 255) bias.awarness = 255;
			if(bias.awarness < 0) bias.awarness = 0;
		}
		
	}

	//Return the total number of damage arms
	public int getNumberOfDamageArm() {
		int r = 0;
		
		for(int i = 0; i < 4; i++) 
			if(arms[i] instanceof DamageArm) r += 1;
			
		return r;
	}
	
	//Return the total number of reproduce arms
	public int getNumberOfReproduceArm() {
		int r = 0;
		
		for(int i = 0; i < 4; i++) 
			if(arms[i] instanceof ReproduceArm) r += 1;
			
		return r;
	}
	
	//Return the total number of eat arms
	public int getNumberOfEatArm() {
		int r = 0;
		
		for(int i = 0; i < 4; i++) 
			if(arms[i] instanceof EatArm) r += 1;
			
		return r;
	}
	
	public boolean equals(Dna other) {
		if(other == null) return false;
		return getNumberOfDamageArm() == other.getNumberOfDamageArm() && getNumberOfEatArm() == other.getNumberOfEatArm() && getNumberOfReproduceArm() == other.getNumberOfReproduceArm();
	}
}

//It contains the values for the ranges of visibility, Every field it must be in [0, 255]
//Those 3 values compose the color of the head of the cell
class Bias {
	int food, reproduce, awarness;
	
	public Bias() {
		this(125, 125, 125);
	}
	
	public Bias(int f, int r, int a) {
		if(r > 255) r = 255;
		if(r < 0) r = 0;
		
		if(f > 255) f = 255;
		if(f < 0) f = 0;
		
		if(a > 255) a = 255;
		if(a < 0) a = 0;
		
		food = f;
		reproduce = r;
		awarness = a;
	}
	
	public float getFoodRange() {
		return food;
	}
	
	public float getAwarnessRange() {
		return awarness;
	}
	
	public float getReproduceRange() {
		return reproduce;
	}
	
	//Return the max range the bias has
	public float getMaxRange() {
		float max =  Math.max(getFoodRange(), getAwarnessRange());
		max =  Math.max(max, getReproduceRange());
		
		return max;
	}
}
