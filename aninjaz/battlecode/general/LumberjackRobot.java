package aninjaz.battlecode.general;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
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
	private static MapLocation treeLoc;
	private static TreeInfo currentTree;
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
					currentTree = trees[0];
					controller.chop(currentTree.location);
					
					if (currentTree.getHealth() < GameConstants.LUMBERJACK_CHOP_DAMAGE) {
						state = LJState.NONE;
						currentTree = null;
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
							direction = controller.getLocation().directionTo(treeLoc);
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
		treeLoc = loc;
		direction = controller.getLocation().directionTo(treeLoc);
		state = LJState.MOVING;
	}
}
