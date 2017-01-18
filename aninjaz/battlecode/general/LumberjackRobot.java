package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class LumberjackRobot {
	private static Direction direction;
	private static int currentTree;

	public static void run(RobotController controller) throws GameActionException {
		direction = Util.randomDirection();
		while (true) {
			TreeInfo[] trees = controller.senseNearbyTrees(1.5f, Team.NEUTRAL);
			if (trees.length > 0) {
				TreeInfo bestTree = findBestTree(trees);
				if(controller.canChop(bestTree.getID())){
					controller.chop(bestTree.getID());
				}
			}else {
				direction = Util.tryRandomMove(direction);
				RobotInfo[] enemyRobots = controller.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, Constants.OTHER_TEAM);
				if (enemyRobots.length > 0) {
					if(controller.canStrike()){
						controller.strike();
					}
				}
			}
			Util.yieldByteCodes();
		}
	}
	public static TreeInfo findBestTree(TreeInfo[] trees){
		if(trees.length==0){
			return null;
		}
		for(TreeInfo tree: trees){
			if(tree.getID()==currentTree){
				return tree;
			}
		}
		currentTree = trees[0].getID();
		return trees[0];
	}
}
