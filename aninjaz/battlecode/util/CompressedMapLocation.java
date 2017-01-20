package aninjaz.battlecode.util;

import battlecode.common.MapLocation;

public class CompressedMapLocation {
	private static final int MAP_X_LOCATION = 16;
	private static final int BYTE_LIMIT = 65535;
	private final MapLocation location;
	private final int compressedData;
	public CompressedMapLocation(MapLocation location){
		this.location = location;
		int locationX = (Math.round(location.x*100))<<MAP_X_LOCATION;
		int locationY = (Math.round(location.y*100));
		this.compressedData = locationX | locationY;
	}
	public CompressedMapLocation(int compressedData){
		this.compressedData = compressedData;
		this.location = new MapLocation(
				((float)((compressedData>>>MAP_X_LOCATION)&BYTE_LIMIT))/100f,
				((float)(compressedData&BYTE_LIMIT))/100f
		);
	}
	public MapLocation getLocation(){
		return location;
	}
	public int getCompressedData(){
		return compressedData;
	}
	@Override
	public String toString(){
		return String.format("CompressedMapLocation[(%f, %f) -> %d]", location.x, location.y, compressedData);
	}
}
