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
	public static final int REACHED_GOAL = 0;
	public static final int HAS_MOVED = 1;
	public static final int HAS_NOT_MOVED = 2;
	public static RobotController controller;
	public static int goTowardsBidirectional(MapLocation target) throws GameActionException{
		float distance = controller.getLocation().distanceTo(target);
		if(distance==0){
			return REACHED_GOAL;
		}
		Direction direction = controller.getLocation().directionTo(target);
		controller.setIndicatorDot(target, 0, 255, 0);
		MapLocation bugPathing = bugPathfindingBidirectional(direction, Math.min(distance, controller.getType().strideRadius), Constants.RANDOM_TRIES);
		if(controller.canMove(bugPathing)){
			controller.move(bugPathing);
			return HAS_MOVED;
		}else{
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(controller.getLocation().directionTo(bugPathing), 5f), 0, 0, 0);
			return HAS_NOT_MOVED;
		}
	}
	public static MapLocation bugPathfindingBidirectional(Direction targetDirection, float targetDistance, int tries) throws GameActionException{
		MapLocation endpoint = controller.getLocation().add(targetDirection, targetDistance);
		if(tries<=0){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			return endpoint;
		}
		if(controller.canMove(targetDirection, targetDistance)){
			return endpoint;
		}
		MapLocation startpoint = controller.getLocation();
		controller.setIndicatorLine(startpoint, endpoint, 255, 128, 0);
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(targetDistance+controller.getType().bodyRadius);
		for(TreeInfo tree: nearbyTrees){
			MapLocation t = findNearest(tree.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+tree.getRadius();
			float distance = t.distanceTo(tree.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(tree.getLocation().add(Util.randomDirection(), (float)(Math.random()*tree.getRadius())), 128, 0, 128);
			if(distance<fatness){
				distance = controller.getLocation().distanceTo(tree.getLocation());
				float angle = (float)Math.asin(fatness/distance);
				Direction direction = controller.getLocation().directionTo(tree.getLocation());
				Direction leftDirection = direction.rotateLeftRads(angle);
				Direction rightDirection = direction.rotateRightRads(angle);
				float leftBetween = Math.abs(targetDirection.radiansBetween(leftDirection));
				float rightBetween = Math.abs(targetDirection.radiansBetween(rightDirection));
				if(leftBetween<rightBetween){
					if(leftBetween==0){
						return bugPathfindingBidirectional(leftDirection.rotateLeftRads(Constants.EPSILON), targetDistance, tries-1);
					}else{
						return bugPathfindingBidirectional(leftDirection, targetDistance, tries-1);
					}
				}else{
					if(rightBetween==0){
						return bugPathfindingBidirectional(rightDirection.rotateRightRads(Constants.EPSILON), targetDistance, tries-1);
					}else{
						return bugPathfindingBidirectional(rightDirection, targetDistance, tries-1);
					}
				}
			}
		}
		RobotInfo[] nearbyRobots = controller.senseNearbyRobots(targetDistance+controller.getType().bodyRadius);
		for(RobotInfo robot: nearbyRobots){
			MapLocation t = findNearest(robot.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+robot.getRadius();
			float distance = t.distanceTo(robot.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(robot.getLocation().add(Util.randomDirection(), (float)(Math.random()*robot.getRadius())), 128, 0, 128);
			if(distance<fatness){
				distance = controller.getLocation().distanceTo(robot.getLocation());
				float angle = (float)Math.asin(fatness/distance);
				Direction direction = controller.getLocation().directionTo(robot.getLocation());
				Direction leftDirection = direction.rotateLeftRads(angle);
				Direction rightDirection = direction.rotateRightRads(angle);
				float leftBetween = Math.abs(targetDirection.radiansBetween(leftDirection));
				float rightBetween = Math.abs(targetDirection.radiansBetween(rightDirection));
				if(leftBetween<rightBetween){
					if(leftBetween==0){
						return bugPathfindingBidirectional(leftDirection.rotateLeftRads(Constants.EPSILON), targetDistance, tries-1);
					}else{
						return bugPathfindingBidirectional(leftDirection, targetDistance, tries-1);
					}
				}else{
					if(rightBetween==0){
						return bugPathfindingBidirectional(rightDirection.rotateRightRads(Constants.EPSILON), targetDistance, tries-1);
					}else{
						return bugPathfindingBidirectional(rightDirection, targetDistance, tries-1);
					}
				}
			}
		}
		return endpoint; //No Collisions!
	}
	public static int goTowards(MapLocation target) throws GameActionException{
		float distance = controller.getLocation().distanceTo(target);
		if(distance==0){
			return REACHED_GOAL;
		}
		Direction direction = controller.getLocation().directionTo(target);
		controller.setIndicatorDot(target, 0, 255, 0);
		MapLocation bugPathing = bugPathfinding(direction, Math.min(distance, controller.getType().strideRadius), Constants.RANDOM_TRIES);
		if(controller.canMove(bugPathing)){
			controller.move(bugPathing);
			return HAS_MOVED;
		}else{
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(controller.getLocation().directionTo(bugPathing), 5f), 0, 0, 0);
			return HAS_NOT_MOVED;
		}
	}
	public static MapLocation bugPathfinding(Direction targetDirection, float targetDistance, int tries) throws GameActionException{
		MapLocation endpoint = controller.getLocation().add(targetDirection, targetDistance);
		if(tries<=0){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			return endpoint;
		}
		if(controller.canMove(targetDirection, targetDistance)){
			return endpoint;
		}
		MapLocation startpoint = controller.getLocation();
		controller.setIndicatorLine(startpoint, endpoint, 255, 128, 0);
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(targetDistance+controller.getType().bodyRadius);
		for(TreeInfo tree: nearbyTrees){
			MapLocation t = findNearest(tree.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+tree.getRadius();
			float distance = t.distanceTo(tree.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(tree.getLocation().add(Util.randomDirection(), (float)(Math.random()*tree.getRadius())), 128, 0, 128);
			if(distance<fatness){
				distance = controller.getLocation().distanceTo(tree.getLocation());
				Direction direction = controller.getLocation().directionTo(tree.getLocation())
						.rotateRightRads((float)Math.asin(fatness/distance));
				float angleBetween = targetDirection.radiansBetween(direction);
				if(angleBetween==0){
					return bugPathfinding(direction.rotateRightRads(Constants.EPSILON), targetDistance, tries-1);
				}else{
					return bugPathfinding(direction, targetDistance, tries-1);
				}
			}
		}
		RobotInfo[] nearbyRobots = controller.senseNearbyRobots(targetDistance+controller.getType().bodyRadius);
		for(RobotInfo robot: nearbyRobots){
			MapLocation t = findNearest(robot.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+robot.getRadius();
			float distance = t.distanceTo(robot.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(robot.getLocation().add(Util.randomDirection(), (float)(Math.random()*robot.getRadius())), 128, 0, 128);
			if(distance<fatness){
				distance = controller.getLocation().distanceTo(robot.getLocation());
				Direction direction = controller.getLocation().directionTo(robot.getLocation())
						.rotateRightRads((float)Math.asin(fatness/distance));
				float angleBetween = targetDirection.radiansBetween(direction);
				if(angleBetween==0){
					return bugPathfinding(direction.rotateRightRads(Constants.EPSILON), targetDistance, tries-1);
				}else{
					return bugPathfinding(direction, targetDistance, tries-1);
				}
			}
		}
		return endpoint; //No Collisions!
	}

	public static int goTowardsScout(MapLocation target) throws GameActionException{
		float distance = controller.getLocation().distanceTo(target);
		if(distance==0){
			return REACHED_GOAL;
		}
		Direction direction = controller.getLocation().directionTo(target);
		controller.setIndicatorDot(target, 0, 255, 0);
		MapLocation bugPathing = bugPathfindingScout(direction, Math.min(distance, controller.getType().strideRadius), Constants.RANDOM_TRIES);
		if(controller.canMove(bugPathing)){
			controller.move(bugPathing);
			return HAS_MOVED;
		}else{
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(controller.getLocation().directionTo(bugPathing), 5f), 0, 0, 0);
			return HAS_NOT_MOVED;
		}
	}
	public static MapLocation bugPathfindingScout(Direction targetDirection, float targetDistance, int tries) throws GameActionException{
		MapLocation endpoint = controller.getLocation().add(targetDirection, targetDistance);
		if(tries<=0){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			return endpoint;
		}
		if(controller.canMove(targetDirection, targetDistance)){
			return endpoint;
		}
		MapLocation startpoint = controller.getLocation();
		controller.setIndicatorLine(startpoint, endpoint, 255, 128, 0);
		RobotInfo[] nearbyRobots = controller.senseNearbyRobots(targetDistance+controller.getType().bodyRadius);
		for(RobotInfo robot: nearbyRobots){
			MapLocation t = findNearest(robot.getLocation(), startpoint, endpoint);
			float fatness = controller.getType().bodyRadius+robot.getRadius();
			float distance = t.distanceTo(robot.getLocation());
			controller.setIndicatorDot(t, 255, 0, 128);
			controller.setIndicatorDot(robot.getLocation().add(Util.randomDirection(), (float)(Math.random()*robot.getRadius())), 128, 0, 128);
			if(distance<fatness){
				distance = controller.getLocation().distanceTo(robot.getLocation());
				Direction direction = controller.getLocation().directionTo(robot.getLocation())
						.rotateRightRads((float)Math.asin(fatness/distance));
				float angleBetween = targetDirection.radiansBetween(direction);
				if(angleBetween==0){
					return bugPathfindingScout(direction.rotateRightRads(Constants.EPSILON), targetDistance, tries-1);
				}else{
					return bugPathfindingScout(direction, targetDistance, tries-1);
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
}
