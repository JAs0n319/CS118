/* Ex2
 * 
 * I use a linked list to store the initial direction.
 * Nodes in the linked list are removed once the junction is fully traversed.
 * This approach minimizes memory usage and eliminates the need to store the x and y coordinates of the junctions.
 * This program will fail to complete the Loopy Generator maze because it removes nodes, which can lead to an out-of-bounds error.
 */

import java.util.LinkedList;
import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex2 {
  
  private int pollRum = 0; // Number of step
  private boolean backtrackMode = false;
  LinkedList<Integer> backtrackDirection = new LinkedList<>();

  /**
   * Logic Controller
   * 
   * @param robot IRobot object
   */
  public void controlRobot(IRobot robot) {
    int exits = nonwallExits(robot); // Number of nonwall Exits
    int direction = IRobot.AHEAD;

    if ((robot.getRuns() == 0)&&(pollRum == 0)) // Initialize junction data
      backtrackDirection.removeAll(backtrackDirection);

    pollRum++; // Number of step += 1

    switch (exits) {
      case 1 -> direction = deadEnd(robot);
      case 2 -> direction = corridor(robot);
      case 3, 4 -> direction = junctionAndCrossroad(robot);
      default -> System.err.println("Invalid exits value");
    }

    robot.face(direction);
  }

  /**
   * Call when arrived target
   */
  public void reset() {
    /*
    for (int juction : backtrackDirection) {
      System.out.println(juction);
    }
    System.out.println(backtrackDirection.size());
    */
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
   * Find the number of passage exits at current location
   * 
   * @param robot IRobot object
   * @return passage exits
   */
  private int passageExits(IRobot robot) {
    int numOfPassage = 0;

    for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++)
      if (robot.look(i) == IRobot.PASSAGE)
        numOfPassage++;

    return numOfPassage;
  }
  
  /**
   * Handle the situation where a dead end is encountered
   * 
   * @param robot IRobot object
   * @return The direction the robot should be face to
   */
  private int deadEnd(IRobot robot) {
    int direction = IRobot.BEHIND;
    backtrackMode = true;
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
   * Handle the situation where a junction and crossroad is encountered
   * 
   * @param robot IRobot object
   * @return The direction the robot should be face to
   */
  private int junctionAndCrossroad(IRobot robot) {
    if ((backtrackMode)&&(passageExits(robot) == 0)) { // If this junction fully traversed
      int direction = backtrackDirection.getLast();
      backtrackDirection.removeLast(); // Remove this node

      return switch (robot.getHeading() - direction) { // Convert absolute directions to relative directions
        case 1, -3 -> IRobot.LEFT;
        case 2, -2 -> IRobot.BEHIND;
        case 3, -1 -> IRobot.RIGHT;
        default ->    IRobot.AHEAD;
      };
    }

    else if (backtrackMode) { // If this junction is not a new one
      int directions[] = new int[]{IRobot.AHEAD, IRobot.RIGHT, IRobot.BEHIND, IRobot.LEFT};

      for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) // Remove unconsidered directions
        if (robot.look(i) != IRobot.PASSAGE)
          directions[i-IRobot.AHEAD] = 0;
      
      backtrackMode = false;
      return randomDirection(directions);
    }
    else { // If this junction is a new one
      int directions[] = new int[]{IRobot.AHEAD, IRobot.RIGHT, IRobot.BEHIND, IRobot.LEFT};

      for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) // Remove unconsidered directions
        if (robot.look(i) != IRobot.PASSAGE)
          directions[i-IRobot.AHEAD] = 0;

      backtrackDirection.add(oppositeDirection(robot.getHeading())); // Add new node
      return randomDirection(directions);
    }
  }

  /**
   * Get a opposite absolute direction
   * 
   * @param direction Initial absolute directions
   * @return Opposite absolute direction
   */
  private int oppositeDirection(int direction) {
    return switch(direction) {
      case IRobot.NORTH -> IRobot.SOUTH;
      case IRobot.EAST  -> IRobot.WEST;
      case IRobot.SOUTH -> IRobot.NORTH;
      case IRobot.WEST -> IRobot.EAST;
      default -> -1;
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