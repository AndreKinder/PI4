package br.com.diehard.quiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static br.com.diehard.quiz.Validador.validateEmail;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText emailLogin;
    private EditText senhaLogin;
    private Button btnacesso;
    private Resources resources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }
    private void initViews() {
        resources = getResources();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                callClearErrors(s);
            }
        };


            emailLogin=(EditText)

            findViewById(R.id.email_login);

            emailLogin.addTextChangedListener(textWatcher);
            senhaLogin=(EditText)

            findViewById(R.id.senha_login);

            senhaLogin.addTextChangedListener(textWatcher);
            btnacesso=(Button)

            findViewById(R.id.btn_acesso);

            btnacesso.setOnClickListener(this);
        }
    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(emailLogin);
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId()==R.id.btn_acesso){

            Intent i = new Intent(LoginActivity.this, TelaValidacao.class);
            startActivity(i);

            if (validar()){
                /**
                 * WebService de autenticação do Usuário
                 */

                Toast.makeText(this, resources.getString(R.string.autentication), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     * Validação dos Campos. Caso se os campos estejam vazios.
     */
    private boolean validar() {

        String email = emailLogin.getText().toString().trim();
        String password = senhaLogin.getText().toString().trim();
        return (!isEmptyFields(email, password));
    }



    private boolean isEmptyFields(String email, String password){

        if (TextUtils.isEmpty(email)) {
            emailLogin.requestFocus();
            emailLogin.setError(resources.getString(R.string.user_required));
            return true;
        } else if (TextUtils.isEmpty(password)) {
            senhaLogin.requestFocus();
            senhaLogin.setError(resources.getString(R.string.pass_required));
            return true;
        }

    boolean email_valido = validateEmail(email);{
            if (!email_valido) {
                emailLogin.setError("Email inválido");
                emailLogin.setFocusable(true);
                emailLogin.requestFocus();
            }
        return false;
        }
    }


    private void clearErrorFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setError(null);
        }
    }
}