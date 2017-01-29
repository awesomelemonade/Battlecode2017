package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.CompressedData;
import aninjaz.battlecode.util.DynamicTargeting;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Clock;
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
			int startBytecodes = Clock.getBytecodeNum();
			for(TreeInfo tree: nearbyTrees){
				DynamicTargeting.addTarget(DynamicTargeting.SUBIDENTIFIER_TREE, DynamicTargeting.getPriority(tree), tree.getLocation());
			}
			System.out.println("Adding Trees: "+(Clock.getBytecodeNum()-startBytecodes));
			startBytecodes = Clock.getBytecodeNum();
			for(RobotInfo robot: nearbyRobots){
				DynamicTargeting.addTarget(DynamicTargeting.SUBIDENTIFIER_ROBOT, 15, robot.getLocation());
			}
			System.out.println("Adding Robots: "+(Clock.getBytecodeNum()-startBytecodes));
			startBytecodes = Clock.getBytecodeNum();
			MapLocation target = DynamicTargeting.getTarget();
			System.out.println("Retrieving Target: "+(Clock.getBytecodeNum()-startBytecodes));
			if(target==null){
				controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
				direction = Util.tryRandomMove(direction);
			}else{
				controller.setIndicatorDot(target, 255, 128, 0);
				MapLocation location = Pathfinding.pathfindTankLumberjack(target);
				if(controller.canMove(location)){
					controller.move(location);
				}
			}
			if(nearbyRobots.length>0&&controller.canStrike()&&
					controller.getLocation().distanceTo(nearbyRobots[0].getLocation())-nearbyRobots[0].getRadius()<=2f){
				controller.setIndicatorDot(controller.getLocation(), 255, 255, 0);
				controller.strike();
			}else{
				controller.setIndicatorDot(controller.getLocation(), 0, 0, 255);
				chopNearest();
			}
			Util.yieldByteCodes();
		}
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
