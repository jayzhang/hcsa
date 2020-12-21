package io.github.jayzhang.hcsa;

import java.util.ArrayList;
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

		String res = removeMiddle("你是个大傻瓜二百五�??三点大白痴", 8);
		System.out.println(res);
		
		
		String text = "鹿回头酒店�?于大东海的尽头，海景一般，酒店�?人沙滩很�?，而且还在建设中。由于是人造沙滩，所以酒店�?建议游客在海里游泳。因为酒店�?置较�??而且是新建的，所以设施很新，客人�?多，很安�?�。酒店绿化很好，泳池�?错。酒店交通也比较方便，上下�?�有到市区的�?�车，酒店外�?�也有公交车，3元�?�到市区。总的�?�说，酒店�?错，比银泰等市内酒店环境好，喜欢安�?�的朋�?��?�以试试。";
	
		System.out.println("length: " + text.length());
		
		List<Highlight> hls = new ArrayList<Highlight>();
		hls.add(new Highlight(76, 4, 0));
		hls.add(new Highlight(102, 9, 0));
//		hls.add(new Highlight(104, 7, 0));
//		hls.add(new Highlight(158, 3, 0));
		
		HighlightResult result = highlightText(hls, text, "<start>", "<end>",  10);
		System.out.println(result.getHighlight());
		System.out.println(result.getSummaryHighlight());
		
		System.out.println("务�?度比较好，我们开会什么的，�?务都是微笑�?�的，�?是苦瓜�?�脸。装修挺新的，也挺好的。地�?��?置还行�?�，凑�?�，交通很好的，去个市区哪儿的都行。房间里的�?套设施还算�?全�?�，少了个�?�风机。�?饮还�?�以，味�?�还�?�以。价�?�?了解，我没...".length());
	}

}
