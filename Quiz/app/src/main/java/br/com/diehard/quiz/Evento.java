package br.com.diehard.quiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.com.pi.pi4.GroupSelectionActivity;

public class Evento extends AppCompatActivity {

    private EditText edit_id_evento;
    private Button btn_confirmar_evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_evento);

            edit_id_evento = (EditText) findViewById(R.id.edit_id_evento);
            btn_confirmar_evento = (Button) findViewById(R.id.btn_confirmar_evento);

            btn_confirmar_evento.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {



                    ParticipanteSingleton ps = ParticipanteSingleton.getInstance();
                    ps.codEvento = Integer.parseInt(edit_id_evento.getText().toString().trim());
                    Intent i = new Intent(Evento.this, GroupSelectionActivity.class);
                    Bundle b = new Bundle();
                    b.putString("evento", edit_id_evento.getText().toString().trim()); //Your id
                    b.putString("participanteId", ps.codParticipante.toString()); //Your id
                    b.putString("proximaTela", Tela_Aquecimento.class.getName());
                    i.putExtras(b); //Put your id to your next Intent
                    startActivity(i);


                    /*Intent i = new Intent(Evento.this, Tela_Aquecimento.class);
                    i.putExtra("idEvento",edit_id_evento.getText().toString().trim());
                    startActivity(i);*/
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Servidor com problema", Toast.LENGTH_LONG).show();
        }
    }

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
