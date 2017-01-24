package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class MidrangeTank {
	private static MapLocation[] targetArchons;
	private static MapLocation initialArchon;
	private static int index = 0;
	private static int idleCounter;
	public static void run(RobotController controller){
		Direction direction = Util.randomDirection();
		targetArchons = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
		initialArchon = targetArchons[index];
		while(true){
			RobotInfo nearestRobot = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM)[0];
			if(idleCounter<15){
				if(nearestRobot.getType()!=RobotType.ARCHON){
					direction = controller.getLocation().directionTo(initialArchon);
				}
			}
			else{
				
			}
		}
	}
}
