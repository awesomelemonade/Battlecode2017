package aninjaz.battlecode.aggro;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class AggroArchon {
	private static RobotController controller;
	
	public static void run(RobotController controller) throws GameActionException{
		AggroArchon.controller = controller;
		Direction direction = Util.randomDirection();
		hireGardener();
		while(true){
			if(controller.senseNearbyRobots(-1,Constants.OTHER_TEAM).length>0){
				controller.move(controller.getLocation().directionTo(controller.senseNearbyRobots(-1,Constants.OTHER_TEAM)[0].getLocation()).opposite());
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
