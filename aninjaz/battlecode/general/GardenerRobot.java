package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;

public class GardenerRobot {
	//Planet corners first, then sides
	private static Direction[] plantOffset = new Direction[]
			{Constants.SOUTH_WEST, Constants.NORTH_WEST, Constants.SOUTH_EAST, Constants.NORTH_EAST};
	private static final float offsetDistance = 0.01f;
	private static float[] plantOffsetX = new float[]{-2-offsetDistance, -2-offsetDistance, 2+offsetDistance, 2+offsetDistance}; //Offsets from origin; Used for checking for valid origin purposes
	private static float[] plantOffsetY = new float[]{-2-offsetDistance, 2+offsetDistance, -2-offsetDistance, 2+offsetDistance}; //Offsets from origin; Used for checking for valid origin purposes
	private static Direction[] plantDirection = new Direction[]
			{Direction.getWest(), Direction.getWest(), Direction.getEast(), Direction.getEast(),
					Direction.getWest(), Direction.getEast(), Direction.getSouth(), Direction.getNorth()};
	private static Direction[] plantMovement = new Direction[]
			{Direction.getSouth(), Direction.getNorth(), Direction.getSouth(), Direction.getNorth(), null, null, null, null};
	private static MapLocation origin;
	private static int originChannel;
	private static float checkRadius = Constants.ROOT_2*3f;
	private static RobotController controller;
	public static boolean validRobots(RobotInfo[] robots){
		for(RobotInfo robot: robots){
			if(robot.getType()==RobotType.GARDENER||robot.getType()==RobotType.ARCHON||robot.getType()==RobotType.SOLDIER){
				return false;
			}
		}
		return true;
	}
	public static void run(RobotController controller) throws GameActionException {
		Util.broadcastCount = Constants.BROADCAST_GARDENER_COUNT;
		GardenerRobot.controller = controller;
		Direction randDirection = Util.randomDirection();
		while(origin==null){
			controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
			MapLocation current = controller.getLocation();
			MapLocation target = Util.floor(current);
			RobotInfo[] robots = controller.senseNearbyRobots(target, checkRadius, null);
			TreeInfo[] trees = controller.senseNearbyTrees(target, checkRadius, null);
			if(validRobots(robots)&&trees.length==0){
				if(onTheMap(target)){
					Util.waitForMove(target);
					origin = target;
					Util.yieldByteCodes();
					break;
				}
			}
			randDirection = Util.tryRandomMove(randDirection);
			Util.yieldByteCodes();
		}
		originChannel = Util.broadcastNew(new CompressedMapLocation(Constants.BROADCAST_IDENTIFIER_GARDENER, 0, origin));
		while(!controller.getLocation().equals(origin)){
			Util.waitForMove(origin);
			Util.yieldByteCodes();
		}
		while(true){
			controller.setIndicatorDot(origin, 255, 0, 255);
			controller.setIndicatorDot(controller.getLocation(), 128, 0, 128);
			int soldierCount = controller.readBroadcast(Constants.BROADCAST_SOLDIER_COUNT);
			if(controller.isBuildReady()&&soldierCount>=1){
				int i = nextPlantIndex();
				if(i!=-1){ //Invalid plant index
					float currentBullets = controller.getTeamBullets();
					int currentReserved = Util.getReservedBullets();
					if(currentBullets-currentReserved>GameConstants.BULLET_TREE_COST){
						Util.setReservedBullets((int) (currentReserved+GameConstants.BULLET_TREE_COST));
						Direction movement = plantMovement[i];
						Direction direction = plantDirection[i];
						if(movement!=null){
							controller.setIndicatorDot(controller.getLocation(), 0, 255, 0); //Green
							Direction opposite = movement.opposite();
							Direction offset = plantOffset[i];
							Util.waitForMove(movement);
							Util.yieldByteCodes();
							Util.waitForMove(movement);
							Util.yieldByteCodes();
							Util.waitForMove(offset, offsetDistance);
							boolean yielded = false;
							while(!controller.canPlantTree(direction)){
								Util.yieldByteCodes();
								yielded = true;
							}
							Util.subtractReservedBullets((int)GameConstants.BULLET_TREE_COST);
							planted[i] = true;
							controller.plantTree(direction);
							if(!yielded){
								Util.yieldByteCodes();
							}
							Util.waitForMove(offset.opposite(), offsetDistance);
							Util.yieldByteCodes();
							Util.waitForMove(opposite);
							Util.yieldByteCodes();
							Util.waitForMove(opposite);
						}else{
							while(!controller.canPlantTree(direction)){
								Util.yieldByteCodes();
							}
							planted[i] = true;
							Util.subtractReservedBullets((int)GameConstants.BULLET_TREE_COST);
							controller.plantTree(direction);
						}
					}
				}
			}
			waterTrees();
			if(!lowHealth){
				if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If scout is about to die :(
					Util.unsetBroadcastLocation(originChannel);
					lowHealth = true;
				}
			}
			Util.yieldByteCodes();
		}
	}
	private static boolean lowHealth = false;
	public static int nextPlantIndex(){
		for(int i=0;i<planted.length;++i){
			if(!planted[i]){
				return i;
			}
		}
		return -1;
	}
	public static boolean onTheMap(MapLocation location) throws GameActionException {
		for(int i=0;i<plantOffsetX.length;++i){
			if(!controller.onTheMap(location.translate(plantOffsetX[i], plantOffsetY[i]), GameConstants.BULLET_TREE_RADIUS)){
				return false;
			}
		}
		return true;
	}
	private static float[] waterX = new float[]{-2, -2, 2, 2, -2, 2, 0, 0};
	private static float[] waterY = new float[]{-2, 2, -2, 2, 0, 0, -2, 2};
	private static boolean[] planted = new boolean[8];
	public static void waterTrees() throws GameActionException { //Water every tree in all 8 directions
		MapLocation leastLocation = null;
		float leastHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
		for(int i=0;i<planted.length;++i){
			if(planted[i]){
				MapLocation waterLocation = origin.translate(waterX[i], waterY[i]);
				TreeInfo tree = controller.senseTreeAtLocation(waterLocation);
				if(tree==null){
					controller.setIndicatorDot(waterLocation, 255, 128, 0); //Orange
					//Tree is dead!!
					//toBePlanted.add(i);
					planted[i] = false;
					continue;
				}
				controller.setIndicatorDot(tree.getLocation(), 255, 255, 0); //Yellow
				if(tree.getHealth()<leastHealth){
					leastLocation = waterLocation;
					leastHealth = tree.getHealth();
				}
			}
		}
		if(leastLocation!=null){
			controller.setIndicatorDot(leastLocation, 0, 0, 255); //Blue
			controller.water(leastLocation);
		}
		//detect trees that are dead to update treeCount;
	}
}
