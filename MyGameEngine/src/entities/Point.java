package entities;

import utils.Coordinate;

public class Point extends Entity {
	public Point(int x, int y) {
		position = new Coordinate(x, y);
	}
}
