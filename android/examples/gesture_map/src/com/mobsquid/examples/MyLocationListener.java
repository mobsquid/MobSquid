package com.mobsquid.examples;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Listens to location updates. This is a simple implementation of the examples
 * in the android documentation around location reporting.
 */
public class MyLocationListener implements LocationListener {
  /**
   * A reference to the main activity where we will report location.
   */
  private GestureMapsActivity activity;

  /**
   * Keep track of the current location.
   */
  private Location currentLocation;

  /**
   * A reference to the location manager of the system.
   */
  private LocationManager locationManager;

  private static final int TWO_MINUTES = 1000 * 60 * 2;
  private static final int TEN_SECONDS = 1000 * 10;

  /**
   * Constructor stores the location manager reference and initializes the
   * listening for location updates.
   *
   * After each location update, it tells the activity to update the location
   * with the current location.
   *
   * @param locationManager
   */
  public MyLocationListener(GestureMapsActivity activity) {
    this.activity = activity;

    LocationManager locationManager = (LocationManager) activity
        .getSystemService(Context.LOCATION_SERVICE);

    this.locationManager = locationManager;
    start();
  }

  /**
   * Called when the location manager receives a location update.
   */
  public void onLocationChanged(Location location) {
    // Called when a new location is found by the network location provider.
    if (isBetterLocation(location, currentLocation)) {
      currentLocation = location;
      activity.onLocationChanged(location);
    }
  }

  /**
   * Stops listening to location updates. Called when the applications is put
   * in background.
   */
  public void stop() {
    locationManager.removeUpdates(this);
  }

  /**
   * Starts listening to location changes from the location manager.
   */
  public void start() {
    if (locationManager != null) {
      currentLocation = locationManager
          .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

      // Register the listener with the Location Manager to receive location
      // updates
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
          0, 0, this);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
          0, this);
    }
  }

  public void onStatusChanged(String provider, int status, Bundle extras) {
    // Foo implementation.
  }

  public void onProviderEnabled(String provider) {
    // Foo implementation.
  }

  public void onProviderDisabled(String provider) {
    // Foo implementation.
  }

  /**
   * Determines whether one Location reading is better than the current Location
   * fix. This is the sample code used in the android documentation.
   *
   * @param location The new Location that you want to evaluate
   * @param currentBestLocation The current Location fix, to which you want to
   *     compare the new one.
   */
  protected boolean isBetterLocation(Location location,
      Location currentBestLocation) {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    if (timeDelta < TEN_SECONDS) {
      return false;
    }

    // If it's been more than two minutes since the current location, use the
    // new location because the user has likely moved.
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
        .getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
        currentBestLocation.getProvider());

    float distance = location.distanceTo(currentBestLocation);

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate && distance > 10) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  /**
   * Checks whether two providers are the same
   */
  private boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }
}
