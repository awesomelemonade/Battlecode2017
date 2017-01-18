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
	private static float overShoot = 1.5f;
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
				if(bestRobot==null){
					direction = Util.tryRandomMove(direction);
				}else{
					targetRobot(bestRobot);
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static void targetRobot(RobotInfo bestRobot) throws GameActionException{
		controller.setIndicatorLine(controller.getLocation(), bestRobot.getLocation(), 255, 0, 0);
		controller.setIndicatorDot(bestRobot.getLocation(), 255, 0, 0);
		float distance = controller.getLocation().distanceTo(bestRobot.getLocation())-2;
		Direction directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
		if(bestRobot.getType()==RobotType.GARDENER||bestRobot.getType()==RobotType.SCOUT||bestRobot.getType()==RobotType.ARCHON){
			if(controller.canMove(directionTowards, distance-Constants.EPSILON)){
				controller.setIndicatorDot(controller.getLocation(), 0, 255, 0);
				controller.move(directionTowards, distance-Constants.EPSILON);
			}else{
				direction = Util.tryRandomMove(direction);
				directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
			}
			distance = controller.getLocation().distanceTo(bestRobot.getLocation())-2; //Recalculate distance after moving
			if(distance<=RobotType.SCOUT.bulletSpeed){
				shoot(distance, directionTowards);
			}
		}else{
			if(distance>maxDistance){
				controller.setIndicatorDot(controller.getLocation(), 0, 255, 0);
				if(controller.canMove(directionTowards, distance-maxDistance+overShoot)){
					controller.move(directionTowards, distance-maxDistance+overShoot);
				}else{
					direction = Util.tryRandomMove(direction);
					directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
				}
			}else if(distance<minDistance){
				controller.setIndicatorDot(controller.getLocation(), 255, 0, 0);
				Direction opposite = directionTowards.opposite();
				if(controller.canMove(opposite, minDistance-distance+overShoot)){
					controller.move(opposite, minDistance-distance+overShoot);
				}else{
					direction = Util.tryRandomMove(direction);
					directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
				}
			}else{
				controller.setIndicatorDot(controller.getLocation(), 255, 255, 0);
				Direction clockwise = directionTowards.rotateLeftDegrees(45);
				if(controller.canMove(clockwise)){
					controller.move(clockwise);
				}else{
					Direction counterclockwise = directionTowards.rotateRightDegrees(45);
					if(controller.canMove(counterclockwise)){
						controller.move(counterclockwise);
					}
				}
				
				directionTowards = controller.getLocation().directionTo(bestRobot.getLocation());
			}
			distance = controller.getLocation().distanceTo(bestRobot.getLocation())-2; //Recalculate distance after moving
			shoot(distance, directionTowards);
		}
	}
	public static void shoot(float distance, Direction direction) throws GameActionException{
		if(!Util.inFiringRange(controller.senseNearbyRobots(distance, controller.getTeam()), direction, 5)){
			if(controller.canFireSingleShot()){
				controller.fireSingleShot(direction);
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
