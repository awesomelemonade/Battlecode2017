package aninjaz.battlecode.general;

import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Identifier;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;

public class GardenerCommander {
	public static final int UNUSED_GARDENER_ORIGIN = 0;
	public static final int USED_GARDENER_ORIGIN = 1;
	private static final float WATER_RADIUS = 2f;
	private static RobotController controller;
	private static MapLocation origin;
	private static int originChannel; //TODO: Unset gardener origin after gardener dies
	private static Direction[] plantOffset = new Direction[]
			{Constants.SOUTH_WEST, Constants.NORTH_WEST, Constants.SOUTH_EAST, Constants.NORTH_EAST};
	private static Direction[] plantDirection = new Direction[]
			{Direction.getWest(), Direction.getWest(), Direction.getEast(), Direction.getEast(),
					Direction.getWest(), Direction.getEast(), Direction.getSouth()};
	private static Direction[] plantMovement = new Direction[]
			{Direction.getSouth(), Direction.getNorth(), Direction.getSouth(), Direction.getNorth(), null, null, null};
	private static final float offsetDistance = 0.001f;
	private static int useNonBidirectional = 0;
	public static void run(RobotController controller) throws GameActionException{
		GardenerCommander.controller = controller;
		while(true){
			Util.yieldByteCodes();
		}
	}
}
