package utils;


//Utils class with helpful values
public class Utils {
	
	//return euclidean distance between two coordinates
	public static float euclideanDistance(Coordinate p1, Coordinate p2) {
		
		return (float) Math.sqrt( Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
	public static int getSquareDimension() { return 5; }
	
	public static int getScreenWidth() { return 1400; }
	
	public static int getScreenHeight() { return 800; }
}
