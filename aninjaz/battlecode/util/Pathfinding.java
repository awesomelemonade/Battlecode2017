package aninjaz.battlecode.util;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;

public class Pathfinding {
	private static RobotController controller;
	
	private static MapLocation currentLocation;
	private static RobotInfo[] nearbyRobots;
	private static TreeInfo[] nearbyTrees;
	private static Direction[] robotLeftAngles;
	private static Direction[] robotRightAngles;
	private static Direction[] treeLeftAngles;
	private static Direction[] treeRightAngles;
	
	private static float bodyRadius;
	private static float sensorRadius;
	
	public static void init(RobotController controller){
		Pathfinding.controller = controller;
		bodyRadius = controller.getType().bodyRadius;
		sensorRadius = controller.getType().sensorRadius;
	}
	public static MapLocation pathfindScout(MapLocation goal) throws GameActionException{
		return pathfindScout(controller.getLocation().directionTo(goal), Math.min(controller.getLocation().distanceTo(goal), sensorRadius));
	}
	public static MapLocation pathfindScout(Direction direction, float targetDistance) throws GameActionException{
		currentLocation = controller.getLocation();
		if(targetDistance==0){
			controller.setIndicatorDot(currentLocation, 255, 0, 0);
			return currentLocation;
		}
		MapLocation target = currentLocation.add(direction, targetDistance);
		nearbyRobots = controller.senseNearbyRobots(targetDistance);
		nearbyTrees = new TreeInfo[0]; //for scouts, we can just set nearbyTrees to an empty array
		if(isClear(currentLocation, target)){
			return target;
		}
		generateTangentAngles();
		Direction[] angles = splitAngles(direction, direction, 0, 0, 10);
		if(angles[0]==null&&angles[1]==null){
			return pathfind(direction, Math.max(0, targetDistance-1));
		}
		if(angles[0]==null){
			return currentLocation.add(angles[1], targetDistance);
		}
		if(angles[1]==null){
			return currentLocation.add(angles[0], targetDistance);
		}
		float angle1 = direction.radiansBetween(angles[0]);
		float angle2 = angles[1].radiansBetween(direction);
		if(angle1<angle2){
			return currentLocation.add(angles[0], targetDistance);
		}else{
			return currentLocation.add(angles[1], targetDistance);
		}
	}
	public static MapLocation pathfind(MapLocation goal) throws GameActionException{
		//controller.setIndicatorLine(controller.getLocation(), goal, 0, 255, 255);
		return pathfind(controller.getLocation().directionTo(goal), Math.min(controller.getLocation().distanceTo(goal), sensorRadius));
	}
	//Tangent Bug Pathfinding
	public static MapLocation pathfind(Direction direction, float targetDistance) throws GameActionException{
		currentLocation = controller.getLocation();
		MapLocation target = currentLocation.add(direction, targetDistance);
		nearbyRobots = controller.senseNearbyRobots(targetDistance);
		nearbyTrees = controller.senseNearbyTrees(targetDistance); //for scouts, we can just set nearbyTrees to an empty array
		if(isClear(currentLocation, target)){
			return target;
		}
		generateTangentAngles();
		Direction[] angles = splitAngles(direction, direction, 0, 0, 10);
		if(angles[0]==null&&angles[1]==null){
			return pathfind(direction, Math.max(0, targetDistance-1));
		}
		if(angles[0]==null){
			return currentLocation.add(angles[1], targetDistance);
		}
		if(angles[1]==null){
			return currentLocation.add(angles[0], targetDistance);
		}
		float angle1 = direction.radiansBetween(angles[0]);
		float angle2 = angles[1].radiansBetween(direction);
		if(angle1<angle2){
			return currentLocation.add(angles[0], targetDistance);
		}else{
			return currentLocation.add(angles[1], targetDistance);
		}
	}
	public static void generateTangentAngles() throws GameActionException{
		robotLeftAngles = new Direction[nearbyRobots.length];
		robotRightAngles = new Direction[nearbyRobots.length];
		treeLeftAngles = new Direction[nearbyTrees.length];
		treeRightAngles = new Direction[nearbyTrees.length];
		for(int i=0;i<nearbyRobots.length;++i){
			RobotInfo robot = nearbyRobots[i];
			MapLocation location = robot.getLocation();
			Direction direction = currentLocation.directionTo(location);
			float angle = getTangentAngle(location, robot.getRadius());
			robotLeftAngles[i] = direction.rotateLeftRads(angle);
			robotRightAngles[i] = direction.rotateRightRads(angle);
		}
		for(int i=0;i<nearbyTrees.length;++i){
			TreeInfo tree = nearbyTrees[i];
			MapLocation location = tree.getLocation();
			Direction direction = currentLocation.directionTo(location);
			float angle = getTangentAngle(location, tree.getRadius());
			treeLeftAngles[i] = direction.rotateLeftRads(angle);
			treeRightAngles[i] = direction.rotateRightRads(angle);
		}
		for (Direction direction : treeLeftAngles) {
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(direction, 7f), 0, 255, 255);
		}
		for (Direction direction : treeRightAngles) {
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(direction, 7f), 0, 0, 255);
		}
	}
	public static Direction[] splitAngles(Direction leftDirection, Direction rightDirection, float left, float right, int tries) throws GameActionException{
		if(tries<=0){
			System.out.println("MORE THAN 10 TRIES :(");
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			return new Direction[]{leftDirection, rightDirection};
		}
		for(int i=0;i<nearbyRobots.length;++i){
			Direction leftAngle = robotLeftAngles[i];
			Direction rightAngle = robotRightAngles[i];
			if(inBetween(leftDirection, leftAngle, rightAngle)){
				System.out.println("LEFT INTERSECTION: "+leftDirection+" - "+leftAngle+" - "+rightAngle+" - "+left);
				left+=leftDirection.radiansBetween(leftAngle);
				System.out.println("AFTER: "+left);
				if(left>=Math.PI){
					leftDirection = null;
				}else{
					return splitAngles(leftAngle, rightDirection, left, right, tries-1);
				}
			}
			if(inBetween(rightDirection, leftAngle, rightAngle)){
				System.out.println("RIGHT INTERSECTION: "+rightDirection+" - "+leftAngle+" - "+rightAngle+" - "+right);
				right+=rightDirection.radiansBetween(rightAngle);
				System.out.println("AFTER: "+right);
				if(right<=-Math.PI){
					rightDirection = null;
				}else{
					return splitAngles(leftDirection, rightAngle, left, right, tries-1);
				}
			}
		}
		for(int i=0;i<nearbyTrees.length;++i){
			Direction leftAngle = treeLeftAngles[i];
			Direction rightAngle = treeRightAngles[i];
			if(inBetween(leftDirection, leftAngle, rightAngle)){
				System.out.println("LEFT INTERSECTION: "+leftDirection+" - "+leftAngle+" - "+rightAngle+" - "+left);
				left+=leftDirection.radiansBetween(leftAngle);
				System.out.println("AFTER: "+left);
				if(left>=Math.PI){
					leftDirection = null;
				}else{
					return splitAngles(leftAngle, rightDirection, left, right, tries-1);
				}
			}
			if(inBetween(rightDirection, leftAngle, rightAngle)){
				System.out.println("RIGHT INTERSECTION: "+rightDirection+" - "+leftAngle+" - "+rightAngle+" - "+right);
				right+=rightDirection.radiansBetween(rightAngle);
				System.out.println("AFTER: "+right);
				if(right<=-Math.PI){
					rightDirection = null;
				}else{
					return splitAngles(leftDirection, rightAngle, left, right, tries-1);
				}
			}
		}
		return new Direction[]{leftDirection, rightDirection};
	}
	public static boolean inBetween(Direction x, Direction a, Direction b) throws GameActionException{
		if(x==null){
			return false;
		}
		if(x.radians==a.radians||x.radians==b.radians){
			return false;
		}
		float angle1 = Math.abs(x.radiansBetween(a));
		float angle2 = Math.abs(x.radiansBetween(b));
		float total = Math.abs(a.radiansBetween(b));
		return angle1+angle2==total;
	}
	public static float getTangentAngle(MapLocation location, float radius){
		return (float) Math.asin((radius+bodyRadius)/controller.getLocation().distanceTo(location));
	}
	public static boolean isClear(MapLocation from, MapLocation to){
		for(RobotInfo robot: nearbyRobots){
			MapLocation location = robot.getLocation();
			if(findNearest(location, from, to).distanceTo(location)<=robot.getRadius()+bodyRadius){
				return false;
			}
		}
		for(TreeInfo tree: nearbyTrees){
			MapLocation location = tree.getLocation();
			if(findNearest(location, from, to).distanceTo(location)<=tree.getRadius()+bodyRadius){
				return false;
			}
		}
		return true;
	}
	public static MapLocation findNearest(MapLocation p, MapLocation v, MapLocation w){
		float distance = v.distanceSquaredTo(w);
		if(distance==0){
			return v;
		}
		float t = ((p.x-v.x)*(w.x-v.x)+(p.y-v.y)*(w.y-v.y))/distance;
		t = Math.max(0, Math.min(1, t)); //Clamp t between 0 and 1
		return Operation.project(v, w, t);
	}
}
