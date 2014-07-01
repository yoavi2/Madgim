package com.example.maptargetfull;
//package com.example.tabapp;
//
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//
//
//public class TabsPagerAdapter extends FragmentPagerAdapter {
//
//	public TabsPagerAdapter(FragmentManager fm) {
//		super(fm);
//		}
//
//	@Override
//	public int getItemPosition(Object object){
//		SecondFragment f = (SecondFragment) object;
//		   if (f != null) {
//		      f.update();
//		   }
//		  return super.getItemPosition(object);
//
//	}
//    @Override
//	public Fragment getItem(int index) {
//		switch (index) {
//		case 0:
//			
//			Fragment f1 =  new FirstFragment();
//			
//	//		notifyDataSetChanged();
//	//		notifyDataSetChanged();
//			if(GlobalParams.getInstance().getView()!= null){
//				
//		        GlobalParams.getInstance().getView().setWillNotDraw(false);
//				GlobalParams.getInstance().getView().postInvalidate();
//			}
//
//			return f1;
//		case 1:
//			Fragment f = new SecondFragment();
//		//	GlobalParams.getInstance().setProgress(f);
//
//	//		notifyDataSetChanged();
//
//			return f;
//			
//		case 2:
//			
//		}
//		return null;
//    }
//	
//	@Override
//	public int getCount() {
//	// get item count - equal to number of tabs
//		return 2;
//	}
//
//}
