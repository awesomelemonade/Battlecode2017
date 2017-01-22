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
	private static RobotController controller;
	private static final float CHECK_TREE_RADIUS = RobotType.GARDENER.bodyRadius+GameConstants.BULLET_TREE_RADIUS*3.01f;
	private static final float CHECK_ROBOT_RADIUS = CHECK_TREE_RADIUS*3.01f;
	private static final float[] offmapCheck = new float[]{-2.01f, 2.01f};
	public static void run(RobotController controller) throws GameActionException{
		ArchonRobot.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			if(controller.readBroadcast(Constants.CHANNEL_AVAILABLE_GARDENER_ORIGINS)>=1){
				if(controller.isBuildReady()){
					hireGardener();
				}
			}
			TreeInfo[] trees = controller.senseNearbyTrees(CHECK_TREE_RADIUS);
			if(trees.length==0){
				setOrigin:{
					for(int i=0;i<offmapCheck.length;++i){
						for(int j=0;j<offmapCheck.length;++j){
							if(!controller.onTheMap(controller.getLocation().translate(offmapCheck[i], offmapCheck[j]), GameConstants.BULLET_TREE_RADIUS)){
								break setOrigin;
							}
						}
					}
					for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
						for(int bit=0;bit<Integer.SIZE;++bit){
							int compressedDataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
							int compressedData = controller.readBroadcast(compressedDataChannel);
							int identifier = CompressedData.getIdentifier(compressedData);
							if(identifier==Identifier.GARDENER_ORIGIN){
								MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(compressedDataChannel-1));
								if(location.distanceTo(controller.getLocation())<=CHECK_ROBOT_RADIUS){
									break setOrigin;
								}
							}
						}
					}
					int channel = DynamicBroadcasting.markNextAvailableMapper();
					System.out.println("Mapped: "+controller.getLocation()+"("+CompressedData.compressMapLocation(controller.getLocation())+") to Channel "+channel);
					controller.broadcast(channel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, GardenerRobot.UNUSED_GARDENER_ORIGIN));
					controller.broadcast(channel-1, CompressedData.compressMapLocation(controller.getLocation()));
					controller.broadcast(Constants.CHANNEL_AVAILABLE_GARDENER_ORIGINS, controller.readBroadcast(Constants.CHANNEL_AVAILABLE_GARDENER_ORIGINS)+1);
				}
			}
			direction = Util.tryRandomMove(direction);
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
			controller.broadcast(Constants.CHANNEL_AVAILABLE_GARDENER_ORIGINS, controller.readBroadcast(Constants.CHANNEL_AVAILABLE_GARDENER_ORIGINS)-1);
		}
	}
}
