package io.github.jayzhang.hcsa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CategoryCountMap {

	ObjectMapper objectMapper = new ObjectMapper();
	Map<String, Integer> countMap = new HashMap<String, Integer>();

	public int get(int key)
	{
		Integer value = countMap.get(String.valueOf(key));
		if(value != null)
			return value;
		return 0;
	}
	
	public Set<Entry<String, Integer> > entrySet()
	{
		return countMap.entrySet();
	}
	
	
	public void increase(Integer key, int value)
	{
		String key2 = String.valueOf(key);
		Integer oldValue = countMap.get(key2);
		if(oldValue != null)
			countMap.put(key2, oldValue + value);
		else 
			countMap.put(key2,  value);
	}

	public void add(CategoryCountMap other)
	{
		for(Entry<String, Integer> entry : other.entrySet())
			increase(Integer.valueOf(entry.getKey()), entry.getValue());
	}
	
	public String encode()
	{
		String json;
		try {
			json = objectMapper.writeValueAsString(countMap);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void decode(String text)
	{
		try {
			countMap = objectMapper.readValue(text, new TypeReference<Map<String,Integer>>(){});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
}
