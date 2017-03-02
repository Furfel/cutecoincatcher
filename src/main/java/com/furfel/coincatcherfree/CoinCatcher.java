package com.furfel.coincatcherfree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class CoinCatcher extends Activity {

	public boolean left=false,right=false,up=false,down=false;
	
	public boolean pause=true,playing=false,screenhs=false,gameover=false,timeup=false;
	
	int playerx,playery,firex,firey;
	int score=0;
	int hiscores[] = new int[3];
	
	byte gametype=0; //0 - infinite; 1 - time trial; 2 - time trial only gold
	int timeleft=90;
	
	int sW,sH;
	int KONTROLS_SIZE=250;
	DrawCatcher Drawing;
	
	final int MAPX=10,MAPY=10;
	
	int supertime=0;
	boolean superfreeze=false;
	
	byte CoinMap[][] = new byte[10][10];
	
	Context context;

	private static final String APP_ID="<top_secret>";
	private static final String INTERSTITIAL="<top_secret>";

	public static AdRequest adRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.coincatcher_loading);

		MobileAds.initialize(this,APP_ID);
		CoinCatcher.adRequest = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				//.addTestDevice("<DoogeeF5 :^)>")
				.addKeyword("cute").addKeyword("game").addKeyword("arcade")
				.build();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		sW=dm.widthPixels; sH=dm.heightPixels;
		KONTROLS_SIZE=Math.round((25*sH)/48);
		if(KONTROLS_SIZE>350 && (dm.densityDpi==DisplayMetrics.DENSITY_LOW || dm.densityDpi==DisplayMetrics.DENSITY_MEDIUM)) KONTROLS_SIZE=350;
		Drawing=new DrawCatcher(this,sW,sH);
		Drawing.KontrolsSize=KONTROLS_SIZE;
		handler.sendEmptyMessageDelayed(77, 100);
		tmrGfx.scheduleAtFixedRate(drawing, 50, 50);
		tmrMovement.scheduleAtFixedRate(movement, 250, 250);
		tmrRoller.scheduleAtFixedRate(rollgame, 1000, 1000);
		tmrSecond.scheduleAtFixedRate(oneSecond, 1000, 1000);
		context=this;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if(playing && !gameover && !timeup)
		{pause=true;
		Drawing.paused=true;}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Drawing.qredraw();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	TimerTask movement=new TimerTask()
	{
		@Override
		public void run()
		{
			if(playing && !pause && !gameover && !timeup /*&& Drawing.pd==0*/){
				if((playery==firey && playerx==firex && supertime<=0) || (playery==firey && playerx==firex && supertime>0 && superfreeze)) {gameOver(); Log.d("Koins","GameOver"); handler.sendEmptyMessage(1);}
				if(supertime>0) {Drawing.supermode=(superfreeze)?"Freeze: "+Integer.toString(supertime):"Protection: "+Integer.toString(supertime);} else {Drawing.supermode=""; Drawing.supper=false;}
			if(left)
			{if(!(playerx<=0)) playerx--; /*left=false;*/}
			else if(right)
			{if(!(playerx>=MAPX-1)) playerx++; /*right=false;*/}
			else if(up)
			{if(!(playery<=0)) playery--; /*up=false;*/}
			else if(down)
			{if(!(playery>=MAPY-1)) playery++; /*down=false;*/}
			if(CoinMap[playerx][playery]==1) {score++; CoinMap[playerx][playery]=0;} else if(CoinMap[playerx][playery]==2) {score+=100; CoinMap[playerx][playery]=0;}
			else if(CoinMap[playerx][playery]==3) {score+=50; CoinMap[playerx][playery]=0; supertime+=10; Drawing.supper=true; Drawing.supperfreeze=false; superfreeze=false;} else if(CoinMap[playerx][playery]==4) {score+=50; CoinMap[playerx][playery]=0; supertime+=10; Drawing.supper=true; Drawing.supperfreeze=true; superfreeze=true;}
			}
		}
	};
	
	TimerTask drawing=new TimerTask()
	{
		@Override
		public void run()
		{
			if(playing && !pause)
			{
				Drawing.CoinMap=CoinMap;
				Drawing.redraw(playerx,playery,firex,firey,score);
			}
		}
	};
	
	TimerTask rollgame=new TimerTask()
	{
		@Override
		public void run() {
			if(playing && !pause && !gameover && !timeup)
			{
			if(supertime>0)  supertime--;
			CoinMap[firex][firey]=0;
			if(!superfreeze)
			{if(playerx<firex) firex--;
			if(playery<firey) firey--;
			if(playerx>firex) firex++;
			if(playery>firey) firey++;}
			if(superfreeze && supertime<=0) superfreeze=false;
			int cx=(int)Math.round(Math.random()*9),cy=(int)Math.round(Math.random()*9);
			float rnd=(float) Math.random();
			byte ct=(rnd<0.3 && rnd>0.27 && gametype<2)? (byte)4 : (rnd<0.4 && rnd>0.33 && gametype<2)? (byte)2 : (rnd<0.2 && rnd>0.17 && gametype<2) ? (byte)3 : (byte)1;
			CoinMap[cx][cy]=ct;
			}
		}
	};
	
	TimerTask oneSecond=new TimerTask()
	{
		@Override
		public void run() {
			if(playing && !pause && !gameover && !timeup)
			{
				if(gametype!=0){
				if(timeleft<=0)
				timeUp();else timeleft--;}
				Drawing.timeleft=timeleft;
			}
		}
	};
	
	Timer tmrGfx = new Timer();
	Timer tmrMovement = new Timer();
	Timer tmrRoller = new Timer();
	Timer tmrSecond = new Timer();
	
	public Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			//if(msg.what==1) {Toast.makeText(context,"DEBUG: Game Over",Toast.LENGTH_SHORT).show();}
			if(msg.what==888) {
				interstitialAd = new InterstitialAd(CoinCatcher.this);
				interstitialAd.setAdListener(new AdListener() {
					public void onAdLoaded() {
						super.onAdLoaded();
						interstitialAd.show();
					}
				});
				interstitialAd.setAdUnitId(INTERSTITIAL);

				interstitialAd.loadAd(CoinCatcher.adRequest);
			}
			else if(msg.what==77) {
				Drawing.spriteLoader();
				loadScores();
				setContentView(R.layout.activity_coin_catcher);
				loadAds();
			}
		}
	};
	
	public void loadAds() {
		AdView adView = (AdView) findViewById(R.id.adView);
		adView.loadAd(adRequest);
	}

	public static final int GAMETYPE_NORMAL=0;
	public static final int GAMETYPE_TIMETRIAL=1;
	public static final int GAMETYPE_TIMETRIAL_GOLD=2;

	public void prepareGame(View v) {
		if(v.getId()==R.id.normalGame)
			gametype=GAMETYPE_NORMAL;
		else if(v.getId()==R.id.timeTrial)
			gametype=GAMETYPE_TIMETRIAL;
		else if(v.getId()==R.id.timeTrialGold)
			gametype=GAMETYPE_TIMETRIAL_GOLD;
		reInitGame(gametype);
	}

	public void reInitGame(int gameType) {
		if(gameType==GAMETYPE_NORMAL) {
			timeleft=91;
			Drawing.timetrial=false;
		} else if(gameType==GAMETYPE_TIMETRIAL) {
			timeleft=90;
			Drawing.timetrial=true; Drawing.timeleft=90;
		} else if(gameType==GAMETYPE_TIMETRIAL_GOLD) {
			timeleft=90;
			Drawing.timetrial=true; Drawing.timeleft=90;
		}
		firex=8; firey=8;
		Drawing.dfx=80; Drawing.dfy=80;
		playerx=1;
		playery=1;
		Drawing.dpx=5;
		Drawing.dpy=5;
		supertime=0;
		score=0;
		superfreeze=false;
		Drawing.supermode="";
		Drawing.supper=false;
		Drawing.supperfreeze=false;
		Drawing.paused=false;
		gameover=false; timeup=false;
		Drawing.gameover=false; Drawing.timeup=false;
		for(int i=0;i<=9;i++)
			for(int j=0;j<=9;j++)
				CoinMap[i][j]=0;
		setContentView(Drawing);
		pause=false; playing=true;
	}
	
	public void selectGame(View v) {
		setContentView(R.layout.gameselect);
		loadAds();
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(Integer.toString(hiscores[0]));
		tv = (TextView) findViewById(R.id.textView2);
		tv.setText(Integer.toString(hiscores[1]));
		tv = (TextView) findViewById(R.id.textView3);
		tv.setText(Integer.toString(hiscores[2]));
	}

	public void furfelClicked(View v) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
			.setItems(R.array.furfel_list, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialogInterface, int i) {
					if(i==0) {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Furfel")));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pub:Furfel")));
						}
					} //More apps
					else if(i==2) { //Privacy
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1CxRitpEWKJzwL2Zxl-h9td02G88uMVkJZ4GUF2SsPno/pub")));
					} else if(i==1) {
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.furfel.coincatcherfree")));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.furfel.coincatchterfree")));
						}
					} //Rate
				}
			}).setIcon(R.drawable.ic_furfel)
			.setTitle(R.string.about_furfel);
		alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});
		alertDialog.show();
	}
	
	public void showHighscores(View v) {
		
	}

	public InterstitialAd interstitialAd;

	static int gameOverIntersitial = 0;

	public void gameOver() {
		Drawing.gameover=true;
		gameover=true;
		Drawing.redraw(playerx, playery, firex, firey, score);
		if(gametype==0)
			if(score>hiscores[0])
				{hiscores[0]=score;saveScores();}
		if(gameOverIntersitial%5==0)
			handler.sendEmptyMessage(888);
		gameOverIntersitial++;
		//playing=false;
	}
	
	public void timeUp() {
		Drawing.timeup=true;
		timeup=true;
		if(gametype>0)
			if(score>hiscores[gametype])
				{hiscores[gametype]=score;saveScores();}
	}

	private static final String KEY="<supersecretxorkey>";

	public void saveScores()
	{
		String key=KEY; int keyp=0; Byte xbuf,kbuf,buf;
		try{
			FileOutputStream fos = new FileOutputStream(new File(getCacheDir(),"tmpscr"));
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(hiscores[0]);
			dos.writeInt(hiscores[1]);
			dos.writeInt(hiscores[2]);
			dos.close();
			FileInputStream fis = new FileInputStream(new File(getCacheDir(),"tmpscr"));
			DataInputStream dis = new DataInputStream(fis);
			OutputStream os = openFileOutput("hiscores", Context.MODE_PRIVATE);
			dos = new DataOutputStream(os);
			while(dis.available()>0)
			{
				buf=dis.readByte();
				if(keyp<key.length()-1) keyp++; else keyp=0;
				kbuf=(byte)key.charAt(keyp);
				xbuf=(byte) (kbuf^buf);
				dos.writeByte(xbuf);
			}
			dis.close(); dos.close();
			File delcache=new File(getCacheDir(),"tmpscr");
			if(delcache!=null) delcache.delete();
		}
		catch(IOException e) {}
	}
	
	public void loadScores()
	{
		String key=KEY; int keyp=0; Byte xbuf,kbuf,buf;
		File chk = getFileStreamPath("hiscores");
		if(chk.exists()){
		try
		{
			InputStream is = openFileInput("hiscores");
			DataInputStream dis = new DataInputStream(is);
			FileOutputStream fos = new FileOutputStream(new File(getCacheDir(),"tmpscr"));
			DataOutputStream dos = new DataOutputStream(fos);
			while(dis.available()>0)
			{
				buf=dis.readByte();
				if(keyp<key.length()-1) keyp++; else keyp=0;
				kbuf=(byte)key.charAt(keyp);
				xbuf=(byte) (kbuf^buf);
				dos.writeByte(xbuf);
			}
			dis.close(); dos.close();
			FileInputStream fis = new FileInputStream(new File(getCacheDir(),"tmpscr"));
			dis = new DataInputStream(fis);
			hiscores[0]=dis.readInt();
			hiscores[1]=dis.readInt();
			hiscores[2]=dis.readInt();
			dis.close();
			File delcache=new File(getCacheDir(),"tmpscr");
			if(delcache!=null) delcache.delete();
		}
		catch (IOException e) {}
		}
	}
	
	public boolean onTouchEvent(MotionEvent evt)
	{
		int action=evt.getAction();
		float yx=evt.getX(),yy=evt.getY();
		switch(action)
		{
			case MotionEvent.ACTION_DOWN: {
				if(gameover || timeup)
					reInitGame(gametype);
				else if(pause) {
					pause=false; Drawing.paused=false;
				} else {
				if(yy>sH-KONTROLS_SIZE && yy<sH)
				{
					if(yx<KONTROLS_SIZE && yx>0)
					{	
						if((yy<-yx+sH) && (yy<yx+sH-KONTROLS_SIZE) )
							if(!up) {down=false; left=false; right=false; up=true;} else up=true;
						else if((yy>-yx+sH) && (yy>yx+sH-KONTROLS_SIZE) )
							if(!down) {down=true; left=false; right=false; up=false;} else down=true;
						else if((yy<-yx+sH) && (yy>yx+sH-KONTROLS_SIZE) )
							if(!left) {down=false; left=true; right=false; up=false;} else left=true;
						else if((yy>-yx+sH) && (yy<yx+sH-KONTROLS_SIZE) )
							if(!right) {down=false; left=false; right=true; up=false;} else right=true;
					}
					else if(yx>sW-KONTROLS_SIZE && yx<sW)
					{	
						if((yy<-(yx-(sW-KONTROLS_SIZE))+sH) && (yy<yx-(sW-KONTROLS_SIZE)+sH-200) )
							if(!up) {down=false; left=false; right=false; up=true;} else up=true;
						else if((yy>-(yx-(sW-KONTROLS_SIZE))+sH) && (yy>yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!down) {down=true; left=false; right=false; up=false;} else down=true;
						else if((yy<-(yx-(sW-KONTROLS_SIZE))+sH) && (yy>yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!left) {down=false; left=true; right=false; up=false;} else left=true;
						else if((yy>-(yx-(sW-KONTROLS_SIZE))+sH) && (yy<yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!right) {down=false; left=false; right=true; up=false;} else right=true;
					}
				}
			}
			} break;
			case MotionEvent.ACTION_UP: {
				left=false; right=false; up=false; down=false;
			} break;
			case MotionEvent.ACTION_MOVE: {
				if(yy>sH-KONTROLS_SIZE && yy<sH)
				{
					if(yx<KONTROLS_SIZE && yx>0)
					{	
						if((yy<-yx+sH) && (yy<yx+sH-KONTROLS_SIZE) )
							if(!up) {down=false; left=false; right=false; up=true;} else up=true;
						else if((yy>-yx+sH) && (yy>yx+sH-KONTROLS_SIZE) )
							if(!down) {down=true; left=false; right=false; up=false;} else down=true;
						else if((yy<-yx+sH) && (yy>yx+sH-KONTROLS_SIZE) )
							if(!left) {down=false; left=true; right=false; up=false;} else left=true;
						else if((yy>-yx+sH) && (yy<yx+sH-KONTROLS_SIZE) )
							if(!right) {down=false; left=false; right=true; up=false;} else right=true;
					}
					else if(yx>sW-KONTROLS_SIZE && yx<sW)
					{	
						if((yy<-(yx-(sW-KONTROLS_SIZE))+sH) && (yy<yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!up) {down=false; left=false; right=false; up=true;} else up=true;
						else if((yy>-(yx-(sW-KONTROLS_SIZE))+sH) && (yy>yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!down) {down=true; left=false; right=false; up=false;} else down=true;
						else if((yy<-(yx-(sW-KONTROLS_SIZE))+sH) && (yy>yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!left) {down=false; left=true; right=false; up=false;} else left=true;
						else if((yy>-(yx-(sW-KONTROLS_SIZE))+sH) && (yy<yx-(sW-KONTROLS_SIZE)+sH-KONTROLS_SIZE) )
							if(!right) {down=false; left=false; right=true; up=false;} else right=true;
					}
				}
			} break;
		}
		return true;
	}

	public boolean onKeyDown(int KeyCode, KeyEvent evt)
	{
		if(evt.getAction()==KeyEvent.ACTION_DOWN)
			switch(KeyCode) {
				case KeyEvent.KEYCODE_BACK:
				{
					int rootViewId=-1;
					if(findViewById(android.R.id.content)!=null)
						rootViewId=((ViewGroup) findViewById(android.R.id.content)).getChildAt(0).getId();
					if (rootViewId == R.id.mainMenuLayout) {
						interstitialAd = new InterstitialAd(this);
						interstitialAd.setAdUnitId(INTERSTITIAL);
						interstitialAd.setAdListener(new AdListener() {
							public void onAdClosed() {super.onAdClosed(); CoinCatcher.this.finish();}
							public void onAdFailedToLoad(int i) {super.onAdFailedToLoad(i);CoinCatcher.this.finish();}
							public void onAdLeftApplication() {super.onAdLeftApplication(); CoinCatcher.this.finish();}
							public void onAdLoaded() {super.onAdLoaded(); interstitialAd.show();}
						});
						interstitialAd.loadAd(CoinCatcher.adRequest);
					} else if (playing) {
						if (pause) {
							playing = false;
							setContentView(R.layout.activity_coin_catcher); /*drop to menu*/
							loadAds();
						} else if (gameover || timeup) {
							playing = false;
							setContentView(R.layout.activity_coin_catcher); /*drop to menu*/
							loadAds();
						} else {
							pause = true;
							Drawing.paused = true;
							Drawing.qredraw();
						}
					} else if (rootViewId == R.id.gameSelectLayout) {
						setContentView(R.layout.activity_coin_catcher);
						loadAds();
					} else {
						setContentView(R.layout.activity_coin_catcher);
						loadAds();
					}
				} break;
				/*case KeyEvent.KEYCODE_DPAD_LEFT:
				{ left=true; } break;
				case KeyEvent.KEYCODE_DPAD_UP:
				{ up=true; } break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
				{ down=true; } break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
				{ right=true; } break;*/
			}
		else if(evt.getAction()==KeyEvent.ACTION_UP)
			{left=false; right=false; up=false; down=false;}
			/*switch(KeyCode)
			{
				case KeyEvent.KEYCODE_DPAD_LEFT:
				{ left=false; } break;
				case KeyEvent.KEYCODE_DPAD_UP:
				{ up=false; } break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
				{ down=false; } break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
				{ right=false; } break; 
			}*/
		return true;
	}

}
