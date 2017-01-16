package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class ArchonRobot {
	private static Direction direction = Util.randomDirection();
	public static void run(RobotController controller) throws GameActionException{
		while(true){
			Util.checkWin();
			
			int gardenerCommanders = controller.readBroadcast(Constants.BROADCAST_GARDENER_COMMANDER_COUNT);
			int treeCount = controller.getTreeCount();
			int gardenerCount = controller.readBroadcast(Constants.BROADCAST_GARDENER_COUNT);
			if(gardenerCount<1){
				if(treeCount>=(gardenerCount-1)*8+4){
					if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
						Direction direction = Util.randomDirection();
						if(controller.canHireGardener(direction)){
							controller.hireGardener(direction);
							controller.broadcast(Constants.BROADCAST_GARDENER_COUNT, gardenerCount+1);
						}
					}
				}
			}else{
				if(gardenerCommanders<1){
					if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
						controller.broadcast(Constants.BROADCAST_GARDENER_COMMANDER_COUNT, gardenerCommanders+1);
						hireGardenerCommander(controller);
					}
				}
				if(treeCount>=(gardenerCount-1)*8+4){
					if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
						Direction direction = Util.randomDirection();
						if(controller.canHireGardener(direction)){
							controller.hireGardener(direction);
							controller.broadcast(Constants.BROADCAST_GARDENER_COUNT, gardenerCount+1);
						}
					}
				}
			}
			
			//Moves in random direction
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				direction = controller.getLocation().directionTo(nearbyRobots[0].getLocation()).opposite();
				if(controller.canMove(direction)){
					controller.move(direction);
				}else{
					direction = Util.tryRandomMove(direction);
				}
			}else{
				direction = Util.tryRandomMove(direction, RobotType.ARCHON.strideRadius*0.4f);
			}
			//Maybe move in random direction faster if it sees an enemy?
			
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
