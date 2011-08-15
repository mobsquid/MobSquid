package com.mobsquid.examples;

import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.mobsquid.DetectorEvent;
import com.mobsquid.MobSquid;
import com.mobsquid.MobSquidListener;
import com.mobsquid.OrientationDetector;

/**
 * Shows a map that can be controlled through hand gestures. Turn the phone
 * over to switch between Satellite View and Map View. Flick the phone forward
 * and backward to zoom in and out. Put the phone on the table to turn on
 * traffic.
 */
public class GestureMapsActivity extends MapActivity
    implements MobSquidListener {
  MapView mapView;

  /**
   * The point on the screen where the user is pressing. Used to zoom in and
   * out around that point.
   */
  int xPress = 0;
  int yPress = 0;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initialize the library.
    MobSquid.init(this, "5e84fa5ffb9b53f489512d05839d44");

    // Register this activity to listen for MobSquid events.
    MobSquid.registerListener(this);

    setContentView(R.layout.main);
  }

  @Override
  public void onStart() {
    super.onStart();
    MobSquid.onStart(this);

    mapView = (MapView) findViewById(R.id.mapview);
    mapView.setBuiltInZoomControls(true);
  }

  @Override
  public void onStop() {
    super.onStop();
    MobSquid.onStop(this);
  }

  /**
   * Handles MobSquid events and reacts to what the user is doing. Makes calls
   * to the map to zoom in, zoom out, and change modes based on user gestures.
   *
   * @param event The MobSquid event.
   */
  @Override
  public void onDetectorEvent(DetectorEvent event) {
    switch(event.type) {
    case DetectorEvent.FLICK_FORWARD:
      if (xPress != 0 && yPress != 0) {
        // Zoom in around the point where the user is pressing a finger.
        mapView.getController().zoomInFixing(xPress, yPress);
      } else {
        // The user is not touching the screen, just zoom in.
        mapView.getController().zoomIn();
      }
      break;

    case DetectorEvent.FLICK_BACK:
      if (xPress != 0 && yPress != 0) {
        // Zoom out around the point where the user is pressing a finger.
        mapView.getController().zoomOutFixing(xPress, yPress);
      } else {
        // The user is not touching the screen, just zoom in.
        mapView.getController().zoomOut();
      }
      break;

    case DetectorEvent.MAGIC_FLIP:
      mapView.setSatellite(!mapView.isSatellite());
      break;

    case DetectorEvent.FIXED_SURFACE:
      // Get context to see that we are also face up.
      int state = MobSquid.getContext("orientation").getInt("state");

      // If there's at least one fixed direction and the phone is facing up
      // it means that it's likely on the table.
      if (event.context.getInt(MobSquid.NUM_FIXED_AXES) >= 1 &&
          state == OrientationDetector.STATE_FACE_UP) {
        mapView.setTraffic(true);
      }
      break;

    case DetectorEvent.NON_FIXED_SURFACE:
      System.out.println("Not fixed surface");
      mapView.setTraffic(false);
      break;

    }
  }

  /**
   * Intercepts the event for when the user is touching the screen. Keep the
   * coordinates for as long as the finger is still on the device screen.
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    int actionType = ev.getAction();
    switch (actionType) {

    case MotionEvent.ACTION_DOWN:
      xPress = (int)ev.getX();
      yPress = (int)ev.getY();
      break;

    case MotionEvent.ACTION_UP:
      xPress = 0;
      yPress = 0;
      break;

    }
    return super.dispatchTouchEvent(ev);
  }

  /**
   * Foo method that needs to be implemented as part of the MapActivity.
   */
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

}
