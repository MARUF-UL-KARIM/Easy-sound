package com.example.easysound;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
public class VolumeTileService extends TileService {
@Override
public void onStartListening(){
super.onStartListening();
try{
PrefManager pref=new PrefManager(getApplicationContext());
Tile t=getQsTile();
if(t!=null){
boolean en=pref.isEnabled();
t.setState(en?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
t.setIcon(Icon.createWithResource(this, android.R.drawable.ic_lock_silent_mode_off));
t.updateTile();
}
}catch(Exception e){}
}
@Override
public void onClick(){
try{
PrefManager pref=new PrefManager(getApplicationContext());
if(Build.VERSION.SDK_INT>=23 && !Settings.canDrawOverlays(getApplicationContext())){
Intent i=new Intent(getApplicationContext(), MainActivity.class);
i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
startActivityAndCollapse(i);
return;
}
boolean willEnable=!pref.isEnabled();
pref.setEnabled(willEnable);
Tile tile=getQsTile();
if(tile!=null){
tile.setState(willEnable?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
tile.setIcon(Icon.createWithResource(this, android.R.drawable.ic_lock_silent_mode_off));
tile.updateTile();
}
Intent svc=new Intent(getApplicationContext(), BubbleService.class);
svc.setAction(willEnable?"START":"STOP");
getApplicationContext().startForegroundService(svc);
}catch(Exception e){}
}
}