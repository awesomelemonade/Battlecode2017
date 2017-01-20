package aninjaz.battlecode.util;

import aninjaz.battlecode.general.Constants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Pathfinding {
	private static RobotController controller;
	public static void goTowards(MapLocation location){
		float distance = controller.getLocation().distanceTo(location);
		Direction direction = controller.getLocation().directionTo(location);
		while(distance>Constants.EPSILON){
			RobotInfo[] robots = controller.senseNearbyRobots(controller.getType().strideRadius);
			
			
			distance = controller.getLocation().distanceTo(location);
			direction = controller.getLocation().directionTo(location);
		}
	}
}
