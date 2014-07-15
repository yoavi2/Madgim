package com.example.maptargetfull;

import com.example.maptargetfull.PointsDBAccess.Point;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditTargetDialog extends DialogFragment implements OnClickListener {

	public static String TAG = "edit_target_dialog";
	private static final int CAMERA_REQUEST = 1888;

	private String mCallerTag;
	private Point mPoint;
	private ImageView image;
	private Button btnSave;
	private Button btnEdit;
	private Button btnDel;
	private EditText etFirstName;
	private EditText etLastName;

	public EditTargetDialog() {
		// Empty constructor required for DialogFragment
	}

	public interface EditTargetListener {
		void deletePoint(long rowid);
		void savePoint(Point point);
		void imageUpdated(long rowid);
	}

	public static EditTargetDialog newInstance(String title, long rowid,
			String callertag) {
		EditTargetDialog frag = new EditTargetDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("callertag", callertag);
		args.putLong("rowid", rowid);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.customdialog, container);

		image = (ImageView) view.findViewById(R.id.image);
		btnSave = (Button) view.findViewById(R.id.saveButton);
		btnEdit = (Button) view.findViewById(R.id.EditButton);
		btnDel = (Button) view.findViewById(R.id.DeleteButton);
		etFirstName = (EditText) view.findViewById(R.id.FirstName);
		etLastName = (EditText) view.findViewById(R.id.LastName);

		etFirstName.setEnabled(false);
		etLastName.setEnabled(false);

		// Set the listener for the objects we want to handle the click event
		btnSave.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
		image.setOnClickListener(this);

		this.mCallerTag = getArguments().getString("callertag");
		
		if (this.mCallerTag == GoogleMapFragment.TAG)
		{
			this.mPoint = GoogleMapFragment.mGooglePoints.get(getArguments()
					.getLong("rowid"));
		}
		else
		{
			this.mPoint = GlobalParams.getInstance().getPointByRowid(getArguments()
					.getLong("rowid"));
		}

		GlobalParams.loadBitmap(mPoint.rowID, image, getActivity());
		
		etFirstName.setText(mPoint.first_name);
		etLastName.setText(mPoint.last_name);
		btnSave.setVisibility(View.GONE);

		String title = getArguments().getString("title", "Add Target");
		getDialog().setTitle(title);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image:
			v.startAnimation(AnimationUtils.loadAnimation(getActivity(),
					R.anim.image_click));
			;

			// create Intent to take a picture and return control to the calling
			// application
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			Uri fileUri = GlobalParams.getOutputMediaFileUri(mPoint); // create
																		// a
																		// file
																		// to
																		// save
																		// the
																		// image
			intent.putExtra(MediaStore.ACTION_IMAGE_CAPTURE, fileUri); // set the image
																// file name

			// start the image capture Intent
			startActivityForResult(intent, CAMERA_REQUEST);
			break;

		case R.id.EditButton:
			this.btnSave.setVisibility(View.VISIBLE);
			this.btnEdit.setVisibility(View.GONE);
			this.etFirstName.setEnabled(true);
			this.etLastName.setEnabled(true);
			break;

		case R.id.DeleteButton:

			EditTargetListener fragment = (EditTargetListener) getActivity()
					.getFragmentManager().findFragmentByTag(mCallerTag);
			fragment.deletePoint(this.mPoint.rowID);
			this.dismiss();

			break;

		case R.id.saveButton:
			this.btnSave.setVisibility(View.GONE);
			this.btnEdit.setVisibility(View.VISIBLE);
			EditTargetListener frag = (EditTargetListener) getActivity()
					.getFragmentManager().findFragmentByTag(mCallerTag);
			this.mPoint.first_name = this.etFirstName.getText().toString();
			this.mPoint.last_name = this.etLastName.getText().toString();
			frag.savePoint(this.mPoint);
			this.etFirstName.setEnabled(false);
			this.etLastName.setEnabled(false);

			break;

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

			Bitmap bitmap = (Bitmap) data.getExtras().get("data"); 
			
			if (bitmap == null) {
				Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
			} else {

				Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200,
						200, true);
				image.setImageBitmap(resizedBitmap);
				
				GlobalParams.storeImage(resizedBitmap, mPoint);
				
				EditTargetListener frag = (EditTargetListener) getActivity()
						.getFragmentManager().findFragmentByTag(mCallerTag);
				frag.imageUpdated(mPoint.rowID);
			}
		}
	}

}