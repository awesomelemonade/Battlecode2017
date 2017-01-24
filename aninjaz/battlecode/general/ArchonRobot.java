package aninjaz.battlecode.general;

import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Identifier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;

public class ArchonRobot {
	public static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		ArchonRobot.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			if(controller.readBroadcast(Constants.CHANNEL_AVAILABLE_GARDENER_ORIGINS)>=1){
				if(controller.isBuildReady()){
				}
			}
			direction = Util.tryRandomMove(direction);
			Util.yieldByteCodes();
		}
	}
}
