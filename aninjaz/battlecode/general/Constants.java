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
	
	public static final int RANDOM_TRIES = 10;
	
	public static final int CHANNEL_AVAILABLE_GARDENER_ORIGINS = 0;
}
