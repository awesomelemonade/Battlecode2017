package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

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
			hireScout(controller);
			Util.yieldByteCodes();
		}
	}
	public static void hireScout(RobotController controller) throws GameActionException{
		int scoutCount = controller.readBroadcast(Constants.BROADCAST_SCOUT_COUNT);
		if(scoutCount*1000<controller.getTeamBullets()){
			int tries = 10;
			Direction direction = Util.randomDirection();
			while((!controller.canBuildRobot(RobotType.SCOUT, direction))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			controller.broadcast(Constants.BROADCAST_SCOUT_COUNT, scoutCount+1);
			controller.buildRobot(RobotType.SCOUT, direction);
		}
	}
}
