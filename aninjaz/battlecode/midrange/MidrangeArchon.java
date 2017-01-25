package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
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
	
	public static void run(RobotController controller) throws GameActionException{
		MidrangeArchon.controller = controller;
		Direction direction = Util.randomDirection();
		int initialGardener = controller.readBroadcast(Constants.CHANNEL_SPAWNED_INITIAL_GARDENER);
		if(initialGardener==0){
			hireGardener();
			controller.broadcast(Constants.CHANNEL_SPAWNED_INITIAL_GARDENER, 1);
		}
		while(true){
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
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
			if(controller.getRoundNum()>80){
				RobotInfo[] ourGardeners = controller.senseNearbyRobots(-1, controller.getTeam());
				int count = 0;
				for(RobotInfo robot: ourGardeners){
					if(robot.getType()==RobotType.GARDENER){
						count++;
					}
				}
				if(count<=3){
					tryHireGardener();
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static void tryHireGardener() throws GameActionException{
		Direction direction = Util.randomDirection();
		int tries = 10;
		while((!controller.canHireGardener(direction))&&tries>0){
			direction = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			if(controller.getTeamBullets()>700){
				controller.broadcast(Constants.CHANNEL_SPAWN_TANK_GARDENER, controller.readBroadcast(Constants.CHANNEL_SPAWN_TANK_GARDENER)+1);
			}
			controller.hireGardener(direction);
		}
	}
	public static void hireGardener() throws GameActionException{
		Direction direction = Util.randomDirection();
		while((!controller.canHireGardener(direction))){
			direction = Util.randomDirection();
		}
		controller.hireGardener(direction);
	}
}
