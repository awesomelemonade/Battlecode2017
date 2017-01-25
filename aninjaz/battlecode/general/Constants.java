package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.Team;

public class Constants {
	public static Team OTHER_TEAM;
	
	public static final float TWO_PI = (float) (Math.PI*2);
	
	public static final float ROOT_2 = 1.41421356237f;
	public static final float EPSILON = 0.00001f;
	
	public static final Direction NORTH_EAST = new Direction(ROOT_2/2f);
	public static final Direction NORTH_WEST = new Direction(3f*ROOT_2/2f);
	public static final Direction SOUTH_WEST = new Direction(5f*ROOT_2/2f);
	public static final Direction SOUTH_EAST = new Direction(7f*ROOT_2/2f);
	
	public static final Direction[] CARDINAL_DIRECTIONS = new Direction[]{
			Direction.getNorth(), Direction.getSouth(), Direction.getWest(), Direction.getEast()
	};
	
	public static final int RANDOM_TRIES = 10;
	
	public static final int CHANNEL_CURRENT_STRAT = 0;
	public static final int CHANNEL_SPAWNED_INITIAL_GARDENER = 1;
	public static final int CHANNEL_SPAWNED_INITIAL_SCOUT = 2;
	public static final int CHANNEL_SPAWNED_INITIAL_LUMBERJACK = 3;
	public static final int CHANNEL_LUMBERJACK_TARGET_INFO = 4;
	public static final int CHANNEL_LUMBERJACK_TARGET_LOCATION = 5;
	public static final int CHANNEL_SPAWN_TANK_GARDENER = 6;
	public static final int CHANNEL_AVAILABLE_GARDENER_ORIGINS = 999;
	public static final int CHANNEL_SPAWN_GARDENER_COMMANDER = 998;
}
