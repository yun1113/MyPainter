package com.example.painter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;



public class Canvas extends ActionBarActivity {
    ImageButton btn;
    PopupWindow popupwindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        btn = (ImageButton) findViewById(R.id.settingbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPopupWindow();
            }
        });
    }

    private void getPopupWindow() {

        LayoutInflater inflater = (LayoutInflater) Canvas.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup, null);
        popupwindow = new PopupWindow(layout, 350, 300, true);
        popupwindow.setTouchable(true);
        popupwindow.setOutsideTouchable(true);
        popupwindow.setFocusable(true);
        popupwindow.setBackgroundDrawable(new BitmapDrawable());
        popupwindow.update();

        popupwindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        popupwindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupwindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        ImageButton personalsettingBtn = (ImageButton) layout.findViewById(R.id.personalSettingBtn);
        personalsettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Canvas.this, PersonalSetting.class);
                startActivity(intent);
            }
        });

        ImageButton friendBtn = (ImageButton) layout.findViewById(R.id.friendListBtn);
        friendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Canvas.this, FriendTest.class);
                startActivity(intent);
            }
        });

        ImageButton galleryBtn = (ImageButton) layout.findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Canvas.this, Gallery.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_canvas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
