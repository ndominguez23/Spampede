//  Spampede.java
//  Written by: Neftali Dominguez
//  Last Modified: 18 Oct 2014

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * Spampede is an example using both inheritance and interfaces
 */
public class Spampede extends JApplet
  implements ActionListener, KeyListener, Runnable
{

  Image image;                // off-screen buffer
  Graphics g;                 // that buffer's graphical tools

  int sleepTime = 50;         // 50 milliseconds between updates
  int cycleNum;               // number of update cycles so far

  private SpamMaze themaze;   // the model for our Spampede game
  private char dir;           // the direction we're moving now...
  String message;             // A String that will be printed on screen

  // DEFINE CONSTANTS FOR YOUR PROGRAM HERE TO AVOID MAGIC VALUES!
  public static final int STRINGX = 10;
  public static final int STRINGY = 485;
  public static final int GAMEBOARDHEIGHT = 490; //Recommended values: 
       //490 with both menu bar and buttons
       //525 with only the menu bar
       //515 with only buttons
  public static final Color BGCOLOR = Color.white;

  // BELOW ARE DEFINITIONS OF BUTTONS AND MENU ITEMS WHICH WILL APPEAR
  private JButton newGameButton;
  private JButton pauseButton;
  private JButton startButton;

  private JMenu gameMenu;
  private JMenuItem newGameItem;
  private JMenuItem pauseItem;
  private JMenuItem startItem;

  // Here are other data members you might like to use (optional)...
  private AudioClip audioSpam;    // This is for playing a sound
  private AudioClip audioCrunch;  // This is for playing a sound
  private Image     imageSpam;    // This is for loading an image

  private Color     currentColor; // This is for the big square


  // Initialize the applet.  
  // This is called each time the page is reloaded.
  public void init(){
    // set up the maze here
    this.themaze = new SpamMaze();
    this.dir = 'E'; //initally facing east

    this.addKeyListener(this);                // listen for key events
    this.setLayout(new BorderLayout());       //set up layout on the form

    //beginning of button code
    //add a panel for buttons
    JPanel buttonPane = new JPanel(new FlowLayout());
    buttonPane.setBackground(BGCOLOR);
    add(buttonPane, BorderLayout.PAGE_START);


    newGameButton = new JButton("New Game");  // the text in the button
    newGameButton.addActionListener(this);    // watch for button presses
    newGameButton.addKeyListener(this);       // listen for key presses here
    buttonPane.add(newGameButton);            // add button to the panel

    pauseButton = new JButton("Pause");       // a second button
    pauseButton.addActionListener(this);
    pauseButton.addKeyListener(this);
    buttonPane.add(pauseButton);

    startButton = new JButton("Start");       // a third button
    startButton.addActionListener(this);
    startButton.addKeyListener(this);
    buttonPane.add(startButton);
    //end of button code

    //beginning of menu bar code
    //Set up the menu bar
    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);

    //add a menu to contain items
    gameMenu = new JMenu("Game");             //The menu name
    menuBar.add(gameMenu);                    //Add the menu to the menu bar
    
    newGameItem = new JMenuItem("New Game");  //the text in the menu item
    newGameItem.addActionListener(this);      //Watch for button presses
    newGameItem.addKeyListener(this);         //Listen for key presses here 
    gameMenu.add(newGameItem);                //Add the item to the menu

    pauseItem = new JMenuItem("Pause");       //A second menu item
    pauseItem.addActionListener(this);
    pauseItem.addKeyListener(this);
    gameMenu.add(pauseItem);

    startItem = new JMenuItem("Start");       //A third menu item
    startItem.addActionListener(this);
    startItem.addKeyListener(this);
    gameMenu.add(startItem);
    //end of menu bar code

    // Sets up the back (off-screen) buffer for drawing, named image
    image = createImage(getSize().width, GAMEBOARDHEIGHT);
    g = image.getGraphics();                 // g holds the drawing routines
    clear();                                 // clears the screen
    reset();                                 // Set up the game internals!

    //add a central panel which holds the buffer (the game board)
    add(new ImagePanel(image), BorderLayout.CENTER);

    // This is an example of loading in an image and a sound file.
    try {
      URL url = getCodeBase();
      audioSpam = getAudioClip(url,"Spam.au");
      audioCrunch = getAudioClip(url,"crunch.au");
      imageSpam = getImage(url,"spam.gif");
      System.out.println("successful loading of audio/images!");
    } catch (Exception e) {
      System.out.println("problem loading audio/images!");
      audioSpam = null;
      audioCrunch = null;
      imageSpam = null;
    }

    drawEnvironment();   // re-render the environment to our offscreen buffer
    repaint();           // re-render the environment to the screen
  }

  /*
   * reset() is called whenever we want to reset the game internally
   * e.g. pressing "new game" or when the pede hits a wall
   */

  void reset() {
    message = "Welcome to Spampede! (version 2)";
    currentColor = Color.green;   
    if (audioSpam != null) {      //Play audio clip
      audioSpam.play();
    }
    themaze = new SpamMaze();
    this.dir = SpamMaze.east;
  }

  // This is where you will draw your 2D array of colored squares
  // Notice that all drawing occurs in the off-screen buffer "image".
  //     and that the drawing commands themselves are held in the Graphics g
  // repaint() copies the image to the screen for fast rendering and smooth motion
  void drawEnvironment() {
    clear();                       // first, clear everything
    
    for (int r=0; r<themaze.getRows(); r++) {
        for (int c=0; c<themaze.getColumns(); c++) {
            char cont = themaze.getContents(r, c);
            //each of the blocks is 10x10 pixels
            int rcoord = 10*r;
            int ccoord = 10*c;
            switch(cont) {
                case ' ': 
                    g.setColor(Color.white); 
                    g.fillRect(ccoord,rcoord, 10, 10); break;
                case 'D':
                    g.setColor(Color.green); 
                    g.fillRect(ccoord,rcoord,10,10); break;
                case 'S':
                    g.setColor(Color.red); 
                    g.fillRect(ccoord, rcoord, 10, 10); break;
                case 'P':
                    g.setColor(Color.magenta); 
                    g.fillRect(ccoord, rcoord, 10, 10); break;
                case '*':
                    g.setColor(Color.blue); 
                    g.fillRect(ccoord, rcoord, 10, 10); break;
            }
        }
    }
  }


  /*
   * updateCentipede calls advancePede, which checks whether the pede
   * runs into spam, a wall, itself, or an open space. advancePede
   * moves the pede one block and returns 0 if it's game over;
   * updateCentipede then calls reset
   */
  void updateCentipede() {
      int stillalive = themaze.advancePede(this.dir); 

      if (stillalive == 0) { //reset if game over
          reset();
      }
  }

  // this method adds and removes spam every 25 and 50 cycles respectively
  void updateSpam() {
    if (cycleNum%25 == 0) {
        themaze.addSpam();
    }
    
    if (cycleNum%50 == 0) {
        themaze.removeSpam();
    }
  }

  // displays the Welcome to Spampede message
  void displayMessage() {
    g.setColor(Color.blue);
    g.drawString(message, STRINGX, STRINGY);
  }

  // Contains functions we want to be called every cycle
  // called from run().
  void cycle() {
    updateCentipede();  // update the Spampede deque
    updateSpam();       // update the Spam deque
    drawEnvironment();  // draw things to buffer
    displayMessage();   // display messages
    repaint();          // send buffer to the screen
    ++cycleNum;         // One cycle just elapsed
  }

  // checking for button presses
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();

    if (source == newGameButton || source == newGameItem) {  
      reset();
    }

    if (source == pauseButton || source == pauseItem) {
      pause();
    }

    if (source == startButton || source == startItem) {
      go();
    }

    this.requestFocus(); // makes sure the Applet keeps kbd focus
  }

  // Here's how keyboard events are handled...
  public void keyPressed(KeyEvent evt) {
    switch(evt.getKeyChar()) {
      case 'i'://north
        this.dir = SpamMaze.north;
        break;
      case 'k'://south
        this.dir = SpamMaze.south;
        break;
      case 'l'://east
        this.dir = SpamMaze.east;
        break;
      case 'j'://west
        this.dir = SpamMaze.west;
        break;
      case 'r':
        this.dir = themaze.reversePede();
        break;
      case 'a'://autonomous
        this.dir = SpamMaze.auto;
        break;
      case 'S':
        if (audioCrunch != null)    // Example of playing a sound
          audioCrunch.play();
      default:
        currentColor = Color.cyan;
    }
  }

  public void keyReleased(KeyEvent evt) {}
  public void keyTyped(KeyEvent evt) {}

  /*
   * A method to clear the applet's drawing area
   */
  void clear()
  {
    g.setColor(BGCOLOR);
    g.fillRect(0, 0, getSize().width, getSize().height);
    g.setColor(Color.blue);
    g.drawRect(0, 0, getSize().width-1, GAMEBOARDHEIGHT-1);
  }

  /*
   * The following methods and data members are used
   *   to implement the Runnable interface and to
   *   support pausing and resuming the applet.
   *
   */
  Thread thread;           // the thread controlling the updates
  boolean threadSuspended; // whether or not the thread is suspended
  boolean running;         // whether or not the thread is stopped

  /*
   * This is the method that calls the "cycle()"
   * method every so often (every sleepTime milliseconds).
   */
  public void run() {
    while (running) {
      try {
        if (thread != null) {
          thread.sleep(sleepTime);
          synchronized(this) {
            while (threadSuspended)
              wait(); // sleeps until notify() wakes it up
          }
        }
      }
      catch (InterruptedException e) { ; }

      cycle();  // this represents 1 update cycle for the environment
    }
    thread = null;
  }

  /* This is the method attached to the "Start" button
   */
  public synchronized void go() {
    if (thread == null)  {
      thread = new Thread(this);
      running = true;
      thread.start();
      threadSuspended = false;
    } else {
      threadSuspended = false;
    }
    notify(); // wakes up the call to wait(), above
  }

  /*
   * This is the method attached to the "Pause" button
   */
  void pause() {
    if (thread == null)
      ;
    else
      threadSuspended = true;
  }

  /*
   * This is a method called when you leave the page
   *   that contains the applet. It stops the thread altogether.
   */
  public synchronized void stop() {
    running = false;
    notify();
  }

  /* This is the end of the Spampede class */
}
