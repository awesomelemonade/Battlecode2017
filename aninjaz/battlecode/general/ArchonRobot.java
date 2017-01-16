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
		while(true){
			Util.checkWin();
			
			int gardenerCommanders = controller.readBroadcast(Constants.BROADCAST_GARDENER_COMMANDER_COUNT);
			if(gardenerCommanders<1){
				if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
					controller.broadcast(Constants.BROADCAST_GARDENER_COMMANDER_COUNT, gardenerCommanders+1);
					hireGardenerCommander(controller);
				}
			}
			
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
			
			
			//Moves in random direction
			
			direction = Util.tryRandomMove(direction);
			
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
		Direction randomDirection = Util.randomDirection();
		while(!controller.canHireGardener(randomDirection)){
			randomDirection = Util.randomDirection();
		}
		controller.broadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS, 1);
		controller.hireGardener(randomDirection);
	}
}
