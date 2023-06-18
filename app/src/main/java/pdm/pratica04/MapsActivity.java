package pdm.pratica04;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import pdm.pratica04.databinding.ActivityMapsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private boolean isDonor = false; // declare isDonor as a field

    private static final int FINE_LOCATION_REQUEST = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean fine_location;
    private String markerTitle;


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000")
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

    private void showEditDialog(Marker marker) {
        // Obter a lista de itens doados do marcador
        String snippet = marker.getSnippet();
        List<String> items = new ArrayList<>();
        if (snippet != null && !snippet.isEmpty()) {
            String[] parts = snippet.split(":\\n")[1].split("\\n");
            for (String part : parts) {
                items.add(part.substring(3));
            }
        }

        // Criar um layout personalizado para a caixa de diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (String item : items) {
            // Adicionar uma entrada de texto para cada item doado
            EditText editText = new EditText(this);
            editText.setText(item);
            layout.addView(editText);
        }

        // Adicionar um botão "Adicionar item"
        Button addButton = new Button(this);
        addButton.setText("Adicionar item");
        addButton.setOnClickListener(view -> {
            EditText editText = new EditText(this);
            editText.setHint("Digite o nome do item");
            layout.addView(editText);
        });
        layout.addView(addButton);

        // Exibir a caixa de diálogo para editar os itens
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar itens doados")
                .setView(layout)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    // Atualizar os itens doados no marcador
                    StringBuilder newSnippet = new StringBuilder("Itens doados:\n");
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View child = layout.getChildAt(i);
                        if (child instanceof EditText) {
                            String itemName = ((EditText) child).getText().toString().trim();
                            if (!itemName.isEmpty()) {
                                newSnippet.append(String.format("%d- %s\n", i + 1, itemName));
                            }
                        }
                    }
                    if (newSnippet.toString().equals("Itens doados:\n")) {
                        marker.setSnippet("");
                    } else {
                        marker.setSnippet(newSnippet.toString());
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.cancel();
                })
                .show();
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
                    for (Centro centro : centros) {
                         nome = centro.getNome();
                        id = centro.getId();
                         latitude = Double.parseDouble(centro.getLatitude());
                         longitude = Double.parseDouble(centro.getLongitude());
                        teste = new LatLng(latitude, longitude);

                        if (centro.isCentro()) {
                            markerTitle = "Centro de doação";
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        } else {
                            markerTitle = "Pessoa";
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        }

                         centrodedoacaoemrecife = mMap.addMarker(new MarkerOptions().
                                position(teste).
                                title(nome).

                                snippet("Recebendo:\n- Item 1\n- Item 2\n- Item 3\n- Item 4\n- Item 5").
                                icon(icon));
                        centrodedoacaoemrecife.setTag(id);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(centrodedoacaoemrecife.getPosition()));


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

        // Restante do código para adicionar marcadores e manipular eventos do mapa...



        mMap.setOnMarkerClickListener(marker -> {
            // Verificar se o marcador foi criado pelo usuário (e não pelo sistema)
            if (!marker.getTitle().equals("Localização atual")) {
                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                if (snippet == null || snippet.isEmpty()) {
                    snippet = "Nenhum item doado registrado";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String finalSnippet = snippet;
                builder.setTitle(title)
                        .setMessage(snippet)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setNegativeButton("Visualizar", (dialog, which) -> {
                            // Abrir um novo AlertDialog para visualizar os itens doados
                            AlertDialog.Builder viewBuilder = new AlertDialog.Builder(this);
                            viewBuilder.setTitle("Itens doados");

                            // Configurar os itens doados
                            final LinearLayout layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            viewBuilder.setView(layout);

                            // Adicionar os itens doados como campos de texto desabilitados
                            String[] items = finalSnippet.split("\n");
                            for (int i = 1; i < items.length; i++) {
                                TextView itemText = new TextView(this);
                                itemText.setText(items[i].substring(3)); // Ignorar o número de item (exemplo: "1- Arroz" -> "Arroz")
                                itemText.setEnabled(false);
                                layout.addView(itemText);
                            }

                            // Configurar o botão "OK"
                            viewBuilder.setPositiveButton("OK", (viewDialog, viewWhich) -> {
                                // Não fazer nada
                            });
                            viewBuilder.show();
                        })
                        .setNeutralButton("Deletar", (dialog, which) -> {
                            // Remover o marcador do mapa
                            Toast.makeText(getApplicationContext(), "Mensagem de exemplo", Toast.LENGTH_SHORT).show();

                            Object tag = marker.getTag();
                            if (tag != null) {
                                int centroId = (int) tag;
                                Toast.makeText(getApplicationContext(), "Mensagem de exemplo", Toast.LENGTH_SHORT).show();

                                deleteCentro(centroId);

                                // Use o ID do centro conforme necessário
                            } else {
                                // O marcador não possui um ID associado
                            }
                            marker.remove();
                        })
                        .setNegativeButton("Editar", (dialog, which) -> {
                            // Abrir um novo AlertDialog para editar os itens doados
                            AlertDialog.Builder editBuilder = new AlertDialog.Builder(this);
                            editBuilder.setTitle("Editar itens doados");

                            // Configurar os itens doados
                            final List<String> donatedItems = new ArrayList<>();
                            final LinearLayout layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            editBuilder.setView(layout);

                            // Adicionar os itens doados atuais como campos de edição de texto pré-preenchidos
                            String[] items = finalSnippet.split("\n");
                            for (int i = 1; i < items.length; i++) {
                                final EditText itemInput = new EditText(this);
                                itemInput.setText(items[i].substring(3)); // Ignorar o número de item (exemplo: "1- Arroz" -> "Arroz")
                                layout.addView(itemInput);
                                donatedItems.add(items[i].substring(3));
                            }

                            // Botão "Adicionar item"
                            Button addButton = new Button(this);
                            addButton.setText("Adicionar item");
                            addButton.setOnClickListener(view -> {
                                final EditText itemInput = new EditText(this);
                                itemInput.setHint("Digite o nome do item");
                                layout.addView(itemInput);
                            });
                            layout.addView(addButton);

                            // Configurar os botões "Salvar" e "Cancelar"
                            editBuilder.setPositiveButton("Salvar", (editDialog, editWhich) -> {
// Criar um novo snippet com os itens doados atualizados
                                    StringBuilder newSnippet = new StringBuilder();
                            newSnippet.append("Itens doados:\n");
                            int itemCount = 1;
                            for (int i = 0; i < layout.getChildCount(); i++) {
                                View child = layout.getChildAt(i);
                                if (child instanceof EditText) {
                                    String itemName = ((EditText) child).getText().toString();
                                    if (!itemName.isEmpty()) {
                                        newSnippet.append(itemCount).append("- ").append(itemName).append("\n");
                                        donatedItems.add(itemName);
                                        itemCount++;
                                    }
                                }
                            }                    // Atualizar o snippet do marcador e exibir uma mensagem de sucesso
                            marker.setSnippet(newSnippet.toString());
                            Toast.makeText(this, "Itens doados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", (editDialog, editWhich) -> {
                            // Não fazer nada
                        })
                        .show();
            });

            return true;
        }

        return false;
    });









        mMap.setOnMarkerClickListener(marker -> {
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

            // Botão "Adicionar item"
            Button addButton = new Button(this);
            addButton.setText("Adicionar item");
            addButton.setOnClickListener(view -> {
                final EditText itemInput = new EditText(this);
                itemInput.setHint("Digite o nome do item");
                layout.addView(itemInput);
            });
            layout.addView(addButton);

            // Configurar os botões "OK" e "Cancelar"
            builder.setPositiveButton("OK", (dialog, which) -> {
                // Adicionar marcador com título e ícone baseado na opção selecionada
                markerTitle = isDonor ? "Doador" : "Centro de doação";
                BitmapDescriptor icon = isDonor ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE) : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(markerTitle)
                        .icon((icon));
                mMap.addMarker(markerOptions);

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
                    Toast.makeText(MapsActivity.this,
                            "Indo para a sua localização.", Toast.LENGTH_SHORT).show();
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