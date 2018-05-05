package cgi.una.ac.cr.mapsdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Utilizando los google service api se va a obtener la ubicación
 * por medio del GPS
 * Pasos previos:
 * 1. En el archivo AndroidManifest.xml colocar:
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 * <uses-feature android:name="android.hardware.location.gps" />
 * 2. Generar la llave de google.
 * 3. Agregar la librería implementation 'com.google.android.gms:play-services-location:15.0.0'
 * en el archivo build.glandle
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // variable del mapa, se inicializará cuando el mapa esté dibujado
    private FusedLocationProviderClient mFusedLocationClient; //proveedor de los servicios de localización de google
    private static final int SOLICITAR_GPS = 1;// para saber qué permisos se están solicitando
    SupportMapFragment mapFragment;// instancia del fragmento que contiene el mapa
    LocationRequest mLocationRequest; // para configurar la frecuencia de actualización del gps
    private LocationCallback mLocationCallback;// para indicar qué hace la app con cada actualización del gps


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mLocationCallback = new LocationCallback() {
            /**
             * Determina qué hace cada vez que se reciben un conjunto de posiciones gps
             * en nuestro caso las utilizará para mostrarlas en el mapa
             * @param locationResult array con todas las localizaciones
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mMap != null) {// siempre y cuando el mapa esté cargado
                    if (locationResult == null) {// siempre que existan valores
                        return;
                    }
                    // por cada localización la dibujamos en en mapa
                    for (Location location : locationResult.getLocations()) {

                        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                }
            }

            ;
        };
        // inicializa el proveedor de los servicios del mapa
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        // el mLocationRequest determina la frecuencia y prioridad de actualización del gps
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // se carga el request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocation();


    }

    /**
     * se verifica que el usuario haya autorizado el acceso al gps
     * en caso contrario solicita autorización
     */
    public void getLocation() {
        // ¿No tengo los permisos de gps?
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //No tengo permisos, voy a solicitarlos al usuario
            // cuando llamo a este método se llama a @onRequestPermissionsResult
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    SOLICITAR_GPS);


        } else { // Ya tengo el permiso otorgado, inicia la actualización de los puntos gps

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null );
        }
    }

    /**
     * Una vez que se solicitan permisos se muestra una pantalla al usuario
     * para determinar si el usuario autorizó o no el uso del provilegio solicitado
     * considerando la escogencia del usuario se puede en este método deshabilitar
     * funcionalidad que no serviría sin acceso a los privilegios solicitados
     *
     * @param requestCode Código enviado en requestPermissions
     * @param permissions Array de permisos que se solicitaron
     * @param grantResults Array de el resultado de los permisos Autorizado / Rechazado
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SOLICITAR_GPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Ya tengo el permiso otorgado, inicia la actualización de los puntos gp
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            null );

                } else { // el usuario no le dió la gana de dar permisos, va para afuera !!!
                    System.exit(1);
                }
                return;
            }

        }
    }


    /**
     * Este método será ejecutado cuando el mapa se encuentre dibujado y listo
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


}
