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

public class HasslerScout {
	public static void run(RobotController controller) throws GameActionException{
		Direction direction = Util.randomDirection();
		while(true){
			if(controller.getRoundNum()<150&&controller.getTeamBullets()<300){
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
				for(TreeInfo tree: nearbyTrees){
					if(tree.getContainedBullets()>0){
						if(controller.canShake(tree.getID())){
							controller.shake(tree.getID());
						}else{
							MapLocation location = Pathfinding.pathfindScout(tree.getLocation());
							if(controller.canMove(location)){
								controller.move(location);
							}else{
								controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
							}
						}
					}
				}
			}
			else{
				RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
				RobotInfo nearestGardener = getGardener(nearbyRobots);
				if(nearestGardener!=null){
					MapLocation location = Pathfinding.pathfindScout(nearestGardener.getLocation());
					if(controller.canMove(location)){
						controller.move(location);
					}else{
						controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
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
