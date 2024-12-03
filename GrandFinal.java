import uk.ac.warwick.dcs.maze.logic.IRobot;

public class GrandFinal {
  private RobotData robotData = new RobotData();
  private int pollRun = 0;

  public void controlRobot(IRobot robot) {
    int direction = IRobot.AHEAD;
    int exits = nonwallExits(robot);

    if ((robot.getRuns() == 0) && (pollRun == 0))
      robotData.init();
    
    if (robot.getRuns() != 0) {
      direction = robotData.getPath(robot.getLocation().x, robot.getLocation().y);
      robot.setHeading(direction);
      return;
    }

    this.pollRun++;

    switch (exits) {
      case 1 -> direction = deadEnd(robot);
      case 2 -> direction = corridor(robot);
      case 3, 4 -> direction = junctionAndCrossroad(robot);
      default -> System.err.println("Invalid exits value");
    }

    robotData.updateLocation(robot.getLocation().x, robot.getLocation().y);
    robot.face(direction);
    robotData.recordPath(robot.getLocation().x, robot.getLocation().y, robot.getHeading());
  }

  public void reset() {
    robotData.reset();
    //System.out.println("Reset");
  }

  private int nonwallExits(IRobot robot) {
    int exits = 0;

    for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++)
      if (robot.look(i) != IRobot.WALL)
        exits++;
    
    return exits;
  }

  private int deadEnd(IRobot robot) {
    int direction = IRobot.BEHIND;
    robotData.markPath(robot.getLocation().x, robot.getLocation().y);

    if (robot.look(direction) == IRobot.WALL) // Handling the case of facing a wall at the start
      for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++)
        direction = (robot.look(i) == IRobot.WALL)?direction:i;
    
    return direction;
  }

  private int corridor(IRobot robot) {
    int direction = IRobot.AHEAD;
    robotData.markPath(robot.getLocation().x, robot.getLocation().y);

    if (robot.look(direction) == IRobot.WALL) // Handling the case of being at a corner
      direction = (robot.look(IRobot.LEFT) == IRobot.WALL)?IRobot.RIGHT:IRobot.LEFT;
    
    return direction;
  }

  private int junctionAndCrossroad(IRobot robot) {
    robotData.markJunction(robot.getLocation().x, robot.getLocation().y);

    if (areAllPathsExceptEntranceUnmarked(robot)) // Only the entrance that just came from is marked
      return randomDirection(robot, 0); // Pick an arbitrary unmarked entrance
    else if ((robotData.getMark(robotData.getLastX(), robotData.getLastY()) < 2)&&
             (robotData.getMark(robotData.getLastX(), robotData.getLastY()) > 0))
        return IRobot.BEHIND; // Pick the entrance that just came from
    else {
      if (randomDirection(robot, 0) != -1)
        return randomDirection(robot, 0); // Pick the entrance with 0 mark
      else if (randomDirection(robot, 1) != -1)
        return randomDirection(robot, 1); // Pick the entrance with 1 mark
      else if ((randomDirection(robot, -1) != -1))
        return randomDirection(robot, -1); // Pick the entrance that is junction
    }
    
    System.err.println("No optional branches");
    return IRobot.AHEAD;
  }

  private boolean areAllPathsExceptEntranceUnmarked(IRobot robot) {
    int rules[][] = {{0,-1},{1,0},{0,1},{-1,0}};
    int x, y;

    for (int i = 0; i < 4; i++) {
      x = robot.getLocation().x + rules[i][0];
      y = robot.getLocation().y + rules[i][1];

      if ((robotData.getLastX() != x)&&(robotData.getLastY() != y))
        if (robotData.getMark(x, y) != 0)
          return false;
    }

    return true;
  }

  private int randomDirection(IRobot robot, int mark) {
    int rules[][] = {{0,-1},{1,0},{0,1},{-1,0}};
    int x, y;
    int numOfOptional = 0;
    int optionalDirection[] = new int[4];

    for (int i = 0; i < 4; i++) {
      x = robot.getLocation().x + rules[i][0];
      y = robot.getLocation().y + rules[i][1];

      if (robotData.getMark(x, y) == mark)
        if (robot.look(absToRel(robot, IRobot.NORTH + i)) != IRobot.WALL)
          optionalDirection[numOfOptional++] = absToRel(robot, IRobot.NORTH + i);
    }

    if (numOfOptional == 0) // No suitable choice
      return -1;
    else
      return optionalDirection[(int)(Math.random()*numOfOptional)];
  }

  private int absToRel(IRobot robot, int abs) {
    return switch(robot.getHeading() - abs) {
      case 0 -> IRobot.AHEAD;
      case -1, 3 -> IRobot.RIGHT;
      case -2, 2 -> IRobot.BEHIND;
      case -3, 1 -> IRobot.LEFT;
      default -> {
        System.err.println("Invalid absolute direction");
        yield -1;
      }
    };
  }
}

class RobotData {
  private static int MAX_WIDTH = 505;
  private int mazeMap[][];
  private int mazePath[][];
  private int lastX;
  private int lastY;

  public void init() {
    this.mazeMap = new int[RobotData.MAX_WIDTH][RobotData.MAX_WIDTH];
    this.mazePath = new int[RobotData.MAX_WIDTH][RobotData.MAX_WIDTH];
    this.lastX = 0;
    this.lastY = 0;
  }

  public void reset() {
    this.mazeMap = new int[RobotData.MAX_WIDTH][RobotData.MAX_WIDTH];
    this.lastX = 0;
    this.lastY = 0;
  }

  public void updateLocation(int x, int y) {
    this.lastX = x;
    this.lastY = y;
  }

  public void markPath(int x, int y) {
    this.mazeMap[x][y]++;
  }

  public void markJunction(int x, int y) {
    this.mazeMap[x][y] = -1;
  }

  public int getLastX() {
    return this.lastX;
  }

  public int getLastY() {
    return this.lastY;
  }

  public int getPath(int x, int y) {
    return this.mazePath[x][y];
  }

  public int getMark(int x, int y) {
    if ((x > RobotData.MAX_WIDTH)||(y > RobotData.MAX_WIDTH)) {
      System.err.println("X or Y out of Bounds");
    }
    return this.mazeMap[x][y];
  }

  public void recordPath(int x, int y, int orientation) {
    this.mazePath[x][y] = orientation;
  }
}