package com.example.thebeacon;


public class DistanceEstimate {
	
	float[] datacc;
	float peak;
	int peakkey;
	int lencc;
	
	int realplace;
	float realpeak;
	
	float mean =0;
	float sgm = 0;
	float snr = 0;
    int start; 

	public DistanceEstimate(float[] datacc,float peak,int peakkey)
	{
		this.DistanceEstimateGo(datacc, peak,peakkey);
	}
	
	public int getrealplace()
	{
		return realplace;
	}
	
	private void DistanceEstimateGo(float[] datacc,float peak,int peakkey)
	{
		this.datacc = datacc;
		this.peak = peak;
		this.peakkey = peakkey;
		this.lencc = this.datacc.length;
		this.start = peakkey;
		
		if((peakkey+2000)<lencc){
			for(int i=peakkey+2000;i>peakkey;i--)
			{
				if(this.datacc[i]>0.3*peak)
				{
					this.realplace = i;
					break;
				}
				
			}
		}
		else{
			//数组的最后一项为lencc-1
			for(int i=lencc-1;i>peakkey;i--)
			{
				if(this.datacc[i]>0.3*peak)
				{
					this.realplace = i;
					break;
				}
				
			}
		}
		
//		//找0.8倍的点
//		if((peakkey+3000)<lencc)
//		{
//			for(int i=peakkey+3000;i>=peakkey;i--){
//				if(datacc[i]>0.8*peak){
//					start=i;
//					break;
//				}
//			}
//		}
//		else
//		{
//			for(int i=lencc-1;i>=peakkey;i--){
//				if(datacc[i]>0.8*peak){
//					start=i;
//					break;
//				}
//			}		
//		}
//
//			//求方差和均值
//			if((start+2500)<lencc)
//			{
//				for(int i=start+2500;i>=start+2000;i--){
//					mean=mean+datacc[i];
//					sgm=sgm+datacc[i]*datacc[i];
//				}
//				mean=mean/501;
//				sgm=(float) Math.sqrt(sgm/501-mean*mean);
//				snr = peak/(mean+3*sgm);
//			}
//			else{
//				for(int i=lencc-1;i>=lencc-501;i--){
//					mean=mean+datacc[i];
//					sgm=sgm+datacc[i]*datacc[i];
//				}
//				mean=mean/501;
//				sgm=(float) Math.sqrt(sgm/501-mean*mean);
//				snr = peak/(mean+3*sgm);
//			}
//			
//			//调整参数
//			if(snr>10){
//				realpeak=(float) (0.3*peak);	
//			}else if(snr>5){
//				realpeak=(float)(mean+sgm*5+(peak-mean-sgm*5)*0.3);
//			}else if(snr>2){
//				realpeak=(float)(mean+sgm*3+(peak-mean-sgm*3)*0.4);
//			}else{
//				realpeak=(float)(mean+sgm*3+(peak-mean-sgm*3)*0.6);
//			}
//			
//		if(start+2000<lencc)
//		{
//			for(int i=start+2000;i>start;i--)
//			{
//				if(datacc[i]>realpeak){
//					realplace = i;
//				}
//			}
//		}
//		else{
//			for(int i=lencc-1;i>start;i--)
//			{
//				if(datacc[i]>realpeak){
//					realplace = i;
//				}
//			}
//		}
			
	}
}
