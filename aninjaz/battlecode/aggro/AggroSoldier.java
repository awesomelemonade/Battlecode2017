package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class AggroSoldier {
	private static RobotInfo closestEnemy = null;
	public static void run(RobotController controller) throws GameActionException{
		while(true){
			if(controller.senseNearbyRobots(-1, Constants.OTHER_TEAM)!=null){
				closestEnemy = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM)[0];
				Pathfinding.goTowards(controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0]);
			}
			else{
				closestEnemy=null;
				Pathfinding.goTowards(controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0]);
			}
			if(controller.canFireSingleShot()){
				if(closestEnemy!=null){
					controller.fireSingleShot(controller.getLocation().directionTo(closestEnemy.getLocation()));
				}
			}
			Util.yieldByteCodes();
		}
	}
}
