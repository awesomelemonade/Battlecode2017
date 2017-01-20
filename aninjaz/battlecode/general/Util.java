package aninjaz.battlecode.general;

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
		return new Direction((float) (Math.random()*2*Math.PI));
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
		if(controller.getTeamBullets()>GameConstants.VICTORY_POINTS_TO_WIN*10){
			controller.donate(GameConstants.VICTORY_POINTS_TO_WIN*10);
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
}
