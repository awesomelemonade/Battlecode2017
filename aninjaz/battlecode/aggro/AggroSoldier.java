package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;

public class AggroSoldier {
	private static RobotController controller;
	private static MapLocation initialArchon;
	private static int currentTarget = -1;
	private static boolean reachedInitialArchon = false;
	private static int useNonBidirectional = 0;
	public static void run(RobotController controller) throws GameActionException{
		AggroSoldier.controller = controller;
		Direction direction = Util.randomDirection();
		initialArchon = controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0];
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			RobotInfo bestRobot = getBestRobot(nearbyRobots);
			if(bestRobot!=null){
				Pathfinding.goTowardsBidirectional(bestRobot.getLocation());
				shoot(controller.getLocation().directionTo(bestRobot.getLocation()));
			}else{
				if(reachedInitialArchon){
					direction = Util.tryRandomMove(direction);
				}else{
					if(useNonBidirectional>0){
						if(Pathfinding.goTowardsRight(initialArchon)==Pathfinding.HAS_NOT_MOVED){
							TreeInfo[] nearbyTrees = controller.senseNearbyTrees();
							shoot(controller.getLocation().directionTo(nearbyTrees[0].getLocation()));
							useNonBidirectional = -10;
						}else{
							if(controller.getLocation().distanceTo(initialArchon)>controller.getType().sensorRadius){
								shoot(controller.getLocation().directionTo(initialArchon));
							}
						}
						useNonBidirectional--;
					}else if(useNonBidirectional<0){
						if(Pathfinding.goTowardsLeft(initialArchon)==Pathfinding.HAS_NOT_MOVED){
							TreeInfo[] nearbyTrees = controller.senseNearbyTrees();
							shoot(controller.getLocation().directionTo(nearbyTrees[0].getLocation()));
							useNonBidirectional = 10;
						}else{
							if(controller.getLocation().distanceTo(initialArchon)>controller.getType().sensorRadius){
								shoot(controller.getLocation().directionTo(initialArchon));
							}
						}
						useNonBidirectional++;
					}else{
						int status = Pathfinding.goTowardsBidirectional(initialArchon);
						if(status==Pathfinding.REACHED_GOAL){
							reachedInitialArchon = true;
						}else if(status==Pathfinding.HAS_NOT_MOVED){
							useNonBidirectional = 10;
						}
						if(controller.getLocation().distanceTo(initialArchon)>controller.getType().sensorRadius){
							shoot(controller.getLocation().directionTo(initialArchon));
						}
					}
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static void shoot(Direction direction) throws GameActionException{
		if(controller.canFireSingleShot()){
			controller.fireSingleShot(direction);
		}
	}
	public static RobotInfo getBestRobot(RobotInfo[] robots){
		if(robots.length==0){
			return null;
		}
		if(currentTarget!=-1){
			for(RobotInfo robot: robots){
				if(robot.getID()==currentTarget){
					return robot;
				}
			}
		}
		for(RobotInfo robot: robots){
			if(robot.getType()!=RobotType.ARCHON){
				currentTarget = robot.getID();
				return robot;
			}
		}
		return robots[0];
	}
}
