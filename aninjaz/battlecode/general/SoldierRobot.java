package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SoldierRobot {
	private static RobotController controller;
	private static Direction direction;
	public static void run(RobotController controller) throws GameActionException{
		Util.broadcastCount = Constants.BROADCAST_SOLDIER_COUNT;
		SoldierRobot.controller = controller;
		direction = Util.randomDirection();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				doAttackState(nearbyRobots[0]);
			}else{
				nearbyRobots = controller.senseNearbyRobots(-1, controller.getTeam());
				RobotInfo robot = findNearestValidRobot(nearbyRobots);
				if(robot==null){
					doRandomState();
				}else{
					doRandomState();
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static RobotInfo findNearestValidRobot(RobotInfo[] robots){
		for(RobotInfo info: robots){
			switch(info.getType()){
			case GARDENER:
			case ARCHON:
				return info;
			default:
				break;
			}
		}
		return null;
	}
	public static void doRandomState() throws GameActionException{
		direction = Util.tryRandomMove(direction, RobotType.SOLDIER.strideRadius*0.4f);
	}
	public static void doGuardState(RobotInfo info) throws GameActionException{
		/*controller.setIndicatorDot(info.getLocation(), 255, 128, 128);
		Direction towards = controller.getLocation().directionTo(info.getLocation());
		float distance = controller.getLocation().distanceTo(info.getLocation());
		int tries = 10;
		if(distance>MOVEMENT_RADIUS){
			while(((!controller.canMove(direction))||(!(Math.abs(towards.degreesBetween(direction))<45)))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
		}else{
			while(((!controller.canMove(direction))||(!(Math.abs(towards.degreesBetween(direction))>90)))&&tries>0){
				direction = Util.randomDirection();
			}
		}
		if(tries>0){
			controller.move(direction);
		}*/
	}
	public static void doAttackState(RobotInfo robot) throws GameActionException{
		float distance = controller.getLocation().distanceTo(robot.getLocation())-2;
		Direction directionToShoot = controller.getLocation().directionTo(robot.getLocation());
		if(robot.getType()==RobotType.LUMBERJACK){
			if(distance<3f){
				Direction opposite = directionToShoot.opposite();
				if(controller.canMove(opposite, 3f-distance)){
					controller.move(opposite, 3f-distance);
				}else{
					Direction clockwise = directionToShoot.rotateLeftDegrees(45);
					if(controller.canMove(clockwise)){
						controller.move(clockwise);
					}else{
						direction = Util.tryRandomMove(direction);
					}
					directionToShoot = controller.getLocation().directionTo(robot.getLocation());
				}
			}else{
				if(controller.canMove(directionToShoot, distance-3f)){
					controller.move(directionToShoot, distance-3f);
				}else{
					Direction clockwise = directionToShoot.rotateLeftDegrees(45);
					if(controller.canMove(clockwise)){
						controller.move(clockwise);
					}else{
						direction = Util.tryRandomMove(direction);
					}
					directionToShoot = controller.getLocation().directionTo(robot.getLocation());
				}
			}
		}else{
			if(controller.canMove(directionToShoot, distance)){
				controller.move(directionToShoot, distance);
			}else{
				Direction clockwise = directionToShoot.rotateLeftDegrees(45);
				if(controller.canMove(clockwise)){
					controller.move(clockwise);
				}else{
					direction = Util.tryRandomMove(direction);
				}
				directionToShoot = controller.getLocation().directionTo(robot.getLocation());
			}
		}
		if(!Util.inFiringRange(controller.senseNearbyRobots(distance, controller.getTeam()), directionToShoot, 50)){
			if(distance<RobotType.SOLDIER.bulletSpeed*1.6f){
				if(robot.getType()==RobotType.SOLDIER||robot.getType()==RobotType.TANK){
					if(controller.canFirePentadShot()){
						controller.firePentadShot(directionToShoot);
					}
				}else{
					if(controller.canFireTriadShot()){
						controller.fireTriadShot(directionToShoot);
					}
				}
			}else{
				if(robot.getType()==RobotType.SOLDIER||robot.getType()==RobotType.TANK){
					if(controller.canFireTriadShot()){
						controller.fireTriadShot(directionToShoot);
					}
				}else{
					if(controller.canFireSingleShot()){
						controller.fireSingleShot(directionToShoot);
					}
				}
			}
		}
	}
}
