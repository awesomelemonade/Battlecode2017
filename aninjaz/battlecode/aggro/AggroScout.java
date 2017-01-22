package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class AggroScout {
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		AggroScout.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1,Constants.OTHER_TEAM);
			RobotInfo nearestGardener = getGardener(nearbyRobots);
			if(nearestGardener!=null){
				Pathfinding.goTowardsScout(nearestGardener.getLocation());
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(direction);
				}
			}
			else{
				direction = Util.tryRandomMove(direction);
			}
		}
	}
	public static RobotInfo getGardener(RobotInfo[] robots){
		if(robots.length==0){
			return null;
		}
		
		for(RobotInfo robot: robots){
			if(robot.getType()==RobotType.GARDENER){
				return robot;
			}
		}
		return null;
	}
}
