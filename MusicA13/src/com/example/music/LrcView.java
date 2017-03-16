package com.example.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class LrcView extends View {
	
	float ww,wh;
	
	public float getWw() {
		return ww;
	}

	public void setWw(float ww) {
		this.ww = ww;
	}

	public float getWh() {
		return wh;
	}

	public void setWh(float wh) {
		this.wh = wh;
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public void init(){
		paint=new Paint();
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(2);
		paint.setColor(Color.RED);
		paint.setTextSize(lrcSize);
		paint.setTextAlign(Align.CENTER);
		
		other=new Paint();
		other.setStyle(Style.FILL);
		other.setStrokeWidth(2);
		other.setColor(Color.BLUE);
		other.setTextSize(lrcSize);
		other.setTextAlign(Align.CENTER);
	}
	Paint  paint,other;
	int margin=35;//歌词行间距
	int lrcSize=30;//歌词文本size
	//当前歌词下标
	int LrcIndex;
	//time 歌曲播放时间--ms
	public void getLrcIndex(long time){
		for(int i=map.size()-1;i>0;i--){
			if(time<map.get(i).getBeginTime()&&
					time>=map.get(i-1).getBeginTime()){
				LrcIndex=i-1;
				break;
			}
		}
		
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		if(map==null||map.size()==0){
			canvas.drawText("未找到歌词文件", ww/2, wh/2, paint);
			return;
		}
		
		for (int i = LrcIndex-1; i >=0; i--) {
			if(wh/2-(margin+lrcSize)*(LrcIndex-i)<0){
				break;
			}
			
			if(LrcIndex-1>=0){
				canvas.drawText(map.get(i).getContent(), ww/2, wh/2-(margin+lrcSize)*(LrcIndex-i), other);
			}
		}
		
		
		canvas.drawText(map.get(LrcIndex).getContent(), ww/2, wh/2, paint);
		
		for (int i = LrcIndex+1; i < map.size(); i++) {
			if((lrcSize+margin)*(i-LrcIndex)>wh/2){
				break;
			}
			
			canvas.drawText(map.get(i).getContent(), ww/2, wh/2+(lrcSize+margin)*(i-LrcIndex), other);
		}
		
		super.onDraw(canvas);
	}	
	
	
	//集合保存所有歌词
	Map<Integer, Lrc> map;
	
	//读取歌词  file--.mp3
	public void getLrc(String file){
		String path= file.substring(0, file.lastIndexOf("."))+".lrc";
		System.out.println(path);
		File lrcFile=new File(path);
		if(lrcFile.exists()&&lrcFile.isFile()){
			try {
				//读一行的流
				map = new HashMap<Integer, Lrc>();
				BufferedReader read = new BufferedReader(new InputStreamReader(
						new FileInputStream(path)));
				
				//歌词的行数---map : key
				int index=0;
				
				while(true){
					String text=read.readLine();
					if(text==null){
						break;
					}
					
					
					//空白行处理
					if(text.trim().length()==0){
//						Lrc lrc=new Lrc();
//						lrc.setBeginTime(0);
//						lrc.setContent("");
//						map.put(index, lrc);
						continue;
					}else{
						text=text.replace("[", "");
						Lrc lrc=new Lrc();
						
						if(text.endsWith("]")){//没有歌词
							
							String[] times= text.replace("]", "").replace(".", ":").split(":");
							lrc.setBeginTime((Integer.valueOf(times[0])*60+Integer.valueOf(times[1]))*1000+Integer.valueOf(times[2]));
							lrc.setContent("");
							
						}else{//有歌词
								
							String[] times= text.split("]")[0].replace(".", ":").split(":");
							lrc.setBeginTime((Integer.valueOf(times[0])*60+Integer.valueOf(times[1]))*1000+Integer.valueOf(times[2]));
							lrc.setContent(text.split("]")[1]);
							
						}
						System.out.println(lrc.getContent());
						map.put(index, lrc);
						
						
						index++;
					}
					
				}
				System.out.println("map:"+map.size());
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}else{//没有歌词文件
			if(map!=null){
				map.clear();
			}
			
		}
		

	}
}
