package pdm.pratica04;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.Date;

import pdm.pratica04.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int FINE_LOCATION_REQUEST = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean fine_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestPermission();
    }

    private void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        this.fine_location = (permissionCheck == PackageManager.PERMISSION_GRANTED);
        if (this.fine_location) return;
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_REQUEST);
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        this.fine_location = (requestCode == FINE_LOCATION_REQUEST) && granted;

        if (mMap != null) {
            mMap.setMyLocationEnabled(this.fine_location);
        }

        findViewById(R.id.button_location).setEnabled(this.fine_location);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng centrodedoacaoemrecife = new LatLng(-8.05, -34.9);
        LatLng caruaru = new LatLng(-8.27, -35.98);
        LatLng joaopessoa = new LatLng(-7.12, -34.84);
        mMap.addMarker(new MarkerOptions().
                position(centrodedoacaoemrecife).
                title("Centro de doação\n").

                snippet("Recebendo:\n- Item 1\n- Item 2\n- Item 3\n- Item 4\n- Item 5").
                icon(BitmapDescriptorFactory.defaultMarker(35)));
        mMap.addMarker(new MarkerOptions().
                position(caruaru).
                title("Caruaru").
                icon(BitmapDescriptorFactory.defaultMarker(120)));
        mMap.addMarker(new MarkerOptions().
                position(joaopessoa).
                title("João Pessoa").
                icon(BitmapDescriptorFactory.defaultMarker(230)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centrodedoacaoemrecife));

        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(MapsActivity.this,
                    "Você clicou em " + marker.getTitle(),
                    Toast.LENGTH_SHORT).show();
            return false;
        });
        //possivelmente é nessa função em baixo que eu vou ter que armazenar a localização que o
        //usuário for digitar
        mMap.setOnMapClickListener(latLng -> mMap.addMarker(new MarkerOptions().
                position(latLng).
                title("Local de doação:\nData: " + new Date()).
                icon(BitmapDescriptorFactory.defaultMarker(0))));

        mMap.setOnMyLocationButtonClickListener(
                () -> {
                    Toast.makeText(MapsActivity.this,
                            "Indo para a sua localização.", Toast.LENGTH_SHORT).show();
                    return false;
                });

        mMap.setOnMyLocationClickListener(
                location -> Toast.makeText(MapsActivity.this,
                        "Você está aqui!", Toast.LENGTH_SHORT).show());

        mMap.setMyLocationEnabled(this.fine_location);

        findViewById(R.id.button_location).setEnabled(this.fine_location);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                // Retorna null para indicar que o layout padrão do InfoWindow não deve ser usado
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Infla o layout do InfoWindow personalizado
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Obtém as referências para os elementos de layout dentro do InfoWindow personalizado
                TextView titleTextView = view.findViewById(R.id.titleTextView);
                TextView snippetTextView = view.findViewById(R.id.snippetTextView);

                // Define o texto dos elementos de layout com as informações do marcador
                titleTextView.setText(marker.getTitle());
                snippetTextView.setText(marker.getSnippet());

                // Retorna a View personalizada para ser exibida dentro do InfoWindow
                return view;
            }
        });

    }

    public void currentLocation(View view) {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location!=null) {
                Toast.makeText(MapsActivity.this, "Localização atual: \n" +
                        "Lat: " + location.getLatitude() + " " +
                        "Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void irParaActivityAddItens(View view) {
        Intent intent = new Intent(this, AddItens.class);
        startActivity(intent);
    }

    public void irParaActivityAddLocal(View view) {
        Intent intent = new Intent(this, AddLocal.class);
        startActivity(intent);
    }

}