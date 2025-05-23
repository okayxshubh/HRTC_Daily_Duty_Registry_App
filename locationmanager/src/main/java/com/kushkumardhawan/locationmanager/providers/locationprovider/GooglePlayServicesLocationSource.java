package com.kushkumardhawan.locationmanager.providers.locationprovider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.kushkumardhawan.locationmanager.constants.RequestCode;

class GooglePlayServicesLocationSource extends LocationCallback {

    private final Context context;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationRequest locationRequest;
    private final SourceListener sourceListener;

    interface SourceListener extends OnSuccessListener<LocationSettingsResponse>, OnFailureListener {
        void onSuccess(LocationSettingsResponse locationSettingsResponse);
        void onFailure(@NonNull Exception exception);
        void onLocationResult(@Nullable LocationResult locationResult);
        void onLastKnowLocationTaskReceived(@NonNull Task<Location> task);
    }

    GooglePlayServicesLocationSource(Context context, LocationRequest locationRequest, SourceListener sourceListener) {
        this.context = context.getApplicationContext();  // Prevent memory leaks
        this.sourceListener = sourceListener;
        this.locationRequest = locationRequest;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
    }

    void checkLocationSettings() {
        LocationServices.getSettingsClient(context)  // Corrected context usage
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .build()
                )
                .addOnSuccessListener(locationSettingsResponse -> {
                    if (sourceListener != null)
                        sourceListener.onSuccess(locationSettingsResponse);
                })
                .addOnFailureListener(exception -> {
                    if (sourceListener != null) sourceListener.onFailure(exception);
                });
    }

    void startSettingsApiResolutionForResult(@NonNull ResolvableApiException resolvable, Activity activity) throws IntentSender.SendIntentException {
        resolvable.startResolutionForResult(activity, RequestCode.SETTINGS_API);
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void requestLocationUpdate() {
        if (!hasLocationPermission()) return; // Permission check added

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, this, Looper.getMainLooper());
    }

    @NonNull
    Task<Void> removeLocationUpdates() {
        return fusedLocationProviderClient.removeLocationUpdates(this);
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void requestLastLocation() {
        if (!hasLocationPermission()) return; // Permission check added

        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (sourceListener != null)
                        sourceListener.onLastKnowLocationTaskReceived(task);
                });
    }

    @Override
    public void onLocationResult(@Nullable LocationResult locationResult) {
        if (sourceListener != null) sourceListener.onLocationResult(locationResult);
    }

    // ✅ Permission Check Method
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
