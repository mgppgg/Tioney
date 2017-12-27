package mgppgg.tioney;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import recycler_view.Anuncio;

public class Chat extends BaseActivity{

        private FirebaseListAdapter <Mensaje_chat> adapter;
        private ListView listOfMessages;
        private DatabaseReference database;
        private FirebaseAuth mAuth;
        private FirebaseUser user;
        private Anuncio anun;
        private String chatUrl;
        private boolean conversaciones = false;
        private boolean crear = true;
        private boolean crear2 = false;
        private int n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        anun = (Anuncio) getIntent().getSerializableExtra("anuncio");
        conversaciones = getIntent().getExtras().getBoolean("conversaciones");
        chatUrl = getIntent().getExtras().getString("url");
        if(!conversaciones)chatUrl = user.getUid() + "--" + anun.getUID();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_chat);
        listOfMessages = (ListView)findViewById(R.id.list_chat);
        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listOfMessages.setStackFromBottom(true);


        database.child("Chats").child(chatUrl).child("Estado").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((int)dataSnapshot.getValue()==1)crear2 = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

               if(!input.getText().toString().isEmpty()) {

                   if (conversaciones) {

                       if(crear2){
                           final Conver_listaConvers c1 = new Conver_listaConvers(anun.getUsuario(), chatUrl);
                           final Conver_listaConvers c2 = new Conver_listaConvers(user.getDisplayName(), chatUrl);
                           database.child("Usuarios").child(user.getUid()).child("Chats").push().setValue(c1);
                           database.child("Usuarios").child(anun.getUID()).child("Chats").push().setValue(c2);
                           database.child("Chats").child(chatUrl).child("Estado").setValue(0);
                           crear2 = false;

                       }
                       database.child("Chats").child(chatUrl).child("Mensajes").push()
                               .setValue(new Mensaje_chat(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                               );

                   } else {

                        if(crear) {
                            final Conver_listaConvers c1 = new Conver_listaConvers(anun.getUsuario(), chatUrl);
                            final Conver_listaConvers c2 = new Conver_listaConvers(user.getDisplayName(), chatUrl);
                            database.child("Usuarios").child(user.getUid()).child("Chats").push().setValue(c1);
                            database.child("Usuarios").child(anun.getUID()).child("Chats").push().setValue(c2);
                            database.child("Chats").child(chatUrl).child("Estado").setValue(0);
                            crear = false;
                        }

                       database.child("Chats").child(chatUrl).child("Mensajes").push()
                               .setValue(new Mensaje_chat(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));

                   }
               }

                input.setText("");
            }
        });

        mostrar_msgs(database.child("Chats").child(chatUrl));
    }


    public void mostrar_msgs(DatabaseReference ref){
        adapter = new FirebaseListAdapter<Mensaje_chat>(this, Mensaje_chat.class, R.layout.mensaje, ref) {
            @Override
            protected void populateView(View v, Mensaje_chat model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)finish();
        return super.onOptionsItemSelected(item);
    }
}
