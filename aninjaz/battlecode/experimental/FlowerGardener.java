package aninjaz.battlecode.experimental;

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
	public static final int FLOWER_GARDENER_ORIGIN = 35; // [0, 255]
	public static final int COMPRESSED_UNUSED_ORIGIN = CompressedData.compressData(FLOWER_GARDENER_ORIGIN, 0, 0);
	public static final int COMPRESSED_USED_ORIGIN = CompressedData.compressData(FLOWER_GARDENER_ORIGIN, 0, 1);
	public static final float STANDARD_FLOWER_RADIUS = 4f+GameConstants.GENERAL_SPAWN_OFFSET;
	public static final float TANK_FLOWER_RADIUS = 6f+GameConstants.GENERAL_SPAWN_OFFSET;
	public static final float NEUTRAL_TREE_RADIUS = 3f+GameConstants.GENERAL_SPAWN_OFFSET;
	private static RobotController controller;
	private static Direction randomDirection;
	private static boolean settled = false;
	private static MapLocation origin;
	private static int originChannel = -1;
	private static Direction opening;
	private static Direction[] plants;
	private static RobotType spawnType;
	public static void run(RobotController controller) throws GameActionException{
		FlowerGardener.controller = controller;
		//findorigin
			//spawn initial robots
		randomDirection = Util.randomDirection();
		while(!settled){
			if(findOrigin()){
				settled = true;
			}else{
				if(origin==null){
					randomDirection = Util.tryRandomMove(randomDirection);
				}else{
					MapLocation location = Pathfinding.pathfind(origin);
					if(controller.canMove(location)){
						controller.move(location);
						if(controller.getLocation().equals(origin)){
							settled = true;
						}
					}
				}
			}
			Util.yieldByteCodes();
		}
		//setupTrees
		while(true){
			
			waterTrees();
			Util.yieldByteCodes();
		}
	}
	public static boolean isValidOrigin(MapLocation origin, RobotType type) throws GameActionException{
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(NEUTRAL_TREE_RADIUS, Team.NEUTRAL);
		if(nearbyTrees.length>0){
			return false;
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
	public static boolean findOrigin(){
		//attempt
		if(currentLocationIsValid()){
			//set origin to controller.getLocation()
			//return;
			return true;
		}
		if(spawnType==RobotType.TANK){
			return false;
		}
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
					if(compressedData==FlowerGardener.COMPRESSED_UNUSED_ORIGIN){
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
			if(origin!=null&&originChannel!=bestChannel){
				controller.broadcast(originChannel, COMPRESSED_UNUSED_ORIGIN);
			}
			if(origin==null||originChannel!=bestChannel){
				origin = bestLocation;
				originChannel = bestChannel;
				controller.broadcast(bestChannel, COMPRESSED_USED_ORIGIN);
			}
		}
		return false;
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
}
