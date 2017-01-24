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
	private static final float WATER_RADIUS = 2f;
	private static RobotController controller;
	private static MapLocation origin;
	private static int originChannel; //TODO: Unset gardener origin after gardener dies
	private static Direction[] plantOffset = new Direction[]
			{Constants.SOUTH_WEST, Constants.NORTH_WEST, Constants.SOUTH_EAST, Constants.NORTH_EAST};
	private static Direction[] plantDirection = new Direction[]
			{Direction.getWest(), Direction.getWest(), Direction.getEast(), Direction.getEast(),
					Direction.getWest(), Direction.getEast(), Direction.getSouth(), Direction.getNorth()};
	private static Direction[] plantMovement = new Direction[]
			{Direction.getSouth(), Direction.getNorth(), Direction.getSouth(), Direction.getNorth(), null, null, null, null};
	private static final float offsetDistance = 0.001f;
	private static int useNonBidirectional = 0;
	public static int goTowards(MapLocation location) throws GameActionException{
		int status = 0;
		/*if(useNonBidirectional>0){
			status = Pathfinding.goTowardsRight(location);
			if(status==Pathfinding.HAS_NOT_MOVED){
				useNonBidirectional = -10;
			}else{
				useNonBidirectional--;
			}
		}else if(useNonBidirectional<0){
			status = Pathfinding.goTowardsLeft(location);
			if(status==Pathfinding.HAS_NOT_MOVED){
				useNonBidirectional = 10;
			}else{
				useNonBidirectional++;
			}
		}else{
			status = Pathfinding.goTowardsBidirectional(location);
			if(status==Pathfinding.HAS_NOT_MOVED){
				useNonBidirectional = 10;
			}
		}*/
		return status;
	}
	public static void run(RobotController controller) throws GameActionException{
		GardenerRobot.controller = controller;
		ArchonRobot.controller = controller;
		
		findOrigin();
		
		while(true){
			/*if(goTowards(origin)==Pathfinding.REACHED_GOAL){
				break;
			}
			int channel = ArchonRobot.checkValidGardenerOrigin();
			if(channel!=-1){
				controller.broadcast(originChannel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, UNUSED_GARDENER_ORIGIN));
				origin = controller.getLocation();
				originChannel = channel;
				controller.broadcast(channel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, USED_GARDENER_ORIGIN));
			}
			Util.yieldByteCodes();*/
			break;
		}
		Util.yieldByteCodes();
		
		while(true){
			controller.setIndicatorDot(origin, 128, 0, 255);
			plantTrees();
			waterTrees();
			Util.yieldByteCodes();
		}
	}
	private static float[] treeX = new float[]{-2, -2, 2, 2, -2, 2, 0, 0};
	private static float[] treeY = new float[]{-2, 2, -2, 2, 0, 0, -2, 2};
	public static void plantTrees() throws GameActionException{
		int plantIndex = nextPlantIndex();
		if(plantIndex!=-1){ //Invalid plant index
			if(controller.getTeamBullets()>GameConstants.BULLET_TREE_COST){
				Direction movement = plantMovement[plantIndex];
				Direction direction = plantDirection[plantIndex];
				if(movement==null){
					waitForPlantWhileWatering(direction);
					planted[plantIndex] = true;
				}else{
					Direction opposite = movement.opposite();
					Direction offset = plantOffset[plantIndex];
					waitForMoveWhileWatering(movement);
					waitForMoveWhileWatering(movement);
					waitForMoveWhileWatering(movement);
					waitForMoveWhileWatering(movement);
					waitForMoveWhileWatering(offset, offsetDistance);
					waitForPlantWhileWatering(direction);
					planted[plantIndex] = true;
					waitForMoveWhileWatering(offset.opposite(), offsetDistance);
					waitForMoveWhileWatering(opposite);
					waitForMoveWhileWatering(opposite);
					waitForMoveWhileWatering(opposite);
					waitForMoveWhileWatering(opposite);
				}
			}
		}
		for(int i=0;i<planted.length;++i){
			if(planted[i]){
				MapLocation treeLocation = origin.translate(treeX[i], treeY[i]);
				TreeInfo tree = controller.senseTreeAtLocation(treeLocation);
				if(tree==null){
					controller.setIndicatorDot(treeLocation, 255, 128, 0); //Orange
					planted[i] = false;
				}
			}
		}
	}
	public static int nextPlantIndex(){
		for(int i=0;i<planted.length;++i){
			if(!planted[i]){
				return i;
			}
		}
		return -1;
	}
	public static void waitForPlantWhileWatering(Direction direction) throws GameActionException{
		while(!controller.canPlantTree(direction)){
			waterTrees();
			Util.yieldByteCodes();
		}
		controller.plantTree(direction);
	}
	public static void waitForMoveWhileWatering(Direction direction) throws GameActionException{
		controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
		while((!controller.canMove(direction))||(controller.hasMoved())){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			waterTrees();
			Util.yieldByteCodes();
		}
		controller.move(direction);
	}
	public static void waitForMoveWhileWatering(Direction direction, float distance) throws GameActionException{
		controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
		waterTrees();
		Util.yieldByteCodes();
		while((!controller.canMove(direction, distance))||(controller.hasMoved())){
			controller.setIndicatorDot(controller.getLocation(), 128, 128, 128);
			waterTrees();
			Util.yieldByteCodes();
		}
		controller.move(direction, distance);
	}
	public static void findOrigin() throws GameActionException{
		while(origin==null){
			controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
			int candidateChannel = 0;
			float candidateDistance = Float.MAX_VALUE;
			MapLocation candidateLocation = null;
			for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
				for(int bit=0;bit<Integer.SIZE;++bit){
					int compressedDataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					int compressedData = controller.readBroadcast(compressedDataChannel);
					int identifier = CompressedData.getIdentifier(compressedData);
					if(identifier==Identifier.GARDENER_ORIGIN){
						int data = CompressedData.getData(compressedData);
						if(data==UNUSED_GARDENER_ORIGIN){
							MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(compressedDataChannel-1));
							controller.setIndicatorDot(location, 128, 128, 128);
							controller.setIndicatorLine(controller.getLocation(), location, 255, 128, 0);
							float distance = controller.getLocation().distanceTo(location);
							if(distance<candidateDistance){
								candidateChannel = compressedDataChannel;
								candidateDistance = distance;
								candidateLocation = location;
							}
						}
					}
				}
			}
			if(candidateLocation!=null){
				origin = candidateLocation;
				originChannel = candidateChannel;
				controller.broadcast(candidateChannel, CompressedData.compressData(Identifier.GARDENER_ORIGIN, USED_GARDENER_ORIGIN));
			}
			//Explore and find candidates?
			waterTrees(); //why not :P
			Util.yieldByteCodes();
		}
	}
	private static boolean[] planted = new boolean[8];
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

