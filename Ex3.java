/*
Ex3
My headingController uses a hierarchical design.
The direction of the wall is not selectable.
The direction opposite to the target is the second choice.
The target direction is the first choice.
The robot moves closer to the target with each step.
Because of this, if it needs to move in a direction away from the target, it will get stuck.
Based on my design, I will set the previously traveled path as the second choice.
*/

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex3 {

  private byte isTargetNorth(IRobot robot) {
    if (robot.getLocation().y == robot.getTargetLocation().y) return 0;
    else return (byte)(robot.getLocation().y > robot.getTargetLocation().y?1:-1);
  }

  private byte isTargetEast(IRobot robot) {
    if (robot.getLocation().x == robot.getTargetLocation().x) return 0;
    else return (byte)(robot.getLocation().x > robot.getTargetLocation().x?-1:1);
  }

  private int lookHeading(IRobot robot, int direction) {
    return robot.look(switch (direction - robot.getHeading()) {
      case -3,1 -> IRobot.RIGHT;
      case -2,2 -> IRobot.BEHIND;
      case -1,3 -> IRobot.LEFT;
      default   -> IRobot.AHEAD;
    });
  }

  private int headingController(IRobot robot) {
    /*
     * When the value of direction equal to 0 means it's a wall
     * When the value of direction less than 0 means it's not the first choice
     */
    int direction[] = new int[]{IRobot.NORTH, IRobot.EAST, IRobot.SOUTH, IRobot.WEST};
    int result = 0;

    for (int i = 0; i < 4; i++) {
      direction[i] *= (lookHeading(robot, direction[i]) == IRobot.WALL)?0:1;
    }

    direction[0] *= isTargetNorth(robot)*1;
    direction[1] *= isTargetEast(robot)*1;
    direction[2] *= isTargetNorth(robot)*-1;
    direction[3] *= isTargetEast(robot)*-1;

    for (int i = 0; i < 4; i++) if (direction[i] > 0) result++;
    if (result == 0) for (int i = 0; i < 4; i++) direction[i] *= -1;//If all directions are either walls or second choices, make all second choices first choices

    result = (int)(Math.random()*4);
    while (direction[result] <= 0) result = (int)(Math.random()*4);//If the result not the first choise, re-randomize direction

    return direction[result];
  }

  public void reset() {
    ControlTest.printResults();
  }

  public void controlRobot(IRobot robot) {
    int heading = headingController(robot);
    
    ControlTest.test(heading, robot);
    robot.setHeading(heading);
  }

}