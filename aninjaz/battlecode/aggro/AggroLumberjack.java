package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.TreeInfo;

public class AggroLumberjack {
	private static RobotController controller;
	private static MapLocation target;
	public static void run(RobotController controller) throws GameActionException{
		AggroLumberjack.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			findTarget();
			if(target==null){
				direction = Util.tryRandomMove(direction);
			}else{
				if(controller.canMove(target)){
					controller.move(target);
				}
			}
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(2f, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0&&controller.canStrike()){
				controller.strike();
			}else{
				chopNearest();
			}
			Util.yieldByteCodes();
		}
	}
	public static void findTarget(){
		
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
