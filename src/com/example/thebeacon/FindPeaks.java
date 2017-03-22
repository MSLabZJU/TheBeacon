package com.example.thebeacon;


public class FindPeaks {
	
	Float[] peaks;
	Integer[] peakkeys;
	float max1,max2;
	int maxplace1,maxplace2;
	int len;
	
	int place1,place2;
	
	public FindPeaks(Float[] peaks,Integer[] peakkeys,float max,int maxplace)
	{
		this.FindPeaksGo(peaks, peakkeys,max,maxplace);
	}
	//返回在互相关矩阵中两个峰值的位置
	public int getpeakkey1()
	{
		return this.peakkeys[place1];
	}
	
	public int getpeakkey2()
	{
		return this.peakkeys[place2];
	}
	//返回在互相关矩阵两个峰值的幅值大小
	public float getpeak1()
	{
//		return this.peaks[place1];
		return this.max1;
	}
	
	public float getpeak2()
	{
		return this.peaks[place2];
	}
	
	private void FindPeaksGo(Float[] peaks,Integer[] peakkeys,float max,int maxplace)
	{
		this.peaks = peaks;
		this.peakkeys = peakkeys;
		this.max1 = max;
		this.maxplace1 = maxplace;
		
		this.len = this.peaks.length;
		
		for(int i=0;i<this.len;i++)
		{
			if(this.peaks[i]==this.max1)
			{
				place1 = i;//找到最大值在峰值矩阵中的位置
				break;
			}
		}
		//将最大值前后400个峰值置0
		for(int i=0;i<700;i++)
		{
			if((this.place1+i)<this.len)
			{
				this.peaks[this.place1+i] = 0f;
			}
			if((this.place1-i)>0)
			{
				this.peaks[this.place1-i] = 0f;
			}	
		}
			
		this.max2 = 0;//将最大值的初值设为0
		for(int i=0;i<len;i++)
		{
			if(this.peaks[i]>this.max2)
			{
				this.max2 = this.peaks[i];
				this.place2 = i;
			}
		}
//		
//		System.out.println(place1);
//		System.out.println(place2);
	}

}
