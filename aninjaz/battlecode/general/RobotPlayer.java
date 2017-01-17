package aninjaz.battlecode.general;

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
				/*case TANK:
					TankRobot.run(controller);
					break;*/
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
		if(n>0){
			controller.broadcast(Constants.BROADCAST_REQUEST_GARDENER_COMMANDERS, n-1);
			GardenerCommander.run(controller);
		}else{
			GardenerRobot.run(controller);
		}
	}
}
