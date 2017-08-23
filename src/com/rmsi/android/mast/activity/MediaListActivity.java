package com.rmsi.android.mast.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.adapter.MediaListingAdapterTemp;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.util.CommonFunctions;

/**
 * 
 * @author prashant.nigam
 * 
 */
public class MediaListActivity extends ActionBarActivity {

	Button addNew, attribe, btnback;
	Context context;
	ListView listView;
	List<Attribute> attribute = new ArrayList<Attribute>();
	MediaListingAdapterTemp tempAdapter;
	Long featureId = 0L;
	private ImageView imageView;
	CommonFunctions cf = CommonFunctions.getInstance();
	String mediaFolderName = Environment.getExternalStorageDirectory()
			.getAbsolutePath()
			+ File.separator
			+ CommonFunctions.parentFolderName
			+ File.separator
			+ CommonFunctions.mediaFolderName;
	String timeStamp = new SimpleDateFormat("MMdd_HHmmss").format(new Date()
			.getTime());
	static private File file;

	FileOutputStream fo;
	int groupId = 0;
	int roleId = 0;
	String serverFeatureId;

	private void deleteEntry(final int groupId) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setMessage(R.string.deleteEntryMsg);
		alertDialogBuilder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO DELETE ENTRIES FROM MEDIA TABLE AFter 13th
						DBController sqllite = new DBController(context);
						String keyword = "media";
						boolean result = sqllite.deleteRecord(groupId, keyword);
						if (result) {
							refereshList();
						} else {
							String info = getResources().getString(
									R.string.unable_delete);
							Toast.makeText(context, info, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) // Image
		{
			if (resultCode == RESULT_OK) {
				try {
					// photo = rotate(photo, 90);
					Media media = new Media();
					if (!(file == null)) {

						if (file.length() > 200000) {
							// picking the file and compressing it.
							Bitmap photo = BitmapFactory
									.decodeStream(new FileInputStream(file));
							ByteArrayOutputStream outFile = new ByteArrayOutputStream();
							photo.compress(Bitmap.CompressFormat.JPEG, 20,
									outFile);
							byte[] bitmapdata = outFile.toByteArray();
							try {
								fo = new FileOutputStream(file.getPath());
								fo.write(bitmapdata);
								fo.flush();
								fo.close();
							} catch (Exception e) {
								Log.e("no file", e.getMessage());
								e.printStackTrace();
							}
						}
						media.setMediaPath(file.getAbsolutePath());
						media.setFeatureId(featureId);
						media.setMediaType("Image");
						groupId = cf.getGroupId();
						media.setMediaId(groupId);
						new DBController(context).insertMedia(media);
						Intent myIntent = new Intent(context,
								AddMediaActivity.class);
						myIntent.putExtra("groupid", groupId);
						myIntent.putExtra("featureid", featureId);
						startActivity(myIntent);
					} else {

						Toast.makeText(context, R.string.error_saving_media,
								Toast.LENGTH_LONG).show();

					}
				} catch (Exception e1) {
					cf.appLog("", e1);
					e1.printStackTrace();
				}
			}

		}
		if (requestCode == 2) // Video
		{

			if (resultCode == RESULT_OK) {

				try {
					Media media = new Media();
					media.setMediaPath(file.getAbsolutePath());
					media.setFeatureId(featureId);
					media.setMediaType("Video");
					groupId = cf.getGroupId();
					media.setMediaId(groupId);
					new DBController(context).insertMedia(media);

					Intent myIntent = new Intent(context,
							AddMediaActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				} catch (Exception e) {
					cf.appLog("", e);
					e.printStackTrace();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Video was not captured",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Video was not captured",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_media_list);

		roleId = CommonFunctions.getRoleID();
		TextView spatialunitValue = (TextView) findViewById(R.id.spatialunit_lbl);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_media_list);
		if (toolbar != null)
			setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		context = this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureId = extras.getLong("featureid");
			serverFeatureId = extras.getString("serverFeaterID");
		}

		if (!TextUtils.isEmpty(serverFeatureId) && serverFeatureId != null) {
			spatialunitValue.setText("USIN" + "   :  "
					+ serverFeatureId.toString());
		} else {
			spatialunitValue.setText(spatialunitValue.getText() + "   :  "
					+ featureId.toString());
		}
		addNew = (Button) findViewById(R.id.btn_addNew);
		btnback = (Button) findViewById(R.id.btn_back);

		listView = (ListView) findViewById(R.id.list_view);
		TextView emptyText = (TextView) findViewById(android.R.id.empty);
		listView.setEmptyView(emptyText);

		tempAdapter = new MediaListingAdapterTemp(context, this, attribute,
				"medialist");
		listView.setAdapter(tempAdapter);

		if (roleId == 2) // Hardcoded Id for Role (1=Trusted Intermediary,
							// 2=Adjudicator)
		{
			addNew.setEnabled(false);

		}

		addNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		btnback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		refereshList();
		super.onResume();
	}

	private void refereshList() {
		attribute.clear();
		DBController sqllite = new DBController(context);
		attribute.addAll(sqllite.getMediaList(featureId));
		sqllite.close();
		tempAdapter.notifyDataSetChanged();
	}

	private Bitmap rotate(Bitmap photo, int degree) {
		int w = photo.getWidth();
		int h = photo.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(photo, 0, 0, w, h, mtx, true);
	}

	private void showDialog() {
		try {
			String[] media_options = getResources().getStringArray(
					R.array.media);

			final Dialog dialog = new Dialog(context, R.style.DialogTheme);
			dialog.setContentView(R.layout.dialog_show_list);
			dialog.setTitle(getResources().getString(
					R.string.selectOptionDialogTitle));
			dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
			ListView listViewForMedia = (ListView) dialog
					.findViewById(R.id.commonlistview);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					R.layout.item_list_single_choice, media_options);

			Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
			btn_ok.setVisibility(View.GONE);

			listViewForMedia.setAdapter(adapter);

			listViewForMedia.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					try {
						int itemPosition = position;
						if (itemPosition == 0) // Image
						{
							timeStamp = new SimpleDateFormat("MMdd_HHmmss")
									.format(new Date().getTime());
							Intent cameraIntent = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							file = new File(mediaFolderName + File.separator
									+ "MTP_" + timeStamp + ".jpg");
							cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(file));
							startActivityForResult(cameraIntent, 1);
							dialog.dismiss();
						} else if (itemPosition == 1) // video
						{
							/*
							 * Intent videoIntent = new Intent(context,
							 * VideoActivity.class);
							 * videoIntent.putExtra("featureid", featureId);
							 * startActivityForResult(videoIntent, 2);
							 * dialog.dismiss();
							 */
							timeStamp = new SimpleDateFormat("MMdd_HHmmss")
									.format(new Date().getTime());
							Intent videoIntent = new Intent(
									android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
							file = new File(mediaFolderName + File.separator
									+ "MTP_" + timeStamp + ".mp4");
							videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(file));
							videoIntent.putExtra(
									MediaStore.EXTRA_VIDEO_QUALITY, 0);
							long maxsize = 1 * 1024 * 1024;
							videoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,
									maxsize); // 3MB
							// videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,20);
							// //20 secs
							startActivityForResult(videoIntent, 2);
							dialog.dismiss();
						}
					} catch (Exception e) {

						e.printStackTrace();
						cf.appLog("", e);
					}

				}
			});

			dialog.show();
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	public void showPopup(View v, Object object) {
		PopupMenu popup = new PopupMenu(context, v);
		MenuInflater inflater = popup.getMenuInflater();

		if (roleId == 1) // Hardcoded roleId 1 for TI 2 for Adjudicator
		{
			inflater.inflate(R.menu.media_attribute_listing_options,
					popup.getMenu());

		} else {
			inflater.inflate(
					R.menu.media_attribute_listing_options_for_adjudicator,
					popup.getMenu());

		}

		int position = (Integer) object;
		final int groupId = attribute.get(position).getGroupId();

		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.edit_attributes:
					// Open attributes form to edit --------------
					Intent myIntent = new Intent(context,
							AddMediaActivity.class);
					myIntent.putExtra("groupid", groupId);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
					return true;
				case R.id.delete_entry:
					deleteEntry(groupId);
					return true;
				case R.id.view:
					Media media = new DBController(context)
							.getMediaFile(groupId);
					if (media.getMediaType().equalsIgnoreCase("image")) {
						Intent intent = new Intent();
						intent.setAction(android.content.Intent.ACTION_VIEW);
						File file = new File(media.getMediaPath());
						intent.setDataAndType(Uri.fromFile(file), "image/*");
						startActivity(intent);
					}
					if (media.getMediaType().equalsIgnoreCase("video")) {
						Intent intent = new Intent();
						intent.setAction(android.content.Intent.ACTION_VIEW);
						File file = new File(media.getMediaPath());
						intent.setDataAndType(Uri.fromFile(file), "video/*");
						startActivity(intent);
					}
					return true;
				default:
					return false;
				}
			}
		});
		popup.show();
	}
}
