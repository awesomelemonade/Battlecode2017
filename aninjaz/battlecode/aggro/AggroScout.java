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
	public static void run(RobotController controller) throws GameActionException{
		Direction direction = Util.randomDirection();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			RobotInfo nearestGardener = getGardener(nearbyRobots);
			if(nearestGardener!=null){
				if(controller.getLocation().distanceTo(nearestGardener.getLocation())>1){
					Pathfinding.goTowardsScout(nearestGardener.getLocation());
				}
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(controller.getLocation().directionTo(nearestGardener.getLocation()));
				}
			}else{
				direction = Util.tryRandomMove(direction);
			}
			Util.yieldByteCodes();
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
		return robots[0];
	}
}
