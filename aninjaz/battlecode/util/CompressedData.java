package aninjaz.battlecode.util;

import battlecode.common.MapLocation;

public class CompressedData {
	private static final int MAP_X_LOCATION = 16;
	private static final int MAP_LOCATION_SIZE = 65535;
	
	private static final int IDENTIFIER_BYTES = 255; //2^8-1
	private static final int DATA_LOCATION = 8;
	private static final int DATA_SIZE = 16777215; //2^24-1
	public static int getIdentifier(int compressedData){
		return compressedData & IDENTIFIER_BYTES;
	}
	public static int getData(int compressedData){
		return (compressedData>>>DATA_LOCATION) & DATA_SIZE;
	}
	public static int compressData(int identifier, int data){
		return (data>>>DATA_LOCATION) | identifier;
	}
	public static MapLocation uncompressMapLocation(int compressedData){
		return new MapLocation(
				((float)((compressedData>>>MAP_X_LOCATION)&MAP_LOCATION_SIZE))/100f,
				((float)(compressedData&MAP_LOCATION_SIZE))/100f
		);
	}
	public static int compressMapLocation(MapLocation location){
		int locationX = (Math.round(location.x*100))<<MAP_X_LOCATION;
		int locationY = (Math.round(location.y*100));
		return locationX | locationY;
	}
}
