package aninjaz.battlecode.util;

import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
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
	private static float strideRadius;
	
	public static void init(RobotController controller){
		Pathfinding.controller = controller;
		bodyRadius = controller.getType().bodyRadius;
		strideRadius = controller.getType().strideRadius;
	}
	public static MapLocation pathfindTankLumberjack(MapLocation target, float radius) throws GameActionException{
		return pathfindTankLumberjack(controller.getLocation().add(controller.getLocation().directionTo(target), controller.getLocation().distanceTo(target)-radius));
	}
	//Tangent Bug Pathfinding

	//Tangent Bug Pathfinding
	public static MapLocation pathfindTankLumberjack(MapLocation target) throws GameActionException{
		currentLocation = controller.getLocation();
		Direction direction = controller.getLocation().directionTo(target);
		float distance = currentLocation.distanceTo(target)+Constants.EPSILON;
		nearbyRobots = controller.senseNearbyRobots(distance);
		nearbyTrees = controller.senseNearbyTrees(distance, controller.getTeam()); //for scouts, we can just set nearbyTrees to an empty array
		if(isClear(currentLocation, target)){
			return target;
		}
		generateTangentAngles();
		Direction[] angles = splitAngles(direction, direction, 0, 0, 10);
		if(angles[0]==null&&angles[1]==null){
			float newDistance = controller.getLocation().distanceTo(target)-1;
			if(newDistance<=0){
				return currentLocation;
			}else{
				return pathfindTankLumberjack(currentLocation.add(direction, newDistance));
			}
		}
		if(angles[0]==null){
			MapLocation location = currentLocation.add(angles[1], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				float newDistance = controller.getLocation().distanceTo(target)-1;
				return pathfindTankLumberjack(currentLocation.add(direction, newDistance));
			}else{
				return location;
			}
		}
		if(angles[1]==null){
			MapLocation location = currentLocation.add(angles[0], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				float newDistance = controller.getLocation().distanceTo(target)-1;
				return pathfindTankLumberjack(currentLocation.add(direction, newDistance));
			}else{
				return location;
			}
		}
		float angle1 = direction.radiansBetween(angles[0]);
		float angle2 = angles[1].radiansBetween(direction);
		if(angle1<angle2){
			MapLocation location = currentLocation.add(angles[0], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				location = currentLocation.add(angles[1], strideRadius);
				if(!controller.onTheMap(location, bodyRadius)){
					float newDistance = controller.getLocation().distanceTo(target)-1;
					return pathfindTankLumberjack(currentLocation.add(direction, newDistance));
				}else{
					return location;
				}
			}else{
				return location;
			}
		}else{
			MapLocation location = currentLocation.add(angles[1], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				location = currentLocation.add(angles[0], strideRadius);
				if(!controller.onTheMap(location, bodyRadius)){
					float newDistance = controller.getLocation().distanceTo(target)-1;
					return pathfindTankLumberjack(currentLocation.add(direction, newDistance));
				}else{
					return location;
				}
			}else{
				return location;
			}
		}
	}
	public static MapLocation pathfindScout(MapLocation target, float radius) throws GameActionException{
		return pathfindScout(controller.getLocation().add(controller.getLocation().directionTo(target), controller.getLocation().distanceTo(target)-radius));
	}
	//Tangent Bug Pathfinding

	//Tangent Bug Pathfinding
	public static MapLocation pathfindScout(MapLocation target) throws GameActionException{
		currentLocation = controller.getLocation();
		Direction direction = controller.getLocation().directionTo(target);
		float distance = currentLocation.distanceTo(target)+Constants.EPSILON;
		nearbyRobots = controller.senseNearbyRobots(distance);
		nearbyTrees = new TreeInfo[]{}; //for scouts, we can just set nearbyTrees to an empty array
		if(isClear(currentLocation, target)){
			return target;
		}
		generateTangentAngles();
		Direction[] angles = splitAngles(direction, direction, 0, 0, 10);
		if(angles[0]==null&&angles[1]==null){
			float newDistance = controller.getLocation().distanceTo(target)-1;
			if(newDistance<=0){
				return currentLocation;
			}else{
				return pathfindScout(currentLocation.add(direction, newDistance));
			}
		}
		if(angles[0]==null){
			MapLocation location = currentLocation.add(angles[1], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				float newDistance = controller.getLocation().distanceTo(target)-1;
				return pathfindScout(currentLocation.add(direction, newDistance));
			}else{
				return location;
			}
		}
		if(angles[1]==null){
			MapLocation location = currentLocation.add(angles[0], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				float newDistance = controller.getLocation().distanceTo(target)-1;
				return pathfindScout(currentLocation.add(direction, newDistance));
			}else{
				return location;
			}
		}
		float angle1 = direction.radiansBetween(angles[0]);
		float angle2 = angles[1].radiansBetween(direction);
		if(angle1<angle2){
			MapLocation location = currentLocation.add(angles[0], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				location = currentLocation.add(angles[1], strideRadius);
				if(!controller.onTheMap(location, bodyRadius)){
					float newDistance = controller.getLocation().distanceTo(target)-1;
					return pathfindScout(currentLocation.add(direction, newDistance));
				}else{
					return location;
				}
			}else{
				return location;
			}
		}else{
			MapLocation location = currentLocation.add(angles[1], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				location = currentLocation.add(angles[0], strideRadius);
				if(!controller.onTheMap(location, bodyRadius)){
					float newDistance = controller.getLocation().distanceTo(target)-1;
					return pathfindScout(currentLocation.add(direction, newDistance));
				}else{
					return location;
				}
			}else{
				return location;
			}
		}
	}
	public static MapLocation pathfind(MapLocation target, float radius) throws GameActionException{
		return pathfind(controller.getLocation().add(controller.getLocation().directionTo(target), controller.getLocation().distanceTo(target)-radius));
	}
	//Tangent Bug Pathfinding
	public static MapLocation pathfind(MapLocation target) throws GameActionException{
		currentLocation = controller.getLocation();
		Direction direction = controller.getLocation().directionTo(target);
		float distance = currentLocation.distanceTo(target)+Constants.EPSILON;
		nearbyRobots = controller.senseNearbyRobots(distance);
		nearbyTrees = controller.senseNearbyTrees(distance); //for scouts, we can just set nearbyTrees to an empty array
		if(isClear(currentLocation, target)){
			return target;
		}
		generateTangentAngles();
		Direction[] angles = splitAngles(direction, direction, 0, 0, 10);
		if(angles[0]==null&&angles[1]==null){
			float newDistance = controller.getLocation().distanceTo(target)-1;
			if(newDistance<=0){
				return currentLocation;
			}else{
				return pathfind(currentLocation.add(direction, newDistance));
			}
		}
		if(angles[0]==null){
			MapLocation location = currentLocation.add(angles[1], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				float newDistance = controller.getLocation().distanceTo(target)-1;
				return pathfind(currentLocation.add(direction, newDistance));
			}else{
				return location;
			}
		}
		if(angles[1]==null){
			MapLocation location = currentLocation.add(angles[0], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				float newDistance = controller.getLocation().distanceTo(target)-1;
				return pathfind(currentLocation.add(direction, newDistance));
			}else{
				return location;
			}
		}
		float angle1 = direction.radiansBetween(angles[0]);
		float angle2 = angles[1].radiansBetween(direction);
		if(angle1<angle2){
			MapLocation location = currentLocation.add(angles[0], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				location = currentLocation.add(angles[1], strideRadius);
				if(!controller.onTheMap(location, bodyRadius)){
					float newDistance = controller.getLocation().distanceTo(target)-1;
					return pathfind(currentLocation.add(direction, newDistance));
				}else{
					return location;
				}
			}else{
				return location;
			}
		}else{
			MapLocation location = currentLocation.add(angles[1], strideRadius);
			if(!controller.onTheMap(location, bodyRadius)){
				location = currentLocation.add(angles[0], strideRadius);
				if(!controller.onTheMap(location, bodyRadius)){
					float newDistance = controller.getLocation().distanceTo(target)-1;
					return pathfind(currentLocation.add(direction, newDistance));
				}else{
					return location;
				}
			}else{
				return location;
			}
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
	}
	public static Direction[] splitAngles(Direction leftDirection, Direction rightDirection, float left, float right, int tries) throws GameActionException{
		if(tries<=0){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			return new Direction[]{leftDirection, rightDirection};
		}
		for(int i=0;i<nearbyRobots.length;++i){
			Direction leftAngle = robotLeftAngles[i];
			Direction rightAngle = robotRightAngles[i];
			if(inBetween(leftDirection, leftAngle, rightAngle)){
				controller.setIndicatorDot(nearbyRobots[i].getLocation(), 128, 0, 255);
				left+=leftDirection.radiansBetween(leftAngle);
				if(left>=Math.PI){
					leftDirection = null;
				}else{
					return splitAngles(leftAngle, rightDirection, left, right, tries-1);
				}
			}
			if(inBetween(rightDirection, leftAngle, rightAngle)){
				controller.setIndicatorDot(nearbyRobots[i].getLocation(), 128, 0, 255);
				right+=rightDirection.radiansBetween(rightAngle);
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
				controller.setIndicatorDot(nearbyTrees[i].getLocation(), 128, 0, 255);
				left+=leftDirection.radiansBetween(leftAngle);
				if(left>=Math.PI){
					leftDirection = null;
				}else{
					return splitAngles(leftAngle, rightDirection, left, right, tries-1);
				}
			}
			if(inBetween(rightDirection, leftAngle, rightAngle)){
				controller.setIndicatorDot(nearbyTrees[i].getLocation(), 128, 0, 255);
				right+=rightDirection.radiansBetween(rightAngle);
				if(right<=-Math.PI){
					rightDirection = null;
				}else{
					return splitAngles(leftDirection, rightAngle, left, right, tries-1);
				}
			}
		}
		return new Direction[]{leftDirection, rightDirection};
	}
	public static boolean inBetween(Direction x, Direction left, Direction right) throws GameActionException{
		if(x==null){
			return false;
		}
		if(x.radians==left.radians||x.radians==right.radians){
			return false;
		}
		return (x.radiansBetween(left)>0&&x.radiansBetween(right)<0); //Assumes the angles are acute angles
	}
	public static float getTangentAngle(MapLocation location, float radius){
		return (float) Math.asin((radius+bodyRadius)/controller.getLocation().distanceTo(location));
	}
	public static boolean isClear(MapLocation from, MapLocation to){
		float distanceSquared = from.distanceSquaredTo(to); //Precalculate to save bytecodes
		for(RobotInfo robot: nearbyRobots){
			MapLocation location = robot.getLocation();
			if(findNearest(location, from, to, distanceSquared).distanceTo(location)<robot.getRadius()+bodyRadius){
				return false;
			}
		}
		for(TreeInfo tree: nearbyTrees){
			MapLocation location = tree.getLocation();
			if(findNearest(location, from, to, distanceSquared).distanceTo(location)<tree.getRadius()+bodyRadius){
				return false;
			}
		}
		return true;
	}
	public static MapLocation findNearest(MapLocation p, MapLocation v, MapLocation w, float distanceSquared){
		if(distanceSquared==0){
			return v;
		}
		float t = ((p.x-v.x)*(w.x-v.x)+(p.y-v.y)*(w.y-v.y))/distanceSquared;
		t = Math.max(0, Math.min(1, t)); //Clamp t between 0 and 1
		return Operation.project(v, w, t);
	}
	public static Direction findSpawn(float radius) throws GameActionException{
		MapLocation location = controller.getLocation();
		float spawnOffset = radius+GameConstants.GENERAL_SPAWN_OFFSET+2f;
		float spacing = (float) Math.PI;
		MapLocation zero = location.add(new Direction(0), spawnOffset);
		if((!controller.isCircleOccupied(zero, radius))&&controller.onTheMap(zero, radius)){
			return new Direction(0);
		}
		for(int i=0;i<Constants.RANDOM_TRIES;++i){
			for(float angle=spacing;angle<Constants.TWO_PI;angle+=spacing*2){
				Direction direction = new Direction(angle);
				MapLocation temp = location.add(direction, spawnOffset);
				controller.setIndicatorDot(temp, 0, 255, 255);
				if((!controller.isCircleOccupied(temp, radius))&&controller.onTheMap(temp, radius)){
					return direction;
				}
			}
			spacing/=2;
		}
		return null;
	}
}
