package aninjaz.battlecode.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;

public class DynamicTargeting {
	public static RobotController controller;
	public static final int TARGET_IDENTIFIER = 87;
	public static final int SUBIDENTIFIER_ROBOT = 1;
	public static final int SUBIDENTIFIER_TREE = 2;
	public static final int SUBIDENTIFIER_ARCHON = 3;
	public static final int PRIORITY_TARGET_ARCHON = 3;
	public static final int PRIORITY_CHOP_STANDARD_TREE = 5;
	public static final int PRIORITY_CHOP_ROBOT_TREE = 10;
	public static final int PRIORITY_ATTACK_ENEMY = 15;
	public static void addArchonTarget(MapLocation target) throws GameActionException{
		int compressedTargetData = CompressedData.compressData(TARGET_IDENTIFIER, SUBIDENTIFIER_ARCHON, PRIORITY_TARGET_ARCHON);
		int compressedTargetLocation = CompressedData.compressMapLocation(target);
		controller.setIndicatorDot(target, 0, 255, 0);
		int targetChannel = DynamicBroadcasting.markNextAvailableMapper();
		controller.broadcast(targetChannel, compressedTargetData);
		controller.broadcast(targetChannel-1, compressedTargetLocation);
		controller.broadcast(targetChannel-3, 0);
	}
	public static void addTreeTarget(TreeInfo tree) throws GameActionException{
		int compressedTargetData = CompressedData.compressData(TARGET_IDENTIFIER, SUBIDENTIFIER_TREE,
				tree.getContainedRobot()==null?PRIORITY_CHOP_STANDARD_TREE:PRIORITY_CHOP_ROBOT_TREE);
		int compressedTargetLocation = CompressedData.compressMapLocation(tree.getLocation());
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					if(compressedTargetData==controller.readBroadcast(dataChannel)){
						if(compressedTargetLocation==controller.readBroadcast(dataChannel-1)){
							return;
						}
					}
				}
			}
		}
		int targetChannel = DynamicBroadcasting.markNextAvailableMapper();
		controller.broadcast(targetChannel, compressedTargetData);
		controller.broadcast(targetChannel-1, compressedTargetLocation);
		controller.broadcast(targetChannel-3, CompressedData.compressFloatData(0, tree.getRadius()));
	}
	public static void addRobotTarget(RobotInfo robot) throws GameActionException{
		int count = 0;
		int oldestChannel = -1;
		int oldestRound = 3000;
		int compressedTargetData = CompressedData.compressData(TARGET_IDENTIFIER, SUBIDENTIFIER_ROBOT, PRIORITY_ATTACK_ENEMY);
		int compressedTargetLocation = CompressedData.compressMapLocation(robot.getLocation());
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					int compressedData = controller.readBroadcast(dataChannel);
					if(compressedTargetData==compressedData){
						if(compressedTargetLocation==controller.readBroadcast(dataChannel-1)){
							return;
						}
					}
					if(CompressedData.getIdentifier(compressedData)==TARGET_IDENTIFIER){
						if(CompressedData.getSubIdentifier(compressedData)==SUBIDENTIFIER_ROBOT){
							count++;
							int round = controller.readBroadcast(dataChannel-2);
							if(round<=oldestRound){
								oldestChannel = dataChannel;
								oldestRound = round;
							}
						}
					}
				}
			}
		}
		if(count<5){
			int targetChannel = DynamicBroadcasting.markNextAvailableMapper();
			controller.broadcast(targetChannel, compressedTargetData);
			controller.broadcast(targetChannel-1, compressedTargetLocation);
			controller.broadcast(targetChannel-2, controller.getRoundNum());
			controller.broadcast(targetChannel-3, CompressedData.compressFloatData(robot.getType()==RobotType.LUMBERJACK?1:0, robot.getRadius()));
		}else{
			controller.broadcast(oldestChannel, compressedTargetData);
			controller.broadcast(oldestChannel-1, compressedTargetLocation);
			controller.broadcast(oldestChannel-2, controller.getRoundNum());
			controller.broadcast(oldestChannel-3, CompressedData.compressFloatData(robot.getType()==RobotType.LUMBERJACK?1:0, robot.getRadius()));
		}
	}
	public static MapLocation targetLocation;
	public static float targetRadius;
	public static boolean targetLumberjack = false;
	public static void getTargetNonTree() throws GameActionException{
		targetLocation = null;
		targetRadius = -1;
		targetLumberjack = false;
		int bestPriority = 0;
		float bestDistance = Float.MAX_VALUE;
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					int compressedData = controller.readBroadcast(dataChannel);
					if(CompressedData.getIdentifier(compressedData)==TARGET_IDENTIFIER){
						if(CompressedData.getSubIdentifier(compressedData)!=SUBIDENTIFIER_TREE){
							int priority = CompressedData.getData(compressedData);
							MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
							float distance = location.distanceTo(controller.getLocation());
							if(priority>bestPriority||(priority==bestPriority&&distance<bestDistance)){
								bestPriority = priority;
								bestDistance = distance;
								targetLocation = location;
								int data = controller.readBroadcast(dataChannel-3);
								targetLumberjack = CompressedData.getFloatDataA(data)==1;
								targetRadius = CompressedData.getFloatDataB(data);
							}
						}
					}
				}
			}
		}
	}
	public static void getTarget() throws GameActionException{
		targetLocation = null;
		targetRadius = -1;
		targetLumberjack = false;
		int bestPriority = 0;
		float bestDistance = Float.MAX_VALUE;
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					int compressedData = controller.readBroadcast(dataChannel);
					if(CompressedData.getIdentifier(compressedData)==TARGET_IDENTIFIER){
						int priority = CompressedData.getData(compressedData);
						MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
						float distance = location.distanceTo(controller.getLocation());
						if(priority>bestPriority||(priority==bestPriority&&distance<bestDistance)){
							bestPriority = priority;
							bestDistance = distance;
							targetLocation = location;
							int data = controller.readBroadcast(dataChannel-3);
							targetLumberjack = CompressedData.getFloatDataA(data)==1;
							targetRadius = CompressedData.getFloatDataB(data);
						}
					}
				}
			}
		}
	}
	public static void removeTargets() throws GameActionException{
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					if(CompressedData.getIdentifier(controller.readBroadcast(dataChannel))==DynamicTargeting.TARGET_IDENTIFIER){
						MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
						if(location.isWithinDistance(controller.getLocation(), controller.getType().sensorRadius)){
							DynamicBroadcasting.unmarkMapper(dataChannel);
							controller.broadcast(dataChannel, 0);
						}
					}
				}
			}
		}
	}
	public static void indicateTargets() throws GameActionException{
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					if(CompressedData.getIdentifier(controller.readBroadcast(dataChannel))==TARGET_IDENTIFIER){
						MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
						controller.setIndicatorDot(location, 255, 255, 0);
					}
				}
			}
		}
	}
}
