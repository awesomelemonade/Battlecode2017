package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class GardenerCommander {
	private static int scoutSpawnRate = 1000;
	private static boolean lowHealth = false;
	private static int scoutQueue = 0;
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
			if(!lowHealth){
				if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If commander about to die :(
					System.out.println("LOWHEALTH :(");
					controller.broadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER,
							controller.readBroadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER)+1);
					lowHealth = true;
				}
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
		if((scoutCount+scoutQueue)*scoutSpawnRate<controller.getTeamBullets()){
			Util.addReservedBullets(RobotType.SCOUT.bulletCost);
			scoutQueue++;
		}
		if(scoutQueue>0&&controller.getTeamBullets()>=RobotType.SCOUT.bulletCost){
			int tries = 10;
			Direction direction = Util.randomDirection();
			while((!controller.canBuildRobot(RobotType.SCOUT, direction))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			if(tries>0){
				controller.broadcast(Constants.BROADCAST_SCOUT_COUNT, scoutCount+1);
				controller.buildRobot(RobotType.SCOUT, direction);
				Util.subtractReservedBullets(RobotType.SCOUT.bulletCost);
				scoutQueue--;
			}
		}
	}
}
