package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class AggroSoldier {
	private static MapLocation initialArchon;
	public static void run(RobotController controller) throws GameActionException{
		initialArchon = controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0];
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				Pathfinding.goTowards(nearbyRobots[0].getLocation());
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(controller.getLocation().directionTo(nearbyRobots[0].getLocation()));
				}
			}else{
				Pathfinding.goTowards(initialArchon);
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(controller.getLocation().directionTo(initialArchon));
				}
			}
			Util.yieldByteCodes();
		}
	}
}
