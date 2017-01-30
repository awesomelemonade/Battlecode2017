package aninjaz.battlecode.general;

import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Util {
	public static RobotController controller;
	public static Direction randomDirection(){
		return new Direction((float) (Math.random()*Constants.TWO_PI));
	}
	public static Direction toDirection(float x, float y){
		return new Direction((float)Math.atan2(y, x));
	}
	public static void waitForMove(Direction direction) throws GameActionException{
		while(!controller.canMove(direction)){
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(direction), 255, 255, 255);
			Util.yieldByteCodes();
		}
		controller.move(direction);
	}
	public static void waitForMove(Direction direction, float distance) throws GameActionException{
		while(!controller.canMove(direction, distance)){
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(direction, distance), 255, 255, 255);
			Util.yieldByteCodes();
		}
		controller.move(direction, distance);
	}
	public static void waitForMove(MapLocation target) throws GameActionException{
		while(!controller.canMove(target)){
			controller.setIndicatorLine(controller.getLocation(), target, 255, 255, 255);
			Util.yieldByteCodes();
		}
		controller.move(target);
	}
	public static void yieldByteCodes() throws GameActionException{
		Util.checkWin();
		//Tally up robots
		Clock.yield();
	}
	public static void checkWin() throws GameActionException{
		if(controller.getTeamBullets()>Constants.DONATE_WHEN_OVER){
			controller.donate((float) (Math.floor((controller.getTeamBullets()-Constants.DONATE_WHEN_OVER)/10)*10));
		}
		if(controller.getTeamBullets()/(7.5+(controller.getRoundNum()*12.5/3000))+controller.getTeamVictoryPoints()>GameConstants.VICTORY_POINTS_TO_WIN){
			controller.donate(controller.getTeamBullets());
		}
		if(controller.getRoundLimit()-controller.getRoundNum()<=2){
			controller.donate((float) (Math.floor(controller.getTeamBullets()/10)*10));
		}
	}
	public static Direction tryRandomMove(Direction direction, float distance) throws GameActionException{
		return tryRandomMove(direction, distance, Constants.RANDOM_TRIES);
	}
	public static Direction tryRandomMove(Direction direction, float distance, int tries) throws GameActionException{
		while((!controller.canMove(direction, distance))&&tries>0){
			direction = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			controller.move(direction, distance);
		}
		return direction;
	}
	public static Direction tryRandomMove(Direction direction) throws GameActionException{
		return tryRandomMove(direction, Constants.RANDOM_TRIES);
	}
	public static Direction tryRandomMove(Direction direction, int tries) throws GameActionException{
		while((!controller.canMove(direction))&&tries>0){
			direction = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			controller.move(direction);
		}
		return direction;
	}
	public static boolean inFiringRange(RobotInfo[] robots, Direction direction, float angle){
		for(RobotInfo robot: robots){
			Direction dir = controller.getLocation().directionTo(robot.getLocation());
			if(Math.abs(dir.degreesBetween(direction))<=angle){
				return true;
			}
		}
		return false;
	}
	public static boolean isSafeToShoot(Direction direction) throws GameActionException{
		RobotInfo[] robots = controller.senseNearbyRobots();
		float minDistance = Float.MAX_VALUE;
		boolean safe = true;
		for(RobotInfo robot: robots){
			Direction towards = controller.getLocation().directionTo(robot.getLocation());
			float angle = getTangentAngle(robot.getLocation(), robot.getRadius());
			if(Pathfinding.inBetween(direction, towards.rotateLeftRads(angle), towards.rotateRightRads(angle))){
				float distance = controller.getLocation().distanceTo(robot.getLocation());
				if(distance<minDistance){
					minDistance = distance;
					safe = robot.team==Constants.OTHER_TEAM;
				}
			}
		}
		return safe;
	}
	public static float getTangentAngle(MapLocation location, float radius){
		return (float) Math.asin(radius/controller.getLocation().distanceTo(location));
	}
	
}
