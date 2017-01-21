package aninjaz.battlecode.util;

import java.util.function.IntConsumer;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;

public class DynamicBroadcasting {
	public static RobotController controller;
	public static final int MAPPERS = 4;
	public static final int DATA_STRIDE = 2;
	private static final int START_MAPPER_CONSTANT = GameConstants.BROADCAST_MAX_CHANNELS-1;
	private static final int START_DATA_CONSTANT = START_MAPPER_CONSTANT-MAPPERS;
	private static final int INTERNAL_STRIDE = DATA_STRIDE*Integer.SIZE;
	/**
	 * 
	 * @param mapper Between [0, DynamicBroadcasting.MAPPERS-1]
	 * @return channel to be used in controller.broadcast()
	 */
	public static int getMapperChannel(int mapperNumber){
		return START_MAPPER_CONSTANT-mapperNumber;
	}
	/**
	 * 
	 * @param mapper Between [0, DynamicBroadcasting.MAPPERS-1]
	 * @param bit # in mapper
	 * @return channel to be used in controller.broadcast(); Ranges from [channel-DATA_STRIDE+1, channel]
	 */
	public static int getDataChannel(int mapperNumber, int bit){
		return START_DATA_CONSTANT-mapperNumber*INTERNAL_STRIDE-bit*DATA_STRIDE;
	}
	public static int markNextAvailableMapper() throws GameActionException{
		for(int mapperNumber=0;mapperNumber<MAPPERS;++mapperNumber){
			int mapperChannel = getMapperChannel(mapperNumber);
			int mapper = controller.readBroadcast(mapperChannel);
			if(mapper!=-1){ //If not all the bits are filled
				int currentBit = 0;
				while(((mapper>>>currentBit)&1)==1){ //while current bit is filled
					currentBit++; //move to next bit
				}
				if(currentBit<32){ //not out of the integer
					controller.broadcast(mapperChannel, mapper|(1<<currentBit));
					return getDataChannel(mapperNumber, currentBit);
				}
			}
		}
		return -1;
	}
	/**
	 * Don't forget to overwrite the necessary channels to zeroes
	 * @param channel
	 */
	public static void unmarkMapper(int dataChannel) throws GameActionException{
		int n = START_DATA_CONSTANT-dataChannel;
		int mapperNumber = n/INTERNAL_STRIDE;
		int mapperBit = n/DATA_STRIDE%Integer.SIZE;
		int mapperChannel = getMapperChannel(mapperNumber);
		int mapper = controller.readBroadcast(mapperChannel);
		controller.broadcast(mapperChannel, mapper&(~(1<<mapperBit)));
	}
	//Utility Functions
	public static int find(int identifierFind, int dataFind) throws GameActionException{
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			for(int bit=0;bit<Integer.SIZE;++bit){
				int compressedDataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
				int compressedData = controller.readBroadcast(compressedDataChannel);
				int identifier = CompressedData.getIdentifier(compressedData);
				if(identifier==identifierFind){
					int data = CompressedData.getData(compressedData);
					if(data==dataFind){
						return compressedDataChannel;
					}
				}
			}
		}
		return -1;
	}
	public static void findAll(int identifierFind, IntConsumer consumer) throws GameActionException{
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			for(int bit=0;bit<Integer.SIZE;++bit){
				int compressedDataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
				int compressedData = controller.readBroadcast(compressedDataChannel);
				int identifier = CompressedData.getIdentifier(compressedData);
				if(identifier==identifierFind){
					consumer.accept(compressedDataChannel);
				}
			}
		}
	}
	public static void findAll(int identifierFind, int dataFind, IntConsumer consumer) throws GameActionException{
		for(int mapper=0;mapper<DynamicBroadcasting.MAPPERS;++mapper){
			for(int bit=0;bit<Integer.SIZE;++bit){
				int compressedDataChannel = DynamicBroadcasting.getDataChannel(mapper, bit);
				int compressedData = controller.readBroadcast(compressedDataChannel);
				int identifier = CompressedData.getIdentifier(compressedData);
				if(identifier==identifierFind){
					int data = CompressedData.getData(compressedData);
					if(data==dataFind){
						consumer.accept(compressedDataChannel);
					}
				}
			}
		}
	}
}
