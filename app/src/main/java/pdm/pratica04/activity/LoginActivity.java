package pdm.pratica04.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pdm.pratica04.R;
import pdm.pratica04.model.AccessTokenResponse;
import pdm.pratica04.model.LoginRequest;
import pdm.pratica04.service.MyApiService;
import retrofit2.Call;
import retrofit2.Callback;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements TokenCallback {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referenciar os elementos da interface do usuário
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Definir um listener para o botão de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.89.189:8000/") // Substitua pela sua URL base
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                MyApiService apiService;

                apiService = retrofit.create(MyApiService.class);

                // Código para acionar o login, por exemplo, quando o botão de login for clicado
                String username = email;

                LoginRequest loginRequest = new LoginRequest(username, password);
                Call<AccessTokenResponse> call = apiService.login(loginRequest);

                call.enqueue(new Callback<AccessTokenResponse>() {
                    @Override
                    public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                        if (response.isSuccessful()) {
                            AccessTokenResponse tokenResponse = response.body();
                            String accessToken = tokenResponse.getAccessToken();

                            // Envie o accessToken para uma nova activity
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            intent.putExtra("access_token", accessToken);
                            Toast.makeText(LoginActivity.this, "Login successful! acess aqui: " + accessToken, Toast.LENGTH_SHORT).show();

                            startActivity(intent);
                            finish();
                        } else {
                            // Lidar com erro na resposta
                            Toast.makeText(LoginActivity.this, "Login FALHOU! acess aqui: ", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                        // Lidar com falha na solicitação
                    }
                });


                // Aqui você pode adicionar a lógica de validação do login
                // Por exemplo, verificar se o email e a senha são válidos

                // Exemplo simples para exibir uma mensagem de sucesso no login
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });

        // Definir um listener para o link de registro
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre a tela de registro
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onTokenReceived(String accessToken) {

    }
}
