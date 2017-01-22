package aninjaz.battlecode.aggro;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
public class AggroGardener {
	public static void run(RobotController controller) throws GameActionException {
		Direction move = Util.randomDirection();
		while(true){
			Direction direction = Util.randomDirection();
			if(controller.canBuildRobot(RobotType.SOLDIER, direction)){
				controller.buildRobot(RobotType.SOLDIER, direction);
			}
			move = Util.tryRandomMove(move);
			Util.yieldByteCodes();
		}
	}
}