package aninjaz.battlecode.experimental;

import aninjaz.battlecode.general.Constants;
import aninjaz.battlecode.general.Util;
import aninjaz.battlecode.util.Pathfinding;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TreeInfo;

public class RandomGardener {
	private static final float WATER_RADIUS = 2f;
	private static final float PLANT_RADIUS = 3.01f;
	private static final float WANDERING_RADIUS = 4f;
	private static RobotController controller;
	public static void run(RobotController controller) throws GameActionException{
		RandomGardener.controller = controller;
		Direction direction = Util.randomDirection();
		while(true){
			tryPlantTreesOnBorder();
			TreeInfo[] nearbyTrees = controller.senseNearbyTrees(-1, controller.getTeam());
			TreeInfo bestTree = findTreeWithLeastHealth(nearbyTrees);
			if(bestTree==null){
				direction = Util.tryRandomMove(direction);
			}else{
				if(bestTree.getHealth()/bestTree.getMaxHealth()<0.9f){
					Pathfinding.goTowardsBidirectional(bestTree.getLocation());
					direction = Util.randomDirection();
				}else{
					if(controller.getLocation().distanceTo(bestTree.getLocation())>WANDERING_RADIUS){
						Pathfinding.goTowardsBidirectional(bestTree.getLocation());
						direction = Util.randomDirection();
					}else{
						direction = generalRandomMove(direction);
					}
				}
			}
			waterTrees();
			Util.yieldByteCodes();
		}
	}
	private static Direction generalRandomMove(Direction direction) throws GameActionException {
		for(Direction dir: Constants.CARDINAL_DIRECTIONS){
			if(!controller.onTheMap(controller.getLocation().add(dir, 3f))){
				return dir.opposite();
			}
		}
		return direction;
	}
	public static TreeInfo findTreeWithLeastHealth(TreeInfo[] trees){
		float leastHealth = GameConstants.BULLET_TREE_MAX_HEALTH+1;
		TreeInfo bestTree = null;
		for(TreeInfo tree: trees){
			if(tree.getHealth()<leastHealth){
				bestTree = tree;
				leastHealth = tree.getHealth();
			}
		}
		return bestTree;
	}
	public static void tryPlantTreesOnBorder() throws GameActionException{
		if(!controller.isBuildReady()){
			return;
		}
		if(!controller.onTheMap(controller.getLocation(), PLANT_RADIUS)){
			controller.setIndicatorDot(controller.getLocation(), 0, 255, 0);
			binarySearchBorder();
		}
	}
	public static void binarySearchBorder() throws GameActionException{
		Direction borderDirection = null;
		for(Direction direction: Constants.CARDINAL_DIRECTIONS){
			if(!controller.onTheMap(controller.getLocation().add(direction, PLANT_RADIUS))){
				borderDirection = direction;
				break;
			}
		}
		if(borderDirection==null){
			return;
		}
		float currentLeft = (float)Math.PI;
		float currentRight = currentLeft;
		float binary = currentLeft/2;
		Direction bestLeft = borderDirection.rotateLeftRads(currentLeft);
		Direction bestRight = borderDirection.rotateRightRads(currentRight);
		MapLocation bestLeftLocation = null;
		MapLocation bestRightLocation = null;
		float spawnOffset = GameConstants.GENERAL_SPAWN_OFFSET+GameConstants.BULLET_TREE_RADIUS+1;
		for(int i=0;i<10;++i){
			Direction leftDirection = borderDirection.rotateLeftRads(currentLeft);
			Direction rightDirection = borderDirection.rotateRightRads(currentRight);
			MapLocation leftLocation = controller.getLocation().add(leftDirection, spawnOffset);
			MapLocation rightLocation = controller.getLocation().add(rightDirection, spawnOffset);
			controller.setIndicatorLine(controller.getLocation(), leftLocation, 255, 0, 128);
			controller.setIndicatorLine(controller.getLocation(), rightLocation, 255, 0, 128);
			if(controller.onTheMap(leftLocation, GameConstants.BULLET_TREE_RADIUS)){
				bestLeft = leftDirection;
				bestLeftLocation = leftLocation;
				currentLeft-=binary;
			}else{
				currentLeft+=binary;
			}
			if(controller.onTheMap(rightLocation, GameConstants.BULLET_TREE_RADIUS)){
				bestRight = rightDirection;
				bestRightLocation = rightLocation;
				currentRight-=binary;
			}else{
				currentRight+=binary;
			}
			binary/=2;
		}
		controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(bestLeft), 0, 0, 255);
		controller.setIndicatorLine(controller.getLocation(), controller.getLocation().add(bestRight), 0, 0, 255);
		controller.setIndicatorDot(bestLeftLocation, 255, 128, 0);
		controller.setIndicatorDot(bestRightLocation, 255, 128, 0);
		if(controller.canPlantTree(bestLeft)){
			controller.plantTree(bestLeft);
			return;
		}
		if(controller.canPlantTree(bestRight)){
			controller.plantTree(bestRight);
			return;
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
