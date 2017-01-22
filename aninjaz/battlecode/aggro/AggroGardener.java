package aninjaz.battlecode.aggro;
import aninjaz.battlecode.general.Util;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TreeInfo;
public class AggroGardener {
	private static final float CHECK_RADIUS = 3f;
	private static final float WATER_RADIUS = 3f;
	private static RobotController controller;
	private static MapLocation origin;
	private static Direction[] plants;
	private static Direction opening;
	private static Direction randomMove = Util.randomDirection();
	public static void run(RobotController controller) throws GameActionException {
		AggroGardener.controller = controller;
		float offset = (float) (Math.PI*2);
		opening = new Direction(offset);
		plants = new Direction[]{
				new Direction((float) (Math.PI/3+offset)),
				new Direction((float) (Math.PI*2/3+offset)),
				new Direction((float) (Math.PI+offset)),
				new Direction((float) (Math.PI*4/3+offset)),
				new Direction((float) (Math.PI*5/3+offset))
		};
		while(controller.getRoundNum()<600){
			Direction direction = Util.randomDirection();
			if(controller.canBuildRobot(RobotType.SOLDIER, direction)){
				controller.buildRobot(RobotType.SOLDIER, direction);
			}
			randomMove = Util.tryRandomMove(randomMove);
			Util.yieldByteCodes();
		}
		//find valid origin
		findOrigin();
		while(true){
			tryPlant();
			waterTrees();
			createUnits();
			Util.yieldByteCodes();
		}
	}
	public static void createUnits() throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		if(controller.canBuildRobot(RobotType.SCOUT, opening)){
			controller.buildRobot(RobotType.SCOUT, opening);
		}
	}
	public static void tryPlant() throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		for(Direction plant: plants){
			if(controller.canPlantTree(plant)){
				controller.plantTree(plant);
				return;
			}
		}
	}
	public static void findOrigin() throws GameActionException{
		while(origin==null){
			randomMove = Util.tryRandomMove(randomMove);
			RobotInfo[] nearbyRobots = controller.senseNearbyRobots(CHECK_RADIUS, controller.getTeam());
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(CHECK_RADIUS);
			if(nearbyRobots.length==0&&nearbyTrees.length==0){
				origin = controller.getLocation();
			}
			Util.yieldByteCodes();
		}
	}
	public static void waterTrees() throws GameActionException{
		TreeInfo[] nearbyTrees = controller.senseNearbyTrees(WATER_RADIUS, controller.getTeam());
		int bestTreeId = -1;
		float leastHealth = GameConstants.BULLET_TREE_MAX_HEALTH;
		for(TreeInfo tree: nearbyTrees){
			if(tree.getHealth()<leastHealth){
				bestTreeId = tree.getID();
				leastHealth = tree.getHealth();
			}
		}
		if(bestTreeId!=-1){
			controller.water(bestTreeId); //No need for canWater()?
		}
	}
}