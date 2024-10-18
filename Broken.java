import uk.ac.warwick.dcs.maze.logic.IRobot;

public class Broken 
{

     private byte isTargetNorth(IRobot robot) {
          return (robot.getLocation().y < robot.getTargetLocation().y)?(byte)1:(byte)-1;
     }

     public void controlRobot(IRobot robot) {

          int direction;	
          int randno;
          
          direction = IRobot.AHEAD;
          
          do {
               randno = (int) Math.round(Math.random()*3);
               if ( randno == 0)
                    direction = IRobot.LEFT;
               else if (randno == 1)
                    direction = IRobot.RIGHT;
               else if (randno == 2)
                    direction = IRobot.BEHIND;
               else 
                    direction = IRobot.AHEAD;
          } while (robot.look(direction)==IRobot.WALL);
          
          robot.face(direction);  /* Face the direction */   

     }

}