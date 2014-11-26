package com.example.maptargetfull;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DetailsFragment extends DialogFragment {

	public static String TAG = "Details";
	public View rootView;

	public interface onclickListener {
		public void onRefreshSelected();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

//	@Override
//	 public Dialog onCreateDialog(Bundle savedInstanceState) {
//		Dialog m_dialog = new Dialog(this.getActivity(), R.style.Dialog_No_Border);
//	 	LayoutInflater m_inflater = LayoutInflater.from(this.getActivity());
//	 	View m_view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_details, null, false);
//	        
//	 	// SET ALL THE VIEWS
//	    m_dialog.setTitle(null);
//	    m_dialog.setContentView(m_view);
//	    m_dialog.show();
//	    
//	    return m_dialog;
//	 }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		rootView = inflater.inflate(R.layout.fragment_details, container, false);

		((Button) rootView.findViewById(R.id.btn_cancel))
		.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		getDialog().setTitle("המטלות של " + GlobalParams.getInstance().currMarkerName);
		return rootView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		((MainActivity) getActivity()).lockDrawer(false);
	}
	
	@Override
	public void onResume() {
		((MainActivity) getActivity()).lockDrawer(true);
		setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
		super.onResume();
	}
}