package aninjaz.battlecode.general;

import aninjaz.battlecode.aggro.AggroArchon;
import aninjaz.battlecode.aggro.AggroGardener;
import aninjaz.battlecode.aggro.AggroScout;
import aninjaz.battlecode.aggro.AggroSoldier;
import aninjaz.battlecode.experimental.FlowerGardener;
import aninjaz.battlecode.midrange.CollectorScout;
import aninjaz.battlecode.midrange.MidrangeArchon;
import aninjaz.battlecode.midrange.MidrangeLumberjack;
import aninjaz.battlecode.midrange.MidrangeSoldier;
import aninjaz.battlecode.midrange.MidrangeTank;
import aninjaz.battlecode.util.DynamicBroadcasting;
import aninjaz.battlecode.util.DynamicTargeting;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.*;

public class RobotPlayer {
	private static final int NO_STRAT = 0;
	private static final int AGGRO_STRAT = 1;
	private static final int CRAMPED = 2;
	private static final int TURTLE_STRAT = 3;
	private static final int SOLDIER_RANGE = 4;
	
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		RobotPlayer.controller = controller;
		Pathfinding.init(controller);
		DynamicBroadcasting.controller = controller;
		DynamicTargeting.controller = controller;
		Util.controller = controller;
		Constants.OTHER_TEAM = controller.getTeam()==Team.A?Team.B:Team.A;
		int currentStrat = controller.readBroadcast(Constants.CHANNEL_CURRENT_STRAT);
		if(currentStrat==NO_STRAT){
			currentStrat = findBestStrat();
			switch(currentStrat){
			case AGGRO_STRAT:
				indicate(255, 0, 0);
				break;
			case CRAMPED:
				indicate(0, 255, 0);
				break;
			case TURTLE_STRAT:
				indicate(0, 0, 255);
				break;
			case SOLDIER_RANGE:
				indicate(255,255,255);
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
				case CRAMPED:
					runMidrangeStrat();
					break;
				case TURTLE_STRAT:
					runTurtleStrat();
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
			if(ourArchons[0].distanceTo(theirArchons[0])<=10){
				//Check trees in between
				return AGGRO_STRAT;
			}
		}
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-5, Team.NEUTRAL);
		if(nearbyTrees.length>=15){
			return CRAMPED;
		}
		int sum = 0;
		for(TreeInfo tree: nearbyTrees){
			sum+=tree.radius;
		}
		if(sum>=20){
			return CRAMPED;
		}
		for(TreeInfo tree : nearbyTrees){
			if(tree.getContainedRobot()==RobotType.TANK){
				return CRAMPED;
			}
		}
		for(MapLocation archon : ourArchons){
			for(MapLocation theirarchon : theirArchons){
				if(archon.distanceTo(theirarchon)<71){
					return SOLDIER_RANGE;
				}
			}
		}
		return TURTLE_STRAT;
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
			MidrangeLumberjack.run(controller);
			break;
		case SCOUT:
			AggroScout.run(controller);
			break;
		case TANK:
			MidrangeTank.run(controller);
			break;
		}
	}
	public static void runMidrangeStrat() throws Exception{
		switch (controller.getType()) {
		case ARCHON:
			MidrangeArchon.run(controller);
			break;
		case GARDENER:
			FlowerGardener.run(controller);
			break;
		case SOLDIER:
			MidrangeSoldier.run(controller);
			break;
		case LUMBERJACK:
			MidrangeLumberjack.run(controller);
			break;
		case SCOUT:
			CollectorScout.run(controller);
			break;
		case TANK:
			MidrangeTank.run(controller);
			break;
		}
	}
	public static void runTurtleStrat() throws Exception{
		switch (controller.getType()) {
		case ARCHON:
			MidrangeArchon.run(controller);
			break;
		case GARDENER:
			FlowerGardener.run(controller);
			break;
		case SOLDIER:
			MidrangeSoldier.run(controller);
			break;
		case LUMBERJACK:
			MidrangeLumberjack.run(controller);
			break;
		case SCOUT:
			CollectorScout.run(controller);
			break;
		case TANK:
			MidrangeTank.run(controller);
			break;
		}
	}
	public static void runDefaultStrat() throws Exception{
		switch (controller.getType()) {
		case ARCHON:
			MidrangeArchon.run(controller);
			break;
		case GARDENER:
			FlowerGardener.run(controller);
			break;
		case SOLDIER:
			AggroSoldier.run(controller);
			break;
		case LUMBERJACK:
			MidrangeLumberjack.run(controller);
			break;
		case SCOUT:
			CollectorScout.run(controller);
			break;
		case TANK:
			MidrangeTank.run(controller);
			break;
		}
	}
}
