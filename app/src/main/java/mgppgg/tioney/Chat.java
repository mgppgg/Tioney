package mgppgg.tioney;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Chat extends AppCompatActivity{

        private FirebaseListAdapter <Mensaje_chat> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_chat);
        ListView listOfMessages = (ListView)findViewById(R.id.list_chat);

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
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new Mensaje_chat(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });
    }
}
