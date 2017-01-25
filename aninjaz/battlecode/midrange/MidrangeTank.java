package aninjaz.battlecode.midrange;

import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.general.Constants;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class MidrangeTank {
	private static MapLocation[] initialArchons;
	private static int index = 0;
	
	public static void run(RobotController controller) throws GameActionException{
		initialArchons = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
		Direction direction = Util.randomDirection();
		while(true){
			if(index<initialArchons.length){
				if(controller.getLocation().distanceTo(initialArchons[index])<2f){
					index++;
				}
			}
			if(index<initialArchons.length){
				if(controller.canMove(initialArchons[index])){
					controller.move(initialArchons[index]);
					direction = Util.randomDirection();
				}else{
					direction = Util.tryRandomMove(direction);
				}
			}else{
				direction = Util.tryRandomMove(direction);
			}
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			for(RobotInfo robot: nearbyRobots){
				Direction dir = controller.getLocation().directionTo(robot.getLocation());
				if(Util.isSafeToShoot(dir)){
					if(controller.canFirePentadShot()){
						controller.firePentadShot(dir);
					}else if(controller.canFireSingleShot()){
						controller.fireSingleShot(dir);
					}
					break;
				}
			}
			Util.yieldByteCodes();
		}
	}
}
