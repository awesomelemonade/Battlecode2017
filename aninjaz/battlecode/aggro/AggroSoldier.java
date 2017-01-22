package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class AggroSoldier {
	private static MapLocation initialArchon;
	private static int currentTarget = -1;
	private static boolean reachedInitialArchon = false;
	public static void run(RobotController controller) throws GameActionException{
		Direction direction = Util.randomDirection();
		initialArchon = controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0];
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			RobotInfo bestRobot = getBestRobot(nearbyRobots);
			if(bestRobot!=null){
				Pathfinding.goTowardsBidirectional(bestRobot.getLocation());
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(controller.getLocation().directionTo(bestRobot.getLocation()));
				}
			}else{
				if(reachedInitialArchon){
					direction = Util.tryRandomMove(direction);
				}else{
					if(Pathfinding.goTowardsBidirectional(initialArchon)==Pathfinding.REACHED_GOAL){
						reachedInitialArchon = true;
					}
					if(controller.getLocation().distanceTo(initialArchon)>controller.getType().sensorRadius){
						if(controller.canFireSingleShot()){
							controller.fireSingleShot(controller.getLocation().directionTo(initialArchon));
						}
					}
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static RobotInfo getBestRobot(RobotInfo[] robots){
		if(robots.length==0){
			return null;
		}
		if(currentTarget!=-1){
			for(RobotInfo robot: robots){
				if(robot.getID()==currentTarget){
					return robot;
				}
			}
		}
		for(RobotInfo robot: robots){
			if(robot.getType()!=RobotType.ARCHON){
				currentTarget = robot.getID();
				return robot;
			}
		}
		return robots[0];
	}
}
