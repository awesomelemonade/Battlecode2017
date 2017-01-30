package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class CollectorScout {
	public static void run(RobotController controller) throws GameActionException{
		Direction direction = Util.randomDirection();
		while(true){
			MapLocation shoot = null;
			move:{
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
				for(TreeInfo tree: nearbyTrees){
					if(tree.getContainedBullets()>0){
						if(controller.canShake(tree.getID())){
							controller.shake(tree.getID());
						}else{
							MapLocation location = Pathfinding.pathfindScout(tree.getLocation());
							if(controller.canMove(location)){
								controller.move(location);
								break move;
							}else{
								controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
							}
						}
					}
				}
				RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
				RobotInfo nearestGardener = getGardener(nearbyRobots);
				if(nearestGardener!=null){
					shoot = nearestGardener.getLocation();
					if(nearestGardener.getType()==RobotType.LUMBERJACK){
						float distance = nearestGardener.getLocation().distanceTo(controller.getLocation());
						if(distance<4f){
							Direction dir = nearestGardener.getLocation().directionTo(controller.getLocation());
							if(controller.canMove(dir)){
								controller.move(dir);
							}
							break move;
						}
						if(distance<6f){
							break move;
						}
					}
					MapLocation location = Pathfinding.pathfindScout(nearestGardener.getLocation());
					if(controller.canMove(location)){
						controller.move(location);
						break move;
					}else{
						controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
					}
				}
				direction = Util.tryRandomMove(direction);
			}
			if(shoot!=null){
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(controller.getLocation().directionTo(shoot));
				}
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
