package io.github.jayzhang.hcsa;

import java.util.List;



public class TextUtil {
	
	public static String REPLACE = "...";
	public static int MIN_CXT_LEN = 2;
	
	public static String removeMiddle(String text, int limit)
	{
		if(text.length() <= limit)
			return text;
		
		int remove = text.length() - limit;
		
		int start = limit/2;
		
		int end = start + remove;
		
		return text.substring(0, start) + REPLACE + text.substring(end);
	}
	
	
	public static String removeBegin(String text, int limit)
	{
		if(text.length() <= limit)
			return text;
		
		int remove = text.length() - limit;
		return REPLACE + text.substring(remove);
	}
	
	public static String removeEnd(String text, int limit)
	{
		if(text.length() <= limit)
			return text;
		return text.substring(0, limit) + REPLACE;
	}
	
	public static HighlightResult highlightText(List<Highlight> hls , String text, String highlightSTag, String highlightETag, int wordLimit)
	{
		HighlightResult result = new HighlightResult();
		if(text == null)
			return null;
		
		if(text.length() == 0)
			return result;
		
		if( hls == null || hls.size() == 0
				|| highlightSTag == null || highlightSTag.length() == 0
				|| highlightETag == null || highlightETag.length() == 0
				)
		{
			result.setHighlight(text);
			if(wordLimit > 0)
			{
				if(wordLimit < text.length())
					result.setSummaryHighlight(text.substring(0, wordLimit) + REPLACE);
				else 
					result.setSummaryHighlight(text);
			}
			else 
				result.setSummaryHighlight(text);
			return result;
		}
		
		int last = 0;
		
		int cxtlen = -1;
		
		if(wordLimit > 0)
		{
			int segCount = 0;
			int highlen = 0;
			for(Highlight hl : hls)
			{
				if(last <= hl.getOffset())
				{
					highlen += hl.getLength();
					last = hl.getOffset() + hl.getLength();
					++ segCount;
				}
			}
			cxtlen = (wordLimit - highlen) / segCount;
			if((wordLimit - highlen) % hls.size() != 0)
				cxtlen ++;
			
			if(cxtlen < MIN_CXT_LEN)
				cxtlen = MIN_CXT_LEN;
		}
		
//		System.out.println("cxtlen: " + cxtlen);
		
		last = 0;
		String highlight = "";
		String summaryHighlight = "";
		int sumlen = 0;
		for(Highlight hl : hls)
		{
			try
			{
				if(last <= hl.getOffset())
				{
					String context = text.substring(last, hl.getOffset());
					highlight += context
							+ highlightSTag 
							+ text.substring(hl.getOffset(),hl.getOffset() + hl.getLength()) 
							+ highlightETag;
					
					if(cxtlen > 0)
					{
						String contextSummary = removeMiddle(context, cxtlen);
						if(last == 0)
							contextSummary = removeBegin(context, cxtlen%2==0? cxtlen/2 : (cxtlen/2+1));
						summaryHighlight += contextSummary
								+ highlightSTag 
								+ text.substring(hl.getOffset(),hl.getOffset() + hl.getLength()) 
								+ highlightETag;	
						
						sumlen += contextSummary.length() + hl.getLength();
					}
					last = hl.getOffset() + hl.getLength();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		highlight += text.substring(last);
		if(cxtlen > 0)
		{
			int remain = text.length() - last; 
			if(remain + sumlen <= wordLimit)
				summaryHighlight += text.substring(last);
			else 
				summaryHighlight += removeEnd(text.substring(last), cxtlen%2==0? cxtlen/2 : (cxtlen/2+1));
		}
		else 
		{
			summaryHighlight = highlight;
		}
		
		
		result.setHighlight(highlight);
		result.setSummaryHighlight(summaryHighlight);
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
	}

}
