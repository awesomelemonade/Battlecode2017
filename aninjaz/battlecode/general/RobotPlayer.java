package aninjaz.battlecode.general;

import aninjaz.battlecode.aggro.AggroArchon;
import aninjaz.battlecode.aggro.AggroGardener;
import aninjaz.battlecode.aggro.AggroSoldier;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.*;

public class RobotPlayer {
	public static void run(RobotController controller){
		Pathfinding.controller = controller;
		DynamicBroadcasting.controller = controller;
		Util.controller = controller;
		Constants.OTHER_TEAM = controller.getTeam()==Team.A?Team.B:Team.A;
		
		while(true){
			try{
				switch (controller.getType()) {
				case ARCHON:
					AggroArchon.run(controller);
					break;
				case GARDENER:
					AggroGardener.run(controller);
					break;
				case SOLDIER:
					AggroSoldier.run(controller);
					break;
				case LUMBERJACK:
					LumberjackRobot.run(controller);
					break;
				case SCOUT:
					ScoutRobot.run(controller);
					break;
				case TANK:
					TankRobot.run(controller);
					break;
				}
			}catch(GameActionException ex){
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}catch(Exception ex){
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
}
