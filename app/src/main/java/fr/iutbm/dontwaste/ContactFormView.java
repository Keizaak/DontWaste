package fr.iutbm.dontwaste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactFormView extends AppCompatActivity {
    private EditText textTo;
    private EditText textSubject;
    private EditText textMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form_view);

        textTo = findViewById(R.id.txtTo);
        textTo.setText("dontwaste.devteam@gmail.com");
        textSubject = findViewById(R.id.txtSubject);
        textMessage = findViewById(R.id.txtMessage);

        Button buttonSend = findViewById(R.id.btnSend);
        buttonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendMail();
            }
        });
    }

    private void sendMail(){
        String sendTo = textTo.getText().toString();
        String subject = textSubject.getText().toString();
        String message = textMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        
        startActivity(Intent.createChooser(intent, "Choose an email app :"));
    }
}
