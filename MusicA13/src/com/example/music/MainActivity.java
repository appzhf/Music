package com.example.music;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
/**
 * ��ý��:
 * 		��Ƶ,��Ƶ,ͼƬ,¼��,����
 *		
 *	Mediaplayer 
 *		��Դ:res/uri
 *		    sdcard 
 *
 *	sdcard read_only
 *  ���: mount -o remount rw /
 *  ע��sdcard ��С
 *  ����ģ����
 * 
 *  
 *  ��ҵ:���ֲ�����
 *  
 *  ͼ��ͼ��---�Զ������
 *  	--����   ����Canvas  ��Paint   Ϳ��Color
 *  		  	
 *  			
 *  	
 *  	-���ͬ��
 *  	
 */

public class MainActivity extends Activity implements OnClickListener 
,OnCompletionListener,OnSeekBarChangeListener{
	MediaPlayer  mp;
	Button start,forward,next;
	SeekBar bar;
	TextView showTime;
	//��ǰ�����±�
	int index=0;
	int max;
	int time=0;
	List<Song>  songs;
	//��ȡ��������--
	public void getSongs(){
		songs=new ArrayList<Song>();
		Cursor c= getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
				new String[]{Media.DISPLAY_NAME,
					Media.DURATION,Media.DATA,Media.ARTIST}
		, null, null, null);
		while(c.moveToNext()){
			songs.add(new Song(c.getString(0), c.getLong(1), c.getString(2), c.getString(3)));
		}
		
		System.out.println("size:"+songs.size());
	}
	
	
	LrcView lrc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSongs();
        max=(int)songs.get(index).getDuration()/1000;
       
        
        lrc=(LrcView) findViewById(R.id.showlrc);
        
        lrc.setWw(getWindowManager().getDefaultDisplay().getWidth());
        lrc.setWh(getWindowManager().getDefaultDisplay().getHeight()-getResources().getDimension(R.dimen.linearH)-getResources().getDimension(R.dimen.seekBarH));
        
        //lrc.postInvalidate();
       // System.out.println(lrc);
        //3-9
        try { 
	        mp = new MediaPlayer();
	    	mp.setDataSource(songs.get(index).getPath());
	    	//3-9
	    	lrc.getLrc(songs.get(index).getPath());
	    	lrc.postInvalidate();
	    	
	    	
			mp.prepare();
			mp.setOnCompletionListener(this);
        }
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
        
        
        start=(Button) findViewById(R.id.start);
        start.setOnClickListener(this);
        forward=(Button) findViewById(R.id.forward);
        forward.setOnClickListener(this);
        next=(Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        
        showTime=(TextView) findViewById(R.id.showtime);
        
        bar=(SeekBar) findViewById(R.id.bar);
        bar.setMax(max);
        
        bar.setOnSeekBarChangeListener(this);
    }
    Handler handler=new Handler();
    Thread t=new Thread(){
    	public void run(){
    		time++;
    		if(time>max){
    			return;
    		}
    		bar.setProgress(time);
    		
    		//3-9
    		lrc.getLrcIndex(mp.getCurrentPosition());
    		lrc.postInvalidate();
    		
    		
    		showTime.setText(songs.get(index).getName()+"\n"+songs.get(index).getPath()+"\n"+songs.get(index).getSinger()+"\n"+time+"��");
    		handler.postDelayed(this, 1000);
    	}
    };
    public void play(){
    	handler.postDelayed(t, 1000);
    }
    
    //����״̬,ֹͣtrue ����false
    boolean isPause=true;
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.start:
			
				if (isPause) {
					//if(mp==null){}
					System.out.println("������ʼ����");
					play();
					mp.start();

					start.setText("pause");
					isPause = false;
				} else {
					System.out.println("����������ͣ");
					handler.removeCallbacks(t);
					mp.pause();
					start.setText("start");
					isPause = true;

				}
			
			break;

			
		case R.id.forward:
			index--;
			if(index<0){
				index=songs.size()-1;
			}
			playSong(index);
			break;
		case R.id.next:
			index++;
			if(index>songs.size()-1){
				index=0;
			}
			playSong(index);
			
			break;
		default:
			break;
		}
		
	}
	
	public void playSong(int index){
		try {
			time=0;
			
			max=(int)songs.get(index).getDuration()/1000;
			bar.setMax(max);
			if (mp != null) {
				mp.stop();
			} else {
				mp = new MediaPlayer();
			}
			mp.reset();
			mp.setDataSource(songs.get(index).getPath());
			//3-9
			lrc.getLrc(songs.get(index).getPath());
			lrc.postInvalidate();
			
			
			mp.prepare();
			mp.start();
			
			//3-9 ��ͣ״̬�¸����л�
			if(isPause){
				handler.postDelayed(t,1000);
				start.setText("pause");
				isPause=false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		System.out.println("�������Ž���");
		index++;
		if(index>songs.size()-1){
			index=0;
		}
		playSong(index);
	}
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		if(arg2){
			mp.seekTo(arg1*1000);
			time=arg1;
			if(isPause){
				mp.start();
				play();
				start.setText("pause");
				isPause=false;
			}
		}
		
	}
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		
		
	}


}
