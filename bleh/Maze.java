/*
 * The Maze class contains the ASCII representation of the maze
 * that will be used in SpamPede. It uses the Queue implemented
 * in Queue.java. The Maze class also contains the Breadth-First
 * Search algorithm that will be used to find the nearest spam 
 * and print the shortest path into the Maze. 
 *
 */ 

class Maze
{

  // MazeCell - an inner class supporting Maze
  //  
  // The following convention is used in mazes:
  // Walls are represented by '*'
  // Empty area is represented by the blank symbol ' '
  // Starting point is represented by 'S'
  // Destination (SPAM!) is represented by 'D'
  /**************************************************************************
   * start of MazeCell class
   */

  class MazeCell {

    private int row;                 // The row at which this cell is located
    private int col;                 // The col at which this cell is located
    private char contents;           // Each cell has contents (a char)
    private boolean visited;         // A cell can be marked as visited.
    private MazeCell parent;         // parent is where we came from!

    // Constructor of the MazeElement at row, col, with contents c
    //   "visited" is set to false, and "parent" is set to null
    private MazeCell(int row, int col, char c) {
      this.row = row;        // this is required to avoid name confusion!
      this.col = col;        // ditto
      this.contents = c;     
      this.visited = false;  // we haven't been here yet...
      this.parent = null;    // ... so we have no parent yet
    }

    // toString returns the string representation of a MazeElement
    public String toString()  { 
      return "[" + row + "," + col + "," + contents + "]"; }
    private boolean isWall()  { 
      return this.contents == '*';  }
    private boolean isOpen()  { 
      return this.contents == ' ' || this.contents == 'D'; }
  
    /*
     * Simple methods for Spampede
     */
    public int getRow() {
        return this.row;
    }
    public int getCol() {
        return this.col;
    }
    public char getContents() {
        return this.contents;
    }
    public void setContents(char newcontents) {
        this.contents = newcontents;
    }
  
  }


  /* data member for the Maze class...
   * a 2d rectangular array of MazeCells
   */ 
  protected MazeCell[][] maze;  // this is the maze!

  /* method: constructor
   * input: none
   * output: a maze containing the data in mazeStrings, below
   */
  protected Maze() {
    int HEIGHT = mazeStrings.length;
    int WIDTH = mazeStrings[0].length();
    this.maze = new MazeCell[HEIGHT][WIDTH];
    for (int r=0 ; r<HEIGHT ; ++r) {
      for (int c=0 ; c<WIDTH ; ++c) {
        maze[r][c] = new MazeCell(r,c,mazeStrings[r].charAt(c));
      }
    }
  }

  private static final String[] mazeStrings =  {
    "**************************************************",
    "*PS                                              *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                       **                       *",
    "*                       **                       *",
    "*                       **                       *",
    "*                       **                       *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "*                                        D       *",
    "*                                                *",
    "*                                                *",
    "*                                                *",
    "**************************************************"
  };

  // the findMazeCell method takes the maze and a char token
  // (either 'S' or 'D') and returns the MazeElement containing the
  // location of that token in the maze.
  // PROVIDED METHOD.  NO NEED TO ALTER.
  public MazeCell findMazeCell(char charToFind) {
    for (int r = 0; r < maze.length; ++r) {
      for (int c = 0; c < maze[r].length; ++c) {
        if (maze[r][c].contents == charToFind) {
          return maze[r][c];
        }
      }
    }
    return null;  // Error in finding the itemToFind: it's not there!
  }
  
  
  /*
   * shortcut for printing...
   */
  public static void pl(Object o) {System.out.println(o);}
  

  /* method: BFS
   * input: two maze cells
   * output: none; prints the path into the maze
   */
  public void BFS(MazeCell start, MazeCell destination) {
    Queue cellsToVisit = new Queue();

    start.visited = true;
    cellsToVisit.enqueue( start );

    while (!cellsToVisit.isEmpty()) {
      MazeCell current = (MazeCell)cellsToVisit.dequeue();

      if (current == destination) {//found a spam
        MazeCell pathElement = current.parent;

        //In order to find the path, we need to go backwards from current
        // to the MazeCell next to start and mark those as part of the path
        while (pathElement != start && pathElement != null) {
          pathElement.contents = 'o';
          pathElement = pathElement.parent;
        }
        pl("Maze is\n" + this);
        return; // done!
      }

      // find the neighbors
      int curRow = current.row;
      int curCol = current.col;
      MazeCell above = maze[(curRow-1)][curCol];
      MazeCell below = maze[(curRow+1)][curCol];
      MazeCell  right = maze[curRow][(curCol+1)];
      MazeCell  left = maze[curRow][(curCol-1)];
    
      MazeCell[] neighbors = { below, right, left, above };
      
      for (MazeCell mazeCell : neighbors) {
        if (!mazeCell.visited && !mazeCell.isWall()) { 
          //add the neighbors to the queue if we haven't already
          mazeCell.visited = true;
          mazeCell.parent = current;
          cellsToVisit.enqueue(mazeCell);
        }
      }
    } // end of while cellsToVisit is not empty 
    System.out.println("\nMaze not solvable!\n");
  }

  /* method: clearFlags
   * input: none
   * output: clears all visited and parent data members 
   *         also removes any path indicators, e.g., 'o'   
   * 
   * CURRENTLY UNUSED
   */
  private void clearFlags() {
    for (int row=0; row<maze.length; ++row) {
        for (int col=0; col<maze[i].length; ++col) {
            MazeCell it = maze[row][col];
            it.visited = false;
            it.parent = null;

            if (!it.isWall() && it.contents == 'o') {
              // reset contents of the path MazeCells
                it.contents = ' ';
            }
        }
    }
    }
  


  /* method: multiBFS
   * input: a starting cell and a char to seek
   * output: the maze cell that is BESIDE START and NEXT ALONG
   *         the path to the nearest destination!
   *
   * if there is no path, this method should return an open MazeCell
   * that is NEXT TO START
   *
   * if there is no open MazeCell that is NEXT TO START, this method
   * should return any MazeCell that is next to start (and it will crash)
   */
  protected MazeCell multiBFS(Maze.MazeCell start, char destination) {
    Queue findSpam = new Queue();
    
    start.visited = true;
    findSpam.enqueue(start);
    
    while(!findSpam.isEmpty()) {
        MazeCell currCell = (MazeCell)findSpam.dequeue();
        
       if (currCell.getContents() == destination) { //we found a spam!
            Maze.MazeCell pathElement = currCell.parent;
            MazeCell oldCell = currCell;

            //back-trace the path
            while (pathElement != start && pathElement != null) {
              pathElement = pathElement.parent;
              oldCell = oldCell.parent;
            }

           return oldCell;
        }
        
       //the neighbors
       MazeCell above = maze[currCell.row+1][currCell.col];
       MazeCell right = maze[currCell.row][currCell.col+1];
       MazeCell below = maze[currCell.row-1][currCell.col];
       MazeCell left = maze[currCell.row][currCell.col-1];
       
       Maze.MazeCell[] neighbors = {above, right, below, left};
       
       for (MazeCell mazeCell : neighbors) {
        //add neighbors to the queue
        if (!mazeCell.visited && !mazeCell.isWall()) {
          mazeCell.visited = true;
          mazeCell.parent = currCell;
          findSpam.enqueue(mazeCell);
        }
      }
    }
    
    return maze[start.row+1][start.col];
    
  }

  /*
   * toString converts a maze to a string for printing
   */
  public String toString()
  {
    String result = "\n";

    for (int r=0 ; r<maze.length ; ++r) {
      for (int c=0 ; c<maze[r].length ; ++c) {
        result += maze[r][c].contents;
      }
      result += "\n";
    }
    result += "\n";

    return result;
  }
  
  // PROVIDED METHOD.  NO NEED TO ALTER.
  // FOR TESTING ONLY
  public static void main(String args[]) {
    Maze M = new Maze();
    MazeCell start = M.findMazeCell('S');    // get the source
    
    MazeCell nextCellToGoTo = M.multiBFS(start, 'D');  
    
    System.out.println("\nM is" + M);         // M should not change!
    
  }  
  
}

