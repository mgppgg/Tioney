package mgppgg.tioney;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Chat extends AppCompatActivity{

        private FirebaseListAdapter <Mensaje_chat> adapter;
        private ListView listOfMessages;
        private DatabaseReference database;
        private FirebaseAuth mAuth;
        private String IDuser;

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
        final FirebaseUser user = mAuth.getCurrentUser();

        IDuser = getIntent().getStringExtra("Id");

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_chat);
        listOfMessages = (ListView)findViewById(R.id.list_chat);
        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listOfMessages.setStackFromBottom(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                /*FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new Mensaje_chat(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        );*/

                database.child(user.getUid()).child(IDuser).setValue(new Mensaje_chat(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));

                // Clear the input
                input.setText("");
            }
        });

        mostrar_msgs();
    }


    public void mostrar_msgs(){
        adapter = new FirebaseListAdapter<Mensaje_chat>(this, Mensaje_chat.class, R.layout.mensaje, FirebaseDatabase.getInstance().getReference()) {
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
