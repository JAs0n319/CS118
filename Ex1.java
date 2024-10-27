/*
Ex1
I first randomly selected a direction as the direction for the robot to move forward.
Then I will make a judgment. if the robot is facing a wall, I will Re-randomize direction until it is not facing a wall.
I separated the random direction selection into a separate function to reduce duplication of code.
 */

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex1 {

	private int randomDirection() {
    return switch ((int) (Math.random() * 4)) { //Return the corresponding direction based on the value of Math.random()*4
      case 0 -> IRobot.LEFT;
      case 1 -> IRobot.RIGHT;
      case 2 -> IRobot.BEHIND;
      case 3 -> IRobot.AHEAD;
      default -> -1;
    };
  }

  private void logPrinter(IRobot robot, int direction) {
    int wallNum = 0;

    for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) if (robot.look(i) == IRobot.WALL) {
      wallNum++; //Count the number of walls around the current position
    }

    System.out.println("I'm going " +
      switch (direction) {
        case IRobot.LEFT ->   "left ";
        case IRobot.RIGHT ->  "right ";
        case IRobot.BEHIND -> "backwards ";
        case IRobot.AHEAD ->  "forward ";
        default ->            "Error!!!";
      } +
      switch (wallNum) {
        case 0 ->   "at a crossroads";
        case 1 ->   "at a junction";
        case 2 ->   "down a corridor";
        case 3 ->   "at a deadend";
        default ->  "Error!!!";
      }
    );
  }

  public void controlRobot(IRobot robot) {
    int direction = randomDirection(); //Randomly initialize directions
    
    while (robot.look(direction) == IRobot.WALL) {
      direction = randomDirection(); //Reselect direction when the robot is face to a wall
    }
    
    logPrinter(robot, direction);
    robot.face(direction); //Face the direction
  }

}
