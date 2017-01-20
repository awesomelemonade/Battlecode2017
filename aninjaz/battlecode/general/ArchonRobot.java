package aninjaz.battlecode.general;

import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Identifier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class ArchonRobot {
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		ArchonRobot.controller = controller;
		for(int i=0;i<10;++i){
			int channel = DynamicBroadcasting.markNextAvailableMapper();
			controller.broadcast(channel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, 0));
			controller.broadcast(channel-1, CompressedData.compressMapLocation(controller.getLocation()));
		}
		while(true){
			if(controller.isBuildReady()){
				hireGardener();
			}
			Util.yieldByteCodes();
		}
	}
	public static void hireGardener() throws GameActionException{
		Direction direction = Util.randomDirection();
		int tries = 10;
		while((!controller.canHireGardener(direction))&&tries>0){
			direction = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			controller.hireGardener(direction);
		}
	}
}
