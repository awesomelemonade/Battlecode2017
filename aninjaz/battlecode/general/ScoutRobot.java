package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class ScoutRobot {
	public static final int TO_INITIAL_ARCHON_STATE = 0;
	public static final int RANDOM_DIRECTION_STATE = 1;
	public static final int TARGET_DIRECTION_STATE = 2;
	private static int currentState;
	private static MapLocation targetInitialArchon;
	private static RobotInfo targetRobot;
	private static Direction direction = Util.randomDirection();
	public static void run(RobotController controller) throws GameActionException{
		Util.broadcastCount = Constants.BROADCAST_SCOUT_COUNT;
		targetInitialArchon = getAverage(controller.getInitialArchonLocations(Constants.OTHER_TEAM));
		while(true){
			switch(currentState){
			case TO_INITIAL_ARCHON_STATE:
				if(!moveTowardsTarget(controller, targetInitialArchon)){
					while(!controller.canMove(direction)){
						direction = Util.randomDirection();
					}
					controller.move(direction);
				}
				if(controller.getLocation().equals(targetInitialArchon)){
					currentState = RANDOM_DIRECTION_STATE;
				}
				break;
			case RANDOM_DIRECTION_STATE:
				while(!controller.canMove(direction)){
					direction = Util.randomDirection();
				}
				controller.move(direction);
				break;
			case TARGET_DIRECTION_STATE:
				if(!moveTowardsRobot(controller, targetRobot)){
					while(!controller.canMove(direction)){
						direction = Util.randomDirection();
					}
					controller.move(direction);
				}
				break;
			}
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				RobotInfo robot = getNearestNonArchon(nearbyRobots);
				if(robot==null){
					if(currentState==TARGET_DIRECTION_STATE){
						currentState = RANDOM_DIRECTION_STATE;
					}
				}else{
					Direction direction = controller.getLocation().directionTo(robot.getLocation());
					//float distance = controller.getLocation().distanceTo(robot.getLocation())-2f;
					if(controller.canFireSingleShot()){
						controller.fireSingleShot(direction);
					}
					
					targetRobot = robot;
					currentState = TARGET_DIRECTION_STATE;
				}
			}else{
				if(currentState==TARGET_DIRECTION_STATE){
					currentState = RANDOM_DIRECTION_STATE;
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static RobotInfo getNearestTree(RobotInfo[] nearbyTrees){
		for(RobotInfo tree: nearbyTrees){
			return tree;
		}
		return null;
	}
	public static RobotInfo getNearestNonArchon(RobotInfo[] nearbyRobots){
		for(RobotInfo robot: nearbyRobots){
			if(robot.getType()!=RobotType.ARCHON){
				return robot;
			}
		}
		return null;
	}
	public static boolean moveTowardsTarget(RobotController controller, MapLocation location) throws GameActionException{
		if(controller.canMove(location)){
			controller.move(location);
			return true;
		}
		return false;
	}
	public static boolean moveTowardsRobot(RobotController controller, RobotInfo robotman) throws GameActionException{
		float distance = controller.getLocation().distanceTo(robotman.getLocation())-2f;//2 because 1 radius for each robot. Distance is between the centers, so you have to subtract 2 to the radius
		if(robotman.getType()==RobotType.LUMBERJACK){
			distance-=5;
		}
		
		if(distance<=RobotType.SCOUT.strideRadius){
			Direction direction = controller.getLocation().directionTo(robotman.getLocation());
			if(robotman.getType()==RobotType.LUMBERJACK){
				if(controller.getLocation().distanceTo(robotman.getLocation())<7){
					if(controller.canMove(direction.opposite())){
						controller.move(direction.opposite(), 7-controller.getLocation().distanceTo(robotman.getLocation()));						
					}
					else{
						if(controller.canMove(direction.rotateLeftDegrees(90f))){
							controller.move(direction.rotateLeftDegrees(90f), 7-controller.getLocation().distanceTo(robotman.getLocation()));	
						}
						else {
							if(controller.canMove(direction.rotateRightDegrees(90f))){
								controller.move(direction.rotateRightDegrees(90f), 7-controller.getLocation().distanceTo(robotman.getLocation()));	
							}
						}
					}
				}
				return true;
			}
			else if(controller.canMove(direction, distance)){
				controller.move(direction, distance);
				return true;
			}
		}else{
			if(controller.canMove(robotman.getLocation())){
				controller.move(robotman.getLocation());
				return true;
			}
		}
		return false;
	}
	public static MapLocation getAverage(MapLocation[] locations){
		float totalX = 0;
		float totalY = 0;
		for(MapLocation location: locations){
			totalX+=location.x;
			totalY+=location.y;
		}
		return new MapLocation(totalX/locations.length, totalY/locations.length);
	}
}
