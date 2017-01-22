package aninjaz.battlecode.general;

import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.TreeInfo;

public class ScoutRobot {
	public static void run(RobotController controller) throws GameActionException{
		Direction direction = Util.randomDirection();
		while(true){
			move:{
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
				for(TreeInfo tree: nearbyTrees){
					if(tree.getContainedBullets()>0){
						if(controller.canShake(tree.getID())){
							controller.shake(tree.getID());
							continue;
						}else{
							controller.setIndicatorLine(controller.getLocation(), tree.getLocation(), 255, 255, 0);
							if(Pathfinding.goTowardsScout(tree.getLocation())==Pathfinding.HAS_NOT_MOVED){
								direction = Util.tryRandomMove(direction);
							}
							break move;
						}
					}
				}
				direction = Util.tryRandomMove(direction);
			}
			Util.yieldByteCodes();
		}
	}
}
