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
import battlecode.common.TreeInfo;

public class MidrangeSoldier {
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		MidrangeSoldier.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			DynamicTargeting.removeTargets();
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				DynamicTargeting.addRobotTarget(nearbyRobots[0]);
			}
			DynamicTargeting.getTargetRobot();
			if(DynamicTargeting.targetLocation==null||controller.getRoundNum()<=100){
				direction = Util.tryRandomMove(direction);
			}else{
				if(DynamicTargeting.targetLumberjack){
					float distance = controller.getLocation().distanceTo(DynamicTargeting.targetLocation);
					if(distance>6f){
						MapLocation location = Pathfinding.pathfind(DynamicTargeting.targetLocation, DynamicTargeting.targetRadius);
						if(controller.canMove(location)){
							controller.move(location);
						}
					}else if(distance<5f){
						Direction temp = controller.getLocation().directionTo(DynamicTargeting.targetLocation).opposite();
						if(controller.canMove(temp)){
							controller.move(temp);
						}
					}
				}else{
					MapLocation location = Pathfinding.pathfind(DynamicTargeting.targetLocation, DynamicTargeting.targetRadius);
					if(controller.canMove(location)){
						controller.move(location);
					}
				}
				shoot(DynamicTargeting.targetLocation);
			}
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(2f);
			for(TreeInfo tree: nearbyTrees){
				if(tree.getContainedBullets()>0){
					if(controller.canShake(tree.getID())){
						controller.shake(tree.getID());
						break;
					}
				}
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
		if(controller.canFirePentadShot()&&distance<4f){
			controller.firePentadShot(direction);
		}else if(controller.canFireSingleShot()&&distance<10f){
			controller.fireSingleShot(direction);
		}
	}
}
