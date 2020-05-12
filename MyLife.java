/** 
* --------------------------------------------------------------------------- 
* File name: MyLife.java
* Project name: CSCI 1900 001 Project 1
* --------------------------------------------------------------------------- 
* Author’s name and email: William Kinser; kinserw@etsu.edu
* Course-Section: CSCI 1900 001
* Creation Date: 9/28/2019
* Last modified: (Name, Date, email) 
*
* Purpose: This file contains MyLife class plus the inner classes needed to 
* simulation John Conway's Game of Life (http://www.conwaylife.com/). Conway
* created Life in the late 60s and it gained popularity among mathmeticians 
* and computer enthusiasts in the early 70s. Life models the birth, death, and 
* living conditions of "cells" in an nxm matrix using some very simple rules:
* 	1) a "dead" cell with exactly 3 living neighbors comes to life (birth)
* 	2) a "living" cell with < 2 "living" neighbors, dies (death by starvation)
* 	3) a "living" cell with > 3 "living" neighbors, dies (death by overcrowding)
* 	4) a "living" cell with 2 or 3 "living" neighbors, lives to the next generation
* Many groups have existed over the years that explore this simple world of rules.
* Some groups tweak the rules to evaluate different varieties of "life" (
* https://dev.to/lexjacobs/conways-game-of-life-with-different-rules-13l0). Others, 
* try define the boundaries. For example, Conway originally believed that no configuration
* of cells would grow indefinitely, either stabilizing or dissapearing at some point.
* A group from MIT showed that a specific configuration was stable but generated 
* gliders at a predictable frequency, thus never fully stabilized http://www.conwaylife.com/wiki/Gosper_glider_gun.
*
* This program is a simple simulation of Conway's rules while at the same time
* exploring some of the different aspects of Java. The World class is a basic 2D
* grid. The living cells are modeled using an ArrayList of Points. Keeping track of
* just the living cells is more efficient than tracking all cells. I made the 
* window resizable which causes the world to grow and shrink accordingly. Any Cells
* outside the window during a shrinking of the World, will be lost (dropped from 
* this array). 
* Currently the borders are fixed and no growth is allowed beyond them. I did this
* by adding a blank set of columns and rows at the outside edge of the world
* containing all "dead" cells. This effectively halts growth at the edges. I plan
* to allow wrapping but I haven't figured out how I want to handle the case at the
* corners of "the world". 
* I added buttons on the screen so the use can place some well known, popular cell 
* configurations onto the world canvas. I plan to expand this set over time.
* Most of the configurations are stationary with the exception of "glider" which
* appears to "move" across the screen diagonally to the right and downward. I may
* add the ability to rotate the glider but for now it just goes in the same direction
* every time. The other more notable button is "cell" which allows the user to draw
* multiple cells as they drag the mouse across the canvas thus creating an endless and
* unpredictable collection of "living" cells. Some of these die quickly, others survive.
* The user can clear the screen to start over.
*
* This is list of things I had to learn for this program:
* 1) AWT and Swing classes in Java
*    Especially: a) callbacks, b) layout managers, c) concurrentmodificationexception
* 2) How to use the ArrayList
* 3) How to use Point (I'm thinking of extending this class in the future)
* 4) How Enumerated types work in Java
* 5) How to use the enhanced for loop
* 
* Things I learned that weren't specifically required for this program:
* 1) Several people have expanded on Conway's original rules including some 
* interesting applications to smooth surfaces which goes beyond my knowledge of math.
* https://0fps.net/2012/11/19/conways-game-of-life-for-curved-surfaces-part-1/
* 2) Several people have found exotic configurations of cells to do things either 
* extremely efficiently or pretty darn impressively. For example, there are glider 
* guns which are configurations of cells which produce gliders at some regular rate 
* and the original configuration morphs but continues to "live", some for eternity.
* http://www.conwaylife.com/wiki/Simkin_glider_gun
* 3) There is a link between the Pentomino configurations and Tetris.
* https://www.livecubepuzzle.com/about-pentomino-40807227
* --------------------------------------------------------------------------- */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;


/** 
* Class Name: MyLife <br> 
* Class Purpose: 
* MyLife is the main class that contains the various inner classes to model and simulate Conway's 
* game of life. IntroPane and World are the two primary inner classes. IntroPane contains the buttons
* that allows the user to choose which life configurations to place in "the world". World is the 
* canvas that displays the world of living cells and it enforces the rules of Conway's life.
* 
* <hr> 
* Date created: date here <br> 
* Last modified: name, email, date here 
* @author Author’s name here 
*/

// Possible TODO: extend Point class to encapsulate age and other attributes, use for cells
// Possible TODO: Wrap cells on all 4 borders so that the "world" is endless and wraps around top/bottom and left/right.
public class MyLife extends JFrame 
{
	private static  int cellSize = 10;
    private int moves = 10;
	public World myWorld;
	public enum MouseAction  {CELL, GLIDER, BLINKER, STOPSIGN, BLOCK, PENTOMINO, HIVE, CLEAR;}
	MouseAction mouseAction = MouseAction.CELL; 


	// IntroPane is an inner class that displays all the buttons to place various 
	// configurations of Conway life forms.
	// TODO: move to its own file (in game package) and set up at title screen for game
	public class IntroPane extends JPanel 
	{
		//  IntroPane default constructor adds all the buttons and register callbacks
		public IntroPane() 
		{
			GridLayout layout = new GridLayout(0,1);
			setLayout(layout);
			
			JLabel label = new JLabel("<html><h3>Conway's <br>Game of Life</h3></html>",JLabel.CENTER);
			add(label);
			
			// add a button for freestyle cells to the panel and register a callback method 			
			JButton cellBtn = new JButton("Cell");
			cellBtn.setToolTipText("Click to add multiple live cells using mouse drag.");
			add(cellBtn);
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.CELL;
				  } // end actionPerformed
			});	// end registering callback method for button
			
			// add a button for Glider to the panel and register a callback method 
			cellBtn = new JButton("Glider");
			add(cellBtn);
			cellBtn.setToolTipText("Gliders 'walk' across the screen. Place it using mouse click.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.GLIDER;
				  } // end actionPerformed
			});	// end registering callback method for button		

			// add a button for Blinker to the panel and register a callback method 
			cellBtn = new JButton("Blinker");
			add(cellBtn);
			cellBtn.setToolTipText("Blinkers alternate between two configurations. Place it using mouse click.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.BLINKER;
				  } // end actionPerformed
			});	// end registering callback method for button	
			
			// add a button for Stop Sign to the panel and register a callback method 
			cellBtn = new JButton("Stop Sign");
			add(cellBtn);
			cellBtn.setToolTipText("Stop Signs are stationary configurations. Place it using mouse click.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.STOPSIGN;
				  } // end actionPerformed
			});	// end registering callback method for button	
			
			// add a button for Block to the panel and register a callback method 			
			cellBtn = new JButton("Block");
			add(cellBtn);
			cellBtn.setToolTipText("Block is the smallest stationary configuration. Place it using mouse click.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.BLOCK;
				  } // end actionPerformed
			});	// end registering callback method for button

			// add a button for Hive to the panel and register a callback method 
			cellBtn = new JButton("Hive");
			add(cellBtn);
			cellBtn.setToolTipText("Stationary configuration similar to Stop Sign. Place it using mouse click.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.HIVE;
				  } // end actionPerformed
			});	// end registering callback method for button

			// add a button for Pentomino to the panel and register a callback method 
			cellBtn = new JButton("Pentomino");
			add(cellBtn);
			cellBtn.setToolTipText("Pentomino morph indefinitely and cause the formation of gliders . Place it using mouse click.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.PENTOMINO;
				  } // end actionPerformed
			});	// end registering callback method for button

			// add a button for clear to the panel and register a callback method 
			cellBtn = new JButton("Clear");
			add(cellBtn);
			cellBtn.setToolTipText("Click to delete all live cells.");
			cellBtn.addActionListener(new ActionListener() {
				// actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 mouseAction = MouseAction.CLEAR;
					 myWorld.cells.clear();

				  } // end actionPerformed
			}); // end registering callback method for button

			// add a button for Exit to the panel and register a callback method 
			cellBtn = new JButton("Exit");
			add(cellBtn);
			cellBtn.setToolTipText("Click to exit the program gracefully.");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 System.exit(0);
				  } // end actionPerformed
			});	// end registering callback method for button

			// add a button for shrink the cell size to the panel and register a callback method 
			cellBtn = new JButton("Shrink");
			add(cellBtn);
			cellBtn.setToolTipText("Click to shrink size of cells");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 cellSize--;
					 if (cellSize < 4) cellSize = 4;
					 myWorld.updateSizeOfWorld();
				  } // end actionPerformed
			});	// end registering callback method for button

			// add a button for grow the cell size to the panel and register a callback method 
			cellBtn = new JButton("Grow");
			add(cellBtn);
			cellBtn.setToolTipText("Click to grow size of cells");
			cellBtn.addActionListener(new ActionListener() {
				  // actionPerformed is an inline method for anonymous class of ActionListener
				  public void actionPerformed(ActionEvent event) {
					 cellSize++;
					 if (cellSize >20) cellSize = 20;
					 myWorld.updateSizeOfWorld();
				  } // end actionPerformed
			});	// end registering callback method for button
		} // end IntroPane constructor 
	} // end IntroPane class 
	
	private IntroPane intro; 
	
	// World class models Conway's rules of life and manages the list of living cells. It is a nested
	// class for now to that I don't have to complicate my compilations with multiple files. 
	// TODO: Move to its own file (in game package) and make public
	private class World extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable {
        private Dimension worldSize = null;
		// keep track of the live cells (this improves speed so we don't track all cells in the world)
        private ArrayList<Point> cells = new ArrayList<Point>(0);
 
		// World default constructor
        public World() {

            addComponentListener(this); // for resizing of frame so we can resize the world
            addMouseListener(this); // for detecting mouse pressed
            addMouseMotionListener(this); // for detecting mouse movement
        } // end World constructor
 
        
		// addCell will validate the x,y coordinates passed in to ensure they are within the
		// currently defined dimensions of the world. If so, the living cell is set.
        public void addCell(int x, int y) {
			// if the x,y are not valid for world size, don't add it.
			if ((x >= 0) && (x < worldSize.width) && (y >= 0) && (y < worldSize.height)) {
				// don't add it if it already exists
				if (!cells.contains(new Point(x,y))) { 
					cells.add(new Point(x,y));
					repaint(); // force a repaint since we added a new living cell
				} // end if cell already exists as a living cell
			} // end if x,y values are valid for world size
        } // end addCell
 
		// addCell adds a new living cell at the specified location in the world matrix. Each
		// configuration tries to center on the x,y coordinate provided. Validation of the 
		// coordinates in the configurations is done in the "addCell(x,y)" method called from here
		// so I don't have to have that validation in multiple places.
        public void addCell(MouseEvent event) {
			// calculate the x and y for the new cell, then call addCell (if x,y is in the world)
            int x = (event.getPoint().x/cellSize)-1;
            int y = (event.getPoint().y/cellSize)-1;
			
		    // Mouse was released (user clicked) which means they want to place 
			// live cells at this location in one of the following possible configurations:
			// MouseAction  {CELL, GLIDER, BLINKER, STOPSIGN, BLOCK, PENTOMINO, HIVE;}

			// user wants to draw a freestyle configuration
            if (mouseAction == MouseAction.CELL) 
				addCell(x,y);
			// user wants to draw a glider configuration
			else if (mouseAction == MouseAction.GLIDER) {
				addCell(x,y);
				addCell(x-1,y);
				addCell(x-2,y);
				addCell(x,y-1);
				addCell(x-1,y-2);
			} // end if draw a glider
			// user wants to draw a Blinker configuration
			else if (mouseAction == MouseAction.BLINKER) {
				addCell(x,y);
				addCell(x-1,y);
				addCell(x+1,y);
			} // end if draw a blinker
			// user wants to draw a stop sign configuration
			else if (mouseAction == MouseAction.STOPSIGN) {
				addCell(x+1,y);
				addCell(x+1,y+1);
				addCell(x-2,y);
				addCell(x-2,y+1);
				addCell(x-1,y+2);
				addCell(x,y+2);
				addCell(x,y-1);
				addCell(x-1,y-1);
			} // end if draw a stop sign
			// user wants to draw a block configuration
			else if (mouseAction == MouseAction.BLOCK) {
				addCell(x,y);
				addCell(x-1,y);
				addCell(x-1,y-1);
				addCell(x,y-1);
			} // end if draw a block
			// user wants to draw a pentomino configuration
			else if (mouseAction == MouseAction.PENTOMINO) {
				addCell(x,y);
				addCell(x-1,y);
				addCell(x,y-1);
				addCell(x,y+1);
				addCell(x+1,y-1);
			} // end if draw a pentomino 
			// user wants to draw a Hive configuration
			else if (mouseAction == MouseAction.HIVE) {
				addCell(x+1,y);
				addCell(x-2,y);
				addCell(x,y-1);
				addCell(x-1,y-1);
				addCell(x,y+1);
				addCell(x-1,y+1);
			} // end if draw a Hive 
			// user wants to clear screen (kill all living cells)
			else if (mouseAction == MouseAction.CLEAR) {
				cells.clear();
			} // end if user wants to clear screen (kill all living cells)
			else // by default, set mouseAction to create cells on mouse drags
				mouseAction = MouseAction.CELL;

        } // end addCell
 
		// updateSizeOfWorld is only called if the user resizes the window of the program.
		// It will determine if any living cells should be dropped as being outside the new world.
		private void updateSizeOfWorld() {
			//TODO: consider checking if world got bigger, if so, skip this logic altogether
			// Setup the world size with proper boundries (cell size -2 to reflect the  cells on the edge)
            worldSize = new Dimension(getWidth()/cellSize-2, getHeight()/cellSize-2);
			
			// find all the cells, if any,  that are lost in the resizing of the world
            ArrayList<Point> removeList = new ArrayList<Point>(0);
            for (Point current : cells) {
				// mark any and all cells outside the new world to be removed
                if ((current.x > worldSize.width-1) || (current.y > worldSize.height-1)) {
                    removeList.add(current);
                }// end if cell is outside the new world dimensions
            } // end loop through all living cells
			// remove the lost cells from array of living cells
            cells.removeAll(removeList);
            repaint(); // force redrawing since we changed size (most important when shrinking)
        } // end updateSizeOfWorld
 
		// removeCells will remove any living cells that exists at the specified coordinates passed in
        public void removeCell(int x, int y) {
			// no need to check if it is in the array, just call remove
            cells.remove(new Point(x,y));
        } // end removeCells
 
        
		// pre-populate the world with a random number of cells based on whole percentage passed in.
		// this method is primarily used in testing.
        public void fillWorld(int percent) {
            for (int i=0; i < worldSize.width; i++) { // loop through rows in world matrix
                for (int j=0; j < worldSize.height; j++) { // loop through cols in world matrix
                    if (Math.random()*100 < percent) {
                        addCell(i,j); // add a living cell here
                    } // end if random number meets criteria passed in
                } // end loop through all the cols in the world matrix
            } // end loop through all rows in the world matrix
        } // end fillWorld
 
        @Override
		// override base class methods so that we can draw all the living cells in our world
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.red);
            try {
				// enhanced for loop simplifies iterating through array of coordinates (live cells)
                for (Point newPoint : cells) {
                    // Draw all living cells
					g.fillOval(cellSize*(1 + newPoint.x), cellSize*(1 + newPoint.y), 
								cellSize, cellSize);
                } // end loop through all the live cells
            } // end try block
			catch (Exception exception) 
			{
				// do nothing; just capture errors for now
				// TODO: figure out why it's throwing exceptions (ConcurrentModificationException?)
				// 
				// I saw several places in the class documentation that Swing is NOT thread safe but
				// running my world class in a thread was based on examples I found on 
				// Oracles and StackOverflow websites. Catching and ignoring these errors 
				// doesn't seem to cause a problem in quick runs of the program.			
			} // end catch all exceptions
	  		
        } // end paintComponent
 
        @Override
		// allow the window to be resized by the user, and adjust the world size accordingly (I keep
		// the button area fixed in size so only the world has to adjust to resizing)
        public void componentResized(ComponentEvent e) {
            updateSizeOfWorld();
        } // end componentResized
        
		// override all the abstract methods even if nothing is done
		// note: "@override" is a compiler instruction to force validation against base class methods
		@Override
        public void componentMoved(ComponentEvent e) {}
        @Override
        public void componentShown(ComponentEvent e) {}
        @Override
        public void componentHidden(ComponentEvent e) {} 
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) { }
        @Override
		// mouseEntered is used to detect when the cursor is moved to the canvas area
		// where the world is. The only time this really matters is when the user
		// is trying to clear the screen.
        public void mouseEntered(MouseEvent e) {
			if (mouseAction == MouseAction.CLEAR) 
				cells.clear();
		} // end mouseEntered
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
 
        @Override
        public void mouseReleased(MouseEvent e) {
			addCell(e);
        } // end mouseReleased
        @Override
        public void mouseDragged(MouseEvent e) {
            // Mouse is being dragged, user wants multiple selections
            if (mouseAction == MouseAction.CELL)
				addCell(e); 
        } // end mouseDragged
 
        @Override
        public void run() {
			if (worldSize == null) // if we haven't created a world yet, do so now.
			{
				worldSize = new Dimension(getWidth()/cellSize-2, getHeight()/cellSize-2);
			} // end if worldSize is null
			
			// define a world of boolean since each cell is either alive or dead (thus, boolean)
			// add 2 more rows/cols than the world size to allow for empty cells at edge of grid
			// as this avoids indexing error when counting neighbors and we don't have to check
			// world boundaries constantly.
            int[][] world = new int[worldSize.width+2][worldSize.height+2];
			
			// iterate through all the live cells, set world coordinates to indicate alive
			// increment x & y to allow for the first row and first column of empty cells
            for (Point liveCell : cells) {
                world[liveCell.x+1][liveCell.y+1] = 1;
            }
			
			// create an array that holds the next generation
			// have to use a new array to avoid corrupting the current list of live cells
			// until all the calculations are completed 
            ArrayList<Point> nextGen = new ArrayList<Point>(0);
			
            // determine which cells will live or die
			// TODO: consider just looking at cells immediately around the ones already alive,( faster??)
			int neighbors = 0;
			// note: start at 1 instead of 0 and end at length -1 because we padded all 4 sides of the matrix
			// with an extra row/col
            for (int i=1; i < world.length-1; i++) { // loop through rows of world matrix
                for (int j=1; j < world[0].length-1; j++) { // loop through columns of world matrix
					// calculate how many cells around this one are alive
					neighbors = // look at top row first
								world[i-1][j-1] +
								world[i-1][j] +
								world[i-1][j+1] +
								// then left and right neighbors
								world[i][j-1] +
								world[i][j+1] +
								// then bottom row
								world[i+1][j-1] +
								world[i+1][j] +
								world[i+1][j+1];
								
                    // determine if the cell we are on should live or die
					if (world[i][j] != 0) 
					{
                        // if this cell is alive, it stays alive IFF surrounded by 2 or 3 other living cells
                        if ((neighbors == 2) || (neighbors == 3)) 
						{
                            nextGen.add(new Point(i-1,j-1));
                        } // end if 2 or 3 neighbors
                    } // end if cell at i,j is alive 
					else // cell must be empty
					{
                        // if this cell is dead, will it come to life ?
                        if (neighbors == 3) 
						{
                            nextGen.add(new Point(i-1,j-1));
                        } // end if 3 neighbors
                    } // end else (cell at location must be empty to get into the else)
                } // end loop for the columns of the world matrix
            } // end loop for the rows of the world matrix
			
            cells.clear(); // don't need current generation anymore
            cells.addAll(nextGen); // make the next generation the current generation
            repaint(); // force a repaint of canvas
			
			// for now, program runs too fast so create a pause
            try {
                Thread.sleep(1000/moves);
                run();
            }  // cause a pause
			catch (Exception exception) 
			{
				// do nothing for now
				// TODO: figure out why it's throwing exceptions. I saw several
				// places in the class documentation that Swing is NOT thread safe but
				// running my world class in a thread was based on examples I found on 
				// Oracles and StackOverflow websites. Catching and ignoring these errors 
				// doesn't seem to cause a problem in quick runs of the program.
			} // end catch
        } // end run
		
    } // end class World
	
	
	// default constructor for MyLife
	public MyLife()
	{
		// set up default operations and starting size/position of screen 
		setTitle("Life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(300,100);
		intro = new IntroPane();
		intro.setSize(100,500);

		// World is runnable so, pass it to a thread once created
		myWorld = new World();
		Thread thread = new Thread (myWorld);
		
		// the default content pane already has a border layout manager by default.
		// use that to add the intro panel (with buttons) and world (where the cells live)
		Container pane = getContentPane();
		pane.add(intro, BorderLayout.LINE_START);
		pane.add(myWorld, BorderLayout.CENTER);
		pack();
		setSize(500,500);
		setVisible(true);
		thread.start();
	}// end constructor for MyLife
	
	public static void main(String[] args) 
	{
		JOptionPane.showMessageDialog(null, 
		"Welcome to MyLife program. \n" +
		"This program models a basic implementation of John Conway's Game of Life\n" +
		"www.conwaylife.com. Basically, it models " + 
		" the birth, death, and living conditions \n" +
		"of 'cells' in an NxM matrix using some very simple rules:\n" +
		"\t1) a dead cell with exactly 3 living neighbors comes to life (birth)\n"+
		"\t2) a living cell with < 2 living neighbors, dies (death by starvation)\n "+
		"\t3) a living cell with > 3 living neighbors, dies (death by overcrowding)\n " +
		"\t4) a living cell with 2 or 3 living neighbors, lives to the next generation \n"+
		"This program lets you 'draw' living cells across a canvas by dragging the mouse around. \n" +
		"You can also place popular configurations of cells on the canvas by clicking the named \n" +
		" buttons then clicking anywhere on the canvas."
		);

		new MyLife(); // this works because I start a thread in the constructor of MyLife

	} // end main
} // end class MyLife