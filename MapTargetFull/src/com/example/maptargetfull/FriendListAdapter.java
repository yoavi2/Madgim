package com.example.maptargetfull;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maptargetfull.PointsDBAccess.Point;

public class FriendListAdapter extends BaseAdapter {

	private final Context context;
	private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

	public FriendListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_friend, parent, false);
			
			holder = new ViewHolder();
			holder.firstName = (TextView) convertView.findViewById(R.id.FirstName);
			holder.lastName = (TextView) convertView.findViewById(R.id.LastName);
			holder.location = (TextView) convertView.findViewById(R.id.position);
			holder.img = (ImageView) convertView.findViewById(R.id.type_pict);
			holder.img.setTag(this.getItemId(position));
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if (mSelection.get(position) != null) {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.purple_dark));
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		if (GlobalParams.getInstance().getFriend()) {
			
			Point currFriend = GlobalParams.getInstance().getSpecificPoint(position);
			
			if (currFriend != null) {
				holder.firstName.setText(currFriend.first_name);
				holder.lastName.setText("last name");
				
				if (currFriend.pointType == 1) {
					holder.img.setBackgroundResource(R.color.friend);
				} else {
					holder.img.setBackgroundResource(R.color.red);
				}
				
				if (((Long) holder.img.getTag()) != currFriend.rowID || holder.img.getDrawable() == null) {
					GlobalParams.loadBitmap(currFriend.rowID, holder.img, this.context);
					
					if (holder.img.getDrawable() == null) {
						holder.img.setTag(currFriend.rowID);
					}
				}
				
			    holder.location.setText(Double.toString(currFriend.langitude)
						+ " ; " + Double.toString(currFriend.longitude));
			}
		}
		
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return GlobalParams.getInstance().getPoints().size();

	}

	@Override
	public Object getItem(int position) {
		return GlobalParams.getInstance().getSpecificPoint(position).first_name;
		// TODO Auto-generated method stub
		// return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return GlobalParams.getInstance().getSpecificPoint(position).rowID;
	}

	public void setItemSelected(int position) {
		this.mSelection.put(position, true);
		notifyDataSetChanged();
	}

	public void removeSelection(int position) {
		mSelection.remove(position);
		notifyDataSetChanged();
	}

	public HashMap<Integer, Boolean> getSelection() {
		return this.mSelection;
	}

	public void clearSelection() {
		this.mSelection.clear();
	}

}

class ViewHolder {
	TextView firstName;
	TextView lastName;
	TextView location;
	ImageView img;
}