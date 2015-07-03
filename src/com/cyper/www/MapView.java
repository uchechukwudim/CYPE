package com.cyper.www;



import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MapView extends FragmentActivity {
	private static final String ADDRESS = "Address";
	private static final String CLUBNAME = "Clubname";
	
  private GoogleMap map;
  private LatLng club;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_view);
		
		Bundle bundle = getIntent().getExtras();
		final String clubName = bundle.getString(CLUBNAME);
		String address = bundle.getString(ADDRESS);
		double lat = getLat(address);
		double lng = getLng(address);
		club = new LatLng(lat, lng);
		
		if(lat == 0.0 && lng == 0.0 || lat != 0.0 && lng != 0.0)
		{
			Toast.makeText(getApplicationContext(), "Could get club location", Toast.LENGTH_LONG).show();
		}
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		
		 map.addMarker(new MarkerOptions().position(club)
		        .title(clubName));
		 map.addMarker(new MarkerOptions()
        .position(club)
        .title(clubName)
        .icon(BitmapDescriptorFactory
            .fromResource(R.drawable.ic_launcher)));

		  // Move the camera to address with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(club, 30));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	    map.getUiSettings().setMyLocationButtonEnabled(true);
      
	}

	private double getLng(String address2) {
		double lng = 0.0;
		Geocoder gcd = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        
        List<Address> addresses;
		try 
		{
			addresses = gcd.getFromLocationName(address2 , 1);
			if (addresses.size() > 0)
			{
				lng = addresses.get(0).getLongitude();
				Log.d("LNG", ""+lng);
			}	
		} 
		catch (IOException e) 
		{
			Log.e("IO TOP", e.getMessage())
			;e.printStackTrace();
		}
		return lng;
		
	}

	private double getLat(String address2) {
		double lat = 0.0;
		Geocoder gcd = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        
        List<Address> addresses;
		try 
		{
			addresses = gcd.getFromLocationName(address2 , 1);
			if (addresses.size() > 0)
			{
				lat = addresses.get(0).getLatitude();
				Log.d("LAT", ""+lat);
			}	
		} 
		catch (IOException e) 
		{
			Log.e("IO TOP", e.getMessage())
			;e.printStackTrace();
		}
		return lat;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_view, menu);
		return true;
	}

	
	

}
