package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private List<Message> messages = new ArrayList<>(); //elements of the listview
    MyListAdapter myAdapter = new MyListAdapter();
    DetailsFragment fragment;
    MessageRepository messageRepository;

    Button sendBtn;
    Button receiveBtn;
    EditText editText;
    ListView listView;
    boolean isTablet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        messageRepository = new MessageRepository(this);
        load();

        //If layout id is null, device is phone, else a tablet
        if(findViewById(R.id.flayout) == null){
            isTablet = false;
        }

        listView = findViewById(R.id.listView);
        listView.setAdapter(myAdapter);

        editText = findViewById(R.id.edittextchat);

        sendBtn = findViewById(R.id.Sendbtn);
        sendBtn.setOnClickListener(click -> {
            addMessage(editText.getText().toString(), Message.Type.SENT);
            editText.setText(null); //resetting the text to blank
        });

        receiveBtn = findViewById(R.id.Receivebtn);
        receiveBtn.setOnClickListener(click -> {
            addMessage(editText.getText().toString(), Message.Type.RECEIVED);
            editText.setText(null); //resetting the text to blank
        });

        //shows alert dialogues when a row is clicked on for longer
        listView.setOnItemLongClickListener(((parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.
                    setTitle(getString(R.string.delete))
                    .setMessage(
                            getString(R.string.row) + position + "\n" +
                                    getString(R.string.database) + myAdapter.getItemId(position)
                    )
                    .setPositiveButton(getString(R.string.yes), (click, arg) -> {
                        messageRepository.delete((Message) myAdapter.getItem(position));
                        messages.remove(position);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.no, (click, arg) -> {
                    })
                    .show();

            return false;
        }));

        listView.setOnItemClickListener((list, item, position, id) -> {
            Bundle dataToPass = new Bundle();
            Message message = messages.get(position);
            dataToPass.putString("message", message.getText());
            dataToPass.putLong("id", message.getId());
            dataToPass.putBoolean("status", message.getType().ordinal() == 0);
            dataToPass.putBoolean("onTablet", isTablet);

            if (isTablet) {
                fragment = new DetailsFragment();
                fragment.setArguments(dataToPass);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flayout, fragment)
                        .commit();
            } else {
                Intent nextActivity =
                        new Intent(ChatRoomActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass);
                startActivity(nextActivity);
            }

        });
    }

    //load data from the database and notify the adapter
    private void load(){
        messages.addAll(messageRepository.findAll());
        myAdapter.notifyDataSetChanged();
    }

    //add a new message to the messages list then notify adapter
    private void addMessage(String text, Message.Type type){
        Message message = new Message(text, type);
        messages.add(messageRepository.save(message));
        myAdapter.notifyDataSetChanged();
    }

    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            View newView = view;
            LayoutInflater inflater = getLayoutInflater();

            if (messages.get(position).getType() == Message.Type.SENT) {
                newView = inflater.inflate(R.layout.row_receive, parent, false);
            } else if (messages.get(position).getType() == Message.Type.RECEIVED) {
                newView = inflater.inflate(R.layout.row_send, parent, false);
            }

            TextView messageText = newView.findViewById(R.id.message);
            messageText.setText(messages.get(position).getText());

            return newView;

            }
        }
    }