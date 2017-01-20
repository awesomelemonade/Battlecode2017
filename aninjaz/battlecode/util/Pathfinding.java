package aninjaz.battlecode.util;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Pathfinding {
	private static RobotController controller;
	public static void goTowards(MapLocation target) throws GameActionException{
		float distance = controller.getLocation().distanceTo(target);
		Direction direction = controller.getLocation().directionTo(target);
		while(distance>Constants.EPSILON){
			MapLocation bugPathing = bugPathfinding(direction, 0);
			if(controller.canMove(bugPathing)){
				controller.move(bugPathing);
			}else{
				controller.setIndicatorDot(controller.getLocation(), 1, 0, 0); //It's surrounded? ;o
			}
			distance = controller.getLocation().distanceTo(target);
			direction = controller.getLocation().directionTo(target);
			Util.yieldByteCodes();
		}
	}
	public static MapLocation bugPathfinding(Direction direction, float angle){
		RobotInfo[] robots = controller.senseNearbyRobots(controller.getType().strideRadius);
		MapLocation startpoint = controller.getLocation();
		MapLocation endpoint = controller.getLocation().add(direction, controller.getType().strideRadius);
		float currentDistance = startpoint.distanceTo(endpoint);
		float currentFatness = 0;
		for(RobotInfo robot: robots){
			MapLocation nearest = findNearest(robot.getLocation(), startpoint, endpoint);
			float distance = startpoint.distanceTo(nearest);
			float fatness = controller.getType().bodyRadius+robot.getType().bodyRadius;
			if(distance<fatness){
				endpoint = nearest;
				currentDistance = distance;
				currentFatness = fatness;
			}
		}
		if(currentFatness==0){ //No obstructions in the way
			return endpoint;
		}else{
			float newAngle = (float) Math.asin((currentFatness)/currentDistance);
			float rotatedAngle = angle+newAngle;
			if(rotatedAngle>Constants.TWO_PI){
				return endpoint;
			}else{
				return bugPathfinding(direction.rotateLeftRads(angle), angle+newAngle);
			}
		}
	}
	public static MapLocation findNearest(MapLocation p, MapLocation v, MapLocation w){
		//Assumes v and w are not the same point
		float t = (p.x-v.x)*(w.x-v.x)+(p.y-v.y)*(w.y-v.y);
		t = Math.max(0, Math.min(1, t)); //Clamp t between 0 and 1
		return Operation.project(v, w, t);
	}
	static class Move{
		Direction direction;
		float distance;
		public Move(Direction direction, float distance){
			this.direction = direction;
			this.distance = distance;
		}
	}
}
