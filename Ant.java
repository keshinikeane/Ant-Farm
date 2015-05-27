package lab11;

import java.awt.Point;
import java.util.Random;

public class Ant {
	
	public Ant(int x, int y, AntFarm farm) {
		this.x = x;
		this.y = y;
		this.farm = farm;
		brain = new Random();
	}
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public boolean hasFood() {
		return food>0.0;
	}
	
	
	
	public Point getHomeLoc() {
		Point homeL = null;
		for (int i=farm.validX(x-10); i<farm.validX(x+10); ++i) {
			for (int j=farm.validY(y-10); j<farm.validY(y+10); ++j) {
				if (farm.home(i,j)) {
					homeL = new Point(i,j);
					break;
				}
			}
		}
		return homeL;
	}
	
	public Point getFoodLoc() {
		Point foodL = null;
		for (int i=farm.validX(x-5); i<farm.validX(x+5); ++i) {
			for (int j=farm.validY(y-5); j<farm.validY(y+5); ++j) {
				if (farm.food(i,j)>0.0 && !farm.home(i,j)) {
					foodL = new Point(i,j);
				}
			}
		}
		return foodL;
	}
	
	private Point homeLoc = null;
	private Point foodLoc = null;
	
	public int getDirectionToward(Point p) {
		int direct = dir;
		if (p.x<this.x) {
			if (p.y>this.y) {
				direct = 5;
			} else if (p.y<this.y) {
				direct = 7;
			} else {
				direct = 6;
			}
		} else if (p.x>this.x) {
			if (p.y>this.y) {
				direct = 3;
			} else if (p.y<this.y) {
				direct = 1;
			} else {
				direct = 2;
			}
		} else {
			if (p.y>this.y) {
				direct = 4;
			} else if (p.y<this.y) {
				direct = 0;
			}
		}
		return direct;
	}
	
	public int desiredDirection() {
		homeLoc = getHomeLoc();
		foodLoc = getFoodLoc();
		int direct = dir;
		if (homeLoc!=null && hasFood()) {
			direct = getDirectionToward(homeLoc);
		} else if (foodLoc!=null && !hasFood()) {
			direct = getDirectionToward(foodLoc);
		}
		return direct;
	}
	
	public void behave() {
		dropPher();
		turn();
		dir = desiredDirection();
		walk();
		if (!hasFood()) {
			takeFood();
		}
		if (isHome()) {
			dropFood();
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Private
	
	private int x,y;
	private int dir;
	private double food;
	private AntFarm farm;
	private Random brain; // used to update direction
	
	private static final double PHER_TO_DROP = 1.0;
	private static final double FOOD_TO_TAKE = 1.0;
	private static final int[] xstep = { 0, 1, 1, 1, 0, -1, -1, -1};
	private static final int[] ystep = {-1,-1, 0, 1, 1,  1,  0, -1};

	private boolean isHome() {
		return farm.home(x,y);
	}
	
	private void dropPher() {
		farm.dropPher(x,y,PHER_TO_DROP);
	}
	
	private void dropFood() {
		farm.dropFood(x, y, food);
		food = 0.0;
	}
	
	private void takeFood() {
		food += farm.takeFood(x, y, FOOD_TO_TAKE);
	}
	
	private void walk() {
		x = farm.validX(x+xstep[dir]);
		y = farm.validY(y+ystep[dir]);
	}
	
	private void turn() {
		double r = brain.nextDouble();
		if (r<0.250) {
			turnLeft();
		} else if (r>0.750) {
			turnRight();
		}	
	}
	
	private void turnLeft() {
		dir -= 1;
		if (dir<0)
			dir = 7;
	}
	
	private void turnRight() {
		dir += 1;
		if (dir>7)
			dir = 0;
	}
	
}
