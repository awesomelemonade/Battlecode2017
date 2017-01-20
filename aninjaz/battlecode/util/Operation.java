package aninjaz.battlecode.util;

import battlecode.common.MapLocation;

public class Operation {
	public static MapLocation project(MapLocation x, MapLocation y, float t){
		return new MapLocation(x.x+t*(y.x-x.x), x.y+t*(y.y-x.y));
	}
}
