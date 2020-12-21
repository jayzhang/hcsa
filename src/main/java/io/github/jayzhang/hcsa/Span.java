package io.github.jayzhang.hcsa;

public class Span {
	public int begin;
	public int end;
	public String text;
	
	public String toString()
	{
		return text + "(" + begin + "-" + end + ")";
	}
}
