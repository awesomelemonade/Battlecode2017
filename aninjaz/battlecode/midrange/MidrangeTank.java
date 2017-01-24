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
	private static MapLocation initialArchon;
	public static void run(RobotController controller){
		Direction direction = Util.randomDirection();
		initialArchon = controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0];
	}
}
