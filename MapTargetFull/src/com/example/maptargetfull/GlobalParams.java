package com.example.maptargetfull;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Fragment;
import android.app.ProgressDialog;


public class GlobalParams {
	private boolean ShowEenemy;
	private boolean ShowFriend;
	private DrawSample mydraw;
	public PointsDBAccess PointsDBaccess;
	public int selectedItem;
    public  ProgressDialog pdialog;
    public String syncFinished = "com.example.maptargetfull.sync_finished"; 

	public SecondFragment listFriends;
    private Fragment frag;
	private ArrayList<Friend> MyFriends = new ArrayList<Friend>();
	public void setProgress(Fragment p){
		this.frag = p;
	}
	public Fragment getf(){
		return this.frag;
	}
	public void setEnemy(boolean show) {
		this.ShowEenemy = show;
	}
	public void setFriend(boolean show){
		this.ShowFriend = show;
	}
	public void clearList(){
		this.MyFriends.clear();
	}
	public boolean getEenemy(){
		return this.ShowEenemy;
	}
	public boolean getFriend(){
		return this.ShowFriend;
	}
	GlobalParams(boolean a,boolean b){
		this.ShowEenemy = a;
		this.ShowFriend = b;
	}
	public void setview(DrawSample view){
		this.mydraw = view;
	}
	public DrawSample getView(){
		return this.mydraw;
	}
	public void addFriend(Friend newFriend)
	{
		MyFriends.add(newFriend);
	}
	public Friend getSpecificFriend(int position){
		return this.MyFriends.get(position);
	}
	public ArrayList<Friend> getFriends(){
		return this.MyFriends;
	}
	public void DeleteFriend(String name){
		for (Friend iterable : MyFriends) {
			if(iterable.getName().equals(name)){
				MyFriends.remove(iterable);
				break;
			}
		}
	}
	public void deleteFriend(int position){
		this.MyFriends.remove(position);
		}
	public void deleteFriends(Collection<Friend> collection){
		this.MyFriends.removeAll(collection);
	}
	
	static final private GlobalParams INSTANCE = new GlobalParams(true,true);
    static public GlobalParams getInstance() { return INSTANCE; }
}
