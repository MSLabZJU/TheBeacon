package com.example.thebeacon;


import java.util.ArrayList;

public class FindAllPeaks {
	
	float[] data;
	Float[] peaks;
	Integer[] peakkeys;
			
	public FindAllPeaks(float[] data)
	{
		FindAllPeaksGo(data);
	}
	
	public Float[] getpeaks()
	{
		return peaks;
	}
	
	public Integer[] getpeakkeys()
	{
		return peakkeys;
	}
	
	private void FindAllPeaksGo(float[] data)
	{
		this.data = data;
		ArrayList<Float> peaklist = new ArrayList<Float>();
		ArrayList<Integer> peakkeylist = new ArrayList<Integer>();
		
		for(int i=3;i<this.data.length;i++)
		{
			if((this.data[i-1]>this.data[i-2])&&(this.data[i-1]>this.data[i]))
			{
				peaklist.add(this.data[i-1]);
				peakkeylist.add(i-1);
			}
		}
		//将ArrayList类转换为数组
		peaks = (Float[])peaklist.toArray(new Float[0]);
		peakkeys = (Integer[])peakkeylist.toArray(new Integer[0]); 
	}

}
