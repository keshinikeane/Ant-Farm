package lab11;

import static java.lang.Math.*;
import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class AntFarm extends JPanel {
	public AntFarm(int nx, int ny, int nant) { // number of cells is nx*ny
		this.nx = nx;
		this.ny = ny;
		int xhome = 20;
		int yhome = 20;
		int whome = (int)sqrt(nant);	
		ants = new Ant[nant];
		for (int iant=0, x=xhome, y=yhome; iant<nant; ++iant) {
			ants[iant] = new Ant(x,y,this);
			x+=1;
			if (x==xhome+whome)
				x=xhome;
		}
		home = new boolean[nx][ny];
		pher = new double[nx][ny];
		food = new double[nx][ny];
		
		int hx = 20;
		int hy = 20;
		for (int i=hx; i<hx+8; ++i) {
			for (int j=hy; j<hy+8; ++j) {
				home[i][j] = true;
			}
		}
		
		//for (int i=0; i<nant; ++i) {
		//	ants[i] = new Ant(hx+2,hy+2,this);
		//}
		
		Random random = new Random(1);
		for (int x=0; x<nx; ++x) {
			for (int y=0; y<ny; ++y) {
				double r = random.nextDouble();
				if (!home[x][y] && r<FOOD_FRAC) 
					food[x][y] = FOOD_MAX;
			}
		}
		
		makeFrame(this);
	}
	
	public boolean home(int x, int y) {
		return home[x][y];
	}
	
	public double food(int x, int y) {
		return food[x][y];
	}
	
	public double pher(int x, int y) {
		return pher[x][y];
	}
	
	public void dropPher(int x, int y, double p) {
		pher[x][y] += p;
		if (pher[x][y] > PHER_MAX)
			pher[x][y] = PHER_MAX;
	}
	
	public void dropFood(int x, int y, double f) {
		food[x][y] += f;
		if (food[x][y] > FOOD_MAX)
			food[x][y] = FOOD_MAX;
	}
	
	public double takeFood(int x, int y, double f) {
		f = min(f,food[x][y]);
		food[x][y] -= f;
		return f;
	}
	
	public int validX(int x) {
		if (x>nx-1) {
			x=0;
		} else if (x<0) {
			x=nx-1;
		}
		return x;
	}
	
	public int validY(int y) {
		if (y>ny-1) {
			y=0;
		} else if (y<0) {
			y=ny-1;
		}
		return y;
	}
	
	public static void main(String[] args) {
		System.out.println("Success!");
		AntFarm farm = new AntFarm(80,80,64);
	}

	
	private static final double FOOD_FRAC = 0.01; // fraction of cells that have food
	private static final double FOOD_MAX = 10.0; // maximum amount of food/cell
	private static final double PHER_MAX = 10.0; // maximum amount of pher/cell
	private static final double PHER_DECAY = 0.99; // pher decrease by this factor
	private static final int SLEEP_MS = 100;
	private int nx,ny;
	private Ant[] ants;
	private double[][] food;
	private double[][] pher;
	private boolean[][] home; // true for cells that are home
	
	private static void sleep() {
		try {
			Thread.sleep(SLEEP_MS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void evaporate() {
		for (int x=0; x<nx; ++x) 
			for (int y=0; y<ny; ++y)
				pher[x][y] *= PHER_DECAY;
	}
	
	private void tickTock() {
		sleep();
		evaporate();
		for(Ant ant:ants) 
			ant.behave();
		repaint();
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Graphics
	
	public Dimension getPreferredSize() {
		return new Dimension(nx*CELL_SIZE,ny*CELL_SIZE);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // clears the background
		int rw = CELL_SIZE;
		int rh = CELL_SIZE;
		for (int x=0; x<nx; ++x) {
			for (int y=0; y<ny; ++y) {
				int rx = x*rw;
				int ry = y*rh;
				if (pher[x][y]>0.0) {
					float red = 0.0f;
					float green = 1.0f;
					float blue = 0.0f;
					float alpha = (float)(pher[x][y]/PHER_MAX); // opacity
					g.setColor(new Color(red, green, blue, alpha));
					g.fillRect(rx,ry,rw,rh);
				}
				if (home[x][y]) {
					g.setColor(Color.BLUE);
					g.drawRect(rx,ry,rw,rh);
				}
				if (food[x][y]>0.0) {
					float red = 1.0f;
					float green = 0.0f;
					float blue = 0.0f;
					float alpha = (float)(food[x][y]/FOOD_MAX); // opacity
					g.setColor(new Color(red, green, blue, alpha));
					g.fillRect(rx,ry,rw,rh);
				}
			}
		}
		for (Ant ant:ants) {
			int x = ant.x();
			int y = ant.y();
			int rx = x*rw;
			int ry = y*rh;
			g.setColor(Color.BLACK);
			g.fillOval(rx,ry,rw,rh);		
			if (ant.hasFood()) {
				g.setColor(Color.RED);
				g.fillOval(rx,ry,rw,rh);	
			}
		}
		
		tickTock();
	}
	
	private static final int CELL_SIZE = 8; // width & height in pixels
	
	private static void makeFrame(JPanel panel) {
		JFrame frame = new JFrame("Our ant farm");
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
