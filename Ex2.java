/*
Ex2
The value of Math.random()*8 will be randomly between 0 and 7.99.
So when I convert it to int it will be a number between 0 and 7 with equal probability.

I don't want to randomly choose the direction every time, so I initialize the direction to IRobot.AHEAD.

The initial uneven probabilities are due to the use of Math.round(), so their value ranges are
0 -> (0,0.499)		0.5/3
1 -> (0.5,1.499)	1.0/3
2 -> (1.5,2.499)	1.0/3
3 -> (2.5,2.999)	0.5/3

I think the current code is not fundamentally different from the previous one, they all run randomly.
I think it would be a disruptive change if the search could be done logically, similar to using DFS or other algorithms.
*/

import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Ex2
{

  private int randomDirection() {

    return switch ((int)(Math.random()*4)) { //Return the corresponding direction based on the value of Math.random()*4
      case 0 -> IRobot.LEFT;
      case 1 -> IRobot.RIGHT;
      case 2 -> IRobot.BEHIND;
      case 3 -> IRobot.AHEAD;
      default-> -1;
    };

  }

  private void logPrinter(IRobot robot, int direction) {

    int wallNum = 0;
    for (int i = IRobot.AHEAD; i <= IRobot.LEFT; i++) if (robot.look(i) == IRobot.WALL) wallNum++; //Count the number of walls around the current position
    System.out.println( "I'm going " +
      switch (direction) {
        case IRobot.LEFT	-> "left ";
        case IRobot.RIGHT	-> "right ";
        case IRobot.BEHIND	-> "backwards ";
        case IRobot.AHEAD	-> "forward ";
        default				-> "Error!!!";
      }+
      switch (wallNum) {
        case 0	-> "at a crossroads";
        case 1	-> "at a junction";
        case 2	-> "down a corridor";
        case 3	-> "at a deadend";
        default	-> "Error!!!";
    });

  }

  public void controlRobot(IRobot robot) {

    int direction = IRobot.AHEAD;
    while ((robot.look(direction) == IRobot.WALL)||((int)(Math.random()*8) == 0)) direction = randomDirection(); //Reselect direction when the current direction is a wall or Math.random()*8 == 0
    logPrinter(robot,direction);
    robot.face(direction); //Face the direction

  }

}