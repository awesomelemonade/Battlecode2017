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
	private static final int MIDRANGE_STRAT = 2;
	
	private static RobotController controller;
	
	private static MapLocation[] ourArchons;
	private static MapLocation[] theirArchons;
	
	public static void run(RobotController controller) throws GameActionException{
		RobotPlayer.controller = controller;
		Pathfinding.init(controller);
		DynamicBroadcasting.controller = controller;
		DynamicTargeting.controller = controller;
		Util.controller = controller;
		Constants.OTHER_TEAM = controller.getTeam()==Team.A?Team.B:Team.A;
		int currentStrat = controller.readBroadcast(Constants.CHANNEL_CURRENT_STRAT);
		if(currentStrat==NO_STRAT){
			ourArchons = controller.getInitialArchonLocations(controller.getTeam());
			theirArchons = controller.getInitialArchonLocations(Constants.OTHER_TEAM);
			currentStrat = findBestStrat();
			if(currentStrat==AGGRO_STRAT){
				indicate(255, 0, 0);
			}else if(currentStrat==MIDRANGE_STRAT){
				indicate(0, 255, 0);
				float minDistance = getMinArchonDistance();
				TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, Team.NEUTRAL);
				if(hasTreesWithin(nearbyTrees, 5f)||hasRobotTreeWithin(nearbyTrees)){
					controller.broadcast(Constants.CHANNEL_SPAWN_INITIAL_LUMBERJACK, 1);
					if(minDistance<40f){
						controller.broadcast(Constants.CHANNEL_SPAWN_INITIAL_SOLDIER, 1);
					}else if(exceedsBullets(nearbyTrees, 20, 5)){
						controller.broadcast(Constants.CHANNEL_SPAWN_INITIAL_SCOUT, 1);
					}
				}else{
					controller.broadcast(Constants.CHANNEL_SPAWN_INITIAL_SOLDIER, 1);
					if(minDistance<40f){
						controller.broadcast(Constants.CHANNEL_SPAWN_INITIAL_SOLDIER, 2);
					}else if(exceedsBullets(nearbyTrees, 20, 5)){
						controller.broadcast(Constants.CHANNEL_SPAWN_INITIAL_SCOUT, 1);
					}
				}
			}else{
				indicate(0, 0, 0);
			}
			controller.broadcast(Constants.CHANNEL_CURRENT_STRAT, currentStrat);
		}
		while(true){
			try{
				switch(currentStrat){
				case AGGRO_STRAT:
					runAggroStrat();
					break;
				default:
					runMidrangeStrat();
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
	public static boolean hasRobotTreeWithin(TreeInfo[] nearbyTrees){
		for(TreeInfo tree: nearbyTrees){
			if(tree.getContainedRobot()!=null){
				return true;
			}
		}
		return false;
	}
	public static boolean exceedsBullets(TreeInfo[] nearbyTrees, float bullets, int trees){
		for(TreeInfo tree: nearbyTrees){
			bullets-=tree.getContainedBullets();
			if(tree.getContainedBullets()>0){
				trees--;
			}
			if(bullets<=0||trees<=0){
				return true;
			}
		}
		return false;
	}
	public static boolean hasTreesWithin(TreeInfo[] nearbyTrees, float distance){
		for(TreeInfo tree: nearbyTrees){
			if(tree.getLocation().distanceTo(controller.getLocation())-tree.getRadius()<=distance){
				return true;
			}
		}
		return false;
	}
	public static float getMinArchonDistance(){
		float minDistance = Float.MAX_VALUE;
		for(MapLocation ourArchon: ourArchons){
			for(MapLocation theirArchon: theirArchons){
				float distance = ourArchon.distanceTo(theirArchon);
				if(distance<minDistance){
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}
	public static void indicate(int red, int green, int blue) throws GameActionException{
		for(int i=-10;i<=10;++i){
			for(int j=-10;j<=10;++j){
				controller.setIndicatorDot(controller.getLocation().translate(i, j), red, green, blue);
			}
		}
	}
	public static int findBestStrat(){
		if(ourArchons.length==1){
			if(ourArchons[0].distanceTo(theirArchons[0])<=10){
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
}
