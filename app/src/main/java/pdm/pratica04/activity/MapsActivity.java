package pdm.pratica04.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import pdm.pratica04.model.Centro;
import pdm.pratica04.OAuth2Interceptor;
import pdm.pratica04.R;
import pdm.pratica04.databinding.ActivityMapsBinding;

import pdm.pratica04.model.Item;
import pdm.pratica04.service.MyApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private boolean isDonor = false; // declare isDonor as a field

    private static final int FINE_LOCATION_REQUEST = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean fine_location;
    private String markerTitle;


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.117:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    MyApiService apiService = retrofit.create(MyApiService.class);

    public void deleteCentro(int id) {
        Call<ResponseBody> call = apiService.deleteCentro(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Manipular a resposta bem-sucedida aqui
                } else {
                    // Manipular a resposta de erro aqui
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Manipular a falha de rede ou outros erros aqui
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);


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

        //findViewById(R.id.button_location).setEnabled(this.fine_location);
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.authenticator(new OAuth2Interceptor(getIntent().getStringExtra("access_token")));

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        // Criar uma instância do Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.117:8000") // Substitua pela URL base da sua API
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Criar uma instância da sua interface
        MyApiService apiService = retrofit.create(MyApiService.class);

        // Fazer chamada à API para obter os centros
        Call<ResponseBody> callGetCentros = apiService.getCentros();
        callGetCentros.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Lidar com a resposta da API aqui
                if (response.isSuccessful()) {
                    // A requisição foi bem-sucedida
                    ResponseBody responseBody = response.body();
                    // Converter o corpo da resposta em uma lista de objetos Centro usando Gson
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Centro>>() {}.getType();
                    List<Centro> centros = null;

                    try {


                        centros = gson.fromJson(responseBody.string(), listType);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    LatLng teste = new LatLng(-7.12, -34.84);

                    // Iterar sobre os objetos Centro e acessar as propriedades desejadas
                    double latitude = 0;
                    double longitude = 0;
                    String nome ="";
                    int id =0;
                    BitmapDescriptor icon;
                    Marker centrodedoacaoemrecife;
                    System.out.println("opa: "+centros);
                    for (Centro centro : centros) {
                         nome = centro.getNome();
                        id = centro.getId();
                         latitude = Double.parseDouble(centro.getLatitude());
                         longitude = Double.parseDouble(centro.getLongitude());
                        teste = new LatLng(latitude, longitude);

                        if (centro.isCentroOuPessoa()) {
                            markerTitle = "Centro de doação";
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        } else {
                            markerTitle = "Pessoa";
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        }


                         centrodedoacaoemrecife = mMap.addMarker(new MarkerOptions().
                                position(teste).
                                title(nome).

                                snippet("").
                                icon(icon));
                        centrodedoacaoemrecife.setTag(id);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(centrodedoacaoemrecife.getPosition()));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f));
                        Toast.makeText(MapsActivity.this,
                                "Indo para a sua localização.", Toast.LENGTH_SHORT).show();
                        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        Task<Location> task = fusedLocationProviderClient.getLastLocation();
                        task.addOnSuccessListener(location -> {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                // Definir o nível de zoom desejado (ajuste conforme necessário)
                                float zoomLevel = 20.0f;

                                // Movimentar a câmera para a localização atual do usuário com o zoom desejado
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
                            }
                        });
                        // Faça o que for necessário com os dados obtidos
                        Log.i("API Success", "Nome: " + nome + ", Latitude: " + latitude + ", Longitude: " + longitude);
                    }

                    // Faça o processamento necessário com o responseBody
                } else {
                    // A requisição retornou um erro
                    // Obtenha o código de erro e a mensagem de erro
                    int errorCode = response.code();
                    String errorMessage = response.message();
                    System.out.println("Erro na requisição: " + errorCode + " - " + errorMessage);
                    Log.e("API Failure", "ERRO API: ");

                    // Faça o tratamento de erro apropriado
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Lidar com falhas de rede ou outras falhas de comunicação
                Log.e("API Failure", "Falha na comunicação com a API: " + t.getMessage());
                Toast.makeText(MapsActivity.this, "Falha na comunicação com a API", Toast.LENGTH_SHORT).show();


            }
        });



        mMap.setOnMarkerClickListener(marker -> {

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl("http://192.168.0.117:8000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MyApiService apiService2 = retrofit2.create(MyApiService.class);
            Object tag2 = marker.getTag();
            int centroId2 = 0;
            if (tag2 != null) {
                 centroId2 = (int) tag2;
            }
            else {
                 centroId2 = 0;

            }

            Call<List<Item>> call = apiService2.getItensCentro("", centroId2);
            call.enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    if (response.isSuccessful()) {
                        List<Item> items = response.body();

                        StringBuilder concatenatedItems = new StringBuilder();

                        for (Item item : items) {
                            concatenatedItems.append("Item ")
                                    .append(item.getId())
                                    .append(": ")
                                    .append(item.getItens())
                                    .append("\n");
                        }

                        // Use a string concatenada como necessário
                        String result = concatenatedItems.toString();
                        System.out.println(result);

                        marker.setSnippet(result);

                    } else {
                        // Handle error response
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    // Handle failure
                }
            });


            if (marker.getSnippet() != null && !marker.getSnippet().isEmpty()) {
                // Exibir as informações adicionais do marcador em um diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(marker.getTitle());
                builder.setMessage(marker.getSnippet());
                builder.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                }) .setNeutralButton("Deletaraqui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remover o marcador do mapa

                        Object tag = marker.getTag();
                        if (tag != null) {
                            int centroId = (int) tag;
                            Toast.makeText(getApplicationContext(), "Deletar " + centroId, Toast.LENGTH_SHORT).show();

                            deleteCentro(centroId);

                            // Use o ID do centro conforme necessário
                        } else {
                            // O marcador não possui um ID associado
                        }
                        marker.remove();
                    }
                }) .setNegativeButton("Editar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            } else {
                return false;
            }
        });


        //possivelmente é nessa função em baixo que eu vou ter que armazenar a localização que o
        //usuário for digitar


        mMap.setOnMapClickListener(latLng -> {


            AtomicReference<String> centroTitle = new AtomicReference<>("");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Novo ponto, você é um doador ou um centro de doação?");

            // Opções de doação
            final String[] donationOptions = {"Centro de doação", "Doador"};
            builder.setSingleChoiceItems(donationOptions, -1, (dialog, which) -> {
                if (which == 0) {
                    // Centro de doação selecionado
                    isDonor = false;
                } else if (which == 1) {
                    // Doador selecionado
                    isDonor = true;
                }
            });

            // Configurar os itens doados
            final List<String> donatedItems = new ArrayList<>();
            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            builder.setView(layout);

            final EditText titleInput = new EditText(this);
            titleInput.setHint("Digite o título do centro");
            layout.addView(titleInput);




            // Botão "Adicionar item"
            Button addButton = new Button(this);
            addButton.setText("Adicionar item");
            addButton.setOnClickListener(view -> {
                final EditText itemInput = new EditText(this);
                itemInput.setHint("Digite o nome do item");
                layout.addView(itemInput);
            });
            layout.addView(addButton);

            builder.setPositiveButton("OK", (dialog, which) -> {
                // Adicionar marcador com título e ícone baseado na opção selecionada
                markerTitle = isDonor ? "Doador" : "Centro de doação";
                centroTitle.set(titleInput.getText().toString().trim());

                // Configurar os botões "OK" e "Cancelar"
                String finalCentroTitle = centroTitle.get();

                if (!finalCentroTitle.isEmpty()) {
                    markerTitle += ": " + finalCentroTitle;
                }
                BitmapDescriptor icon = isDonor ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE) : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(markerTitle)
                        .icon((icon));
                mMap.addMarker(markerOptions);


                Centro centro = new Centro();

                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                String latitudeString = String.valueOf(latitude);
                String longitudeString = String.valueOf(longitude);

                centro.setNome(finalCentroTitle);
                centro.setCentroOuPessoa(isDonor);
                centro.setLatitude(latitudeString);
                centro.setLongitude(longitudeString);
                Toast.makeText(MapsActivity.this, "final centro: " + finalCentroTitle, Toast.LENGTH_SHORT).show();

                OkHttpClient.Builder okHttpClientBuilder2 = new OkHttpClient.Builder();
                okHttpClientBuilder2.authenticator(new OAuth2Interceptor(getIntent().getStringExtra("access_token")));

                OkHttpClient okHttpClient2 = okHttpClientBuilder2.build();

                // Criar uma instância do Retrofit
                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.117:8000/") // Substitua pela URL base da sua API
                        .client(okHttpClient2)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Toast.makeText(MapsActivity.this, "acess token: " + getIntent().getStringExtra("access_token"), Toast.LENGTH_SHORT).show();

                // Criar uma instância da sua interface
                MyApiService apiService2 = retrofit2.create(MyApiService.class);

                Headers headers = new Headers.Builder()
                        .add("Authorization", "Bearer " + getIntent().getStringExtra("access_token"))
                        .build();



                Call<ResponseBody> call = apiService2.criarCentro(headers, centro);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            ResponseBody createdCentro = response.body();
                            Toast.makeText(MapsActivity.this, "Response body: " + createdCentro, Toast.LENGTH_SHORT).show();

                            // O centro foi criado com sucesso, faça o tratamento necessário aqui
                        } else {
                            ResponseBody createdCentro = response.body();

                            Toast.makeText(MapsActivity.this, "Response body: " + createdCentro, Toast.LENGTH_SHORT).show();

                            // Houve um erro na criação do centro, faça o tratamento necessário aqui
                        }
                    }


                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Houve um erro na chamada à API, faça o tratamento necessário aqui
                    }
                });


                // Adicionar informações adicionais do marcador como um snippet
                StringBuilder snippet = new StringBuilder("Itens doados:\n");
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    if (child instanceof EditText) {
                        String itemName = ((EditText) child).getText().toString().trim();
                        if (!itemName.isEmpty()) {
                            donatedItems.add(itemName);
                            snippet.append(String.format("%d- %s\n", donatedItems.size(), itemName));
                        }
                    }
                }
                if (donatedItems.size() > 0) {
                    mMap.addMarker(markerOptions.snippet(snippet.toString()));
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> {
                dialog.cancel();
            });

            // Exibir o diálogo
            builder.show();
        });





        mMap.setOnMyLocationButtonClickListener(
                () -> {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f));
                    Toast.makeText(MapsActivity.this,
                            "Indo para a sua localização.", Toast.LENGTH_SHORT).show();
                    FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                    Task<Location> task = fusedLocationProviderClient.getLastLocation();
                    task.addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Definir o nível de zoom desejado (ajuste conforme necessário)
                            float zoomLevel = 20.0f;

                            // Movimentar a câmera para a localização atual do usuário com o zoom desejado
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
                        }
                    });

                    return false;
                });
        mMap.setOnMyLocationClickListener(
                location -> Toast.makeText(MapsActivity.this,
                        "Você está aqui!", Toast.LENGTH_SHORT).show());

        mMap.setMyLocationEnabled(this.fine_location);

        //findViewById(R.id.button_location).setEnabled(this.fine_location);
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



    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }




}