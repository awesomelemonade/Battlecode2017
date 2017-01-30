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
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class MidrangeArchon {
	private static RobotController controller;
	private static int lastHireTurn = 0;
	
	public static void run(RobotController controller) throws GameActionException{
		MidrangeArchon.controller = controller;
		Direction direction = Util.randomDirection();
		while(controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_GARDENER)==0){
			direction = Util.tryRandomMove(direction);
			if(tryHireGardener()){
				MapLocation[] initialArchons = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
				for(MapLocation archon: initialArchons){
					DynamicTargeting.addArchonTarget(archon);
				}
				controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_GARDENER, 1);
			}
			Util.yieldByteCodes();
		}
		while(true){
			DynamicTargeting.indicateTargets();
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyTrees.length>0&&controller.getRoundNum()<400){
				controller.broadcast(Constants.CHANNEL_REQUEST_LUMBERJACKS, controller.getRoundNum());
			}
			if(nearbyRobots.length>0){
				Direction opposite = controller.getLocation().directionTo(nearbyRobots[0].getLocation()).opposite();
				MapLocation location = Pathfinding.pathfind(controller.getLocation().add(opposite, 4f));
				if(controller.canMove(location)){
					controller.move(location);
				}else{
					controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
				}
			}else{
				move:{
					for(TreeInfo tree: nearbyTrees){
						if(tree.getContainedBullets()>0){
							if(controller.canShake(tree.getID())){
								controller.shake(tree.getID());
								continue;
							}else{
								controller.setIndicatorLine(controller.getLocation(), tree.getLocation(), 255, 255, 0);
								MapLocation location = Pathfinding.pathfind(tree.getLocation(), tree.getRadius());
								if(controller.canMove(location)){
									controller.move(location);
									break move;
								}else{
									controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
								}
							}
						}
					}
					int tries = 10;
					while((!controller.canMove(direction))&&tries>0){
						direction = Util.randomDirection();
						tries--;
					}
					if(tries>0){
						controller.move(direction);
					}
				}
			}
			if(controller.isBuildReady()&&controller.getRoundNum()>80){
				if(controller.getTreeCount()>=(controller.readBroadcast(Constants.CHANNEL_GARDENER_COUNT)-1)*3+2
						&&((controller.getRoundNum()-lastHireTurn)>35)){
					tryHireGardener();
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static boolean tryHireGardener() throws GameActionException{
		Direction direction = Pathfinding.findSpawn(RobotType.GARDENER.bodyRadius);
		if(direction!=null){
			controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(direction, 4f), 255, 0, 0);
			if(controller.canHireGardener(direction)){
				controller.broadcast(Constants.CHANNEL_GARDENER_COUNT, controller.readBroadcast(Constants.CHANNEL_GARDENER_COUNT)+1);
				controller.hireGardener(direction);
				lastHireTurn = controller.getRoundNum();
				return true;
			}
		}
		return false;
	}
}
