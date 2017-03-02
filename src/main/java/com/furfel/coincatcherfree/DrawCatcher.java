package com.furfel.coincatcherfree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class DrawCatcher extends View {

	public Context context;
	
	public int dpx,dpy,dfx,dfy,px,py,fx,fy;
	public int margin;
	public int pd;
	int score;
	
	String supermode="";
	
	public int SpriteSize=64;
	public int BgSize=480;
	public int KontrolsSize=250;
	
	int sW,sH;
	
	public int animationframe=0;
	
	public byte CoinMap[][] = new byte[10][10];
	
	Bitmap playermove[][] = new Bitmap[5][5];
	Bitmap playerbmp;
	Bitmap fire,protect;
	Bitmap troll[] = new Bitmap[5];
	Bitmap trollfreeze;
	Bitmap coin,coin100,candy,candy2;
	Bitmap mapbg;
	Bitmap kontrols;
	
	float TextSize=32.0f, GOSize=48.0f;
	
	public boolean gameover=false, supper=false, supperfreeze=false, timeup=false, timetrial=false, paused=false; int timeleft=0;
	
	Paint ScorePaint = new Paint();
	Paint RedPaint = new Paint();
	Paint GOPaint = new Paint();
	Paint SuperPaint = new Paint();
	
	public DrawCatcher(Context context, int sW, int sH) {
		super(context);
		this.context=context;
		this.sH=sH; this.sW=sW;
		if(sW>sH)
		{SpriteSize=Math.round(sH/10); margin=Math.round((sW-sH)/2); BgSize=sH;}
		else {SpriteSize=Math.round(sW/10); margin=Math.round((sH-sW)/2); BgSize=sW;}
		//spriteLoader();
		setBackgroundResource(R.drawable.bgrpt);
		GOSize=sH/10;
		TextSize=(2.0f/30.0f)*sH;
		ScorePaint.setColor(Color.BLACK);
		ScorePaint.setTextSize(TextSize);
		ScorePaint.setAntiAlias(true);
		RedPaint.setColor(Color.RED);
		GOPaint.setColor(Color.BLACK);
		GOPaint.setTextSize(GOSize);
		GOPaint.setTextAlign(Align.CENTER);
		GOPaint.setAntiAlias(true);
		SuperPaint.setColor(Color.BLACK);
		SuperPaint.setTextSize(TextSize);
		SuperPaint.setTextAlign(Align.RIGHT);
		SuperPaint.setAntiAlias(true);
	}
	
	public void spriteLoader()
	{
		playerbmp=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player), SpriteSize, SpriteSize, true);
		playermove[1][0]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerd1), SpriteSize, SpriteSize, true);
		playermove[1][1]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerd2), SpriteSize, SpriteSize, true);
		playermove[1][2]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerd3), SpriteSize, SpriteSize, true);
		playermove[1][3]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerd4), SpriteSize, SpriteSize, true);
		playermove[1][4]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerd5), SpriteSize, SpriteSize, true);
		
		playermove[0][0]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playeru1), SpriteSize, SpriteSize, true);
		playermove[0][1]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playeru2), SpriteSize, SpriteSize, true);
		playermove[0][2]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playeru3), SpriteSize, SpriteSize, true);
		playermove[0][3]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playeru4), SpriteSize, SpriteSize, true);
		playermove[0][4]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playeru5), SpriteSize, SpriteSize, true);
		
		playermove[2][0]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerl1), SpriteSize, SpriteSize, true);
		playermove[2][1]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerl2), SpriteSize, SpriteSize, true);
		playermove[2][2]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerl3), SpriteSize, SpriteSize, true);
		playermove[2][3]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerl4), SpriteSize, SpriteSize, true);
		playermove[2][4]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerl5), SpriteSize, SpriteSize, true);
		
		playermove[3][0]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerr1), SpriteSize, SpriteSize, true);
		playermove[3][1]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerr2), SpriteSize, SpriteSize, true);
		playermove[3][2]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerr3), SpriteSize, SpriteSize, true);
		playermove[3][3]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerr4), SpriteSize, SpriteSize, true);
		playermove[3][4]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerr5), SpriteSize, SpriteSize, true);
		
		playermove[4][0]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerf1), SpriteSize, SpriteSize, true);
		playermove[4][1]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerf2), SpriteSize, SpriteSize, true);
		playermove[4][2]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerf3), SpriteSize, SpriteSize, true);
		playermove[4][3]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerf4), SpriteSize, SpriteSize, true);
		playermove[4][4]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.playerf5), SpriteSize, SpriteSize, true);
		
		fire=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.trollight), SpriteSize*3, SpriteSize*3, true);
		protect=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.protect), SpriteSize, SpriteSize, true);
		
		troll[0]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.troll1), SpriteSize, SpriteSize, true);
		troll[1]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.troll2), SpriteSize, SpriteSize, true);
		troll[2]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.troll3), SpriteSize, SpriteSize, true);
		troll[3]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.troll4), SpriteSize, SpriteSize, true);
		troll[4]=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.troll5), SpriteSize, SpriteSize, true);
		trollfreeze=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.trollfreeze), SpriteSize, SpriteSize, true);
		
		coin=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coin1), SpriteSize, SpriteSize, true);
		coin100=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coin100), SpriteSize, SpriteSize, true);
		candy=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.candy), SpriteSize, SpriteSize, true);
		candy2=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.candy2), SpriteSize, SpriteSize, true);
		
		mapbg=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mepz), BgSize, BgSize, true);
		kontrols=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.kontrols), KontrolsSize, KontrolsSize, true);
		
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(mapbg, margin, 0, null);
		canvas.drawBitmap(fire, (dfx/10.0f)*SpriteSize+margin-SpriteSize, (dfy/10.0f)*SpriteSize-SpriteSize, null);
		for(int i=0;i<10;i++)
			for(int j=0;j<10;j++)
				if(CoinMap[i][j]==2) canvas.drawBitmap(coin100, i*SpriteSize+margin, j*SpriteSize, null); else if(CoinMap[i][j]==1) canvas.drawBitmap(coin, i*SpriteSize+margin, j*SpriteSize, null); else if(CoinMap[i][j]==3) canvas.drawBitmap(candy, i*SpriteSize+margin, j*SpriteSize, null); else if(CoinMap[i][j]==4) canvas.drawBitmap(candy2, i*SpriteSize+margin, j*SpriteSize, null);
		if(pd>0)
			canvas.drawBitmap(playermove[pd-1][animationframe], (dpx/5.0f)*SpriteSize+margin, (dpy/5.0f)*SpriteSize, null);
		else canvas.drawBitmap(playerbmp, (dpx/5.0f)*SpriteSize+margin, (dpy/5.0f)*SpriteSize, null);
		if(supper && !supperfreeze) canvas.drawBitmap(protect, (dpx/5.0f)*SpriteSize+margin, (dpy/5.0f)*SpriteSize, null);
		//canvas.drawCircle((px/5.0f)*SpriteSize+(SpriteSize/2.0f)+margin, (py/5.0f)*SpriteSize+(SpriteSize/2.0f), 5.0f, RedPaint);
		//canvas.drawBitmap(fire, (dfx/10.0f)*SpriteSize+margin,  (dfy/10.0f)*SpriteSize,null);
		if(supper && supperfreeze) canvas.drawBitmap(trollfreeze, (dfx/10.0f)*SpriteSize+margin, (dfy/10.0f)*SpriteSize, null); else canvas.drawBitmap(troll[animationframe], (dfx/10.0f)*SpriteSize+margin,  (dfy/10.0f)*SpriteSize, null);
		canvas.drawText("Score: "+Integer.toString(score), 2.0f, TextSize+2.0f, ScorePaint);
		canvas.drawText(supermode, sW, TextSize, SuperPaint);
		if(timetrial) canvas.drawText("Time left:"+Integer.toString(timeleft), 2.0f, TextSize*2.0f+4.0f, ScorePaint);
		canvas.drawBitmap(kontrols, 0, sH-KontrolsSize, null);
		canvas.drawBitmap(kontrols, sW-KontrolsSize, sH-KontrolsSize, null);
		if(gameover) canvas.drawText("Game Over", sW/2, (sH/2)-(GOSize/2), GOPaint);
		if(timeup) canvas.drawText("Time is up.",sW/2, (sH/2)-(GOSize/2), GOPaint);
		if(paused) {canvas.drawText("Paused",sW/2,(sH/2)-(GOSize/2),GOPaint);}
	}
	
	private Handler hndl=new Handler()
	{
		public void handleMessage(Message msg)
		{
			invalidate();
		}
	};
	
	public void updanim()
	{
		pd=0;
		if(animationframe>=4) animationframe=0; else animationframe++;
		if(!gameover){if(dpx<px) {dpx++; pd=4;} if(dpx>px) {dpx--; pd=3;} if(dpy<py) {dpy++; pd=2;} if(dpy>py) {dpy--; pd=1;}} else pd=5;
		if(!gameover){if(dfx<fx) dfx++; if(dfx>fx) dfx--; if(dfy<fy) dfy++; if(dfy>fy) dfy--;}
	}
	
	public void redraw(int px,int py,int fx,int fy, int score)
	{
		this.px=px*5; this.py=py*5; this.fx=fx*10; this.fy=fy*10; this.score=score;
		updanim();
		hndl.sendEmptyMessage(0);
	}
	
	public void qredraw()
	{
		hndl.sendEmptyMessage(0);
	}

}
