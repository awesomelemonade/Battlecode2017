package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class TankRobot {
	private enum ShootingModes {
		Mono,
		Tri,
		Penta
	}
	
	private static RobotController controller;
	private static Direction targetDirection;
	
	public static void run(RobotController controller) throws GameActionException {
		TankRobot.controller = controller;
		targetDirection = randomDirectionTowards(getEnemyTerritoryVector(), 1/4);
		
		while (true) {
			RobotInfo[] enemyRobots = controller.senseNearbyRobots(-1, controller.getTeam().opponent());
			
			if (enemyRobots.length > 0) {
				System.out.println("Enemy robots");
				Direction shootDirection = controller.getLocation().directionTo(enemyRobots[0].location);
				
				if (controller.getLocation().isWithinDistance(enemyRobots[0].getLocation(), 2)) {
					shoot(ShootingModes.Tri, shootDirection);
				}
				else if (controller.getLocation().isWithinDistance(enemyRobots[0].getLocation(), 5)) {
					shoot(ShootingModes.Penta, shootDirection);
				}
				else {
					moveRandomlyTowardsTarget(shootDirection, 60);		
					shoot(ShootingModes.Mono, shootDirection);
				}
			}
			
			/*RobotInfo[] teamRobots = controller.senseNearbyRobots(-1, controller.getTeam());
			if (teamRobots.length > 0) {
				
			}*/
			
			else {
				if (controller.isCircleOccupiedExceptByThisRobot(controller.getLocation().add(targetDirection), 2)) {
					targetDirection = Util.randomDirection();
				}
				
				moveRandomlyTowardsTarget(targetDirection, 180);
			}
			
			Util.yieldByteCodes();
		}
		
	}
	
	private static Direction getBounce(Direction incoming) {
		return new Direction((float) ((incoming.radians > 0 ? 1 : -1) * (Math.PI - Math.abs(incoming.radians))));
	}
	
	//Change functionality later to run over enemy trees
	private static boolean doesTreeExist(Direction target) throws GameActionException {
		return controller.isLocationOccupiedByTree(controller.getLocation().add(target, 1));
	}
	
	private static void shoot(ShootingModes mode, Direction direction) throws GameActionException {
		for (int i = mode.ordinal()+1; i > 0; --i) {
			mode = ShootingModes.values()[mode.ordinal() - 1];
			
			switch (mode) {
				case Mono:
					if (controller.canFireSingleShot()) {
						controller.fireSingleShot(direction); 
						return;
					}
					break;
				case Tri:
					if (controller.canFireTriadShot()) {
						controller.fireTriadShot(direction); 
						return;
					}
					break;
				case Penta:
					if (controller.canFirePentadShot()) {
						controller.firePentadShot(direction); 
						return;
					}
					break;
			}
		}
	}
	
	private static void moveRandomlyTowardsTarget(Direction target, int degreesOfVariance) throws GameActionException {
		targetDirection = Util.tryRandomMove(target);
	}
	
	private static Direction randomDirectionTowards(Direction target, int degreesOfVariance) {
		degreesOfVariance /= 2;
		if (degreesOfVariance == 180) degreesOfVariance = 179;
		
		Direction random = new Direction((float) (Math.random()*2*Math.PI));
		float randomness = degreesOfVariance / (180-degreesOfVariance);
		
		return new Direction((float) ((target.radians + random.radians*randomness)/(1+randomness)));
	}
	
	private static Direction getEnemyTerritoryVector() {
		MapLocation[] enemyArchonLocations = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
		int numberOfArchons = enemyArchonLocations.length;
		float totalRadians = 0;
		
		for (MapLocation archon: enemyArchonLocations) {
			totalRadians += archon.directionTo(controller.getLocation()).radians;
		}
		
		return new Direction(totalRadians/numberOfArchons);
	}
}
