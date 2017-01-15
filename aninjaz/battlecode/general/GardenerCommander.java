package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class GardenerCommander {
	private static int scoutSpawnRate = 1000;
	private static boolean lowHealth = false;
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
					controller.broadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER,
							controller.readBroadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER)+1);
				}
				lowHealth = true;
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
		System.out.println(scoutCount+" - "+(scoutCount*scoutSpawnRate)+" - "+controller.getTeamBullets());
		if(scoutCount*scoutSpawnRate<controller.getTeamBullets()){
			int tries = 10;
			Direction direction = Util.randomDirection();
			while((!controller.canBuildRobot(RobotType.SCOUT, direction))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			if(tries>0){
				System.out.println("Hiring Scout");
				controller.broadcast(Constants.BROADCAST_SCOUT_COUNT, scoutCount+1);
				controller.buildRobot(RobotType.SCOUT, direction);
				System.out.println("Done Hiring Scout");
			}
		}
	}
}
