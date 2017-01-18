package aninjaz.battlecode.general;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class LumberjackRobot {
	private enum LJState {
		NONE,
		MOVING,
		CUTTING,
		FIGHTING
	}
	
	private static RobotController controller;
	private static Direction direction;
	private static MapLocation tree;
	private static LJState state;
	
	public static void run(RobotController controller) throws GameActionException{
		LumberjackRobot.controller = controller;
		direction = Util.randomDirection();
		state = LJState.NONE;
		
		while (true) {
			/*RobotInfo[] enemyRobots = controller.senseNearbyRobots(-1, Constants.OTHER_TEAM);
			
			if (enemyRobots.length < 3 && enemyRobots.length > 0) {
				
			}
			
			else if (enemyRobots.length > 0) {
				
			}
			
			else {*/
			
				TreeInfo[] trees = controller.senseNearbyTrees(1.5f, Team.NEUTRAL);
				if (trees.length > 0) {
					controller.chop(trees[0].location);
					if (trees.length == 1) {
						state = LJState.NONE;
						Util.tryRandomMove(direction);
						Util.yieldByteCodes();
					}
					else {
						state = LJState.CUTTING;
						Util.yieldByteCodes();
					}
				}
				
				else {
					if (state == LJState.CUTTING) {
						state = LJState.NONE;
						Util.tryRandomMove(direction);
					}
					if (state == LJState.NONE) {
						Util.tryRandomMove(direction);
					}
					
					else if (state == LJState.MOVING) {
						if (!controller.canMove(direction)) {
							Util.tryRandomMove(direction);
							direction = controller.getLocation().directionTo(tree);
						}
					}
					
					RobotInfo[] enemyRobots = controller.senseNearbyRobots(2, Constants.OTHER_TEAM);
					
					if (enemyRobots.length > 0) {
						controller.strike();
					}
					
					Util.yieldByteCodes();
				}
			//}
		}
	}
	
	public static void targetTree(MapLocation loc) {
		tree = loc;
		direction = controller.getLocation().directionTo(tree);
		state = LJState.MOVING;
	}
}
