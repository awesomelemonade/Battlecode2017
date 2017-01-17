package aninjaz.battlecode.general;

import battlecode.common.MapLocation;

public class CompressedMapLocation {
	private static final int IDENTIFIER_LOCATION = 28;
	private static final int DATA_LOCATION = 26;
	private static final int MAP_X_LOCATION = 13; //Stores to 1 decimal digit; MAP_Y_LOCATION is not necessary because it's 0
	//IDENTIFIER_BITS+DATA_BITS+2*MAP_LOCATION_BITS = 32 bits in an int
	private final int identifier; //Maximum of 4 bits -> [0, 15]
	private final int data; //Maximum of 2 bits -> [0, 3]
	private final MapLocation location; //26 bits with 13 bits for x and 13 bits for y
	private final int compressedData; //32 bits in an int
	public CompressedMapLocation(int identifier, int data, MapLocation location){
		this.identifier = identifier;
		this.data = data;
		this.location = location;
		int locationX = (Math.round(location.x*10))<<MAP_X_LOCATION;
		int locationY = (Math.round(location.y*10));
		this.compressedData = (identifier<<IDENTIFIER_LOCATION) | (data<<DATA_LOCATION) | locationX | locationY;
	}
	public CompressedMapLocation(int compressedData){
		this.compressedData = compressedData;
		this.identifier = compressedData>>>IDENTIFIER_LOCATION;
		this.data = (compressedData>>DATA_LOCATION)&3;
		this.location = new MapLocation(
				((float)((compressedData>>MAP_X_LOCATION)&8191))/10f,
				((float)(compressedData&8191))/10f
		);
	}
	public int getIdentifier(){
		return identifier;
	}
	public int getData(){
		return data;
	}
	public MapLocation getLocation(){
		return location;
	}
	public int getCompressedData(){
		return compressedData;
	}
	@Override
	public String toString(){
		return String.format("[Identifier: %d, Data: %d, MapLocation: (%f, %f), Compressed: %d]",
				identifier, data, location.x, location.y, compressedData);
	}
}
