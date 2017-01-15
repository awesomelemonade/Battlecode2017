package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

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
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				Direction direction = controller.getLocation().directionTo(nearbyRobots[0].getLocation());
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(direction);
				}
				targetLocation = nearbyRobots[0].getLocation();
				currentState = TARGET_DIRECTION_STATE;
			}else{
				if(currentState==TARGET_DIRECTION_STATE){
					currentState = RANDOM_DIRECTION_STATE;
				}
			}
			
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
				moveTowardsTarget(controller, targetLocation);
				break;
			}
			if(!lowHealth){
				if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If scout is about to die :(
					System.out.println("LOW HEALTH: "+controller.readBroadcast(Constants.BROADCAST_SCOUT_COUNT));
					controller.broadcast(Constants.BROADCAST_SCOUT_COUNT,
							controller.readBroadcast(Constants.BROADCAST_SCOUT_COUNT)-1);
					System.out.println("LOW HEALTH 2: "+controller.readBroadcast(Constants.BROADCAST_SCOUT_COUNT));
					lowHealth = true;
				}
			}
			Util.yieldByteCodes();
		}
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
