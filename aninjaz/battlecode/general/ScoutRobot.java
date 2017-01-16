package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class ScoutRobot {
	private static RobotController controller;
	private static Direction direction;
	private static float minDistance = 3f;
	private static float maxDistance = 5f;
	public static void run(RobotController controller) throws GameActionException{
		ScoutRobot.controller = controller;
		Util.broadcastCount = Constants.BROADCAST_SCOUT_COUNT;
		direction = Util.randomDirection();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			RobotInfo bestRobot = getBestRobot(nearbyRobots);
			TreeInfo[] neutralTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
			for(TreeInfo tree: neutralTrees){
				if(controller.canShake(tree.getID())){
					controller.shake(tree.getID());
				}
			}
			targetNeutralTrees:{
				for(TreeInfo tree: neutralTrees){
					if(tree.getContainedBullets()>0){
						if(controller.canMove(tree.getLocation())){
							controller.setIndicatorLine(controller.getLocation(), tree.getLocation(), 255, 128, 0);
							controller.setIndicatorDot(tree.getLocation(), 255, 128, 0);
							controller.move(tree.getLocation());
							break targetNeutralTrees;
						}
					}
				}
				targetRobot(bestRobot);
			}
			Util.yieldByteCodes();
		}
	}
	public static void targetRobot(RobotInfo bestRobot) throws GameActionException{
		controller.setIndicatorLine(controller.getLocation(), bestRobot.getLocation(), 255, 0, 0);
		controller.setIndicatorDot(bestRobot.getLocation(), 255, 0, 0);
		float distance = controller.getLocation().distanceTo(bestRobot.getLocation())-2;
		Direction directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
		if(bestRobot.getType()==RobotType.GARDENER||bestRobot.getType()==RobotType.SCOUT){
			if(controller.canMove(directionTowards, distance)){
				controller.move(directionTowards, distance);
			}else{
				if(distance>Constants.EPSILON){
					direction = Util.tryRandomMove(direction);
				}
			}
		}else{
			if(distance>maxDistance){
				if(controller.canMove(directionTowards, distance-maxDistance)){
					controller.move(directionTowards, distance);
				}else{
					direction = Util.tryRandomMove(direction);
				}
			}else if(distance<minDistance){
				Direction opposite = directionTowards.opposite();
				if(controller.canMove(opposite, minDistance-distance)){
					controller.move(opposite, minDistance-distance);
				}else{
					direction = Util.tryRandomMove(direction);
				}
			}else{
				Direction clockwise = directionTowards.rotateLeftDegrees(45);
				if(controller.canMove(clockwise)){
					controller.move(clockwise);
				}else{
					direction = Util.tryRandomMove(direction);
				}
				directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
			}
		}
		//shoot
		if(!Util.inFiringRange(controller.senseNearbyRobots(distance, controller.getTeam()), directionTowards, 15)){
			if(controller.canFireSingleShot()){
				controller.fireSingleShot(directionTowards);
			}
		}
	}
	public static RobotInfo getBestRobot(RobotInfo[] robots){
		if(robots.length==0){
			return null;
		}
		for(RobotInfo robot: robots){
			if(robot.getType()==RobotType.GARDENER){
				return robot;
			}
		}
		for(RobotInfo robot: robots){
			if(robot.getType()!=RobotType.ARCHON){
				return robot;
			}
		}
		return robots[0];
	}
}
