package br.com.diehard.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JogoVerdadeiro extends AppCompatActivity {

    private int idEvento;
    private int idGrupo;
    private int idQuestao;
    private Boolean blnResposta;

    private TextView pergunta;
    private Button btn_true;
    private Button btn_false;


    private Context context;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo_verdadeiro);

        ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
        //TODO:colocar o grupo nosingleton
        idGrupo = ps.codGrupo;

        //get id do evento
        Intent intent = getIntent();
        idEvento = intent.getIntExtra("idEvento", 0);

        context = this;
        pergunta = (TextView) findViewById(R.id.texto_pergunta);

        btn_true = (Button) findViewById(R.id.btn_true);
        btn_true.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                blnResposta = true;
            NetworkResposta r = new NetworkResposta();
            r.execute((Void) null);
            }
        });

        btn_false = (Button) findViewById(R.id.btn_false);
        btn_false.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                blnResposta = false;
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
                //TODO: colocar o caminho certo do servidor
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
            boolean statusQuestao = false;

            //TODO:apagar essa linha depois
            result = "{\"codTipoQuestao\":\"V\",\"codQuestao\":5,\"textoQuestao\":\"Você sabe como foi feito o mundo?\",\"alternativas\":null,\"tempo\":null,\"codAssunto\":5}";
            try {
                JSONObject json = new JSONObject(result);

                pergunta.setText(json.getString("textoQuestao"));
                idQuestao = json.getInt("codQuestao");

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
                url = new URL("http://tsitomcat.azurewebsites.net/quiz/rest/resposta/"+idGrupo+"/"+idQuestao+"/0/"+blnResposta);

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
            boolean statusQuestao = false;

            //TODO:apagar essa linha depois
            result = "true";
            try {
                //JSONObject json = new JSONObject(result);
                if(result.equals("true"))
                {
                    Intent i = new Intent(JogoVerdadeiro.this, Tela_Aquecimento.class);
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

}
