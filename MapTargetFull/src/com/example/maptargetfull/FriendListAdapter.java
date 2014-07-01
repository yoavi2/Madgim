package com.example.maptargetfull;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {

	private final Context context;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

	public FriendListAdapter(Context context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_friend, parent, false);
		    if(mSelection.get(position) != null){
		    	rowView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
		    }
		TextView firstName = (TextView) rowView.findViewById(R.id.FirstName);
		TextView lastName = (TextView) rowView.findViewById(R.id.LastName);
		TextView location = (TextView) rowView.findViewById(R.id.position);
		ImageView img     = (ImageView) rowView.findViewById(R.id.type_pict);

		Friend currFriend =  GlobalParams.getInstance().getFriends().get(position);
		
		firstName.setText(currFriend.getName());
		lastName.setText("last name");
		img.setImageResource(R.drawable.friend);
		location.setText(Integer.toString(currFriend.getWidth()) + " ; " + Integer.toString(currFriend.getHeight()));
		return rowView;    
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return GlobalParams.getInstance().getFriends().size();
		
	}
	@Override
	public Object getItem(int position) {
		return GlobalParams.getInstance().getFriends().get(position).getName();
		// TODO Auto-generated method stub
		//return position;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void setItemSelected(int position){
		this.mSelection.put(position, true);
		notifyDataSetChanged();
	}
	public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }
	public HashMap<Integer, Boolean> getSelection(){ 
		return this.mSelection;
	}
	public void clearSelection(){
		this.mSelection.clear();
	}

}
