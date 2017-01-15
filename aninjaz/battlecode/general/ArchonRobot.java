package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class ArchonRobot {
	private static Direction direction = Util.randomDirection();
	public static void run(RobotController controller) throws GameActionException{
		hireGardenerCommander(controller);
		while(true){
			Util.checkWin();
			int treeCount = controller.getTreeCount();
			int gardenerCount = controller.readBroadcast(Constants.BROADCAST_GARDENER_COUNT);
			if(treeCount>=(gardenerCount-1)*8+4){
				if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
					Direction direction = Util.randomDirection();
					if(controller.canHireGardener(direction)){
						controller.hireGardener(direction);
						controller.broadcast(Constants.BROADCAST_GARDENER_COUNT, gardenerCount+1);
					}
				}
			}
			
			int gardenerCommanderRequests = controller.readBroadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER);
			System.out.println("Requests: "+gardenerCommanderRequests);
			if(gardenerCommanderRequests>0){
				if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
					controller.broadcast(Constants.BROADCAST_REQUEST_ARCHON_GARDENER_COMMANDER, gardenerCommanderRequests-1);
					hireGardenerCommander(controller);
				}
			}
			
			//Moves in random direction
			
			int tries = 10;
			while((!controller.canMove(direction))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			if(tries>0){
				controller.move(direction);
			}
			
			//Shakes Trees
			
			TreeInfo[] neutralTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
			for(TreeInfo tree: neutralTrees){
				if(controller.canShake(tree.getID())){
					controller.shake(tree.getID());
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static void hireGardenerCommander(RobotController controller) throws GameActionException{
		System.out.println("Respawning GardenerCommander");
		Direction randomDirection = Util.randomDirection();
		while(!controller.canHireGardener(randomDirection)){
			randomDirection = Util.randomDirection();
		}
		controller.broadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS, 1);
		controller.hireGardener(randomDirection);
	}
}
