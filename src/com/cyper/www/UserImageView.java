package com.cyper.www;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;

public class UserImageView extends Activity {

	private static String IMAGE = "image";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_image_view);
		
		Bundle bundle = getIntent().getExtras();
		
		Bitmap bitmap = (Bitmap) bundle.get(IMAGE);
		
		ImageView image = (ImageView) findViewById(R.id.User_image_view);
		
		image.setImageBitmap(bitmap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_image_view, menu);
		return true;
	}
	
	@Override
    public void onBackPressed() {
            super.onBackPressed();
            this.finish();
    }

}
