package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class GardenerCommander {
	public static void run(RobotController controller) throws GameActionException{
		Direction direction = Util.randomDirection();
		while(true){
			//Follows Archons
			int tries = 10;
			while((!controller.canMove(direction))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			if(tries>0){
				controller.move(direction);
			}
			if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If commander about to die :(
				controller.broadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER,
						controller.readBroadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER)+1);
			}
			Util.yieldByteCodes();
		}
	}
}
