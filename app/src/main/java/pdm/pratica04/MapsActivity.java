package pdm.pratica04;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pdm.pratica04.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private boolean isDonor = false; // declare isDonor as a field

    private static final int FINE_LOCATION_REQUEST = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private boolean fine_location;
    private String markerTitle;



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
    // Método para editar um marcador existente
    private void editarMarcador(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar marcador");

        // Opções de doação
        final String[] donationOptions = {"Centro de doação", "Doador"};
        int selectedOption = marker.getTitle().equals("Centro de doação") ? 0 : 1;
        builder.setSingleChoiceItems(donationOptions, selectedOption, (dialog, which) -> {
            if (which == 0) {
                // Centro de doação selecionado
                marker.setTitle("Centro de doação");
            } else if (which == 1) {
                // Doador selecionado
                marker.setTitle("Doador");
            }
        });

        // Lista de itens doados
        final List<String> donatedItems = new ArrayList<>();
        String[] items = marker.getSnippet().split(":")[1].split("\n");
        for (String item : items) {
            if (!item.isEmpty()) {
                donatedItems.add(item.trim());
            }
        }

        // Campo para adicionar ou remover itens
        final EditText input = new EditText(this);
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, donatedItems);
        final ListView listView = new ListView(this);
        listView.setAdapter(itemsAdapter);
        builder.setView(listView);
        builder.setPositiveButton("Adicionar item", (dialog, which) -> {
            String item = input.getText().toString().trim();
            if (!item.isEmpty()) {
                donatedItems.add(item);
                input.setText("");
                itemsAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Remover item", (dialog, which) -> {
            int position = listView.getCheckedItemPosition();
            if (position != ListView.INVALID_POSITION) {
                donatedItems.remove(position);
                itemsAdapter.notifyDataSetChanged();
            }
        });

        // Configurar os botões "OK" e "Cancelar"
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Atualizar informações adicionais do marcador como um snippet
            String snippet = "Itens doados:\n";
            for (String item : donatedItems) {
                snippet += "- " + item + "\n";
            }
            marker.setSnippet(snippet);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.cancel();
        });

        // Exibir o diálogo
        builder.show();
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
                snippet("Recebendo:\n- Item 1\n- Item 2\n- Item 3\n- Item 4\n- Item 5").

                icon(BitmapDescriptorFactory.defaultMarker(120)));
        mMap.addMarker(new MarkerOptions().
                position(joaopessoa).
                title("João Pessoa").
                snippet("Recebendo:\n- Item 1\n- Item 2\n- Item 3\n- Item 4\n- Item 5").

                icon(BitmapDescriptorFactory.defaultMarker(230)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centrodedoacaoemrecife));

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


    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegistro(View view) {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
    public void goToMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
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