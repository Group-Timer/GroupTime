package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GroupChatting extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    DatabaseReference databaseReference;

    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;


    private LinearLayoutManager linearLayoutManager;
    private GroupChattingRecyclerViewAdapter GroupChattingAdapter;

    private RecyclerView ChatRecyclerView;
    private EditText ChatEdit;
    private Button ChatButton;

    private ArrayList<GroupChatRecyclerViewItem> ChatList;

    int ListSize;


    String UserUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatting);


        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat").child("Messages").child(DefineValue.Group_ID);


        UserUID = user.getUid();


        ChatRecyclerView = (RecyclerView) findViewById(R.id.chattingRecyclerView);
        ChatEdit = (EditText) findViewById(R.id.chatEdit);
        ChatButton = (Button) findViewById(R.id.chatButton);


        ChatEdit.setOnClickListener(this);
        ChatButton.setOnClickListener(this);


        ChatList = new ArrayList<GroupChatRecyclerViewItem>();


        linearLayoutManager = new LinearLayoutManager(this);
        if(linearLayoutManager == null)
        {
            Log.d("GT", "LayoutManager is null");
        }
        linearLayoutManager.setStackFromEnd(true);

        ChatRecyclerView.setLayoutManager(linearLayoutManager);


        GroupChattingAdapter = new GroupChattingRecyclerViewAdapter(ChatList);
        ChatRecyclerView.setAdapter(GroupChattingAdapter);


        //Load_Chat();
        Update_Chat();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();


        databaseReference.removeEventListener(childEventListener);
    }

/*
    private void Load_Chat()
    {
        valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot == null)
                {
                    return;
                }


                for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    if(dataSnapshot.getValue() == null)
                    {
                        Log.d("GT", "Null chat message");
                    }
                    else
                    {
                        GroupChatRecyclerViewItem item = childSnapshot.getValue(GroupChatRecyclerViewItem.class);

                        Log.d("GT", item.Message);
                        Log.d("GT", Integer.toString(item.SendTime));
                        Log.d("GT", item.SenderUID);


                        if(item.SenderUID.equals(UserUID) == true)
                        {
                            item.ViewType = DefineValue.Chat_Right;
                        }
                        else
                        {
                            item.ViewType = DefineValue.Chat_Left;
                        }


                        ChatList.add(item);
                        GroupChattingAdapter.notifyItemInserted(ChatList.size());
                    }
                }


                Update_Chat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

 */


    private void Update_Chat()
    {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot == null)
                {
                    return;
                }

                GroupChatRecyclerViewItem item = snapshot.getValue(GroupChatRecyclerViewItem.class);

                if(item == null)
                {
                    return;
                }


                Log.d("GT", item.Message);
                Log.d("GT", Integer.toString(item.SendTime));
                Log.d("GT", item.SenderUID);


                if(item.SenderUID.equals(UserUID) == true)
                {
                    item.ViewType = DefineValue.Chat_Right;
                }
                else
                {
                    item.ViewType = DefineValue.Chat_Left;
                }


                if(item.MemberIndice.get(UserUID) != null)
                {
                    if(item.MemberIndice.get(UserUID) == false)
                    {
                        item.MemberIndice.put(UserUID, true);


                        String key = snapshot.getKey();

                        Log.d("GT", "Key : " + key);

                        Map<String, Object> value = item.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/" + key, value);

                        databaseReference.updateChildren(childUpdates);
                    }
                }


                item.Key = snapshot.getKey();


                ChatList.add(item);

                ListSize = ChatList.size();

                GroupChattingAdapter.notifyItemInserted(ListSize);

                ChatRecyclerView.scrollToPosition(ListSize - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String key = snapshot.getKey();


                for(int i = ChatList.size() - 1; i >= 0; i--)
                {
                    if(ChatList.get(i).Key.equals(key) == true)
                    {
                        GroupChatRecyclerViewItem item = snapshot.getValue(GroupChatRecyclerViewItem.class);

                        ChatList.get(i).Change_Data(item);

                        GroupChattingAdapter.notifyItemChanged(i);


                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Log.d("GT", "Chat child removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d("GT", "Chat child moved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("GT", "Chat child cancelled");
            }
        };


        databaseReference.addChildEventListener(childEventListener);
    }


    private void Send_Chat(String message)
    {
        ChatMessage chatMessage;

        //ChatMemberIndice chatMemberIndice;

        String senderUID;

        int sendTime;

        Map<String, Boolean> memberIndice;


        memberIndice = new HashMap<String, Boolean>();

        senderUID = user.getUid();
        sendTime = Generate_Time();

        for(int i = 0; i < GroupTimeTableActivity.MemberIDList.size(); i++)
        {
            String memberID = GroupTimeTableActivity.MemberIDList.get(i);


            if(memberID.equals(senderUID) == true)
            {
                memberIndice.put(memberID, true);
            }
            else
            {
                memberIndice.put(memberID, false);
            }
        }


        chatMessage = new ChatMessage(message, senderUID, sendTime, memberIndice);
        //chatMemberIndice = new ChatMemberIndice(GroupTimeTableActivity.MemberIDList, senderUID);


        databaseReference.push().setValue(chatMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("GT", "Chat message send : Success");
            }
        });
    }

    private int Generate_Time()
    {
        int sendTime;

        SimpleDateFormat date = new SimpleDateFormat("MMddHHmmss", Locale.KOREAN);


        sendTime = Integer.parseInt(date.format(System.currentTimeMillis()));

        Log.d("GT", Integer.toString(sendTime));


        return sendTime;
    }


    @Override
    public void onClick(View view)
    {
        if(view == ChatEdit)
        {

        }
        else if(view == ChatButton)
        {
            String sendMessage;

            sendMessage = ChatEdit.getText().toString();

            ChatEdit.setText("");

            if(sendMessage.isEmpty() == true)
            {
                return;
            }

            Send_Chat(sendMessage);
        }
    }
}