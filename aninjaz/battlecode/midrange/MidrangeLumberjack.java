package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.DynamicTargeting;
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
	public static void run(RobotController controller) throws GameActionException{
		MidrangeLumberjack.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			DynamicTargeting.removeTargets();
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			TreeInfo bestTree = findBestTree(nearbyTrees);
			if(bestTree!=null){
				DynamicTargeting.addTreeTarget(bestTree);
			}
			if(nearbyRobots.length>0){
				DynamicTargeting.addRobotTarget(nearbyRobots[0]);
			}
			DynamicTargeting.getTarget();
			MapLocation target = DynamicTargeting.targetLocation;
			if(target==null){
				controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
				direction = Util.tryRandomMove(direction);
			}else{
				controller.setIndicatorDot(target, 255, 128, 0);
				MapLocation location = Pathfinding.pathfindTankLumberjack(target, DynamicTargeting.targetRadius);
				if(controller.canMove(location)){
					controller.move(location);
				}
			}
			if(nearbyRobots.length>0&&controller.canStrike()&&
					controller.getLocation().isWithinDistance(nearbyRobots[0].getLocation(), 2f+nearbyRobots[0].getRadius())){
				controller.setIndicatorDot(controller.getLocation(), 255, 255, 0);
				controller.strike();
			}else{
				controller.setIndicatorDot(controller.getLocation(), 0, 0, 255);
				chopNearest();
			}
			Util.yieldByteCodes();
		}
	}
	public static TreeInfo findBestTree(TreeInfo[] nearbyTrees){
		if(nearbyTrees.length==0){
			return null;
		}
		for(TreeInfo tree: nearbyTrees){
			if(tree.getContainedRobot()!=null){
				return tree;
			}
		}
		return nearbyTrees[0];
	}
	public static void chopNearest() throws GameActionException{
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees();
		for(TreeInfo tree: nearbyTrees){
			if(tree.getTeam()!=controller.getTeam()){
				if(controller.canChop(tree.getID())){
					controller.chop(tree.getID());
					return;
				}
			}
		}
	}
}
