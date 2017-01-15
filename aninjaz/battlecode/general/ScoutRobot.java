package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class ScoutRobot {
	public static void run(RobotController controller) throws GameActionException{
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			
			if(nearbyRobots.length>0){
				Direction direction = controller.getLocation().directionTo(nearbyRobots[0].getLocation());
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(direction);
				}
			}
			
			Util.yieldByteCodes();
		}
	}
}
