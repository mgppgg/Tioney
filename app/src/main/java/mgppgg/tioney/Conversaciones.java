package mgppgg.tioney;

import android.content.Intent;
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

import java.util.ArrayList;

import mgppgg.tioney.Chat;
import mgppgg.tioney.Conver_listaConvers;
import mgppgg.tioney.R;

public class Conversaciones extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseListAdapter<Conver_listaConvers> adapter;
    private ListView listOfConvers;
    private FirebaseUser user;
    private ArrayList<Conver_listaConvers> convers;
    private ArrayList<String> keys;
    private Button borrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversaciones);

        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        convers = new ArrayList<>();
        keys = new ArrayList<>();

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
                keys.add(adapter.getRef(position).getKey());
                borrar = (Button)v.findViewById(R.id.BTNborrarConver);

                borrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
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
                });
            }


        };

        listOfConvers.setAdapter(adapter);


        listOfConvers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), Chat.class);
                intent.putExtra("conversaciones",true);
                intent.putExtra("url",convers.get(position).getChatUrl());
                startActivity(intent);
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }
}
