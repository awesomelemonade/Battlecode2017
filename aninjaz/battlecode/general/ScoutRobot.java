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
	private static MapLocation targetLocation;
	private static Direction direction = Util.randomDirection();
	private static boolean lowHealth = false;
	public static void run(RobotController controller) throws GameActionException{
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
				if(!moveTowardsTarget(controller, targetLocation)){
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
					if(controller.canFireSingleShot()){
						controller.fireSingleShot(direction);
					}
					targetLocation = robot.getLocation();
					currentState = TARGET_DIRECTION_STATE;
				}
			}else{
				if(currentState==TARGET_DIRECTION_STATE){
					currentState = RANDOM_DIRECTION_STATE;
				}
			}
			
			if(!lowHealth){
				if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If scout is about to die :(
					controller.broadcast(Constants.BROADCAST_SCOUT_COUNT,
							controller.readBroadcast(Constants.BROADCAST_SCOUT_COUNT)-1);
					lowHealth = true;
				}
			}
			Util.yieldByteCodes();
		}
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
		}else{
			return false;
		}
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
