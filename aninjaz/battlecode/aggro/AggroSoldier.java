package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Identifier;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;

public class AggroSoldier {
	private static RobotInfo closestEnemy;
	public static void run(RobotController controller) throws GameActionException{
		while(true){
			if(controller.senseNearbyRobots(-1, Constants.OTHER_TEAM)!=null){
				closestEnemy = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM)[0];
				Pathfinding.goTowards(controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0]);
			}
			else{
				Pathfinding.goTowards(controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0]);
			}
			
			
			Util.yieldByteCodes();
		}
	}
}
