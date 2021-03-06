package aninjaz.battlecode.general;

import battlecode.common.Direction;
import battlecode.common.Team;

public class Constants {
	public static Team OTHER_TEAM;
	
	public static final float ROOT_2 = 1.41421356237f;
	public static final float EPSILON = 0.00001f;

	public static final Direction NORTH_EAST = new Direction(ROOT_2/2f);
	public static final Direction NORTH_WEST = new Direction(3f*ROOT_2/2f);
	public static final Direction SOUTH_WEST = new Direction(5f*ROOT_2/2f);
	public static final Direction SOUTH_EAST = new Direction(7f*ROOT_2/2f);
	
	public static final float LOW_HEALTH = 0.15f;
	
	public static final int RANDOM_TRIES = 10;
	
	//Broadcast Communication
	public static final int BROADCAST_RESERVED_BULLETS = 0;
	public static final int BROADCAST_GARDENER_COUNT = 1;
	public static final int BROADCAST_REQUEST_GARDENER_COMMANDERS = 2; //Tells RobotPlayer to set Gardener Type to GardenerCommander
	public static final int BROADCAST_GARDENER_COMMANDER_COUNT = 3; //Tells Archons to build a commander robot
	public static final int BROADCAST_SCOUT_COUNT = 4;
	public static final int BROADCAST_SOLDIER_COUNT = 5;
	public static final int BROADCAST_TANK_COUNT = 6;
	public static final int BROADCAST_LJ_COUNT = 7;
	
	public static final int BROADCAST_IDENTIFIER_GARDENER = 0;
	public static final int BROADCAST_IDENTIFIER_SOLDIER = 1;
}
