package flapOver;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;


public class FlapOver implements ActionListener, MouseListener, KeyListener  {
	
	//Static instance of FlapOver.
	public static FlapOver flapOver;

	//final integers
	public final int WIDTH = 800, HEIGHT = 800;

	//create a veriable of Randerer Class
	public Renderer renderer;
	
	//Create a rectangle small box for for a game 
	public Rectangle dot;
	
	//Using array that contains Columns 
	public ArrayList<Rectangle> columns;

	//Motion of the Dot
	public int ticks, yMotion, score;

	public boolean gameOver, started;

	public Random rand;
	
	//Constructor
	public FlapOver() {
		
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this);
		
		//Make a place in a memory create, create a instance of a Renderer 
		renderer = new Renderer();
		rand = new Random();
		
		//Call Jpanel
		jframe.add(renderer);
		jframe.setTitle("Flap Over");
		//Terminate the program on close call.
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		//Add Mouse Listener
		jframe.addMouseListener(this);
		//Add Key Listener
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		//Stop call and make visible
		jframe.setVisible(true);
		
		//define the size of Dot, to make it at center of the screen use this math logic
		dot = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
		
		columns = new ArrayList<Rectangle>();

		//Add columns, at start of the screen
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		//continue Untill Stop
		timer.start();
	}
	
	//create Columns
	public void addColumn(boolean start) {
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);

		if (start) {
			//using Two Columns up and down
			//To keep the columns, top at the grass, using height-120.
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			//Top columns
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
		} else {
			//Last Columns
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
		}
	}
	
	//set the columns color
	public void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	public void jump() {
		
		if (gameOver) {
			dot = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}

		if (!started) {
			started = true;
		} else if (!gameOver) {
			if (yMotion > 0) {
				yMotion = 0;
			}

			yMotion -= 10;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Columns Speed
		int speed = 10;
		ticks++;
		//Game Started Do these Things
		if (started) {
			//Start Columns
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);
				column.x -= speed;
			}
			
			//Motion of the Dot
			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}

			//make infinite columns
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);
				if (column.x + column.width < 0) {
					columns.remove(column);
					if (column.y == 0) {
						addColumn(false);
					}
				}
			}
			
			//y motion of the dot
			dot.y += yMotion;

			for (Rectangle column : columns) {
				//Calculate the Score
				if (column.y == 0 && dot.x + dot.width / 2 > column.x + column.width / 2 - 10 && dot.x + dot.width / 2 < column.x + column.width / 2 + 10)
				{
					score++;
				}

				if (column.intersects(dot)) {
					gameOver = true;
					//if intersect, dont set it to move it, stop Motions
					if (dot.x <= column.x) {
						dot.x = column.x - dot.width;
					} else {
						if (column.y != 0) {
							dot.y = column.y - dot.height;
						}
						else if (dot.y < column.height) {
							dot.y = column.height;
						}
					}
				}
			}

			//Set the GameOver if Dot touch the grass
			if (dot.y > HEIGHT - 120 || dot.y < 0) {
				gameOver = true;
			}

			if (dot.y + yMotion >= HEIGHT - 120) {
				dot.y = HEIGHT - 120 - dot.height;
				gameOver = true;
			}
		}

		renderer.repaint();
	}
	

	public void repaint(Graphics g) {
		
		//set background Color
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		//set Footer as a mud
		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);

		//Set the grass on the mud
		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);

		//set the color of Dot
		g.setColor(Color.red);
		g.fillRect(dot.x, dot.y, dot.width, dot.height);

		//paint the Columns
		for (Rectangle column : columns) {
			paintColumn(g, column);
		}

		//Set Score, Game Start and Game Over Popup/Notification
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));

		if (!started) {
			g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
		}

		if (gameOver) {
			g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
		}

		if (!gameOver && started) {
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		flapOver = new FlapOver();

	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		jump();
	}

	//set the Jump on Mouse/Key Click
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jump();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

}
