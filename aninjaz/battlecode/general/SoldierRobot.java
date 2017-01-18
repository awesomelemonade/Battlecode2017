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
	private static int originChannel;
	private static int originCompressedData;
	private static final float MOVEMENT_RADIUS = RobotType.SOLDIER.sensorRadius+3f;
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
					findOrigin:{
						if(origin==null){
							for(int i=0;i<Util.getMapLocations().length;++i){
								int n = Util.getMapLocations()[i];
								int bit = 0;
								while(((n>>>bit)&1)==0&&bit<32){
									bit++;
								}
								if(bit<32){
									int channel = Util.getChannelLocation(i, bit);
									CompressedMapLocation mapLocation = new CompressedMapLocation(controller.readBroadcast(channel));
									if(mapLocation.getIdentifier()==Constants.BROADCAST_IDENTIFIER_GARDENER&&
											mapLocation.getData()==0){
										origin = mapLocation.getLocation();
										originChannel = channel;
										originCompressedData = new CompressedMapLocation(mapLocation.getIdentifier(), mapLocation.getData()+1, mapLocation.getLocation()).getCompressedData();
										controller.broadcast(channel, originCompressedData);
										break;
									}
								}
							}
							if(origin==null){
								doRandomState();
								break findOrigin;
							}
						}
						doGuardState();
						if(controller.readBroadcast(originChannel)!=originCompressedData){
							origin = null;
						}
					}
				}else{
					doRandomState();
				}
			}
			Util.yieldByteCodes();
		}
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
				controller.setIndicatorDot(controller.getLocation(), 0, 255, 0);
				controller.move(directionToShoot, distance-Constants.EPSILON);
			}else{
				controller.setIndicatorDot(controller.getLocation(), 255, 0, 0);
				direction = Util.tryRandomMove(direction);
				directionToShoot = controller.getLocation().directionTo(robot.getLocation());
			}
		}
		distance = controller.getLocation().distanceTo(robot.getLocation())-2; //Recalculate distance after moving
		if(!Util.inFiringRange(controller.senseNearbyRobots(distance, controller.getTeam()), directionToShoot, 20)){
			if(distance<RobotType.SOLDIER.bulletSpeed*1.4f){
				if(robot.getType()==RobotType.TANK){
					if(controller.canFirePentadShot()){
						controller.firePentadShot(directionToShoot);
					}
				}else{
					if(controller.canFireTriadShot()){
						controller.fireTriadShot(directionToShoot);
					}
				}
			}else{
				if(robot.getType()==RobotType.TANK){
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
