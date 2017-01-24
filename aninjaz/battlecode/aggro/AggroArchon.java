package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class AggroArchon {
	private static RobotController controller;
	
	public static void run(RobotController controller) throws GameActionException{
		AggroArchon.controller = controller;
		Direction direction = Util.randomDirection();
		hireGardener();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				Direction opposite = controller.getLocation().directionTo(nearbyRobots[0].getLocation()).opposite();
				if(controller.canMove(opposite)){
					controller.move(opposite);
				}
			}else{
				move:{
					TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
					for(TreeInfo tree: nearbyTrees){
						if(tree.getContainedBullets()>0){
							if(controller.canShake(tree.getID())){
								controller.shake(tree.getID());
								continue;
							}else{
								controller.setIndicatorLine(controller.getLocation(), tree.getLocation(), 255, 255, 0);
								MapLocation location = Pathfinding.pathfind(tree.getLocation());
								if(controller.canMove(location)){
									controller.move(location);
								}else{
									controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
								}
								break move;
							}
						}
					}
					direction = Util.tryRandomMove(direction);
				}
			}
			if(controller.getRoundNum()>600){
				tryHireGardener();
			}
			Util.yieldByteCodes();
		}
	}	
	public static void tryHireGardener() throws GameActionException{
		Direction direction = Util.randomDirection();
		int tries = 10;
		while((!controller.canHireGardener(direction))&&tries>0){
			direction = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			controller.hireGardener(direction);
		}
	}
	public static void hireGardener() throws GameActionException{
		Direction direction = Util.randomDirection();
		while((!controller.canHireGardener(direction))){
			direction = Util.randomDirection();
		}
		controller.hireGardener(direction);
	}
}
