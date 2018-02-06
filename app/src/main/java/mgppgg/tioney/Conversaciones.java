package mgppgg.tioney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Conversaciones extends BaseActivity {

    private DatabaseReference database;
    private FirebaseListAdapter<Conver_firebase> adapter;
    private ListView listOfConvers;
    private FirebaseUser user;
    private ArrayList<Conver_firebase> convers;
    private ImageButton borrar;
    private TextView noConver;
    private ArrayList<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversaciones);

        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        convers = new ArrayList<>();
        keys = new ArrayList<>();

        listOfConvers = (ListView)findViewById(R.id.list_conver);
        noConver = (TextView)findViewById(R.id.TVnoAnun3);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        adapter = new FirebaseListAdapter<Conver_firebase>(this, Conver_firebase.class, R.layout.card_conversacion, database.child("Usuarios").child(user.getUid()).child("Chats")) {
            @Override
            protected void populateView(View v, final Conver_firebase conver, final int position) {
                noConver.setVisibility(View.GONE);
                TextView Usuario = (TextView)v.findViewById(R.id.TVusuario);
                ImageView nuevo_msg = (ImageView)v.findViewById(R.id.IVnuevo_msg);
                if(conver.getNuevo_msg()==1)nuevo_msg.setVisibility(View.VISIBLE);
                Usuario.setText(conver.getUser());
                convers.add(conver);
                borrar = (ImageButton)v.findViewById(R.id.BTNborrarConver);
                keys.add(adapter.getRef(position).getKey());

                borrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(isOnlineNet())confirmacion(conver,position);
                        else snackBar("Conexión a internet necesaria");
                    }
                });
            }


        };

        if(isOnlineNet())listOfConvers.setAdapter(adapter);
        else snackBar("Sin conexión a internet");


        listOfConvers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isOnlineNet()){
                    Log.d("eeeeeeeeeeeeeeee", convers.get(position).getUser());
                    ImageView nuevo_msg = (ImageView)view.findViewById(R.id.IVnuevo_msg);
                    nuevo_msg.setVisibility(View.GONE);
                    database.child("Usuarios").child(user.getUid()).child("Chats").child(keys.get(position)).child("nuevo_msg").setValue(0);
                    Intent intent = new Intent(getBaseContext(), Chat.class);
                    intent.putExtra("conversaciones",true);
                    intent.putExtra("conver", convers.get(position));
                    intent.putExtra("key_chat", keys.get(position));
                    startActivity(intent);

                }
                else snackBar("Conexión a internet necesaria");

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }

    public void borrarChat(final Conver_firebase conver, final int position){

        database.child("Usuarios").child(user.getUid()).child("Chats").child(keys.get(position)).removeValue();
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

    public void confirmacion(final Conver_firebase conver, final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("¿Está seguro de borrar la conversación?");
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

}
