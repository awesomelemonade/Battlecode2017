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

public class GardenerRobot {
	public static final int UNUSED_GARDENER_ORIGIN = 0;
	public static final int USED_GARDENER_ORIGIN = 1;
	private static final float WATER_RADIUS = 1;
	private static RobotController controller;
	private static MapLocation origin;
	public static void run(RobotController controller) throws GameActionException{
		GardenerRobot.controller = controller;
		
		findOrigin();
		
		Pathfinding.goTowards(origin);
		
		Direction direction = Util.randomDirection();
		
		while(true){
			//direction = Util.tryRandomMove(direction);
			waterTrees();
			Util.yieldByteCodes();
		}
	}
	public static void setupTrees(){
		MapLocation location = origin.translate(0, 1+Constants.EPSILON);
		
		
	}
	public static void findOrigin() throws GameActionException{
		while(origin==null){
			controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
			int candidateChannel = DynamicBroadcasting.find(Identifier.GARDENER_ORIGIN, UNUSED_GARDENER_ORIGIN);
			if(candidateChannel!=-1){
				System.out.println("Found origin: "+candidateChannel+" - "+controller.readBroadcast(candidateChannel));
				controller.broadcast(candidateChannel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, USED_GARDENER_ORIGIN));
				origin = CompressedData.uncompressMapLocation(controller.readBroadcast(candidateChannel-1));
			}
			//Explore and find candidates?
			waterTrees(); //why not :P
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
