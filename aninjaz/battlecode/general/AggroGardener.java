package aninjaz.battlecode.general;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
public class AggroGardener {
	public static void run(RobotController controller) throws GameActionException {
		while(true){
			Direction direction = Util.randomDirection();
			RobotType rand = new RobotType[]{RobotType.SOLDIER, RobotType.SCOUT}[(int)(Math.random()*2)];
			if(controller.canBuildRobot(RobotType.SOLDIER, direction)){
				controller.buildRobot(RobotType.SOLDIER, direction);
			}
			Util.yieldByteCodes();
		}
	}
}