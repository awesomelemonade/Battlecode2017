package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.DynamicTargeting;
import aninjaz.battlecode.util.Pathfinding;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class MidrangeTank {
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		MidrangeTank.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			DynamicTargeting.removeTargets();
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				DynamicTargeting.addRobotTarget(nearbyRobots[0]);
			}
			
			DynamicTargeting.getTargetNonTree();
			
			if(DynamicTargeting.targetLocation==null){
				direction = Util.tryRandomMove(direction);
			}else{
				MapLocation location = Pathfinding.pathfind(DynamicTargeting.targetLocation, DynamicTargeting.targetRadius);
				if(controller.canMove(location)){
					controller.move(location);
				}
				shoot(DynamicTargeting.targetLocation);
			}
			Util.yieldByteCodes();
		}
	}
	public static void shoot(MapLocation location) throws GameActionException{
		Direction direction = controller.getLocation().directionTo(location);
		if(!Util.isSafeToShoot(direction)){
			return;
		}
		float distance = controller.getLocation().distanceTo(location);
		if(controller.canFirePentadShot()&&distance<6f){
			controller.firePentadShot(direction);
		}else if(controller.canFireSingleShot()&&distance<14f){
			controller.fireSingleShot(direction);
		}
	}
}
