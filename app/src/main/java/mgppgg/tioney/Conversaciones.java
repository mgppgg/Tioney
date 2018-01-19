package mgppgg.tioney;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

import mgppgg.tioney.Chat;
import mgppgg.tioney.Conver_listaConvers;
import mgppgg.tioney.R;

public class Conversaciones extends BaseActivity {

    private DatabaseReference database;
    private FirebaseListAdapter<Conver_listaConvers> adapter;
    private ListView listOfConvers;
    private FirebaseUser user;
    private ArrayList<Conver_listaConvers> convers;
    private Button borrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversaciones);

        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        convers = new ArrayList<>();

        listOfConvers = (ListView)findViewById(R.id.list_conver);
        listOfConvers.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        //listOfConvers.setStackFromBottom(true);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        adapter = new FirebaseListAdapter<Conver_listaConvers>(this, Conver_listaConvers.class, R.layout.conversacion, database.child("Usuarios").child(user.getUid()).child("Chats")) {
            @Override
            protected void populateView(View v, final Conver_listaConvers conver, final int position) {
                // Get references to the views of message.xml
                TextView Usuario = (TextView)v.findViewById(R.id.TVusuario);
                Usuario.setText(conver.getUser());
                convers.add(conver);
                borrar = (Button)v.findViewById(R.id.BTNborrarConver);

                borrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        confirmacion(conver,position);
                    }
                });
            }


        };

        if(isOnlineNet())listOfConvers.setAdapter(adapter);
        else
            snack1();


        listOfConvers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Chat.class);
                intent.putExtra("conversaciones",true);
                intent.putExtra("conver", convers.get(position));
                startActivity(intent);
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    public void borrarChat(final Conver_listaConvers conver,final int position){

        database.child("Usuarios").child(user.getUid()).child("Chats").child(adapter.getRef(position).getKey()).removeValue();
        Log.d("pos",":"+adapter.getRef(position).getKey());
        database.child("Chats").child(conver.getChatUrl()).child("Estado").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((long)dataSnapshot.getValue()==1){
                    database.child("Chats").child(conver.getChatUrl()).removeValue();
                }else
                    database.child("Chats").child(conver.getChatUrl()).child("Estado").setValue(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

    }

    public void confirmacion(final Conver_listaConvers conver,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("¿Esta seguro de borrar la conversación?");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarChat(conver,position);
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void snack1(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sin conexión a internet",  Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        View sbView = snackbar.getView();
        snackbar.setActionTextColor(Color.BLACK);
        sbView.setBackgroundColor(Color.RED);
        snackbar.setAction("ACTUALIZAR", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnlineNet())listOfConvers.setAdapter(adapter);
                else snack2();
            }
        });

        snackbar.show();
    }

    public void snack2(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sin conexión a internet",  Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        View sbView = snackbar.getView();
        snackbar.setActionTextColor(Color.BLACK);
        sbView.setBackgroundColor(Color.RED);
        snackbar.setAction("ACTUALIZAR", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnlineNet())listOfConvers.setAdapter(adapter);
                else snack1();
            }
        });

        snackbar.show();
    }
}
