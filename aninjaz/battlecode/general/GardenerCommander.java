package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class GardenerCommander {
	private static int scoutSpawnRate = 1000;
	public static void run(RobotController controller) throws GameActionException{
		Util.broadcastCount = Constants.BROADCAST_GARDENER_COMMANDER_COUNT;
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
			hireScout(controller);
			Util.yieldByteCodes();
		}
	}
	public static void hireScout(RobotController controller) throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		int scoutCount = controller.readBroadcast(Constants.BROADCAST_SCOUT_COUNT);
		if(scoutCount*scoutSpawnRate<controller.getTeamBullets()){
			if(Util.getAvailableBullets()>=RobotType.SCOUT.bulletCost){
				int tries = 10;
				Direction direction = Util.randomDirection();
				while((!controller.canBuildRobot(RobotType.SCOUT, direction))&&tries>0){
					direction = Util.randomDirection();
					tries--;
				}
				if(tries>0){
					controller.broadcast(Constants.BROADCAST_SCOUT_COUNT, scoutCount+1);
					controller.buildRobot(RobotType.SCOUT, direction);
				}
			}
		}
	}
}
