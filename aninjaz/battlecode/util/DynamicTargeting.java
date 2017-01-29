package aninjaz.battlecode.util;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;

public class DynamicTargeting {
	public static RobotController controller;
	public static final int TARGET_IDENTIFIER = 87;
	public static final int SUBIDENTIFIER_ROBOT = 1;
	public static final int SUBIDENTIFIER_TREE = 2;
	public static final int PRIORITY_CHOP_STANDARD_TREE = 5;
	public static final int PRIORITY_CHOP_ROBOT_TREE = 10;
	public static final int PRIORITY_ATTACK_ENEMY = 15;
	public static int getPriority(TreeInfo tree){
		if(tree.getContainedRobot()==null){
			return PRIORITY_CHOP_STANDARD_TREE;
		}else{
			return PRIORITY_CHOP_ROBOT_TREE;
		}
	}
	public static void addTreeTarget(int priority, MapLocation target) throws GameActionException{
		int compressedTargetData = CompressedData.compressData(TARGET_IDENTIFIER, SUBIDENTIFIER_TREE, priority);
		int compressedTargetLocation = CompressedData.compressMapLocation(target);
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
		controller.setIndicatorDot(target, 0, 255, 0);
		int targetChannel = DynamicBroadcasting.markNextAvailableMapper();
		controller.broadcast(targetChannel, compressedTargetData);
		controller.broadcast(targetChannel-1, compressedTargetLocation);
	}
	public static void addRobotTarget(int priority, MapLocation target) throws GameActionException{
		int count = 0;
		int oldestChannel = -1;
		int oldestRound = 3000;
		int compressedTargetData = CompressedData.compressData(TARGET_IDENTIFIER, SUBIDENTIFIER_ROBOT, priority);
		int compressedTargetLocation = CompressedData.compressMapLocation(target);
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
		controller.setIndicatorDot(target, 0, 255, 0);
		if(count<5){
			int targetChannel = DynamicBroadcasting.markNextAvailableMapper();
			controller.broadcast(targetChannel, compressedTargetData);
			controller.broadcast(targetChannel-1, compressedTargetLocation);
			controller.broadcast(targetChannel-2, controller.getRoundNum());
		}else{
			controller.broadcast(oldestChannel, compressedTargetData);
			controller.broadcast(oldestChannel-1, compressedTargetLocation);
			controller.broadcast(oldestChannel-2, controller.getRoundNum());
		}
	}
	public static MapLocation getTarget(int subIdentifier) throws GameActionException{
		int bestPriority = 0;
		MapLocation bestLocation = null;
		float bestDistance = Float.MAX_VALUE;
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			int mapperChannel = DynamicBroadcasting.getMapperChannel(mapper);
			int mapperData = controller.readBroadcast(mapperChannel);
			for(int bit=0;bit<Integer.SIZE;++bit){
				if(((mapperData>>>bit)&1)==1){
					int dataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
					int compressedData = controller.readBroadcast(dataChannel);
					if(CompressedData.getIdentifier(compressedData)==TARGET_IDENTIFIER){
						if(CompressedData.getSubIdentifier(compressedData)==subIdentifier){
							int priority = CompressedData.getData(compressedData);
							MapLocation location = CompressedData.uncompressMapLocation(controller.readBroadcast(dataChannel-1));
							if(priority>bestPriority){
								bestPriority = priority;
								bestLocation = location;
							}else if(priority==bestPriority){
								float distance = bestLocation.distanceTo(controller.getLocation());
								if(distance<bestDistance){
									bestLocation = location;
									bestDistance = distance;
								}
							}
						}
					}
				}
			}
		}
		return bestLocation;
	}
	public static MapLocation getTarget() throws GameActionException{
		int bestPriority = 0;
		MapLocation bestLocation = null;
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
						if(priority>bestPriority){
							bestPriority = priority;
							bestLocation = location;
							bestDistance = distance;
						}else if(priority==bestPriority){
							if(distance<bestDistance){
								bestLocation = location;
								bestDistance = distance;
							}
						}
					}
				}
			}
		}
		return bestLocation;
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
						if(location.isWithinDistance(controller.getLocation(), 2f)){
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
