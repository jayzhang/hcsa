package io.github.jayzhang.hcsa;

import java.util.regex.Pattern;

public class RegexAssertion {
	public Pattern pattern;
	public String orgPattern;
	public int property;
	public int rate;
	
	public String toString()
	{
		return "RegexPattern[" + pattern.pattern() + "/property=" + property 
				+ ", rate=" + rate + ", orgPattern:" + orgPattern + "]";
	}
}
