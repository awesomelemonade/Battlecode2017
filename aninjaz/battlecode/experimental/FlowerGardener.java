package aninjaz.battlecode.experimental;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class FlowerGardener {
	private static final float WATER_RADIUS = 2f;
	public static final int FLOWER_GARDENER_ORIGIN = 35; // [0, 255]
	public static final int COMPRESSED_UNUSED_STANDARD_ORIGIN = CompressedData.compressData(FLOWER_GARDENER_ORIGIN, 0, 0);
	public static final int COMPRESSED_USED_STANDARD_ORIGIN = CompressedData.compressData(FLOWER_GARDENER_ORIGIN, 0, 1);
	public static final int COMPRESSED_UNUSED_TANK_ORIGIN = CompressedData.compressData(FLOWER_GARDENER_ORIGIN, 1, 0);
	public static final int COMPRESSED_USED_TANK_ORIGIN = CompressedData.compressData(FLOWER_GARDENER_ORIGIN, 1, 1);
	public static final float STANDARD_FLOWER_RADIUS = 4f+GameConstants.GENERAL_SPAWN_OFFSET;
	public static final float TANK_FLOWER_RADIUS = 6f+GameConstants.GENERAL_SPAWN_OFFSET;
	public static final float NEUTRAL_TREE_RADIUS = 3f+GameConstants.GENERAL_SPAWN_OFFSET;
	private static RobotController controller;
	private static boolean settled = false;
	private static MapLocation origin;
	private static int originChannel = -1;
	private static Direction opening;
	private static Direction[] plants;
	public static RobotType spawnType;
	private static int spawnTime;
	private static final int CRAMPED = 2;
	private static final int TURTLE_STRAT = 3;
	private static final int SOLDIER_RANGE = 4;
	private static final int FAR_SOLDIER = 5;
	private static int soldierDefenseCount = 0;
	public static void run(RobotController controller) throws GameActionException{
		FlowerGardener.controller = controller;
		spawnTime = controller.getRoundNum();
		Direction randomDirection = Util.randomDirection();
		//findorigin
			//spawn initial robots
		int battleMode = controller.readBroadcast(Constants.CHANNEL_CURRENT_STRAT);
		if(battleMode == CRAMPED||battleMode == TURTLE_STRAT){
			controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_SOLDIER, 1);
		}
		else if(battleMode == SOLDIER_RANGE || battleMode == FAR_SOLDIER){
			controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_LUMBERJACK, 1);
		}
		int initialScout = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_SCOUT);
		int initialLumberjack = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_LUMBERJACK);
		int initialSoldier = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_SOLDIER);
		while(initialScout==0||initialLumberjack==0||initialSoldier==0){
			controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
			if(initialScout==0){
				Direction direction = Pathfinding.findSpawn(RobotType.SCOUT.bodyRadius);
				if(controller.canBuildRobot(RobotType.SCOUT, direction)){
					controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_SCOUT, 1);
					controller.buildRobot(RobotType.SCOUT, direction);
				}
			}else if(initialLumberjack==0){
				Direction direction = Pathfinding.findSpawn(RobotType.LUMBERJACK.bodyRadius);
				if(controller.canBuildRobot(RobotType.LUMBERJACK, direction)){
					controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_LUMBERJACK, 1);
					controller.buildRobot(RobotType.LUMBERJACK, direction);
				}
			}else if(initialSoldier==0){
				Direction direction = Pathfinding.findSpawn(RobotType.SOLDIER.bodyRadius);
				if(controller.canBuildRobot(RobotType.SOLDIER, direction)){
					controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_SOLDIER, 1);
					controller.buildRobot(RobotType.SOLDIER, direction);
				}
			}
			randomDirection = Util.tryRandomMove(randomDirection);
			Util.yieldByteCodes();
			initialScout = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_SCOUT);
			initialLumberjack = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_LUMBERJACK);
			initialSoldier = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_SOLDIER);
		}
		//Determine spawntype
		while(!settled){
			if(originChannel!=-1){
				controller.broadcast(originChannel, COMPRESSED_UNUSED_STANDARD_ORIGIN);
				originChannel = -1;
			}
			if(controller.getTeamBullets()>500f){
				spawnType = RobotType.TANK;
			}else{
				spawnType = RobotType.LUMBERJACK;
			}
			if(isValidOrigin(controller.getLocation(), spawnType)){
				origin = controller.getLocation();
				originChannel = DynamicBroadcasting.markNextAvailableMapper();
				if(spawnType==RobotType.TANK){
					controller.broadcast(originChannel, COMPRESSED_USED_TANK_ORIGIN);
				}else{
					controller.broadcast(originChannel, COMPRESSED_USED_STANDARD_ORIGIN);
				}
				controller.broadcast(originChannel-1, CompressedData.compressMapLocation(origin));
				settled = true;
			}else{
				float bestDistance = Float.MAX_VALUE;
				MapLocation bestLocation = null;
				int bestChannel = -1;
				for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
					int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
					int mapperData = controller.readBroadcast(mapperChannel);
					for(int bit=0;bit<Integer.SIZE;++bit){
						if(((mapperData>>>bit)&1)==1){
							int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
							int compressedData = controller.readBroadcast(dataChannel);
							if(compressedData==FlowerGardener.COMPRESSED_UNUSED_STANDARD_ORIGIN){
								MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
								float distance = controller.getLocation().distanceTo(location);
								if(distance<bestDistance){
									bestLocation = location;
									bestChannel = dataChannel;
									bestDistance = distance;
								}
							}
						}
					}
				}
				if(bestLocation!=null){
					origin = bestLocation;
					originChannel = bestChannel;
					controller.broadcast(originChannel, COMPRESSED_USED_STANDARD_ORIGIN);
					MapLocation location = Pathfinding.pathfind(origin);
					if(controller.canMove(location)){
						controller.move(origin);
						if(controller.getLocation().equals(origin)){
							settled = true;
						}
					}
				}else{
					randomDirection = Util.tryRandomMove(randomDirection);
				}
			}
			Util.yieldByteCodes();
		}
		if(controller.getRoundNum()-spawnTime>80){ //forced to be settled
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(3.5f, Team.NEUTRAL);
			while(nearbyTrees.length>0){
				Direction direction = Pathfinding.findSpawn(RobotType.LUMBERJACK.bodyRadius);
				if(controller.canBuildRobot(RobotType.LUMBERJACK, direction)){
					controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_LUMBERJACK, 1);
					controller.buildRobot(RobotType.LUMBERJACK, direction);
				}
				Util.yieldByteCodes();
				nearbyTrees = controller.senseNearbyTrees(3.5f, Team.NEUTRAL);
			}
		}
		float offsetDirection = calcOffsetDirection();
		if(spawnType==RobotType.TANK){
			setupTankTrees(offsetDirection);
		}else{
			setupTrees(offsetDirection);
		}
		if(battleMode != SOLDIER_RANGE && battleMode != FAR_SOLDIER){
			soldierDefenseCount=3;
		}
		while(true){
			if(soldierDefenseCount<2){
				spawnType = RobotType.SOLDIER;
			}
			else if(spawnType!=RobotType.TANK){
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
				if(nearbyTrees.length>0){
					controller.broadcast(Constants.CHANNEL_REQUEST_LUMBERJACKS, controller.getRoundNum());
				}
				if(controller.getRoundNum()-controller.readBroadcast(Constants.CHANNEL_REQUEST_LUMBERJACKS)<=5){
					spawnType = RobotType.LUMBERJACK;
				}else{
					spawnType = RobotType.SOLDIER;
				}
			}
			plantTrees();
			spawnUnits();
			waterTrees();
			Util.yieldByteCodes();
		}
	}
	public static float calcOffsetDirection() throws GameActionException{
		for(Direction direction: Constants.CARDINAL_DIRECTIONS){
			if(!controller.onTheMap(origin.add(direction, RobotType.GARDENER.sensorRadius-Constants.EPSILON))){
				return direction.opposite().radians;
			}
		}
		return (float) (Math.random()*Math.PI*2);
	}
	public static void plantTrees() throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		for(Direction direction: plants){
			if(controller.canPlantTree(direction)){
				controller.plantTree(direction);
				return;
			}
		}
	}
	public static void spawnUnits() throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		if(controller.canBuildRobot(spawnType, opening)){
			controller.buildRobot(spawnType, opening);
			if(spawnType==RobotType.SOLDIER){
				soldierDefenseCount++;
			}
		}
	}
	public static boolean isValidOrigin(MapLocation origin, RobotType type) throws GameActionException{
		for(Direction direction: Constants.CARDINAL_DIRECTIONS){
			if(!controller.onTheMap(origin.add(direction, 2f+GameConstants.GENERAL_SPAWN_OFFSET), GameConstants.BULLET_TREE_RADIUS)){
				return false;
			}
		}
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(NEUTRAL_TREE_RADIUS, Team.NEUTRAL);
		if(nearbyTrees.length>0){
			if(controller.getRoundNum()-spawnTime<80){
				return false;
			}
		}
		float ourRadius = type==RobotType.TANK?TANK_FLOWER_RADIUS:STANDARD_FLOWER_RADIUS;
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					int compressedData = controller.readBroadcast(dataChannel);
					if(CompressedData.getIdentifier(compressedData)==FLOWER_GARDENER_ORIGIN){
						float radius = CompressedData.getSubIdentifier(compressedData)==1?TANK_FLOWER_RADIUS:STANDARD_FLOWER_RADIUS;
						MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
						float distance = origin.distanceTo(location);
						if(distance<ourRadius+radius){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	public static void setupTrees(float offset){
		opening = new Direction(offset);
		plants = new Direction[]{
				new Direction((float) (Math.PI/3+offset)),
				new Direction((float) (Math.PI*2/3+offset)),
				new Direction((float) (Math.PI+offset)),
				new Direction((float) (Math.PI*4/3+offset)),
				new Direction((float) (Math.PI*5/3+offset))
		};
	}
	public static void setupTankTrees(float offset){
		opening = new Direction(offset);
		plants = new Direction[]{
				new Direction((float) (Math.PI*5/6+offset)),
				new Direction((float) (Math.PI*7/6+offset)),
				new Direction((float) (Math.PI/2+offset)),
				new Direction((float) (Math.PI*3/2+offset))
		};
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
