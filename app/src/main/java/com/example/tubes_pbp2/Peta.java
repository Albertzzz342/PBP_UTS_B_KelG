package com.example.tubes_pbp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class Peta extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {
    private static final String DESTINATION_SYMBOL_LAYER_ID = "destination-symbol-layer-id";
    private static final String DESTINATION_ICON_ID = "destination-icon-id";
    private static final String DESTINATION_SOURCE_ID = "destination-source-id";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private Point origin, destination;

    private LocationComponent locationComponent;
    private static final String TAG = "Direction";
    private NavigationMapRoute navigationMapRoute;
    private Button btnStart;
    private DirectionsRoute currentRoute;

    private LatLng currentLocation;
    private static Point currentPoint, destinationPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_peta);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
                        enableLocationComponent(style);
                        Point origin = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                locationComponent.getLastKnownLocation().getLatitude());
                        Point destination = Point.fromLngLat(110.370529, -7.797068);

                        mapboxMap.addOnMapClickListener(Peta.this);

                        //Marker Yogyakarta (kos)
                        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                                Point.fromLngLat(110.370529, -7.797068)));
                        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                                .withImage(DESTINATION_ICON_ID, BitmapFactory.decodeResource(
                                        Peta.this.getResources(), R.drawable.mapbox_marker_icon_default))
                                .withSource(new GeoJsonSource(DESTINATION_SOURCE_ID,
                                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                                .withLayer(new SymbolLayer(DESTINATION_SYMBOL_LAYER_ID, DESTINATION_SOURCE_ID)
                                        .withProperties(
                                                iconImage(DESTINATION_ICON_ID),
                                                iconAllowOverlap(true),
                                                iconIgnorePlacement(true)
                                        )
                                ));
                        ////////////////
                        getRoute(origin, destination);

                        btnStart = findViewById(R.id.btnStart);
                        btnStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean route = true;
                                NavigationLauncherOptions navigationLauncherOptions = NavigationLauncherOptions
                                        .builder()
                                        .directionsRoute(currentRoute)
                                        .shouldSimulateRoute(route)
                                        .build();
                                NavigationLauncher
                                        .startNavigation(Peta.this, navigationLauncherOptions);
                            }
                        });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Grant Location Permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else{
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        Log.d(TAG, "Respon: " +response.code());
                        if (response.body() == null){
                            Log.e(TAG, "Tidak ada rute, udah set token blom?");
                            return;
                        } else if(response.body().routes().size() <1 ){
                            Log.e(TAG,"Tidak ada rute");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        if(navigationMapRoute != null){
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap,
                                    R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}