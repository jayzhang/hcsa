package io.github.jayzhang.hcsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Function {
	private String expression;
	private List<String> argNameList = new ArrayList<String>();
	private List<Span> argSpans = new ArrayList<Span>();
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Function[");
		
		sb.append("expression=" + expression);
		sb.append(",argNameList=" + argNameList);
		sb.append(",argSpans=" + argSpans);
		sb.append("]");
		
		return sb.toString();
	}
	
	public void parse(String name, String expression, List<String> argNameList)
	{
		this.expression = expression;
		this.argNameList = argNameList;
		String stag = "<<";
		String etag = ">>";
		int end = 0;
		int start = expression.indexOf(stag);
		
		while(start != -1)
		{
			end = expression.indexOf(etag, start);
			if(end != -1)
			{
				Span span = new Span();
				span.begin = start;
				span.end = end + etag.length();
				span.text = expression.substring(span.begin + stag.length(), end).trim();
				argSpans.add(span);
				start = expression.indexOf(stag, span.end);
			}
			else 
				break;
		}
	}
	
	public String unfold(List<String> argValues)
	{
		Map<String, String> kvMap = new HashMap<String,String>();
		
		for(int i = 0 ; i < argNameList.size(); ++ i)
		{
			String argName = argNameList.get(i);
			String argValue = argValues.get(i);
			kvMap.put(argName, argValue);
		}
		
		String ret = "";
		int last = 0;
		for(Span span : argSpans)
		{
			String value = kvMap.get(span.text);
			if(value  == null)
				value = "<<"  + span.text + ">>";
			ret += expression.substring(last, span.begin) + value;
			last = span.end;
		}
		ret += expression.substring(last);
		return ret;
	}
	
	public static void main(String[] args)
	{
		Function func = new Function();
		List<String> argList = new ArrayList<String>();
		argList.add("x");
		argList.add("y");
		func.parse("", "(<<x>>+(<<y>>)sdfasfasdf<<z>>)", argList);
		
		List<String> valList = new ArrayList<String>();
		valList.add("111");
		valList.add("222");
		System.out.println(func.unfold(valList));
		
	}
}
