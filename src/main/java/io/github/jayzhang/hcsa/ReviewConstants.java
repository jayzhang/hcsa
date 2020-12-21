package io.github.jayzhang.hcsa;

import java.util.HashMap;
import java.util.Map;

public class ReviewConstants {
	public static int IN_CONTENT = 0;
	public static int IN_TITLE = 1;
	public static int UNMINED = 0;
	public static int MINED = 1;
	
	public static float INVALID_SCORE = 0;
	
	public static int SEARCH_REVIEW_BUFF_LIMIT = 1000;
	
	public static int SEARCH_SEGMENT_BUFF_LIMIT = 1000;
	
	public static int MINER_REVIEW_BUFF_LIMIT = 1000;
	
	public static Map<Integer, String> RateMap = new HashMap<Integer, String>();
	public static Map<Integer, Float> RateScoreMap = new HashMap<Integer, Float>();
	public static Map<Integer, String> PropertyMap = new HashMap<Integer, String>();
	
	
	static{
		RateMap.put(Rates.BAD.getValue(), "差评");
		RateMap.put(Rates.GOOD.getValue(), "好评");
		RateMap.put(Rates.MEDIUM.getValue(), "中评");
		
		RateScoreMap.put(Rates.BAD.getValue(), new Float(0.0));
		RateScoreMap.put(Rates.GOOD.getValue(), new Float(1.0));
		RateScoreMap.put(Rates.MEDIUM.getValue(), new Float(0.5));
		
		
		PropertyMap.put(HotelProperties.FACILITY.getValue(), "设施");
		PropertyMap.put(HotelProperties.POSITION.getValue(), "位置");
		PropertyMap.put(HotelProperties.SANITATION.getValue(), "环境");
		PropertyMap.put(HotelProperties.SERVICE.getValue(), "服务");
		
	}
	
}
