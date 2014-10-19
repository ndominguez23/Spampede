

/*
 * class SpamMaze
 *
 * represents and handles the model for the Spampede applet
 */

import java.lang.Math;
import java.util.LinkedList;
import java.util.Random;

/* SpamMaze derives from (inherits from) Maze ...
 */
class SpamMaze extends Maze  
{
  // The data members representing the spam and the centipede
  private LinkedList<MazeCell> spamCells;
  private LinkedList<MazeCell> pedeCells;

  public static final char SPAM = 'D';
  public static final char START = 'S';
  public static final char PEDE = 'P';
  public static final char WALL = '*';
  public static final char OPEN = ' ';

  /*
   * SpamMaze constructor
   */
  public SpamMaze() {
      super();
      this.spamCells = new LinkedList<>();
      this.pedeCells = new LinkedList<>(); 

      spamCells.add(findMazeCell('D'));
      pedeCells.add(maze[1][1]); //body
      pedeCells.addFirst(maze[1][2]); //head goest first
  }
  
  /* 
   * resetPede clears out the pedeCells and changes the contents of the 
   * SpamMaze accordingly. It then adds the starting cells to the pede.
   * 
   */
  public void resetPede() {
      for (int i=0; i<pedeCells.size();++i) {
          MazeCell pede = pedeCells.get(i);

          if(pede.getContents() != WALL) {
            //"erase" the pedeCells in the Maze
            setContents(pede.getRow(), pede.getCol(), ' ');
          }
      }

      pedeCells.clear();
      MazeCell pede = maze[1][1];
      MazeCell head = maze[1][2];

      pedeCells.add(head);
      pedeCells.addLast(pede); 
      setContents(head.getRow(), head.getCol(), START);
      setContents(pede.getRow(), pede.getCol(), PEDE);
  }
  
  /* 
   * getRows returns the number of rows in the maze 
   * 
   */
  public int getRows() {
      return maze.length;
  }

  /* 
   * getColumns returns the number of columns in the maze 
   * 
   */
  public int getColumns() {
      return maze[1].length;
  }
  
  /* 
   * getContents returns the contents of the MazeCell at row r, column c 
   * does not alter the maze
   */
  public char getContents(int r, int c) {
      return maze[r][c].getContents();
  }
  
  /* 
   * setContents changes the contents of the MazeCell at row r, column c
   * to newContents
   */
  public void setContents(int r, int c, char newContents) {
      maze[r][c].setContents(newContents);
  }
  
  /* 
   * addSpam adds a new Spam to the Maze and the spamCells by finding
   * a random MazeCell and changing its contents if it's open; otherwise
   * it finds another MazeCell randomly.
   */
  public void addSpam() {
      Random generator = new Random();
      int maxrow = maze.length;
      int maxcol = maze[1].length;
      int row = generator.nextInt(maxrow);
      int col = generator.nextInt(maxcol);

      if(getContents(row, col) == OPEN) {
          setContents(row, col, SPAM);
          spamCells.add(maze[row][col]); //adds to the back of spamCells
          return; //for safety
      } else {
          addSpam(); //need to pick a different MazeCell
      }
  }
  
  /* 
   * removeSpam removes the first Spam from spamCells and changes its contents
   * since the first Spam is the oldest
   */
  public void removeSpam() {
      MazeCell removed = spamCells.removeFirst(); 
      setContents(removed.getRow(), removed.getCol(), OPEN);
  }
  
  public static final char north = 'N';
  public static final char south = 'S';
  public static final char east = 'E';
  public static final char west = 'W';
  public static final char auto = 'A';
  public static final char rev = ' ';
 
  /*
   * advancePede returns the next MazeCell the pede should move to depending
   * on the direction given by the user. the possible directions are up, down
   * right, left, auto, and reverse. auto uses the multiBFS algorithm written
   * in Maze.java to automatically find the nearest Spam and move the pede.
   * reverse implemented in reversePede below.
   * 
   * returns 0 if game over, 1 to continue
   */ 
  public int advancePede(char direction) {
      MazeCell head = pedeCells.peekFirst();
      int row = head.getRow();
      int col = head.getCol();
      
      if (direction == north) {
        //add the new head to the pede
          pedeCells.addFirst(maze[row-1][col]);
      } if (direction == south) {
          pedeCells.addFirst(maze[row+1][col]);
      } if (direction == east) {
          pedeCells.addFirst(maze[row][col+1]);
      } if (direction == west) {
          pedeCells.addFirst(maze[row][col-1]);
      } 

      if (direction == auto) {
          MazeCell next = multiBFS(pedeCells.peekFirst(), SPAM);
          int nr = next.getRow();
          int nc = next.getCol();
          //add the new head to the pede
          pedeCells.addFirst(maze[nr][nc]);
      }
      
      setContents(head.getRow(), head.getCol(), PEDE);
      MazeCell newHead = pedeCells.peek();
      
          
      if (newHead.getContents() == WALL || newHead.getContents() == PEDE) {
        //0 is code for "game over"
          return 0; 
      } if(newHead.getContents() != SPAM) {
        //no new spam found
          MazeCell rem = pedeCells.removeLast();
          setContents(rem.getRow(), rem.getCol(), OPEN);
      }
      
      setContents(newhead.getRow(), newhead.getCol(), START);
      return 1;
  }
  
  /*
   * reversePede changes the order of the MazeCells in pedeCells so that
   * the pede reverses direction. returns the direction the pede should
   * advance.
   */
  public char reversePede() {
      LinkedList<MazeCell> newPede = new LinkedList<>();

      while(pedeCells.size()>0) {
          MazeCell nextCell = pedeCells.removeLast();
          newPede.addLast(nextCell); 
      }
      
      pedeCells = newPede; //we've just reversed the order of the pedeCells
      
      MazeCell head = pedeCells.peek(); 
      setContents(head.getRow(), head.getCol(), START);
      MazeCell tail = pedeCells.peekLast();
      setContents(tail.getRow(), tail.getCol(), PEDE);

      MazeCell next = pedeCells.get(1); //the "second" pedecell
      int row = head.getRow();
      int col = head.getCol();
      int rown = next.getRow();
      int coln = next.getCol();
      
      if (row == rown) {         //next and head in same row
          if (col - coln > 0) {  //next is to the left of head
              return east;
          } else {               //next is right of head
              return west;
          }
      }

      if(row - rown > 0) {       //next is above head
          return south;
      } else {                   //next is below head
          return north;
      }
  }
  
  //FOR TESTING PURPOSES
   public static void main(String[] args)
  {
    SpamMaze SM = new SpamMaze();

    System.out.println("SM is\n" + SM);
    MazeCell nextSpot = SM.multiBFS(SM.pedeCells.getFirst(), 'D');
    System.out.println("nextSpot is\n" + nextSpot);
    System.out.println("SM is\n" + SM);

    SM.advancePede(east);
    System.out.println("SM is\n" + SM);
    System.out.println("pedeCells is " + SM.pedeCells);

    SM.advancePede(east);
    System.out.println("SM is\n" + SM);
    System.out.println("pedeCells is " + SM.pedeCells);

    /*SM.advancePede(east);
    System.out.println("SM is\n" + SM);
    System.out.println("pedeCells is " + SM.pedeCells);*/

    SM.advancePede(south);
    System.out.println("SM is\n" + SM);
    System.out.println("pedeCells is " + SM.pedeCells);

    SM.advancePede(south);
    System.out.println("SM is\n" + SM);
    System.out.println("pedeCells is " + SM.pedeCells);/*

    char dir = SM.reversePede();
    SM.advancePede(dir);
    
    SM.advancePede(dir);
    System.out.println("pedeCells is " + SM.pedeCells);*/
    
    SM.resetPede();
    System.out.println("SM is\n" + SM);
    System.out.println("pedeCells is " + SM.pedeCells);
  }
  
}
