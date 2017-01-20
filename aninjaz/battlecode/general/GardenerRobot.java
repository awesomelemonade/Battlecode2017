package aninjaz.battlecode.general;

import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Identifier;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;

public class GardenerRobot {
	private static final int UNUSED_GARDENER_ORIGIN = 0;
	private static final int USED_GARDENER_ORIGIN = 1;
	private static final float WATER_RADIUS = 1;
	private static RobotController controller;
	private static MapLocation origin;
	public static void run(RobotController controller) throws GameActionException{
		GardenerRobot.controller = controller;
		
		while(origin==null){
			
			int candidateChannel = DynamicBroadcasting.find(Identifier.GARDENER_ORIGIN, UNUSED_GARDENER_ORIGIN);
			if(candidateChannel!=-1){
				controller.broadcast(candidateChannel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, USED_GARDENER_ORIGIN));
				origin = CompressedData.uncompressMapLocation(controller.readBroadcast(candidateChannel-1));
			}
			
			waterTrees(); //why not :P
			Util.yieldByteCodes();
		}
		while(true){
			
			waterTrees();
			Util.yieldByteCodes();
		}
	}
	public static void waterTrees() throws GameActionException{
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(WATER_RADIUS, controller.getTeam());
		int bestTreeId = -1;
		float leastHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
		for(TreeInfo tree: nearbyTrees){
			if(tree.getHealth()<leastHealth){
				bestTreeId = tree.getID();
				leastHealth = tree.getHealth();
			}
		}
		if(bestTreeId!=-1){
			controller.water(bestTreeId); //No need for canWater()?
		}
	}
}
