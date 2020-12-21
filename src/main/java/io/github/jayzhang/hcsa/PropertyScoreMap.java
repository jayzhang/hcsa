package io.github.jayzhang.hcsa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertyScoreMap {
	
	ObjectMapper objectMapper = new ObjectMapper();
	Map<String, Double> scoreMap = new HashMap<String, Double>();
	
	public Double get(int key)
	{
		Double value = scoreMap.get(String.valueOf(key));
		if(value != null)
			return value;
		return 0.0;
	}
	
	public Set<Entry<String, Double> > entrySet()
	{
		return scoreMap.entrySet();
	}

	public void put(Integer prop, Double score)
	{
		scoreMap.put(String.valueOf(prop), score);
	}
	
	public String encode()
	{
		String json;
		try {
			json = objectMapper.writeValueAsString(scoreMap);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void decode(String text)
	{
		try {
			scoreMap = objectMapper.readValue(text, new TypeReference<Map<String,Double>>(){});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
