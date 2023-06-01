package pdm.pratica04;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCPF;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Referenciar os elementos da interface do usuário
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCPF = findViewById(R.id.editTextCPF);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Definir um listener para o botão de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String cpf = editTextCPF.getText().toString();

                // Aqui você pode adicionar a lógica de registro do usuário
                // Por exemplo, criar um novo usuário no banco de dados

                // Exemplo simples para exibir uma mensagem de sucesso no registro
                Toast.makeText(RegistroActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
