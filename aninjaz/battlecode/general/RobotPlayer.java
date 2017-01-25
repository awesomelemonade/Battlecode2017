package aninjaz.battlecode.general;

import aninjaz.battlecode.aggro.AggroArchon;
import aninjaz.battlecode.aggro.AggroGardener;
import aninjaz.battlecode.aggro.AggroScout;
import aninjaz.battlecode.aggro.AggroSoldier;
import aninjaz.battlecode.midrange.HasslerScout;
import aninjaz.battlecode.midrange.MidrangeArchon;
import aninjaz.battlecode.midrange.MidrangeGardener;
import aninjaz.battlecode.midrange.TreeRangeGardener;
import aninjaz.battlecode.midrange.MidrangeLumberjack;
import aninjaz.battlecode.midrange.MidrangeTank;
import aninjaz.battlecode.midrange.TankGardener;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.*;

public class RobotPlayer {
	private static final int NO_STRAT = 0;
	private static final int AGGRO_STRAT = 1;
	private static final int MIDRANGE_STRAT = 2;
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		RobotPlayer.controller = controller;
		Pathfinding.init(controller);
		DynamicBroadcasting.controller = controller;
		Util.controller = controller;
		Constants.OTHER_TEAM = controller.getTeam()==Team.A?Team.B:Team.A;
		int currentStrat = controller.readBroadcast(Constants.CHANNEL_CURRENT_STRAT);
		if(currentStrat==NO_STRAT){
			currentStrat = findBestStrat();
			switch(currentStrat){
			case AGGRO_STRAT:
				indicate(255, 0, 0);
				break;
			case MIDRANGE_STRAT:
				indicate(0, 255, 255);
				break;
			}
			controller.broadcast(Constants.CHANNEL_CURRENT_STRAT, currentStrat);
		}
		while(true){
			try{
				switch(currentStrat){
				case AGGRO_STRAT:
					runAggroStrat();
					break;
				case MIDRANGE_STRAT:
					runMidrangeStrat();
					break;
				default:
					runDefaultStrat();
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
	public static void indicate(int red, int green, int blue) throws GameActionException{
		for(int i=-10;i<=10;++i){
			for(int j=-10;j<=10;++j){
				controller.setIndicatorDot(controller.getLocation().translate(i, j), red, green, blue);
			}
		}
	}
	public static int findBestStrat(){
		MapLocation[] ourArchons = controller.getInitialArchonLocations(controller.getTeam());
		MapLocation[] theirArchons = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
		if(ourArchons.length==1){
			if(ourArchons[0].distanceTo(theirArchons[0])<=36){
				//Check trees in between
				return AGGRO_STRAT;
			}
		}
		return MIDRANGE_STRAT;
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
	public static void runMidrangeStrat() throws Exception{
		switch (controller.getType()) {
		case ARCHON:
			MidrangeArchon.run(controller);
			break;
		case GARDENER:
			spawnGardener();
			break;
		case SOLDIER:
			AggroSoldier.run(controller);
			break;
		case LUMBERJACK:
			MidrangeLumberjack.run(controller);
			break;
		case SCOUT:
			HasslerScout.run(controller);
			break;
		case TANK:
			MidrangeTank.run(controller);
			break;
		}
	}
	public static void spawnGardener() throws GameActionException{
		int tankGardenerRequests = controller.readBroadcast(Constants.CHANNEL_SPAWN_TANK_GARDENER);
		if(tankGardenerRequests>0){
			controller.broadcast(Constants.CHANNEL_SPAWN_TANK_GARDENER, tankGardenerRequests-1);
			TankGardener.run(controller);
			return;
		}
		int treeRange = controller.readBroadcast(Constants.CHANNEL_SPAWN_TREERANGE_GARDENER);
		if(treeRange>0){
			controller.broadcast(Constants.CHANNEL_SPAWN_TREERANGE_GARDENER, treeRange-1);
			TreeRangeGardener.run(controller);
			return;
		}
		MidrangeGardener.run(controller);
		return;
	}
	public static void runDefaultStrat() throws Exception{
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
