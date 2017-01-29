package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class MidrangeSoldier {
	private static RobotController controller;
	private static MapLocation initialArchon;
	private static int currentTarget = -1;
	private static boolean reachedInitialArchon = false;
	public static void run(RobotController controller) throws GameActionException{
		MidrangeSoldier.controller = controller;
		Direction direction = Util.randomDirection();
		initialArchon = controller.getInitialArchonLocations(Constants.OTHER_TEAM)[0];
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			RobotInfo bestRobot = getBestRobot(nearbyRobots);
			if(bestRobot!=null){
				if(bestRobot.getType()!=RobotType.LUMBERJACK||controller.getLocation().distanceTo(bestRobot.getLocation())>6f){
					MapLocation location = Pathfinding.pathfind(bestRobot.getLocation(), bestRobot.getRadius());
					if(controller.canMove(location)){
						controller.move(location);
					}else{
						controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
					}
				}else{
					if(controller.getLocation().distanceTo(bestRobot.getLocation())<5f){
						if(bestRobot.getType()==RobotType.LUMBERJACK){
							Direction temp = controller.getLocation().directionTo(bestRobot.getLocation()).opposite();
							if(controller.canMove(temp)){
								controller.move(temp);
							}
						}
					}
				}
				shootRobot(bestRobot);
			}else{
				if(reachedInitialArchon){
					controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
					direction = Util.tryRandomMove(direction);
				}else{
					controller.setIndicatorDot(initialArchon, 0, 255, 0);
					MapLocation location = Pathfinding.pathfind(initialArchon);
					if(controller.canMove(location)){
						controller.move(location);
						if(controller.getLocation().distanceTo(initialArchon)>controller.getType().sensorRadius){
							shootSingle(initialArchon);
						}
					}else{
						controller.setIndicatorLine(controller.getLocation(), location, 0, 0, 0);
						TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
						if(nearbyTrees.length>0){
							shootSingle(nearbyTrees[0].getLocation());
						}
					}
					if(controller.getLocation().distanceTo(initialArchon)<1f){
						reachedInitialArchon = true;
					}
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static void shootRobot(RobotInfo robot) throws GameActionException{
		if(robot.getType()==RobotType.GARDENER||robot.getType()==RobotType.ARCHON){
			if(controller.getRoundNum()>600&&controller.getTeamBullets()<120){
				return;
			}
		}
		Direction direction = controller.getLocation().directionTo(robot.getLocation());
		if(!Util.isSafeToShoot(direction)){
			return;
		}
		if(controller.canFirePentadShot()){
			controller.firePentadShot(direction);
		}else if(controller.canFireSingleShot()){
			controller.fireSingleShot(direction);
		}
	}
	public static void shootSingle(MapLocation location) throws GameActionException{
		if(controller.getTeamBullets()<120){
			return;
		}
		Direction direction = controller.getLocation().directionTo(location);
		if(!Util.isSafeToShoot(direction)){
			return;
		}
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
				if(robot.getType()==RobotType.GARDENER){
					TreeInfo[] nearbyTrees = controller.senseNearbyTrees(robot.getLocation(), RobotType.GARDENER.bodyRadius+1, Constants.OTHER_TEAM);
					if(nearbyTrees.length==0){
						currentTarget = robot.getID();
						return robot;
					}else{
						checkTrees:{
							float distance = controller.getLocation().distanceTo(robot.getLocation());
							for(TreeInfo tree: nearbyTrees){
								if(controller.getLocation().distanceTo(tree.getLocation())<distance){
									break checkTrees;
								}
							}
							currentTarget = robot.getID();
							return robot;
						}
					}
				}else{
					currentTarget = robot.getID();
					return robot;
				}
			}
		}
		for(RobotInfo robot: robots){
			if(robot.getType()==RobotType.ARCHON){
				return robot;
			}
		}
		return robots[0];
	}
}