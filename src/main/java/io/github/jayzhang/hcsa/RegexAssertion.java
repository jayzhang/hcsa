package io.github.jayzhang.hcsa;

import org.apache.oro.text.regex.Pattern;

public class RegexAssertion {
	public Pattern pattern;
	public String orgPattern;
	public int property;
	public int rate;
	
	public String toString()
	{
		return "RegexPattern[" + pattern.getPattern() + "/property=" + property 
				+ ", rate=" + rate + ", orgPattern:" + orgPattern + "]";
	}
}
