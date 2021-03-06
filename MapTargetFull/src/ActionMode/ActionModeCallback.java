package ActionMode;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.maptargetfull.DetailsFragment;
import com.example.maptargetfull.EditTargetDialog;
import com.example.maptargetfull.GlobalParams;
import com.example.maptargetfull.OfflineMapFragment;
import com.example.maptargetfull.R;
import com.example.maptargetfull.SecondFragment;
import com.example.maptargetfull.SettingsFragment;

public class ActionModeCallback implements ActionMode.Callback {


    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    	mode.setTitle(GlobalParams.getInstance().currMarkerName);
        return true;
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.item_delete:
	        	GlobalParams.getCurrMap().deletePoint(GlobalParams.getInstance().currMarkerName);
	        	GlobalParams.getCurrMap().removeMarkerOnLocationOffline(GlobalParams.getInstance().currMarkerName);
	        	GlobalParams.getCurrMap().removeHaloPulse("beacon");
				GlobalParams.getInstance().currMarkerName = null;
				GlobalParams.getInstance().frgOfflineMap.RemoveContextualMenu();
				
				// Reload current fragment
				Fragment frg = null;
				frg = GlobalParams.getFragment().findFragmentByTag("list_frame");
				final FragmentTransaction ft = GlobalParams.getFragment().beginTransaction();
				ft.detach(frg);
				ft.attach(frg);
				ft.commit();
	        	
	            return true;
	        case R.id.item_info:
	        	long rowId = GlobalParams.getInstance().getRowidByName(GlobalParams.getInstance().currMarkerName);
	        	EditTargetDialog editTargetDialog = EditTargetDialog
						.newInstance(GlobalParams.getInstance().currMarkerName, rowId, OfflineMapFragment.TAG);
				editTargetDialog.show(GlobalParams.getFragment(), EditTargetDialog.TAG);
////	        	Toast.makeText(GlobalParams.getInstance().currActivity, GlobalParams.getInstance().currMarkerName, Toast.LENGTH_LONG).show();
	        	
	            return true;
	            
	        case R.id.item_list:
	        	DetailsFragment details = new DetailsFragment();
	        	details.show(GlobalParams.getFragment(), "details");
	        default:
	            return false;
	    }
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        GlobalParams.getInstance().mActionMode = null;
		if (!GlobalParams.getInstance().Exist) {
	    	GlobalParams.getCurrMap().removeHaloPulse("beacon");
			GlobalParams.getInstance().currMarkerName = null;
		}
    }
	
}
