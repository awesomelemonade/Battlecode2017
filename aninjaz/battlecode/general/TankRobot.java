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
	private static Direction enemyTerritory;
	private static Direction targetDirection;
	private static float randomRatio = 0.7f;
	private static boolean hasMoved = false;
	
	public static void run(RobotController controller) throws GameActionException {
		TankRobot.controller = controller;
		enemyTerritory = getEnemyTerritoryVector();
		targetDirection = randomDirectionTowardsEnemy();
		
		while (true) {
			RobotInfo[] enemyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			
			if (enemyRobots.length > 0) {
				Direction shootDirection = controller.getLocation().directionTo(enemyRobots[0].location);
				if (controller.getLocation().isWithinDistance(enemyRobots[0].getLocation(), 2)) {
					shoot(ShootingModes.Mono, shootDirection);
				}
				else if (controller.getLocation().isWithinDistance(enemyRobots[0].getLocation(), 5)) {
					shoot(ShootingModes.Tri, shootDirection);
				}
				else {
					shoot(ShootingModes.Penta, shootDirection);
				}
				
				hasMoved = false;
				Util.yieldByteCodes();
			}
			
			/*RobotInfo[] teamRobots = controller.senseNearbyRobots(-1, controller.getTeam());
			if (teamRobots.length > 0) {
				
			}*/
			
			else {
				if (!controller.canMove(targetDirection)) {
					for (int tries = 10; tries > 0; --tries) {
						if (controller.canMove(targetDirection)) break;
						else targetDirection = randomDirectionTowardsEnemy();
					}
					
					hasMoved = false;
				}
				
				else {
					hasMoved = true;
					controller.move(targetDirection);
				}
				
				Util.yieldByteCodes();
			}
			
			if (!hasMoved && randomRatio < 3f) {
				randomRatio += 0.1f;
			}
			
			if (hasMoved && randomRatio > 0.7f) {
				randomRatio -= 0.2f;
				if (randomRatio < 0.7f) randomRatio = 0.7f;
			}
			
			System.out.println(controller.getID() + " random: " + randomRatio);
		}
		
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
	
	private static Direction randomDirectionTowardsEnemy() {
		Direction random = new Direction((float) (Math.random()*2*Math.PI));
		return new Direction((float) ((enemyTerritory.radians + random.radians*randomRatio)/(1+randomRatio)));
	}
	
	private static Direction getEnemyTerritoryVector() {
		MapLocation[] enemyArchonLocations = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
		Direction a = enemyArchonLocations[0].directionTo(controller.getLocation());
		Direction b = enemyArchonLocations[1].directionTo(controller.getLocation());
		return new Direction((a.radians + b.radians)/2);
	}
}
