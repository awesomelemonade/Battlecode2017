package aninjaz.battlecode.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class ArchonRobot {
	private static RobotController controller;
	private static Direction direction = Util.randomDirection();
	public static void run(RobotController controller) throws GameActionException{
		ArchonRobot.controller = controller;
		while(true){
			Util.checkWin();
			
			List<Integer> broadcasterinos = new ArrayList<Integer>();
			for(int i=999;i>=970;--i){
				broadcasterinos.add(controller.readBroadcast(i));
			}
			System.out.println(Arrays.toString(broadcasterinos.toArray()));
			
			
			if(controller.isBuildReady()){
				int gardenerCommanderCount = controller.readBroadcast(Constants.BROADCAST_GARDENER_COMMANDER_COUNT);
				int treeCount = controller.getTreeCount();
				int gardenerCount = controller.readBroadcast(Constants.BROADCAST_GARDENER_COUNT);
				if(gardenerCount<1){
					if(treeCount>=(gardenerCount-1)*8+4){
						if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
							hireGardener(gardenerCount);
						}
					}
				}else{
					if(gardenerCommanderCount<1){
						if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
							hireGardenerCommander(gardenerCommanderCount);
						}
					}
					if(treeCount>=(gardenerCount-1)*8+4){
						if(Util.getAvailableBullets()>=RobotType.GARDENER.bulletCost){
							hireGardener(gardenerCount);
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
	public static void hireGardenerCommander(int gardenerCommanderCount) throws GameActionException{
		Direction randomDirection = Util.randomDirection();
		int tries = 10;
		while(!controller.canHireGardener(randomDirection)&&tries>0){
			randomDirection = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			controller.broadcast(Constants.BROADCAST_GARDENER_COMMANDER_COUNT, gardenerCommanderCount+1);
			controller.broadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS,
					controller.readBroadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS)+1);
			controller.hireGardener(randomDirection);
		}
	}
	public static void hireGardener(int gardenerCount) throws GameActionException{
		Direction direction = Util.randomDirection();
		if(controller.canHireGardener(direction)){
			controller.hireGardener(direction);
			controller.broadcast(Constants.BROADCAST_GARDENER_COUNT, gardenerCount+1);
		}
	}
}
