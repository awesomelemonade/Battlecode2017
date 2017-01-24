package aninjaz.battlecode.general;

import aninjaz.battlecode.aggro.AggroArchon;
import aninjaz.battlecode.aggro.AggroGardener;
import aninjaz.battlecode.aggro.AggroScout;
import aninjaz.battlecode.aggro.AggroSoldier;
import aninjaz.battlecode.experimental.RandomGardener;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.*;

public class RobotPlayer {
	private static RobotController controller;
	public static void run(RobotController controller){
		RobotPlayer.controller = controller;
		Pathfinding.init(controller);
		DynamicBroadcasting.controller = controller;
		Util.controller = controller;
		Constants.OTHER_TEAM = controller.getTeam()==Team.A?Team.B:Team.A;
		while(true){
			System.out.println("Running: "+controller.getType());
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
					AggroScout.run(controller);
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
			System.out.println("Exited Loop? :(");
		}
	}
	public static void runAggroStrat() throws Exception{
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
			AggroScout.run(controller);
			break;
		case TANK:
			TankRobot.run(controller);
			break;
		}
	}
	public static void runNormalStrat() throws Exception{
		switch (controller.getType()) {
		case ARCHON:
			ArchonRobot.run(controller);
			break;
		case GARDENER:
			GardenerRobot.run(controller);
			break;
		case SOLDIER:
			SoldierRobot.run(controller);
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
	}
}
