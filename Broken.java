
import java.nio.ReadOnlyBufferException;
import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Broken {

  private byte isTargetNorth(int robotY, int targetY) {
    if (robotY > targetY) {
      return 1;
    } else if (robotY < targetY) {
      return -1;
    } else {
      return 0;
    }
  }

  private byte isTargetEast(int robotX, int targetX) {
    if (robotX > targetX) {
      return -1;
    } else if (robotX < targetX) {
      return 1;
    } else {
      return 0;
    }
  }

  public void controlRobot(IRobot robot) {
    int randno;
    int direction = IRobot.AHEAD;
    
    do {
      randno = (int) Math.round(Math.random() * 3);
      if (randno == 0) {
        direction = IRobot.LEFT;
      } else if (randno == 1) {
        direction = IRobot.RIGHT;
      } else if (randno == 2) {
        direction = IRobot.BEHIND;
      } else {
        direction = IRobot.AHEAD;
      }
    } while (robot.look(direction) == IRobot.WALL);

    System.out.println(isTargetEast(robot.getLocation().x,robot.getTargetLocation().x));
    System.out.println(isTargetNorth(robot.getLocation().y,robot.getTargetLocation().y));

    robot.face(direction); //Face the direction
  }

}
