package io.github.jayzhang.hcsa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.springframework.stereotype.Component;

import io.github.jayzhang.ac.Emit;
import io.github.jayzhang.ac.Trie;
 

@Component
public class SMiner {
	
	public Trie sentSplitter = new Trie().removeOverlaps();
    
	private String regexSplitPattern = "\\||\\[|\\]|\\^|\\$|\\(|\\)|\\.|\\*|\\+|\\?|\\-|\\,|\\{|\\}";
    
    private String sentSplitPattern = ";；.。，,！……!:？?\r\n  \t";
    
	private List<RegexAssertion> assertions = new ArrayList<RegexAssertion>();
	
	private Map<String, String> varMap = new HashMap<String, String>();
	
	private Map<String, Function> funcMap = new HashMap<String, Function>();
	
	private Map<String, Set<Integer> > assertMap = new HashMap<String, Set<Integer> >();
	
	private Trie wordMatcher = new Trie();

	public SMiner()
	{
		for(char ch : sentSplitPattern.toCharArray())	
		{
			String pat = "";
			pat += ch;
			sentSplitter.addPattern(pat);
		}
		sentSplitter.checkBuild();
		
		try {
			load("srule.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public List<Emit> splitSentence(String text)
	{
		
		List<Emit> occurs = sentSplitter.match(text);
		
		List<Emit> spans = new ArrayList<Emit>();
		
		int start = 0;
		int end;
		
		for(Emit pattern : occurs)
		{
			end = pattern.start;
			
			String sent = text.substring(start, end);
			
			if(sent.trim().length() > 0)
			{
			    Emit span = new Emit(start, end, sent);
			    spans.add(span);
			}
			start = end + 1;
		}
		
		String sent = text.substring(start);
		if(sent.trim().length() > 0)
		{
		    Emit span = new Emit(start, text.length(), sent);
			
			spans.add(span);
		}
		
		return spans;
	}
	
	
	private String parseExpression(String exp)
	{
		///< 1. check function, if exist, unfold
		
		String stag = "<func>";
		String etag = "</func>";
		
		String tmp = "";
		int last = 0;
		int start = exp.indexOf(stag);
		int end = 0;
		while(start != -1)
		{
			end = exp.indexOf(etag, start);
			if(end != -1)
			{
				String foccur = exp.substring(start + stag.length(), end);
				
				String[] arr = foccur.split(",");
				
				String fname = arr[0];
				
				List<String> argValList = new ArrayList<String>();
				for(int i = 1 ; i < arr.length; ++ i)
					argValList.add(arr[i]);
				
				Function func = funcMap.get(fname);
				
				String replace = func.unfold(argValList);
				
				tmp += exp.substring(last, start) + replace;
				
				last = end + etag.length();
				
				start = exp.indexOf(stag, last);
			}
			else 
				break;
		}
		
		tmp += exp.substring(last);
		
		///< 2. check variable, if exist, replace
		
		stag = "<";
		etag = ">";
		String tmp2 = "";
		last = 0;
		start = tmp.indexOf(stag);
		end = 0;
		while(start != -1)
		{
			end = tmp.indexOf(etag, start);
			if(end != -1)
			{
				String voccur = tmp.substring(start + stag.length(), end);
				
				String replace = varMap.get(voccur);
				
				tmp2 += tmp.substring(last, start) + replace;
				
				last = end + etag.length();
				
				start = tmp.indexOf(stag, last);
			}
			else 
				break;
		}
		tmp2 += tmp.substring(last);
		
		return tmp2;
	}
	
    public List<String> loadClassPathFileToLines(String filename) throws IOException
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(is));  
        
        String s = "";
        
        List<String> lines = new ArrayList<String>();
        
        while((s = br.readLine())!=null)  
        {
            lines.add(s);
        } 
        
        is.close();
        
        return lines;
    }
	
	private void parseLine(String text)  
	{
		if(text.contains("<=")) // assertion
		{
			String[] arr = text.split("<=");
			
			/////////////////////extract keywords and build assertion index
			String right = arr[1];
			
			Set<String> keySet = new HashSet<String>();
			
			String stag = "<keywords>";
			String etag = "</keywords>";
			
			int start = right.indexOf(stag);
			int end = -1;
			
			if(start != -1 )
			{
				end = right.indexOf(etag, start);
				if(end != -1)
				{
					String keywords = right.substring(start + stag.length(), end);
					String value = parseExpression(keywords);
					String[] keys = value.split(regexSplitPattern);
					for(String key : keys)
					{
						key = key.trim();
						if(key.length() > 0)
							keySet.add(key);
					}
					right = right.substring(0, start);
				}
			}
			int index = assertions.size();
			for(String word : keySet)
			{
				Set<Integer> set = assertMap.get(word);
				if(set != null)
					set.add(index);
				else 
				{
					set = new HashSet<Integer>();
					set.add(index);
					assertMap.put(word, set);
				}
			}
			
			//////////////////////////////////////////////
			String left = arr[0];
			String[] arr2 = left.split(",");
			String value = parseExpression(right);
			RegexAssertion assertion = new RegexAssertion();
			assertion.orgPattern = arr[1];
			assertion.pattern = Pattern.compile(value); 
			assertion.property = HotelProperties.valueOf(arr2[0]).getValue();
			assertion.rate = Rates.valueOf(arr2[1]).getValue();
			assertions.add(assertion);
		}
		else
		{
			String[] arr = text.split("=");
			String left = arr[0];
			String right = arr[1];
			if(left.contains(",")) // function
			{
				arr = left.split(",");
				String fname = arr[0];
				
				List<String> argList = new ArrayList<String>();
				for(int i = 1 ; i < arr.length; ++ i)
					argList.add(arr[i]);
				
				Function func = new Function();
				func.parse(fname, right, argList);
				funcMap.put(fname, func);
			}
			else // variable
			{
				String value = parseExpression(right);
				varMap.put(left, value);
			}	
		}
	}
	
	public boolean load(String file) throws IOException
	{
		List<String> lines = loadClassPathFileToLines(file);
		for(String line : lines)
		{
			line = line.trim();
			if(line.length() == 0 || line.startsWith("#"))
				continue;
			parseLine(line);
		}
		for(String word : assertMap.keySet())
			wordMatcher.addPattern(word);
		return true;
	}
	
	
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
//		sb.append("##################variable##################\n");
//		for(Entry<String, String> entry : varMap.entrySet())
//			sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
//		sb.append("##################function##################\n");
//		for(Entry<String, Function> entry : funcMap.entrySet())
//			sb.append(entry.getKey() + "=" + entry.getValue().toString() + "\n");
		sb.append("##################assertion##################\n");
		for(RegexAssertion ass : assertions)
			sb.append(ass.toString() + "\n");
				
		return sb.toString(); 
	}
	public List<SMiningResult> extractTargets(String text)
	{
		List<SMiningResult> results = new ArrayList<SMiningResult>();
		if(text == null || text.length() == 0)
			return results;
		
		List<Emit> spans = splitSentence(text);
		
		for(Emit span : spans)
		{	
		    List<Emit> occurs = wordMatcher.match(span.pattern);
			Set<Integer> assertSet = new HashSet<Integer>();
			for(Emit pattern : occurs)
			{
				String word = pattern.pattern;
				Set<Integer> tmp = assertMap.get(word);
				assertSet.addAll(tmp);
			}
			
			Set<String> set = new HashSet<String>();
			
			
			for(Integer index : assertSet)
			{
				RegexAssertion pattern = assertions.get(index);
				Matcher matcher = pattern.pattern.matcher(span.pattern);
				
				while (matcher.find()) {
					String matchText = matcher.group();
					int localStart = matcher.start();
					int localEnd = matcher.end();
				     
					String preffix = span.pattern.substring(0, localStart);
		        	
		        	if( !(preffix.contains("不是") || preffix.contains("不算")) )
		        	{
		        		SMiningResult miningResult = new SMiningResult();
			        	miningResult.matchText = matchText;
			        	miningResult.matchPattern = pattern.orgPattern;
			        	miningResult.property = pattern.property;
			        	miningResult.rate = pattern.rate;
			        	miningResult.begin = span.start + localStart;
			        	miningResult.end = span.start + localEnd;
			        	
			        	String key = miningResult.property + "-" + miningResult.begin + "-" + miningResult.end + "-" + miningResult.rate;
			        	
			        	if(!set.contains(key))
			        	{
			        		results.add(miningResult);
			        		set.add(key);
			        	}
		        	}
				}
			}
		}
		return results;
	}
	
	/**
	 * @param args
	 * @throws MalformedPatternException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		SMiner miner = new SMiner();
		 
		
		long t1 = System.currentTimeMillis();
		
		List<SMiningResult> list = miner.extractTargets("服务不怎么样，自助餐还可以，周围很安静");
		for(SMiningResult result : list)
			System.out.println("result:" + result);
				
		long t2 = System.currentTimeMillis();
		System.out.println("time:" + ((t2-t1)/1000) + " sec");
	}
}
