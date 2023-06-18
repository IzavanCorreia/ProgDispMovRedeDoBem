package pdm.pratica04;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;



    public void getToken(String username, String password) {
        String requestBodyString = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyString);

        Call<TokenResponse> call = MyApiService.getToken(requestBody);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    TokenResponse tokenResponse = response.body();
                    String accessToken = tokenResponse.getAccess();
                    String refreshToken = tokenResponse.getRefresh();
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    intent.putExtra("access_token", accessToken);
                    startActivity(intent);
                    // Manipule a resposta bem-sucedida aqui
                    // Faça algo com o access token e refresh token
                } else {
                    // Manipule a resposta de erro aqui
                    String errorMessage = response.message();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                // Manipule a falha de rede ou outros erros aqui
            }
        });
    }

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


                getToken(email, password);
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

}
