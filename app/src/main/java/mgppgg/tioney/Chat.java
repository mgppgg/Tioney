package mgppgg.tioney;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
        private AnunDatabase anun;
        private Conver_firebase conver;
        private String chatUrl, key_chat,nombreUsuario;
        private boolean conversaciones = false;
        private boolean borrada = false;
        private boolean existe = true;
        private boolean notificaciones = true;

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

        anun = (AnunDatabase) getIntent().getSerializableExtra("anuncio");
        conversaciones = getIntent().getExtras().getBoolean("conversaciones");
        conver =(Conver_firebase) getIntent().getSerializableExtra("conver");
        key_chat = getIntent().getExtras().getString("key_chat");
        nombreUsuario = user.getDisplayName();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_chat);
        listOfMessages = (ListView)findViewById(R.id.list_chat);
        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listOfMessages.setStackFromBottom(true);

        if(conversaciones)chatUrl = conver.getChatUrl();
        else
            chatUrl = user.getUid() + "--" + anun.getUID();

        database.child("Chats").child(chatUrl).child("Estado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    if ((long)dataSnapshot.getValue() == 1) borrada = true;
                }else
                    existe = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText input = (EditText)findViewById(R.id.input);

               if(!input.getText().toString().isEmpty()) {

                    if(!existe){
                        final Conver_firebase c1 = new Conver_firebase(anun.getUsuario(), chatUrl,0);
                        final Conver_firebase c2 = new Conver_firebase(user.getDisplayName(), chatUrl,1);
                        database.child("Usuarios").child(user.getUid()).child("Chats").child(anun.getUID()).setValue(c1);
                        database.child("Usuarios").child(anun.getUID()).child("Chats").child(user.getUid()).setValue(c2);
                        database.child("Chats").child(chatUrl).child("Estado").setValue(0);
                        existe = true;
                    }
                    else{

                        if(borrada && conversaciones){
                            final Conver_firebase c2 = new Conver_firebase(user.getDisplayName(),chatUrl,1);
                            database.child("Usuarios").child(key_chat).child("Chats").child(user.getUid()).setValue(c2);
                            database.child("Chats").child(chatUrl).child("Estado").setValue(0);
                            borrada = false;
                        }

                        if(borrada && !conversaciones){
                            final Conver_firebase c1 = new Conver_firebase(anun.getUsuario(), chatUrl,1);
                            database.child("Usuarios").child(user.getUid()).child("Chats").child(user.getUid()).setValue(c1);
                            database.child("Chats").child(chatUrl).child("Estado").setValue(0);
                            borrada = false;
                        }

                    }

                    if(notificaciones && conversaciones){
                        database.child("Notificaciones").child(key_chat).push().child("from").setValue(user.getUid());
                        database.child("Usuarios").child(key_chat).child("Chats").child(user.getUid()).child("nuevo_msg").setValue(1);
                        notificaciones = false;
                    }
                    if(notificaciones && !conversaciones){
                       database.child("Notificaciones").child(anun.getUID()).push().child("from").setValue(user.getUid());
                       database.child("Usuarios").child(anun.getUID()).child("Chats").child(user.getUid()).child("nuevo_msg").setValue(1);
                       notificaciones = false;
                    }

                    database.child("Chats").child(chatUrl).child("Mensajes").push()
                           .setValue(new Mensaje_chat(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));



               }

                input.setText("");
            }
        });

        mostrar_msgs(database.child("Chats").child(chatUrl).child("Mensajes"));
    }

    @TargetApi(24)
    public void mostrar_msgs(DatabaseReference ref){
        adapter = new FirebaseListAdapter<Mensaje_chat>(this, Mensaje_chat.class, R.layout.mensaje, ref) {
            @Override
            protected void populateView(View v, Mensaje_chat model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

                if(!model.getMessageUser().equals(nombreUsuario)){
                    v.setBackgroundResource(R.drawable.bubble_recibir);
                    messageText.setTextColor(Color.parseColor("#000000"));
                    messageTime.setTextColor(Color.parseColor("#000000"));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)messageText.getLayoutParams();
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_END);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)messageTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    params2.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params2.removeRule(RelativeLayout.ALIGN_PARENT_START);

                }
                else{
                    v.setBackgroundResource(R.drawable.bubble_enviar);
                    messageText.setTextColor(Color.parseColor("#FFFFFF"));
                    messageTime.setTextColor(Color.parseColor("#FFFFFF"));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)messageText.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_START);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)messageTime.getLayoutParams();
                    params2.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params2.removeRule(RelativeLayout.ALIGN_PARENT_END);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                }

                if(!model.getMessageUser().equals(nombreUsuario)){

                }
                else{

                }



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
