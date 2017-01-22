package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class AggroArchon {
	private static RobotController controller;
	
	public static void run(RobotController controller) throws GameActionException{
		AggroArchon.controller = controller;
		Direction direction = Util.randomDirection();
		hireGardener();
		while(true){
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			if(nearbyRobots.length>0){
				Direction opposite = controller.getLocation().directionTo(nearbyRobots[0].getLocation()).opposite();
				if(controller.canMove(opposite)){
					controller.move(opposite);
				}
			}else{
				direction = Util.tryRandomMove(direction);
			}
			Util.yieldByteCodes();
		}
	}	
	
	public static void hireGardener() throws GameActionException{
		Direction direction = Util.randomDirection();
		int tries = 10;
		while((!controller.canHireGardener(direction))&&tries>0){
			direction = Util.randomDirection();
			tries--;
		}
		if(tries>0){
			controller.hireGardener(direction);
		}
	}
}
