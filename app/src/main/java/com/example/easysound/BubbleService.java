package com.example.easysound;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
public class BubbleService extends Service {
private WindowManager wm;
private LinearLayout container;
private TextView bPlus;
private TextView bMinus;
private WindowManager.LayoutParams params;
private PrefManager pref;
private AudioManager audio;
private float downRawX;
private float downRawY;
private int downX;
private int downY;
private boolean isMove;
private Handler handler=new Handler();
private boolean pressingPlus=false;
private boolean pressingMinus=false;
private boolean hasFiredPlus=false;
private boolean hasFiredMinus=false;
private static final int NOTIF_ID=101;
private static final String CH_ID="easy_sound_bubble";
private int getDelay(){
int sp=pref.getSpeed();
if(sp==1)return 120;
if(sp==2)return 75;
if(sp==3)return 45;
if(sp==4)return 25;
if(sp==5)return 15;
return 8;
}
private Runnable repeatPlus=new Runnable(){
public void run(){
if(pressingPlus&&!isMove){
audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
hasFiredPlus=true;
handler.postDelayed(this,getDelay());
}
}
};
private Runnable repeatMinus=new Runnable(){
public void run(){
if(pressingMinus&&!isMove){
audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
hasFiredMinus=true;
handler.postDelayed(this,getDelay());
}
}
};
@Override
public void onCreate(){
super.onCreate();
pref=new PrefManager(this);
audio=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
wm=(WindowManager)getSystemService(WINDOW_SERVICE);
createChannel();
startForeground(NOTIF_ID, buildNotif());
build();
}
private void createChannel(){
if(Build.VERSION.SDK_INT>=26){
NotificationChannel ch=new NotificationChannel(CH_ID,"Easy Sound",NotificationManager.IMPORTANCE_LOW);
NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
if(nm!=null)nm.createNotificationChannel(ch);
}
}
private Notification buildNotif(){
if(Build.VERSION.SDK_INT>=26){
Notification.Builder b=new Notification.Builder(this,CH_ID);
b.setContentTitle("Easy Sound");
b.setContentText("Bubble active");
b.setSmallIcon(android.R.drawable.btn_star_big_on);
b.setOngoing(true);
return b.build();
}else{
return new Notification();
}
}
private void build(){
if(container!=null){
try{wm.removeView(container);}catch(Exception e){}
container=null;
}
container=new LinearLayout(this);
boolean vertical=pref.isVertical();
container.setOrientation(vertical?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL);
bPlus=new TextView(this);
bPlus.setText("+");
bPlus.setTypeface(null,Typeface.BOLD);
bPlus.setGravity(17);
bMinus=new TextView(this);
bMinus.setText("−");
bMinus.setTypeface(null,Typeface.BOLD);
bMinus.setGravity(17);
updateLook();
int d=(int)(pref.getDistance()*getResources().getDisplayMetrics().density);
int s=pref.getSize();
LinearLayout.LayoutParams p1;
LinearLayout.LayoutParams p2;
if(vertical){
p1=new LinearLayout.LayoutParams(s,s);
p1.setMargins(0,0,0,d);
p2=new LinearLayout.LayoutParams(s,s);
}else{
p1=new LinearLayout.LayoutParams(s,s);
p1.setMargins(0,0,d,0);
p2=new LinearLayout.LayoutParams(s,s);
}
container.removeAllViews();
container.addView(bPlus,p1);
container.addView(bMinus,p2);
int w=vertical?s:s*2+d;
int h=vertical?s*2+d:s;
params=new WindowManager.LayoutParams(w,h,Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
params.gravity=Gravity.TOP|Gravity.LEFT;
params.x=pref.getX();
params.y=pref.getY();
View.OnTouchListener touchPlus=new View.OnTouchListener(){
public boolean onTouch(View v,MotionEvent ev){
switch(ev.getAction()){
case MotionEvent.ACTION_DOWN:
downRawX=ev.getRawX();
downRawY=ev.getRawY();
downX=params.x;
downY=params.y;
isMove=false;
pressingPlus=true;
hasFiredPlus=false;
handler.removeCallbacks(repeatPlus);
handler.postDelayed(repeatPlus,400);
return true;
case MotionEvent.ACTION_MOVE:
int rx=(int)(ev.getRawX()-downRawX);
int ry=(int)(ev.getRawY()-downRawY);
if(Math.abs(rx)>8||Math.abs(ry)>8){
isMove=true;
pressingPlus=false;
hasFiredPlus=true;
handler.removeCallbacks(repeatPlus);
params.x=downX+rx;
params.y=downY+ry;
try{wm.updateViewLayout(container,params);}catch(Exception e){}
}
return true;
case MotionEvent.ACTION_UP:
handler.removeCallbacks(repeatPlus);
if(isMove){
pref.setX(params.x);
pref.setY(params.y);
pressingPlus=false;
return true;
}
if(!hasFiredPlus){
audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
}
pressingPlus=false;
return true;
case MotionEvent.ACTION_CANCEL:
pressingPlus=false;
handler.removeCallbacks(repeatPlus);
return true;
}
return false;
}
};
View.OnTouchListener touchMinus=new View.OnTouchListener(){
public boolean onTouch(View v,MotionEvent ev){
switch(ev.getAction()){
case MotionEvent.ACTION_DOWN:
downRawX=ev.getRawX();
downRawY=ev.getRawY();
downX=params.x;
downY=params.y;
isMove=false;
pressingMinus=true;
hasFiredMinus=false;
handler.removeCallbacks(repeatMinus);
handler.postDelayed(repeatMinus,400);
return true;
case MotionEvent.ACTION_MOVE:
int rx=(int)(ev.getRawX()-downRawX);
int ry=(int)(ev.getRawY()-downRawY);
if(Math.abs(rx)>8||Math.abs(ry)>8){
isMove=true;
pressingMinus=false;
hasFiredMinus=true;
handler.removeCallbacks(repeatMinus);
params.x=downX+rx;
params.y=downY+ry;
try{wm.updateViewLayout(container,params);}catch(Exception e){}
}
return true;
case MotionEvent.ACTION_UP:
handler.removeCallbacks(repeatMinus);
if(isMove){
pref.setX(params.x);
pref.setY(params.y);
pressingMinus=false;
return true;
}
if(!hasFiredMinus){
audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
}
pressingMinus=false;
return true;
case MotionEvent.ACTION_CANCEL:
pressingMinus=false;
handler.removeCallbacks(repeatMinus);
return true;
}
return false;
}
};
bPlus.setOnTouchListener(touchPlus);
bMinus.setOnTouchListener(touchMinus);
try{wm.addView(container,params);}catch(Exception e){}
}
private void updateLook(){
int s=pref.getSize();
float o=pref.getOpacity();
android.graphics.drawable.GradientDrawable g=new android.graphics.drawable.GradientDrawable();
g.setShape(android.graphics.drawable.GradientDrawable.OVAL);
g.setColor(0xFFFFFFFF);
g.setStroke((int)(s*0.05f),0xFFE2E8F0);
android.graphics.drawable.GradientDrawable g2=new android.graphics.drawable.GradientDrawable();
g2.setShape(android.graphics.drawable.GradientDrawable.OVAL);
g2.setColor(0xFFFFFFFF);
g2.setStroke((int)(s*0.05f),0xFFE2E8F0);
bPlus.setBackgroundDrawable(g);
bMinus.setBackgroundDrawable(g2);
bPlus.setTextColor(0xFF000000);
bMinus.setTextColor(0xFF000000);
bPlus.setWidth(s);
bPlus.setHeight(s);
bMinus.setWidth(s);
bMinus.setHeight(s);
bPlus.setTextSize(0,s*0.55f);
bMinus.setTextSize(0,s*0.65f);
bPlus.setAlpha(o);
bMinus.setAlpha(o);
}
@Override
public int onStartCommand(Intent intent,int flags,int startId){
if(intent!=null){
String a=intent.getAction();
if("STOP".equals(a)){
stopForeground(true);
stopSelf();
return START_NOT_STICKY;
}
if("UPDATE".equals(a)){
if(params!=null){
pref.setX(params.x);
pref.setY(params.y);
}
build();
return START_STICKY;
}
}
if(container==null){
try{build();}catch(Exception e){}
}
return START_STICKY;
}
@Override
public void onDestroy(){
super.onDestroy();
pressingPlus=false;
pressingMinus=false;
handler.removeCallbacks(repeatPlus);
handler.removeCallbacks(repeatMinus);
if(container!=null){
try{wm.removeView(container);}catch(Exception e){}
container=null;
}
}
@Override
public IBinder onBind(Intent i){return null;}
}