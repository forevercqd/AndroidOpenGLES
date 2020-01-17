package com.android.chapter30.md2;

public class Edge
{
	int	start, end;
	int	hs;

	public Edge(int s, int e)
	{
		this.start = (s < e) ? s : e;
		this.end = (s < e) ? e : s;
		this.hs = (start + "." + end).hashCode();
	}

	public int hashCode()
	{
		return hs;
	}

	public boolean equals(Object o)
	{
		if (o instanceof Edge)
		{
			Edge e = (Edge) o;
			return (e.start == start && e.end == end) || (e.start == end && e.end == start);
		}
		return false;
	}
}
