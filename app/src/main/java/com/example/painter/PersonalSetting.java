package com.example.painter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import org.apache.http.NameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db_connect.DBConnector;


public class PersonalSetting extends ActionBarActivity {

	SessionManager session;
	private Thread mthread;
	EditText userName;
	EditText userEmail;
	EditText userPassword;
	Button save;
	TextView changePic;
	ImageView viewImage;
	String email, password, name, galleryPublic, galleryID;
	String value = "";
	String newvalue = "";
	String editName = "";
	HashMap<String, String> user;
	SharedPreferences.Editor editor;
	SharedPreferences pref;
	Switch gShare;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_setting);

		session = new SessionManager(getApplicationContext());
		editor = new SessionManager(getApplicationContext()).editor;

		userName = (EditText) findViewById(R.id.nickNameEdt);
		userEmail = (EditText) findViewById(R.id.emailEdt);
		userPassword = (EditText) findViewById(R.id.passEdit);
		gShare = (Switch) findViewById(R.id.gSharingSwitch);
		save = (Button) findViewById(R.id.saveSetting);
		changePic = (TextView) findViewById(R.id.changePic);
		viewImage = (ImageView) findViewById(R.id.pic);

		user = session.getUserDetails();
		email = user.get(SessionManager.KEY_EMAIL);
		password = user.get(SessionManager.KEY_PASSWORD);
		name = user.get(SessionManager.KEY_NAME);
		galleryPublic = user.get(SessionManager.KEY_GALLERYPUBLIC);

		Log.d("Switch", galleryPublic);
		userName.setText(name);
		userEmail.setText(email);
		userPassword.setText(password);
		if (galleryPublic.equals("1")) {
			gShare.setChecked(true);
		} else {
			gShare.setChecked(false);
		}

//        change password
		userPassword.setClickable(true);
		userPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(PersonalSetting.this);

				alert.setTitle("Title");
				alert.setMessage("Original password");

// Set an EditText view to get user input
				final EditText input = new EditText(PersonalSetting.this);
				input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						value = input.getText().toString();

						if (value.equals(password)) {
							Log.d("Original pass", password);
//   輸入新密碼
							AlertDialog.Builder alert = new AlertDialog.Builder(PersonalSetting.this);
							alert.setTitle("Title");
							alert.setMessage("New password");

							final EditText newpass = new EditText(PersonalSetting.this);
							newpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
							alert.setView(newpass);

							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									newvalue = newpass.getText().toString();
									Log.d("New pass", newvalue);
//   再輸入一次新密碼
									AlertDialog.Builder alert = new AlertDialog.Builder(PersonalSetting.this);
									alert.setTitle("Title");
									alert.setMessage("Again");

									final EditText againpass = new EditText(PersonalSetting.this);
									againpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
									alert.setView(againpass);

									alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											String againvalue = againpass.getText().toString();
											Log.d("Again pass", againvalue);

											if (againvalue.equals(newvalue)) {
												userPassword.setText(newvalue);
												password = newvalue;
											} else {
												Toast.makeText(getApplicationContext(), "not equal", Toast.LENGTH_LONG).show();
											}
										}
									});

									alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											// Canceled.
											dialog.cancel();
										}
									});

									alert.show();

								}
							});

							alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									// Canceled.
									dialog.cancel();
								}
							});

							alert.show();
						} else {
							Toast.makeText(getApplicationContext(), "wrong password", Toast.LENGTH_LONG).show();
						}
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dialog.cancel();
					}
				});

				alert.show();
			}
		});


		TextWatcher textWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//				String originalName = userName.getText().toString();
//				Log.d("TextChange", originalName);
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				//here, after we introduced something in the EditText we get the string from it
				name = userName.getText().toString();
				Log.d("TextChange", name);
			}
		};
		userName.addTextChangedListener(textWatcher);

		gShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					galleryPublic = "1";
				} else {
					galleryPublic = "0";
				}
			}
		});

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				mthread = new Thread(runnable);
				mthread.start();

			}
		});

		changePic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectImage();
			}
		});
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {

			editor.putString(SessionManager.KEY_PASSWORD, password);
			editor.putString(SessionManager.KEY_NAME, name);
			editor.putString(SessionManager.KEY_GALLERYPUBLIC, galleryPublic);
			editor.commit();

			Log.d("NAME", name);
			Log.d("PASSWORD", password);

			DBConnector dbConnector = new DBConnector("connect1.php");
			dbConnector.executeQuery(String.format("update member_v2 set password ='%s' , name = '%s', gallery_public = '%s'  where email = '%s'", password, name, galleryPublic, email));

			pref = new SessionManager(getApplicationContext()).pref;
			Log.d("Test", pref.getString(SessionManager.KEY_NAME, "null"));
			Log.d("Test", pref.getString(SessionManager.KEY_PASSWORD, "null"));
			Log.d("Test", pref.getString(SessionManager.KEY_GALLERYPUBLIC, "null"));
		}
	};


	private void selectImage() {

		final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

		AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSetting.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
		setActionBar();
	}


	private void setActionBar() {
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		toolBar.setTitle(R.string.personal_setting);

		setSupportActionBar(toolBar);
		toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolBar.setOnMenuItemClickListener(onMenuItemClick);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener(){

		public boolean onMenuItemClick(MenuItem menuItem){
			switch (menuItem.getItemId()){
				case R.id.save:
					Log.e("TEST","save");
					break;
				case R.id.changPic:
					Log.e("TEST","changePic");
					break;
			}
			return true;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory().toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
							bitmapOptions);

					viewImage.setImageBitmap(bitmap);

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream outFile = null;
					File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
					try {
						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();

						Log.d("TEST_PATH", path);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = {MediaStore.Images.Media.DATA};
				Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Log.d("TEST_PATH", picturePath);
				Log.w("path of image", picturePath + "");
				viewImage.setImageBitmap(thumbnail);
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_setting, menu);
		//ActionBar actionBar = this.getSupportActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		switch(item.getItemId()){
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
