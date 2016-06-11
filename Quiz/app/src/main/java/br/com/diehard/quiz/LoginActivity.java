package br.com.diehard.quiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

import static br.com.diehard.quiz.Validador.validateEmail;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.email_login) EditText emailLogin;
    @Bind(R.id.senha_login) EditText senhaLogin;
    @Bind(R.id.btn_acesso) Button btnacesso;

    private Resources resources;
    private String email;
    private String senha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViews();

        btnacesso.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


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


        }
    private void callClearErrors(Editable s) {
        if (!s.toString().isEmpty()) {
            clearErrorFields(emailLogin);
        }
    }

    public void login(){

            if (validar()){

                Log.d(TAG, "Login");

                if (!validar()) {
                    onLoginFailed();
                    return;
                }

                btnacesso.setEnabled(false);

                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme_PopupOverlay);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Autenticando...");
                progressDialog.show();

                email = emailLogin.getText().toString().trim();
                senha = senhaLogin.getText().toString().trim();

                // TODO: Implement your own authentication logic here.

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                onLoginSuccess();
                                // onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 5000);

                //SERVICES
                Network e = new Network();
                e.execute((Void)null);

                //msg de auteticado
                //Toast.makeText(this, resources.getString(R.string.autentication), Toast.LENGTH_LONG).show();
            }
        }
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        btnacesso.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        btnacesso.setEnabled(true);
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

    //SERVICO
    public class Network extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground (Void... params)
        {
            URL url = null;
            String result = "";

            try {
                //TODO: colocar o caminho certo do servidor
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/participante/"+ email +"/"+ senha);

                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                InputStream in = con.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while((inputStr = streamReader.readLine()) != null )
                    responseStrBuilder.append(inputStr);

                result = responseStrBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)
        {
            if(result != "") {
                try {
                    JSONObject json = new JSONObject(result);

                    //salvar no session e mudar para a proxima tela
                    ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
                    ps.codParticipante = json.getInt("codParticipante");
                    ps.email = json.getString("email");
                    ps.nome = json.getString("nmParticipante");

                    //proxima activity
                    Intent i = new Intent(LoginActivity.this, Evento.class);
                    startActivity(i);

                   /* Intent i = new Intent(LoginActivity.this, GroupSelectionActivity.class);
                    Bundle b = new Bundle();
                    b.putString("evento", "34409"); //Your id
                    b.putString("participanteId", json.getString("codParticipante")); //Your id
                    b.putString("proximaTela", Tela_Aquecimento.class.getName());
                    i.putExtras(b); //Put your id to your next Intent
                    startActivity(i);*/

                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Usuario ou/e senha invalido", Toast.LENGTH_LONG).show();
            }
        }
    }

    //sobreescrita para inutilizar o botao voltar
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK)
        {
            return true;
        }
        return super.onKeyDown(keycode, event);
    }
}