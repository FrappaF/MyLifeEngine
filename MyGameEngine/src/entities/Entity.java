package entities;

import utils.Coordinate;

//Base class for every entity in the world
//It contains a position, an entity that targeted this and if it's alive or not
public abstract class Entity {

	protected Coordinate position;
	protected Entity targettedBy;
	protected boolean live;
	
	public void setTargetted(Entity other) { targettedBy = other; }
	
	public Coordinate getPosition() { return position; }
	public boolean isTargetted() { return getTargettedBy() != null; }
	
	public String toString() { return this.getClass() + " @ " + position.toString(); }
	
	public void die() { live = false; }
	public boolean isAlive() { return live; }

	public Entity getTargettedBy() {
		return targettedBy;
	}
}
