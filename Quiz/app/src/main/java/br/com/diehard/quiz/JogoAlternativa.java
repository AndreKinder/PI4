package br.com.diehard.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JogoAlternativa extends AppCompatActivity {

    private int idEvento;
    private int idGrupo;
    private int idQuestao;
    private String idAlternativa;

    private Context context;
    private ProgressDialog progress;
    private ViewGroup group;
    private Button enviar;

    private TextView pergunta;
    private EditText respoderAlternativa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo_alternativa);

        ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
        idGrupo = ps.codGrupo;
        idEvento = ps.codEvento;


        context = this;
        pergunta = (TextView) findViewById(R.id.texto_pergunta);
        respoderAlternativa = (EditText) findViewById(R.id.edit_id_alternativa);

        enviar = (Button) findViewById(R.id.btn_confirmar_alternatviva);
        enviar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                idAlternativa = respoderAlternativa.getText().toString().trim();

                NetworkResposta r = new NetworkResposta();
                r.execute((Void) null);
            }
        });

        NetworkQuestao e = new NetworkQuestao();
        e.execute((Void) null);
    }

    //Servico de buscar questao ativa
    public class NetworkQuestao extends AsyncTask<Void, Void, String>
    {
        protected void onPreExecute(){
            progress = ProgressDialog.show(context, "Aguarde", "Carregando questão", true);
        }

        protected String doInBackground (Void... params)
        {
            URL url = null;
            String result = "";

            try {
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/questao/"+idEvento+"/"+idGrupo);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream in = con.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                result = responseStrBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)
        {
            //TODO:apagar essa linha depois
            result = "{\"codTipoQuestao\":\"A\",\"textoQuestao\":\"Teste 2\",\"tempo\":null,\"alternativas\":[{\"textoAlternativa\":\"Alternativa 1\",\"codAlternativa\":1,\"codQuestao\":null},{\"textoAlternativa\":\"Alternativa 2\",\"codAlternativa\":2,\"codQuestao\":null},{\"textoAlternativa\":\"Alternativa 3\",\"codAlternativa\":3,\"codQuestao\":null},{\"textoAlternativa\":\"Alternativa 4\",\"codAlternativa\":4,\"codQuestao\":null}],\"codQuestao\":6,\"codAssunto\":null}";
            try {
                JSONObject json = new JSONObject(result);

                pergunta.setText(json.getString("textoQuestao"));
                idQuestao = json.getInt("codQuestao");

                group = (ViewGroup) findViewById(R.id.container);

                JSONArray jsonarray = new JSONArray(json.getJSONArray("alternativas").toString());

                for(int i = 0; jsonarray.length() > i ;i++) {
                    JSONObject itemJson = jsonarray.getJSONObject(i);

                    LinearLayout item = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.alternativaitem, group, false);
                    TextView id_alternativa = (TextView) item.findViewById(R.id.id_alternativa);
                    TextView texto_alternativa = (TextView) item.findViewById(R.id.texto_alternativa);
                    //id_alternativa.setText(itemJson.getString("codAlternativa"));
                    texto_alternativa.setText(itemJson.getString("codAlternativa")+" - "+itemJson.getString("textoAlternativa"));
                    group.addView(item);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
        }
    }
    public class NetworkResposta extends AsyncTask<Void, Void, String>
    {
        protected void onPreExecute(){
            progress = ProgressDialog.show(context, "", "Salvando sua resposta", true);
        }

        protected String doInBackground (Void... params)
        {
            URL url = null;
            String result = "";

            try {
                //TODO: colocar o caminho certo do servidor
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/resposta/"+idGrupo+"/"+idQuestao+"/"+idAlternativa+"/");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream in = con.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                result = responseStrBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)
        {
            //TODO:apagar essa linha depois
            result = "true";
            try {
                //JSONObject json = new JSONObject(result);
                if(result.equals("true"))
                {
                    Intent i = new Intent(JogoAlternativa.this, Tela_Aquecimento.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
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
