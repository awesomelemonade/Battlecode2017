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
			RobotType rand = new RobotType[]{RobotType.SOLDIER, RobotType.SCOUT}[(int)(Math.random()*2)];
			if(controller.canBuildRobot(rand, direction)){
				controller.buildRobot(rand, direction);
			}
			move = Util.tryRandomMove(move);
			Util.yieldByteCodes();
		}
	}
}