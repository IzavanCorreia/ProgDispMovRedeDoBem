package pdm.pratica04.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pdm.pratica04.R;
import pdm.pratica04.model.RegisterRequest;
import pdm.pratica04.service.MyApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextCPF;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Referenciar os elementos da interface do usuário
        editTextName = findViewById(R.id.editTextName);
        editTextCPF = findViewById(R.id.editTextCPF);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Definir um listener para o botão de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String cpf = editTextCPF.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                String firstName ="";
                String lastName ="";

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.117:8000/") // Substitua pela sua URL base
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                MyApiService apiService = retrofit.create(MyApiService.class);

                RegisterRequest registerRequest = new RegisterRequest(name, password, confirmPassword, email, firstName, lastName);
                Call<Void> call = apiService.register(registerRequest);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Registro bem-sucedido
                            // Execute a ação desejada, como exibir uma mensagem de sucesso

                            Toast.makeText(RegistroActivity.this, "Registro successful! "  , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Lidar com erro na resposta
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Lidar com falha na solicitação
                    }
                });
                // Aqui você pode adicionar a lógica de registro do usuário
                // Por exemplo, criar um novo usuário no banco de dados

                // Exemplo simples para exibir uma mensagem de sucesso no registro
                Toast.makeText(RegistroActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
