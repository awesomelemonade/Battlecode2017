package aninjaz.battlecode.util;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;

public class Pathfinding {
	public static RobotController controller;
	public static void goTowards(MapLocation target) throws GameActionException{
		float distance = controller.getLocation().distanceTo(target);
		Direction direction = controller.getLocation().directionTo(target);
		System.out.println(target);
		float senseDistance = controller.getType().strideRadius+controller.getType().bodyRadius;
		while(distance>Constants.EPSILON){
			System.out.println("Yield: New Turn");
			controller.setIndicatorDot(target, 0, 255, 0);
			MapLocation bugPathing = bugPathfinding(direction, target, Math.min(distance, senseDistance), 10);
			if(controller.canMove(bugPathing)){
				controller.move(bugPathing);
			}else{
				controller.setIndicatorLine(controller.getLocation(), bugPathing, 0, 0, 0);
			}
			distance = controller.getLocation().distanceTo(target);
			direction = controller.getLocation().directionTo(target);
			Util.yieldByteCodes();
		}
	}
	public static MapLocation bugPathfinding(Direction targetDirection, MapLocation target, float targetDistance, int tries) throws GameActionException{
		MapLocation startpoint = controller.getLocation();
		MapLocation endpoint = controller.getLocation().add(targetDirection, targetDistance);
		if(tries<=0){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			return endpoint;
		}
		RobotInfo[] nearbyRobots = controller.senseNearbyRobots(targetDistance);
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(targetDistance);
		controller.setIndicatorLine(startpoint, endpoint, 255, 128, 0);
		for(TreeInfo tree: nearbyTrees){
			MapLocation t = findNearest(tree.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+tree.getRadius();
			float distance = t.distanceTo(tree.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(tree.getLocation(), 128, 0, 128);
			if(distance<=fatness){
				distance = controller.getLocation().distanceTo(tree.getLocation());
				Direction direction = controller.getLocation().directionTo(tree.getLocation());
				float angle = (float)Math.asin(fatness/distance);
				Direction leftDirection = direction.rotateLeftRads(angle);
				Direction rightDirection = direction.rotateRightRads(angle);
				Direction finalDirection = startpoint.directionTo(target);
				if(Math.abs(leftDirection.radiansBetween(finalDirection))<Math.abs(rightDirection.radiansBetween(finalDirection))){
					return bugPathfinding(leftDirection, target, targetDistance, tries-1);
				}else{
					return bugPathfinding(rightDirection, target, targetDistance, tries-1);
				}
			}
		}
		for(RobotInfo robot: nearbyRobots){
			MapLocation t = findNearest(robot.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+robot.getType().bodyRadius;
			float distance = t.distanceTo(robot.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(robot.getLocation(), 128, 0, 128);
			if(distance<=fatness){
				distance = controller.getLocation().distanceTo(robot.getLocation());
				Direction direction = controller.getLocation().directionTo(robot.getLocation());
				float angle = (float)Math.asin(fatness/distance);
				Direction leftDirection = direction.rotateLeftRads(angle);
				Direction rightDirection = direction.rotateRightRads(angle);
				Direction finalDirection = startpoint.directionTo(target);
				if(Math.abs(leftDirection.radiansBetween(finalDirection))<Math.abs(rightDirection.radiansBetween(finalDirection))){
					return bugPathfinding(leftDirection, target, targetDistance, tries-1);
				}else{
					return bugPathfinding(rightDirection, target, targetDistance, tries-1);
				}
			}
		}
		return endpoint; //No Collisions!
	}
	public static MapLocation findNearest(MapLocation p, MapLocation v, MapLocation w){
		float distance = v.distanceSquaredTo(w);
		if(distance==0){
			return v;
		}
		//Assumes v and w are not the same point
		float t = ((p.x-v.x)*(w.x-v.x)+(p.y-v.y)*(w.y-v.y))/distance;
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
