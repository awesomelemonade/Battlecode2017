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
	public static void addReservedBullets(int bullets) throws GameActionException{
		setReservedBullets(getReservedBullets()+bullets);
	}
	public static void subtractReservedBullets(int bullets) throws GameActionException{
		setReservedBullets(getReservedBullets()-bullets);
	}
	public static void setReservedBullets(int bullets) throws GameActionException{
		controller.broadcast(Constants.BROADCAST_RESERVED_BULLETS, bullets);
	}
	public static float getAvailableBullets() throws GameActionException{
		return controller.getTeamBullets()-getReservedBullets();
	}
	public static int getReservedBullets() throws GameActionException{
		return controller.readBroadcast(Constants.BROADCAST_RESERVED_BULLETS);
	}
	public static int broadcastCount = -1;
	public static void yieldByteCodes() throws GameActionException{
		Util.checkWin();
		if(broadcastCount!=-1){
			checkLowHealth(broadcastCount);
		}
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
	private static boolean lowHealth = false;
	public static void checkLowHealth(int broadcast) throws GameActionException{
		if(!lowHealth){
			if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If scout is about to die :(
				controller.broadcast(broadcast, controller.readBroadcast(broadcast)-1);
				lowHealth = true;
			}
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
	private static final int mapLocations = 4;
	public static int getMapLocations(){
		return mapLocations;
	}
	public static int getMapLocationChannel(int mapLocation){
		return GameConstants.BROADCAST_MAX_CHANNELS-mapLocation-1;
	}
	public static int broadcastNew(CompressedMapLocation data) throws GameActionException{
		for(int i=0;i<mapLocations;++i){
			int channel = GameConstants.BROADCAST_MAX_CHANNELS-i-1;
			int n = controller.readBroadcast(channel);
			if(n!=-1){
				int bit = 0;
				while(((n>>>bit)&1)==1){
					bit++;
				}
				if(bit<32){
					controller.broadcast(channel, n|(1<<bit));
					channel = GameConstants.BROADCAST_MAX_CHANNELS-mapLocations-1-i*32-bit;
					controller.broadcast(channel, data.getCompressedData());
					return channel;
				}
			}
		}
		return -1; //Couldn't broadcast :(
	}
	public static int getChannelLocation(int i, int bit){
		return GameConstants.BROADCAST_MAX_CHANNELS-mapLocations-1-i*32-bit;
	}
	public static void unsetBroadcastLocation(int channel) throws GameActionException{
		int x = GameConstants.BROADCAST_MAX_CHANNELS-channel-mapLocations-1;
		int y = GameConstants.BROADCAST_MAX_CHANNELS-(x/32)-1;
		int n = controller.readBroadcast(y);
		controller.broadcast(y, n & (~(1<<(x%32))));
		controller.broadcast(channel, 0);
	}
}
