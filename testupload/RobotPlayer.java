package testupload;

import aninjaz.battlecode.general.ArchonRobot;
import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.GardenerCommander;
import aninjaz.battlecode.general.GardenerRobot;
import aninjaz.battlecode.general.LumberjackRobot;
import aninjaz.battlecode.general.ScoutRobot;
import aninjaz.battlecode.general.SoldierRobot;
import aninjaz.battlecode.general.Util;
import battlecode.common.*;

public class RobotPlayer {
	public static void run(RobotController controller){
		Util.controller = controller;
		Constants.OTHER_TEAM = controller.getTeam()==Team.A?Team.B:Team.A;
		while(true){
			try{
				switch (controller.getType()) {
				case ARCHON:
					ArchonRobot.run(controller);
					break;
				case GARDENER:
					addGardener(controller);
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
	public static void addGardener(RobotController controller) throws GameActionException{
		int n = controller.readBroadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS);
		controller.broadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS, n-1);
		if(n>0){
			GardenerCommander.run(controller);
		}else{
			GardenerRobot.run(controller);
		}
	}
}
