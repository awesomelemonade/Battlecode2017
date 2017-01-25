package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class MidrangeLumberjack {
	private static RobotController controller;
	private static MapLocation target;
	public static void run(RobotController controller) throws GameActionException{
		MidrangeLumberjack.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			findTarget();
			if(target==null){
				int status = controller.readBroadcast(Constants.CHANNEL_LUMBERJACK_TARGET_INFO);
				if(status==1){
					target = CompressedData.uncompressMapLocation(controller.readBroadcast(Constants.CHANNEL_LUMBERJACK_TARGET_LOCATION));
				}
			}else{
				controller.broadcast(Constants.CHANNEL_LUMBERJACK_TARGET_INFO, 1);
				controller.broadcast(Constants.CHANNEL_LUMBERJACK_TARGET_LOCATION, CompressedData.compressMapLocation(target));
			}
			if(target==null){
				direction = Util.tryRandomMove(direction);
			}else{
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees();
				if(nearbyTrees.length>15){
					if(controller.canMove(target)){
						controller.move(target);
					}
				}else{
					MapLocation location = Pathfinding.pathfind(target);
					if(controller.canMove(location)){
						controller.move(location);
					}
				}
			}
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(2f, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0&&controller.canStrike()){
				controller.setIndicatorDot(controller.getLocation(), 255, 255, 0);
				controller.strike();
			}else{
				controller.setIndicatorDot(controller.getLocation(), 0, 0, 255);
				chopNearest();
			}
			Util.yieldByteCodes();
		}
	}
	public static void findTarget(){
		RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
		if(nearbyRobots.length>0){
			target = nearbyRobots[0].getLocation();
			return;
		}
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
		for(TreeInfo tree: nearbyTrees){
			if(tree.getContainedRobot()!=null){
				target = tree.getLocation();
				return;
			}
		}
		if(nearbyTrees.length>0){
			target = nearbyTrees[0].getLocation();
			return;
		}
		target = null;
	}
	public static void chopNearest() throws GameActionException{
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees();
		for(TreeInfo tree: nearbyTrees){
			if(tree.getTeam()!=controller.getTeam()){
				if(controller.canChop(tree.getID())){
					controller.chop(tree.getID());
				}
				return;
			}
		}
	}
}
