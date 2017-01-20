package aninjaz.battlecode.util;

public class CompressedData {
	private static final int IDENTIFIER_BYTES = 255; //2^8-1
	private static final int DATA_LOCATION = 8;
	private static final int DATA_BYTES = 16777215; //2^24-1
	private int identifier;
	private int data;
	private int compressedData;
	public CompressedData(int identifier, int data, int compressedData){
		this.identifier = identifier;
		this.data = data;
		this.compressedData = compressedData;
	}
	public CompressedData(int identifier, int compressedData){
		this.identifier = identifier;
		this.compressedData = compressedData;
		
	}
	public int getIdentifier(){
		return identifier;
	}
	public int getData(){
		return data;
	}
	public int getCompressedData(){
		return compressedData;
	}
	public static int getIdentifier(int compressedData){
		return compressedData & IDENTIFIER_BYTES;
	}
	public static int compressData(int identifier, int data){
		return (data>>>DATA_LOCATION) | identifier;
	}
	public static CompressedData uncompressData(int compressedData){
		return new CompressedData((compressedData & IDENTIFIER_BYTES),
				((compressedData>>>DATA_LOCATION) & DATA_BYTES), compressedData);
	}
	public static CompressedData uncompressData(int identifier, int compressedData){
		return new CompressedData(identifier, ((compressedData>>>DATA_LOCATION) & DATA_BYTES), compressedData);
	}
}
