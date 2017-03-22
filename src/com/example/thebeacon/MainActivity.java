package com.example.thebeacon;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button b_connect;
	int x,y;
	LinearLayout line1,line2,line3;
	BorderTextView btv1;
	BorderTextView tv1,tv2;
	Socket socket;
	BufferedReader br;
	PrintWriter pw;
	Thread thread;
	
	Handler handler;
//	Timer timer = new Timer();
	Timer timer;
	
	private boolean IsRecord = false;//��¼¼��״̬�ı�־λ
	private static int FolderNum = 1;//���ڴ��¼���ļ����� �ļ������Ƶ���ر�־λ
	private static int FileNum = 0;//¼���ļ��ļ����Ƶ���ر�־λ
	private static String audioName = null;//�����ļ���
	private static String newAudioName = null;//����Ŀɲ��ŵ������ļ���
	
	//������Ƶ��ԴΪ��˷�
	private static int audioSource = MediaRecorder.AudioSource.MIC;
	//���ò���Ƶ��
	private static int sampleRateInHz = 44100;
	//������Ƶ��¼������:CHANNEL_IN_STEREOΪ˫������CHANNEL_CONFIGURATION_MONOΪ������
	private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	//��Ƶ���ݸ�ʽ��PCM���������λ��������8λ������16λ��Ҫ��֤�豸֧��
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	//���ñ��������С�ı���
	private int bufferSizeInBytes = 0;
	
	//¼��ʵ����AudioRecord
	private AudioRecord audioRecord;
	//��SoundPool��������
	private SoundPool sp;
	private HashMap<Integer,Integer> spMap;
	
	Socket filetransformationsocket;
	DataInputStream filetransformationin;
	DataOutputStream filetransformationout;
	String datalength = "8192";
	File filetransformation;
	Thread filetransformationthread;
	
	int realpl1,realpl2;
	static boolean comflag = false;
	
	private MulticastSocket multicastSocket=null;    
    private static int BROADCAST_PORT=9898;    
    private static String BROADCAST_IP="224.0.0.1";      
    InetAddress inetAddress=null;      
//    private static String ip ="targetip" ;  
    private static String ip = "192.168.1.100";
    static int ipAddress;
    static String strip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//��ֹ����
		line1 = (LinearLayout)findViewById(R.id.line1);
		line2 = (LinearLayout)findViewById(R.id.line2);
		x=getWindowManager().getDefaultDisplay().getWidth();//��ȡ��Ļ���
		y=getWindowManager().getDefaultDisplay().getHeight();//��ȡ��Ļ�߶�
		
		btv1 = new BorderTextView(this);
		btv1.setWidth(x);
		btv1.setHeight(y/12);
		btv1.setText("����״̬");
		btv1.setTextSize(18);
		btv1.setGravity(Gravity.CENTER);
		btv1.setBackgroundColor(Color.LTGRAY);
		line1.addView(btv1);
		
		tv1 = new BorderTextView(this);
		tv1.setWidth(x/2);
		tv1.setHeight(y/8);
		tv1.setTextSize(16);
		tv1.setGravity(Gravity.CENTER);
		
		tv2 = new BorderTextView(this);
		tv2.setWidth(x/2);
		tv2.setHeight(y/8);
		tv2.setTextSize(16);
		tv2.setGravity(Gravity.CENTER);
		
		line2.addView(tv1);
		line2.addView(tv2);
		
		while(new File("mnt/sdcard/Data"+String.valueOf(FolderNum)).exists()==true){
			FolderNum = FolderNum+1;
		}//�������ļ������ڴ��¼���ļ�
		
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxVolume , 0);
		
		//��û�������С
		bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,channelConfig,audioFormat);
		System.out.println(bufferSizeInBytes);
		//ʵ����AudioRecord
		audioRecord = new AudioRecord(audioSource,sampleRateInHz,channelConfig,audioFormat,bufferSizeInBytes);
		//��ֹ�ļ����ظ�
		
	    sp = new SoundPool(7,AudioManager.STREAM_MUSIC,0);//ͬʱ���ŵ������Ƶ��Ϊ7
	    spMap = new HashMap<Integer,Integer>();
	    spMap.put(1, sp.load(this, R.raw.data_refer, 1));
	    
	    //����UDP�㲥��socket
//		try      
//        {       
//             multicastSocket = new MulticastSocket(BROADCAST_PORT);    
//             inetAddress=InetAddress.getByName(BROADCAST_IP);   
//             multicastSocket.joinGroup(inetAddress);    
//  
//        } catch (Exception e1)     
//        {       
//            e1.printStackTrace();    
//        }   
//		
//		Thread recieveip = new Thread(new Runnable(){
//			public void run()
//			{ 
//			        byte buf[] = new byte[1024];    
//			        DatagramPacket dp =null;      
//			        dp=new DatagramPacket(buf,buf.length,inetAddress,BROADCAST_PORT);   
//			           
//			            while (true)       
//			            {        
//			                try                   
//			                {               
//			                    multicastSocket.receive(dp);  
//			                    Thread.sleep(3000);  
//			                    ip=new String(buf, 0, dp.getLength());  
//			                    System.out.println("��⵽�����IP : "+ip);     
//			                } catch (Exception e)      
//			                {        
//			                    e.printStackTrace();            
//			                }      
//			                
//			                if(ip!="targetip")
//			                {
//			                	try {
//									multicastSocket.leaveGroup(inetAddress);
//									thread.start();
//									break;
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}  
//			                }
//			            }      
//			}
//		});
//		recieveip.start();
	    
	    strip = getIpAddress();
		thread = new Thread(new Runnable(){
			public void run(){
				while(true)
				{
					try{
						socket = new Socket(ip,2002);
						pw = new PrintWriter(socket.getOutputStream(),true);
						while(true){
							br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String line = br.readLine();
							while(line!= null){
								if(line.equals("00"))
								{
									new File("mnt/sdcard/Data"+String.valueOf(FolderNum)).mkdirs();
									IsRecord = true;
									startRecord();
									pw.println("520");//��ʼ¼�����������ƶ�520
									btv1.post(new Runnable(){
										public void run(){
											btv1.setBackgroundColor(Color.GREEN);
										}
									});
								}
								if(line.equals("01"))
								{
									IsRecord = false;
									stop();
									timer = new Timer();
	//								timer.schedule(new filetransport(), 100);//¼��������ʼ�����ļ�
									btv1.post(new Runnable(){
										public void run(){
											btv1.setBackgroundColor(Color.LTGRAY);
										}
									});										
								}
								if(line.equals("02"))
								{
									playSounds(1,1);
									pw.println("000");//��������������־λ000�����ƶ�
								}
								line = br.readLine();
							}
						}
						
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	
	private void startRecord(){	
    	audioRecord.startRecording();
    	IsRecord = true;
    	new Thread(new AudioRecordThread()).start();//��������һ���̣߳��Ӷ������̲߳�������
    }
    
	private void stop(){
    	if(audioRecord != null){
    		System.out.println("stop Recording");
    		IsRecord = false;
    		audioRecord.stop();
    	}
    }

	public void playSounds (int sound, int number){
	
	//AudioManger����ͨ��getSystemService(Service.AUDIO_SERVICE)��ȡ
	AudioManager am = (AudioManager)this.getSystemService(this.AUDIO_SERVICE);  
	//����ֻ����������������
	float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  
	//float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);  
	float volumnRatio = audioMaxVolumn;  
    sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number, 1);
}
    
class AudioRecordThread implements Runnable{
		
		@Override
		public void run(){
			FileNum++;
			audioName = "/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".raw";
			newAudioName = "/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".wav";			
			writeDateTOFile();	//���ļ���д��������			
			copyWaveFile(audioName,newAudioName);//�õ����Բ��ŵ�wav�ļ�
			new File("/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".raw").delete();
			new Thread(new compute()).start();
			new Thread(new resulttrans()).start();
		}	
	}	
	
private void writeDateTOFile(){
		
		//newһ��byte����������һЩ�ֽ����ݣ���СΪ�������Ĵ�С
		byte[] audiodata = new byte[bufferSizeInBytes];		
		FileOutputStream fos = null;		
		int readsize = 0;	
		try{
		File file = new File(audioName);			
			if(file.exists()){				
				file.delete();				
			}			
			fos = new FileOutputStream(file); //����һ���ɴ�ȡ�ֽڵ��ļ�			
		}catch(Exception e){
			e.printStackTrace();
		}		
		while(IsRecord == true){			
			//������Ӳ����ȡ���ݣ�������仺�����飬����������������ݵĴ�С
			readsize = audioRecord.read(audiodata,0, bufferSizeInBytes);				
			if(AudioRecord.ERROR_INVALID_OPERATION != readsize){
				try{
					System.out.println("writeDateTOFile..."+readsize);					
					//��audiodata�е�����д������ļ���fos
					//fos.write(audiodata);
					fos.write(audiodata,0,readsize);					
				}catch(IOException e){
					e.printStackTrace();
				}
			}		
		}
		try{
			fos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	//����ĺ���ʵ�ֵĹ����ǽ������ļ�ת���ɿ��Բ��ŵ���Ƶ�ļ�
private void copyWaveFile(String inFilename,String outFilename){
		
		//���������������͵ı��������ոú�������������
		FileInputStream in = null;
		FileOutputStream out = null;
		
		//�������������Ҫ��Ϊ�������ݸ��Զ���ĺ���WriteWaveFileHeader
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen+36;
		long longSampleRate = sampleRateInHz;
		int channels = 2;
		long byteRate = 16*sampleRateInHz*channels/8;
		
		//newһ��byte����������һЩ�ֽ����ݣ���СΪ�������Ĵ�С����һ�ζ�����Ǹ���ֻ�ܱ��Ǹ�����˽��
		byte[] data = new byte[bufferSizeInBytes];
		
		try{
			in = new FileInputStream(inFilename);//����һ�������ļ���
			out = new FileOutputStream(outFilename);//����һ������ļ���
			
			totalAudioLen = in.getChannel().size();//�������ļ������ļ�ͨ���õ���ͨ���ļ��Ĵ�С
			totalDataLen = totalAudioLen+36;
			
			//�����ļ�ͷ
			WriteWaveFileHeader(out,totalAudioLen,totalDataLen,longSampleRate,channels,byteRate);
			
			//��in�ڵ�����д��data,����data�е�����д��out
			int size = 0;
			while((size = in.read(data)) != -1){
				System.out.println("copyWaveFile..."+size);
				out.write(data,0,size);
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}		
	}
	
	
	/*
	 * �����ṩһ��ͷ�ļ���Ϣ��������Щ��Ϣ�Ϳ��Եõ����Բ��ŵ��ļ�����Щ��Ϣ��WAV��Ƶ�ļ�������ģ�
	 * �������иø�ʽ���ļ�����һ���ģ���Ȼ���ڸ�ʽ���ļ�Ҳ����Ӧ���ļ�ͷ
	 */
private void WriteWaveFileHeader(FileOutputStream out,long totalAudioLen,long totalDataLen,long longSampleRate,int channels,long byteRate)
	throws IOException{
		
		byte[] header = new byte[44];
		header[0]='R';	//RIFF/WAVE header
		header[1]='I';
		header[2]='F';
		header[3]='F';
		header[4]=(byte)(totalDataLen&0xff);
		header[5]=(byte)((totalDataLen>>8)&0xff);
		header[6]=(byte)((totalDataLen>>16)&0xff);
		header[7]=(byte)((totalDataLen>>24)&0xff);
		header[8]='W';
		header[9]='A';
		header[10]='V';
		header[11]='E';
		header[12]='f';	//'fmt ' chunk
		header[13]='m';
		header[14]='t';
		header[15]=' ';
		header[16]=16;	// 4 bytes: size of 'fmt ' chunk
		header[17]=0;
		header[18]=0;
		header[19]=0;
		header[20]=1;//format=1
		header[21]=0;
		header[22]=(byte)channels;
		header[23]=0;
		header[24]=(byte)(longSampleRate&0xff);
		header[25]=(byte)((longSampleRate>>8)&0xff);
		header[26]=(byte)((longSampleRate>>16)&0xff);
		header[27]=(byte)((longSampleRate>>24)&0xff);
		header[28]=(byte)(byteRate&0xff);
		header[29]=(byte)((byteRate>>8)&0xff);
		header[30]=(byte)((byteRate>>16)&0xff);
		header[31]=(byte)((byteRate>>24)&0xff);
		header[32]=(byte)(2*16/8);//block align
		header[33]=0;
		header[34]=16;//bits per sample
		header[35]=0;
		header[36]='d';
		header[37]='a';
		header[38]='t';
		header[39]='a';
		header[40]=(byte)(totalAudioLen&0xff);
		header[41]=(byte)((totalAudioLen>>8)&0xff);
		header[42]=(byte)((totalAudioLen>>16)&0xff);
		header[43]=(byte)((totalAudioLen>>24)&0xff);
		out.write(header,0,44);		
	}

public class BorderTextView extends TextView{  
	  
    public BorderTextView(Context context) {  
        super(context);  
    }  
    public BorderTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
    private int sroke_width = 1;  
    @Override  
    protected void onDraw(Canvas canvas) {  
        Paint paint = new Paint();  
        //  ���߿���Ϊ��ɫ  
        paint.setColor(android.graphics.Color.BLACK);  
        //  ��TextView��4����  
        canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);  
        canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);  
        canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);  
        canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);  
        super.onDraw(canvas);  
    }  
} 

class compute implements Runnable{
	public void run(){
		while(true)
		{
			if(new File("/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".wav").exists()==true)
			{
				String path1 = "/mnt/sdcard/Data"+FolderNum+"/data_"+FileNum+".wav";
				String path2 = "/mnt/sdcard/data_refer.wav";

				WaveFileReader wr1 = new WaveFileReader(path1);
				WaveFileReader wr2 = new WaveFileReader(path2);
				float[] DataSig = wr1.getData()[0];
				float[] DataRefer = wr2.getData()[0];
				int lensig = wr1.getDataLen();
				int lenrefer = wr2.getDataLen();
				
				FFTprepare fpp = new FFTprepare(DataSig);
				float[] FFTsig = fpp.getsig();
				
				FFTcc fcc = new FFTcc(FFTsig,DataRefer);
				fcc.FindFccMax();
				float[] rcc = fcc.getrcc();
				float max = fcc.getmaxvalue();
				int maxplace = fcc.getmaxplace();
				
				int lenrcc = rcc.length;
				float[] rccn;
				
				if(lensig<2400)
				{
					lensig = 4096;
				}
				
				if(lensig<=lenrcc)
				{
					rccn = new float[lensig];
					for(int i=lensig-1; i>=0; i--)
					{
						rccn[i] = rcc[lenrcc-1];
						lenrcc = lenrcc-1;
					}
				}
				else{
					rccn = rcc; 
				}
				
//				FindAllPeaks fap = new FindAllPeaks(rcc);
//				Float[] peaks = fap.getpeaks();
//				Integer[] peakkeys = fap.getpeakkeys();
//				
//				FindPeaks fp = new FindPeaks(peaks,peakkeys,max,maxplace);
//				float peak1 = fp.getpeak1();
//				float peak2 = fp.getpeak2();
//				int peakkey1 = fp.getpeakkey1();
//				int peakkey2 = fp.getpeakkey2();
//				
//				DistanceEstimate de1 = new DistanceEstimate(rcc,peak1,peakkey1);
//				DistanceEstimate de2 = new DistanceEstimate(rcc,peak2,peakkey2);
//				
//				realpl1 = de1.getrealplace();
//				realpl2 = de2.getrealplace();
				
				FindPeaksNew fpn = new FindPeaksNew(rccn);
				int[] realnew = new int[2];
				realnew = fpn.getresult();
				
				realpl1 = realnew[0];
				realpl2 = realnew[1];
				
				tv1.post(new Runnable(){
					public void run(){
						tv1.setText(String.valueOf(realpl1));
					}
				});			
				tv2.post(new Runnable(){
					public void run(){
						tv2.setText(String.valueOf(realpl2));
					}
				});	
				comflag = true;

				break;
			}
		}
	}
}

class resulttrans extends TimerTask{
	public void run(){
		while(true)
		{
			if(comflag==true)
			{
				comflag = false;
				pw.println("521");
				pw.println(String.valueOf(realpl1));
				pw.println(String.valueOf(realpl2));			
//					filetransformationout.writeUTF("start");
//					filetransformationout.flush();
//					filetransformationout.writeUTF(String.valueOf(realpl1));
//					filetransformationout.flush();
//					filetransformationout.writeUTF(String.valueOf(realpl2));
//					filetransformationout.flush();
				break;
			}
		}
	}
};

public String getIpAddress()
{
    //��ȡwifi����  
    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
    //�ж�wifi�Ƿ���  
    if (!wifiManager.isWifiEnabled()) {  
    	wifiManager.setWifiEnabled(true);    
    }  
     
    while(true)
    {   
    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ipAddress = wifiInfo.getIpAddress();   
        if(ipAddress!=0)
        {
        	break;
        }
    }
    String ip = intToIp(ipAddress);   
    return ip;
}

private String intToIp(int i) {       
    
    return (i & 0xFF ) + "." +       
  ((i >> 8 ) & 0xFF) + "." +       
  ((i >> 16 ) & 0xFF) + "." +       
  ( i >> 24 & 0xFF) ;  
} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
