package com.example.easysound;
import android.content.Context;
import android.content.SharedPreferences;
public class PrefManager {
private SharedPreferences s;
public PrefManager(Context c){s=c.getSharedPreferences("easy_sound_pref",0);}
public void setEnabled(boolean v){s.edit().putBoolean("en",v).apply();}
public boolean isEnabled(){return s.getBoolean("en",false);}
public void setSize(int v){if(v<56)v=56;if(v>200)v=200;s.edit().putInt("sz",v).apply();}
public int getSize(){return s.getInt("sz",56);}
public void setOpacity(float v){if(v<0.2f)v=0.2f;if(v>1f)v=1f;s.edit().putFloat("op",v).apply();}
public float getOpacity(){return s.getFloat("op",1f);}
public void setDistance(int v){if(v<0)v=0;if(v>50)v=50;s.edit().putInt("di",v).apply();}
public int getDistance(){return s.getInt("di",10);}
public void setVertical(boolean v){s.edit().putBoolean("ve",v).apply();}
public boolean isVertical(){return s.getBoolean("ve",false);}
public void setX(int v){s.edit().putInt("px",v).apply();}
public int getX(){return s.getInt("px",20);}
public void setY(int v){s.edit().putInt("py",v).apply();}
public int getY(){return s.getInt("py",300);}
public void setSpeed(int v){if(v<1)v=1;if(v>6)v=6;s.edit().putInt("sp",v).apply();}
public int getSpeed(){return s.getInt("sp",1);}
}