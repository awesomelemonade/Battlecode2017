package aninjaz.battlecode.general;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Util {
	public static RobotController controller;
	public static Direction randomDirection(){
		return new Direction((float) (Math.random()*2*Math.PI));
	}
	public static Direction[] cardinalDirections = new Direction[]{Direction.getNorth(), Direction.getSouth(), Direction.getEast(), Direction.getWest()};
	public static Direction randomCardinalDirection(){
		return cardinalDirections[(int)Math.random()*4];
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
	public static float getReservedBullets() throws GameActionException{
		return controller.getTeamBullets()-controller.readBroadcast(Constants.BROADCAST_RESERVED_BULLETS);
	}
	public static boolean reserveBullets(int bullets) throws GameActionException{
		float current = controller.getTeamBullets();
		int reserved = controller.readBroadcast(Constants.BROADCAST_RESERVED_BULLETS);
		if(current-reserved>=bullets){
			controller.broadcast(Constants.BROADCAST_RESERVED_BULLETS, reserved+bullets);
			return true;
		}
		return false;
	}
	public static void subtractBullets(int bullets) throws GameActionException{
		int reserved = controller.readBroadcast(Constants.BROADCAST_RESERVED_BULLETS);
		controller.broadcast(Constants.BROADCAST_RESERVED_BULLETS, reserved-bullets);
	}
	public static void yieldByteCodes(){
		Clock.yield();
	}
	public static MapLocation floor(MapLocation location){
		return new MapLocation((int)location.x, (int)location.y);
	}
	public static void checkWin() throws GameActionException{
		if(controller.getTeamBullets()>GameConstants.VICTORY_POINTS_TO_WIN*10){
			controller.donate(GameConstants.VICTORY_POINTS_TO_WIN*10);
		}
	}
}
