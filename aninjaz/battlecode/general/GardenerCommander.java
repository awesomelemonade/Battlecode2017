package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class GardenerCommander {
	private static int scoutSpawnRate = 1000;
	private static int treeCount = 0;
	
	public static void run(RobotController controller) throws GameActionException{
		Util.broadcastCount = Constants.BROADCAST_GARDENER_COMMANDER_COUNT;
		Direction direction = Util.randomDirection();
		while(true){
			//Follows Archons
			//System.out.println("Trees: " + treeCount);
			direction = Util.tryRandomMove(direction);
			
			treeCount += controller.senseNearbyTrees(3, Team.NEUTRAL).length;
			
			hireSoldiers(controller);
			hireScout(controller);
			//hireTank(controller);
			hireLumberjack(controller);
			Util.yieldByteCodes();
		}
	}
	private static void hireLumberjack(RobotController controller) throws GameActionException {
		if(!controller.isBuildReady()){
			return;
		}

		int LJCount = controller.readBroadcast(Constants.BROADCAST_LJ_COUNT) + 1;
		if (LJCount*LJCount*LJCount*treeCount*.5 / controller.getRoundNum() > 1) {
			System.out.println("Trying to build LJ");
			int tries = 50;
			Direction direction = Util.randomDirection();
			while((!controller.canBuildRobot(RobotType.LUMBERJACK, direction))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			if(tries>0){
				treeCount /= 2;
				controller.broadcast(Constants.BROADCAST_LJ_COUNT, LJCount);
				controller.buildRobot(RobotType.LUMBERJACK, direction);
			}
		}
	}
	public static void hireTank(RobotController controller) throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		int tankCount = controller.readBroadcast(Constants.BROADCAST_TANK_COUNT);
		if(tankCount < 3){ //if being invaded or
			if(Util.getAvailableBullets() > 2500*(tankCount+1)){ //if at checkpoints 3000 - 6000 - 9000
				int tries = 10;
				Direction direction = Util.randomDirection();
				while((!controller.canBuildRobot(RobotType.TANK, direction))&&tries>0){
					direction = Util.randomDirection();
					tries--;
				}
				if(tries>0){
					controller.broadcast(Constants.BROADCAST_TANK_COUNT, tankCount+1);
					controller.buildRobot(RobotType.TANK, direction);
				}
			}
		}
	}
	public static void hireSoldiers(RobotController controller) throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		int soldierCount = controller.readBroadcast(Constants.BROADCAST_SOLDIER_COUNT);
		if(soldierCount<controller.readBroadcast(Constants.BROADCAST_GARDENER_COUNT)){
			if(Util.getAvailableBullets()>=RobotType.SOLDIER.bulletCost){
				int tries = 10;
				Direction direction = Util.randomDirection();
				while((!controller.canBuildRobot(RobotType.SOLDIER, direction))&&tries>0){
					direction = Util.randomDirection();
					tries--;
				}
				if(tries>0){
					controller.broadcast(Constants.BROADCAST_SOLDIER_COUNT, soldierCount+1);
					controller.buildRobot(RobotType.SOLDIER, direction);
				}
			}
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
