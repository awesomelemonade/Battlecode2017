package aninjaz.battlecode.util;

import battlecode.common.MapLocation;

public class CompressedData {
	private static final int MAP_X_LOCATION = 16;
	private static final int MAP_LOCATION_SIZE = 65535;
	
	private static final int IDENTIFIER_BITS = 255; //2^8-1
	private static final int SUBIDENTIFIER_LOCATION = 8;
	private static final int DATA_LOCATION = 16;
	private static final int DATA_BITS = 65535; //2^16-1
	public static int getIdentifier(int compressedData){
		return compressedData & IDENTIFIER_BITS;
	}
	public static int getSubIdentifier(int compressedData){
		return (compressedData>>>SUBIDENTIFIER_LOCATION) & IDENTIFIER_BITS;
	}
	public static int getData(int compressedData){
		return (compressedData>>>DATA_LOCATION) & DATA_BITS;
	}
	public static int compressData(int identifier, int subIdentifier, int data){
		return (data<<DATA_LOCATION) | (subIdentifier<<SUBIDENTIFIER_LOCATION) | identifier;
	}
	public static int getFloatDataA(int compressedData){
		return compressedData&DATA_BITS;
	}
	public static float getFloatDataB(int compressedData){
		return ((float)((compressedData>>>DATA_LOCATION)&DATA_BITS))/100f;
	}
	public static int compressFloatData(int a, float b){
		return (Math.round(b*100)<<DATA_LOCATION) | a;
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
