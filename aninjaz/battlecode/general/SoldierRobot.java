package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class SoldierRobot {
	private static RobotController controller;
	private static Direction direction;
	private static MapLocation origin;
	private static int originChannel;
	private static int originCompressedData;
	private static MapLocation nearestArchon;
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
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
				for(TreeInfo tree: nearbyTrees){
					if(controller.canShake(tree.getID())){
						controller.shake(tree.getID());
					}
				}
				/*TreeInfo tree = findNearestTreeWithRobot(nearbyTrees);
				if(tree!=null){
					attackTree(tree);
				}else{*/
					if(controller.getTeamBullets()<4000){
						findOrigin();
						if(origin==null){
							controller.setIndicatorDot(controller.getLocation(), 0, 255, 255);
							doRandomState();
						}else{
							doGuardState();
							if(controller.readBroadcast(originChannel)!=originCompressedData){
								origin = null;
							}
						}
					}else{
						doRandomState();
					}
				//}
			}
			if(!lowHealth){
				if(origin!=null){
					if((controller.getHealth()/controller.getType().maxHealth)<Constants.LOW_HEALTH){ //If scout is about to die :(
						CompressedMapLocation mapLocation = new CompressedMapLocation(originCompressedData);
						mapLocation = new CompressedMapLocation(mapLocation.getIdentifier(), mapLocation.getData()-1, mapLocation.getLocation());
						controller.broadcast(originChannel, mapLocation.getCompressedData());
						lowHealth = true;
					}
				}
			}
			Util.yieldByteCodes();
		}
	}
	private static boolean lowHealth = false;
	public static TreeInfo findNearestTreeWithRobot(TreeInfo[] nearbyTrees){
		for(TreeInfo tree: nearbyTrees){
			if(tree.getContainedRobot()!=null){
				return tree;
			}
		}
		return null;
	}
	public static void findOrigin() throws GameActionException{
		if(origin!=null){
			return;
		}
		for(int i=0;i<Util.getMapLocations();++i){
			int n = controller.readBroadcast(Util.getMapLocationChannel(i));
			int bit = 0;
			while(bit<32){
				if(((n>>>bit)&1)==1){
					int channel = Util.getChannelLocation(i, bit);
					CompressedMapLocation mapLocation = new CompressedMapLocation(controller.readBroadcast(channel));
					if(mapLocation.getIdentifier()==Constants.BROADCAST_IDENTIFIER_GARDENER&&
							mapLocation.getData()==0){
						origin = mapLocation.getLocation();
						originChannel = channel;
						originCompressedData = new CompressedMapLocation(mapLocation.getIdentifier(), mapLocation.getData()+1, mapLocation.getLocation()).getCompressedData();
						MapLocation[] archons = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
						nearestArchon = Util.getNearest(origin, archons);
						if(origin.distanceTo(nearestArchon)>MOVEMENT_RADIUS){
							nearestArchon = origin.add(origin.directionTo(nearestArchon), MOVEMENT_RADIUS);
						}
						controller.broadcast(channel, originCompressedData);
						return;
					}
				}
				bit++;
			}
		}
	}
	public static void doRandomState() throws GameActionException{
		direction = Util.tryRandomMove(direction, RELAXED_MOVEMENT_SPEED);
	}
	public static void doGuardState() throws GameActionException{
		if(controller.getRoundNum()-lastAttackedRound>15){
			controller.setIndicatorDot(nearestArchon, 128, 128, 128);
			goTowards(nearestArchon);
		}else{
			controller.setIndicatorDot(origin, 255, 128, 128);
			goTowards(origin);
		}
	}
	public static void goTowards(MapLocation origin) throws GameActionException{
		Direction towards = controller.getLocation().directionTo(origin);
		float distance = controller.getLocation().distanceTo(origin);
		int tries = 10;
		if(distance>MOVEMENT_RADIUS){
			while(((!controller.canMove(direction))||
					(!(Math.abs(towards.degreesBetween(direction))<45)))&&tries>0){
				direction = Util.randomDirection();
				tries--;
			}
			if(tries<=0){
				direction = Util.tryRandomMove(direction);
			}
			if(tries>0){
				controller.move(direction);
			}
		}else{
			while(((!controller.canMove(direction, RELAXED_MOVEMENT_SPEED))||
					(controller.getLocation().add(direction, RELAXED_MOVEMENT_SPEED).distanceTo(origin)>MOVEMENT_RADIUS))&&tries>0){
				direction = Util.randomDirection();
			}
			if(tries<=0){
				direction = Util.tryRandomMove(direction);
			}
			if(tries>0){
				controller.move(direction, RELAXED_MOVEMENT_SPEED);
			}
		}
	}
	public static void attackTree(TreeInfo tree) throws GameActionException{
		float distance = controller.getLocation().distanceTo(tree.getLocation())-RobotType.SOLDIER.bodyRadius-tree.getRadius();
		Direction dir = controller.getLocation().directionTo(tree.getLocation());
		if(controller.canMove(dir, distance-Constants.EPSILON)){
			controller.move(dir, distance-Constants.EPSILON);
		}else{
			direction = Util.tryRandomMove(direction);
			dir = controller.getLocation().directionTo(tree.getLocation());
		}
		if(distance<RobotType.SOLDIER.bulletSpeed*0.1f){
			if(tree.getHealth()>=RobotType.SOLDIER.attackPower*5&&tree.getRadius()>=1.2f){ //check radius of tree?
				if(controller.canFirePentadShot()){
					controller.firePentadShot(dir);
				}
			}else{
				if(controller.canFireSingleShot()){
					controller.fireSingleShot(dir);
				}
			}
		}
	}
	private static int lastAttackedRound = 0;
	public static void doAttackState(RobotInfo robot) throws GameActionException{
		lastAttackedRound = controller.getRoundNum();
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
			if(distance<RobotType.SOLDIER.bulletSpeed*1.4f&&controller.getTeamBullets()>=400||distance<RobotType.SOLDIER.bulletSpeed*0.4f){
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
