package com.example.easysound;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;
public class MainActivity extends Activity {
private LinearLayout linear1;
private TextView textview1;
private TextView textSpeed;
private Switch switchEnable;
private SeekBar seekSize;
private SeekBar seekOpacity;
private SeekBar seekDistance;
private SeekBar seekSpeed;
private Switch switchOrientation;
private Button btnPermission;
private Button btnTile;
private PrefManager pref;
@Override
protected void onCreate(Bundle savedInstanceState){
super.onCreate(savedInstanceState);
setContentView(R.layout.main);
linear1=findViewById(R.id.linear1);
textview1=findViewById(R.id.textview1);
textSpeed=findViewById(R.id.textSpeed);
switchEnable=findViewById(R.id.switchEnable);
seekSize=findViewById(R.id.seekSize);
seekOpacity=findViewById(R.id.seekOpacity);
seekDistance=findViewById(R.id.seekDistance);
seekSpeed=findViewById(R.id.seekSpeed);
switchOrientation=findViewById(R.id.switchOrientation);
btnPermission=findViewById(R.id.btnPermission);
btnTile=findViewById(R.id.btnTile);
pref=new PrefManager(this);
seekSize.setMax(144);
seekOpacity.setMax(100);
seekDistance.setMax(100);
seekSpeed.setMax(5);
seekSize.setProgress(pref.getSize()-56);
seekOpacity.setProgress((int)(pref.getOpacity()*100));
seekDistance.setProgress(pref.getDistance());
seekSpeed.setProgress(pref.getSpeed()-1);
switchOrientation.setChecked(pref.isVertical());
switchEnable.setChecked(pref.isEnabled());
textSpeed.setText("Hold Speed: "+pref.getSpeed()+"x");
switchEnable.setOnCheckedChangeListener((b,c)->{
pref.setEnabled(c);
if(c){
if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){
Toast.makeText(this,"Need Overlay Permission",0).show();
switchEnable.setChecked(false);
return;
}
startService(new Intent(this,BubbleService.class));
}else{
stopService(new Intent(this,BubbleService.class));
}
});
btnPermission.setOnClickListener(v->{
Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));
startActivityForResult(intent,101);
});
btnTile.setOnClickListener(v->{
Toast.makeText(this,"Pull down Quick Settings -> Edit -> Add Volume Tile",1).show();
});
SeekBar.OnSeekBarChangeListener upd=new SeekBar.OnSeekBarChangeListener(){
public void onProgressChanged(SeekBar s,int p,boolean f){
if(s==seekSize)pref.setSize(p+56);
if(s==seekOpacity)pref.setOpacity(p/100f);
if(s==seekDistance)pref.setDistance(p);
if(s==seekSpeed){
int sp=p+1;
pref.setSpeed(sp);
textSpeed.setText("Hold Speed: "+sp+"x");
}
}
public void onStartTrackingTouch(SeekBar s){}
public void onStopTrackingTouch(SeekBar s){
Intent i=new Intent(MainActivity.this,BubbleService.class);
i.setAction("UPDATE");
startService(i);
}
};
seekSize.setOnSeekBarChangeListener(upd);
seekOpacity.setOnSeekBarChangeListener(upd);
seekDistance.setOnSeekBarChangeListener(upd);
seekSpeed.setOnSeekBarChangeListener(upd);
switchOrientation.setOnCheckedChangeListener((b,c)->{
pref.setVertical(c);
Intent i=new Intent(this,BubbleService.class);
i.setAction("UPDATE");
startService(i);
});
}
}