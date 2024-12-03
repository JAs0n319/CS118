/*Ex1
 *
 * passageExits - I did not implement a separate passageExits method, I count the number of passage when I decide the direction (In junctionAndCrossroad method).
 * nonwallExits - I traverse all four possible directions and check whether they are walls to determine the number of non-wall exits.
 * deadEnd   - 1. I set the direction to BACKWARD when encountering a dead end.
 *             2. If it is the starting point (the first step), I traverse all four directions to find one that is not blocked by a wall.
 * corridor  - 1. I initially set the direction to AHEAD.
 *             2. If the forward direction is blocked by a wall, I turn either left or right until I find a passable direction.
 * junction  - 1. The logic for handling junctions is the same as for crossroads.
 * crossroad - 1. I traverse all four directions.
 *             2. If a direction leads to a wall or a previously visited path, it is not considered.
 *             3. If all directions have been explored, the robot backtracks using the backtrack method.
 *             4. Otherwise, it chooses a random unexplored direction.
 * efficient - 1. I perform operations only when necessary to reduce traversal time, particularly in the corridor and backtrack methods.
 *             2. Random selection is optimized by limiting the array of available directions in the randomDirection method.
 * RobotData - 1. When designing the RobotData class, I separated all operations that modify the data. This improves robustness and makes the code more maintainable.
 * backtrack - 1. My backtrack method does not strictly follow the design guide to structure it as a mode.
 *             2. It acts as an independent function that is called whenever the robot determines all paths at a junction have been explored.
 *             3. I use RobotData class to find the index and use it to find initially arrived direction.
 *             4..Finally I set the direction to the opposite direction when first arrived.
 * explorer  - 1. I did not explicitly separate explorer and backtracker into distinct components. Instead, the robot explores the entire maze and backtracks whenever a part of the maze is fully explored.
 * repeated code - There is no repeated code in my implementation. I extracted potentially repetitive parts into separate methods to maintain modularity and avoid redundancy.
 * better way - I believe the way junction indices are stored could be improved by using a hash map. This change would enhance the time complexity for lookup operations.
 * worst case analysis - 1. The robot will always find the target if there is only one path to it.
 *                       2. The maximum number of steps is 2 * (maze area - wall area) because the robot may need to explore all paths and then backtrack.
 */

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex1 {
  
  private int pollRun = 0; // Number of step
  private RobotData robotData = new RobotData(); // Junction data

  /**
   * Logic Controller
   * 
   * @param robot IRobot object
   */
  public void controlRobot(IRobot robot) {
    int exits = nonwallExits(robot); // Number of nonwall Exits
    int direction = IRobot.AHEAD;

    if ((robot.getRuns() == 0)&&(pollRun == 0)) // Initialize junction data
      robotData.initRobotData();

    pollRun++; // Number of step += 1

    switch (exits) {
      case 1 -> direction = deadEnd(robot);
      case 2 -> direction = corridor(robot);
      case 3, 4 -> direction = junctionAndCrossroad(robot);
      default -> System.err.println("Invalid exits value");
    }

    robot.face(direction);
  }

  /**
   * Print the log and reset the junctionCounter
   */
  public void reset() {
    printJunction();
    robotData.resetJunctionCounter();
    //System.out.println("reset");
  }

  /**
   * Print the log
   */  
  private void printJunction() {
    for (int i = 0; i < robotData.getJunctionCounter(); i++) {
      System.out.print("Junction " + (i+1));
      System.out.print(" (x=" + robotData.getJuncX(i) + ",y=" + robotData.getJuncY(i) + ") heading ");
      switch (robotData.getArrived(i)) {
        case IRobot.NORTH -> System.out.println("NORTH");
        case IRobot.EAST  -> System.out.println("EAST");
        case IRobot.SOUTH -> System.out.println("SOUTH");
        case IRobot.WEST  -> System.out.println("WEST");
      }
    }
  }

  /**
   * Find the number of nonwall exits at current location
   * 
   * @param robot IRobot object
   * @return nonwall exits
   */
  private int nonwallExits(IRobot robot) {
    int numOfExits = 0;

    for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++)
      if (robot.look(i) != IRobot.WALL)
        numOfExits++;

    return numOfExits;
  }

  /**
   * Handle the situation where a dead end is encountered
   * 
   * @param robot IRobot object
   * @return The direction the robot should be face to
   */
  private int deadEnd(IRobot robot) {
    int direction = IRobot.BEHIND;

    if (robot.look(direction) == IRobot.WALL) // Dealing with the opening wall
      for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++)
        if (robot.look(i) != IRobot.WALL)
          direction = i;

    return direction;
  }

  /**
   * Handle the situation where a corridor is encountered
   * 
   * @param robot IRobot object
   * @return The direction the robot should be face to
   */
  private int corridor(IRobot robot) {
    int direction = IRobot.AHEAD;

    if (robot.look(direction) == IRobot.WALL) // If the robot is in a cornor
      if (robot.look(((direction + 1)%4) + IRobot.AHEAD) != IRobot.WALL) // Turn Right
        direction = ((direction + 1)%4) + IRobot.AHEAD;
      else
        direction = ((direction - 1)%4) + IRobot.AHEAD; // Turn Left

    return direction;
  }

  /**
   * Handle the situation where a junction or corssroad is encountered
   * 
   * @param robot IRobot object
   * @return The direction the robot should be face to
   */
  private int junctionAndCrossroad(IRobot robot) {
    int direction[] = {IRobot.AHEAD,IRobot.RIGHT,IRobot.BEHIND,IRobot.LEFT};
    int NumOfunexplored = 4;

    robotData.addJunction(robot.getLocation().x, robot.getLocation().y, robot.getHeading());

    for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++)
      if (robot.look(i) != robot.PASSAGE) {
        NumOfunexplored--;
        direction[i-IRobot.AHEAD] = 0;
      }
    
    if (NumOfunexplored == 0) {
      return backtrack(robot); // If all the ways are explored then backtrack
    }
    else
      return randomDirection(direction); // Choose a random unexplored way
  }

  /**
   * Find the direction that came from
   * 
   * @param robot IRobot object
   * @return The direction that came from
   */
  private int backtrack(IRobot robot) {
    int junctionNum = robotData.getJunctionNum(robot.getLocation().x,robot.getLocation().y);

    return switch (robot.getHeading() - robotData.getArrived(junctionNum)) { // Get the opposite direction of the way that came from
      case 1, -3 -> IRobot.RIGHT;
      case 2, -2 -> IRobot.AHEAD;
      case 3, -1 -> IRobot.LEFT;
      default ->    IRobot.BEHIND;
    };
  }

  /**
   * Get a random direction from given directions
   * 
   * @param ways[] Optional directions
   * @return Ramdom direction
   */
  private int randomDirection(int ways[]) {
    int availableCount = 0;

    for (int i : ways) // Count how many direction that can choose
      if (i > 0)
        availableCount++;
    
    int direction[] = new int[availableCount];

    for (int i : ways)
      if (i > 0) {
        direction[--availableCount] = i; // Copy the avaliable direction
      }

    return direction[(int)(Math.random()*direction.length)]; // return the random direction
  }

}

class RobotData {
  private static final int MAX_JUNCTIONS = 10000;
  private static int junctionCounter;
  private int juncX[];
  private int juncY[];
  private int arrived[];

  /**
   * Initialize the RobotData object
   * juncX and juncY will initialze to an empty array of size MAX_JUNCTIONS
   */
  public void initRobotData() {
    RobotData.junctionCounter = 0;
    this.juncX = new int[RobotData.MAX_JUNCTIONS];
    this.juncY = new int[RobotData.MAX_JUNCTIONS];
    this.arrived = new int[RobotData.MAX_JUNCTIONS];
  }

  /**
   * Set the junctionCounter to 0
   */
  public void resetJunctionCounter() {
    RobotData.junctionCounter = 0;
  }

  /**
   * Get the junctionCounter
   * 
   * @return junctionCounter
   */
  public int getJunctionCounter() {
    return RobotData.junctionCounter;
  }

  /**
   * Get the juncX
   * 
   * @return juncX
   */
  public int getJuncX(int i) {
    return this.juncX[i];
  }

  /**
   * Get the juncY
   * 
   * @return juncY
   */
  public int getJuncY(int i) {
    return this.juncY[i];
  }

  /**
   * Get the arrived
   * 
   * @return arrived
   */
  public int getArrived(int i) {
    return this.arrived[i];
  }

  /**
   * Get the index of the junction at the given coordinates
   * Return value = -1 means that this is the first time passing this junction
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @return junction index
   */
  public int getJunctionNum(int x, int y) {
    for (int i = 0; i < RobotData.junctionCounter; i++)
      if ((x == this.juncX[i])&&(y == this.juncY[i]))
        return i;
    
    return -1;
  }

  /**
   * Add the coordinates and absolute directions
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   * @param arrived absolute directions
   * @return junction index
   */
  public void addJunction(int x, int y, int direction) {
    if (getJunctionNum(x, y) != -1) return; //Return when the junction is recorded 

    this.juncX[RobotData.junctionCounter] = x;
    this.juncY[RobotData.junctionCounter] = y;
    this.arrived[RobotData.junctionCounter] = direction;
    RobotData.junctionCounter++;
  }
}