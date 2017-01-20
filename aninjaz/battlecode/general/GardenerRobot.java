package aninjaz.battlecode.general;

import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;

public class GardenerRobot {
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		GardenerRobot.controller = controller;
		while(true){
			
			Util.yieldByteCodes();
		}
	}
	public static int find(int identifier, int data) throws GameActionException{
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int n = controller.readBroadcast(DynamicBroadcasting.getMapperChannel(mapper));
			for(int bit=0;bit<Integer.SIZE;++bit){
				int compressedDataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
				int compressedData = controller.readBroadcast(compressedDataChannel);
				CompressedData.getIdentifier(compressedData);
			}
		}
	}
}
