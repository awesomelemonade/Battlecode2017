package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SoldierRobot {
	private static RobotController controller;
	private static Direction direction;
	private static MapLocation origin;
	private static final float MOVEMENT_RADIUS = 15f;
	private static final float RELAXED_MOVEMENT_SPEED = RobotType.SOLDIER.strideRadius*0.4f;
	public static void run(RobotController controller) throws GameActionException{
		Util.broadcastCount = Constants.BROADCAST_SOLDIER_COUNT;
		SoldierRobot.controller = controller;
		direction = Util.randomDirection();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				doAttackState(nearbyRobots[0]);
			}else{
				if(controller.getTeamBullets()<400){
					if(origin==null){
						nearbyRobots = controller.senseNearbyRobots(-1, controller.getTeam());
						RobotInfo robot = findNearestValidRobot(nearbyRobots);
						if(robot==null){
							doRandomState();
						}else{
							origin = robot.getLocation();
							doGuardState();
						}
					}else{
						//checks if the origin still has a robot and is still valid
						if(controller.canSenseLocation(origin)){
							RobotInfo robot = controller.senseRobotAtLocation(origin);
							if(robot==null){
								origin = null;
								doRandomState();
							}else{
								doGuardState();
							}
						}else{
							doGuardState();
						}
					}
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
				return info;
			default:
				break;
			}
		}
		return null;
	}
	public static void doRandomState() throws GameActionException{
		direction = Util.tryRandomMove(direction, RELAXED_MOVEMENT_SPEED);
	}
	public static void doGuardState() throws GameActionException{
		controller.setIndicatorDot(origin, 255, 128, 128);
		Direction towards = controller.getLocation().directionTo(origin);
		float distance = controller.getLocation().distanceTo(origin);
		int tries = 10;
		if(distance>MOVEMENT_RADIUS){
			while(((!controller.canMove(direction, RELAXED_MOVEMENT_SPEED))||
					(!(Math.abs(towards.degreesBetween(direction))<45)))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
		}else{
			while(((!controller.canMove(direction, RELAXED_MOVEMENT_SPEED))||
					(controller.getLocation().add(direction, RELAXED_MOVEMENT_SPEED).distanceTo(origin)>MOVEMENT_RADIUS))&&tries>0){
				direction = Util.randomDirection();
			}
		}
		if(tries>0){
			controller.move(direction, RELAXED_MOVEMENT_SPEED);
		}
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
			if(controller.canMove(directionToShoot, distance-Constants.EPSILON)){
				controller.move(directionToShoot, distance-Constants.EPSILON);
			}else{
				Direction clockwise = directionToShoot.rotateLeftDegrees(45);
				int tries = 10;
				while(!controller.canMove(clockwise)&&tries>0){
					clockwise = clockwise.rotateLeftDegrees(5);
					tries--;
				}
				if(tries>0){
					controller.move(clockwise);
				}
				directionToShoot = controller.getLocation().directionTo(robot.getLocation());
			}
		}
		distance = controller.getLocation().distanceTo(robot.getLocation())-2; //Recalculate distance after moving
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
