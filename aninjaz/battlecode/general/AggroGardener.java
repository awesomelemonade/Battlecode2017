package aninjaz.battlecode.general;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
public class AggroGardener {
	public static void run(RobotController controller) throws GameActionException {
		while(true){
			controller.buildRobot(RobotType.SOLDIER, Util.randomDirection());
			Util.yieldByteCodes();
		}
	}
}