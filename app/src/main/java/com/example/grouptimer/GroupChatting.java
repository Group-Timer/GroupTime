package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.TimerTask;

import kotlinx.coroutines.Delay;

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


    ConnectivityManager connectivityManager = null;
    ConnectivityManager.NetworkCallback networkCallback = null;
    //Network currentNetwork = null;
    NetworkInfo networkInfo = null;

    boolean NetworkConnection = false;
    boolean FirstNetworkCheck = false;


    ProgressDialog progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatting);


        ChatRecyclerView = (RecyclerView) findViewById(R.id.chattingRecyclerView);
        ChatEdit = (EditText) findViewById(R.id.chatEdit);
        ChatButton = (Button) findViewById(R.id.chatButton);


        ChatEdit.setOnClickListener(this);
        ChatButton.setOnClickListener(this);


        NetworkConnection = false;
        FirstNetworkCheck = false;


        ChatList = new ArrayList<GroupChatRecyclerViewItem>();


        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        progressDialog = new ProgressDialog(GroupChatting.this);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Loading ...\n\nNeed to access network");
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        progressDialog.dismiss();

                        finish();
                    }
                });

                progressDialog.show();
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();


        Check_Network();
    }


    @Override
    public void onPause()
    {
        super.onPause();


        if(networkCallback == null)
        {
            return;
        }

        connectivityManager.unregisterNetworkCallback(networkCallback);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();


        if(childEventListener != null)
        {
            databaseReference.removeEventListener(childEventListener);
        }



        user = null;
        databaseReference = null;
        valueEventListener = null;
        childEventListener = null;
        linearLayoutManager = null;
        GroupChattingAdapter = null;
        ChatRecyclerView = null;
        ChatEdit = null;
        ChatButton = null;
        ChatList.clear();
        ChatList = null;
        ListSize = 0;
        UserUID = null;
        connectivityManager = null;
        networkCallback = null;
        networkInfo = null;
        NetworkConnection = false;
        FirstNetworkCheck = false;
    }


    private void Check_Network()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            networkCallback = new ConnectivityManager.NetworkCallback()
            {
                @Override
                public void onAvailable(Network network) {

                    int connectionState = -1;
                    Network currentNetwork = connectivityManager.getActiveNetwork();

                    if(currentNetwork != null) {
                        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork);
                        if(networkCapabilities != null)
                        {
                            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                            {
                                connectionState = NetworkCapabilities.TRANSPORT_WIFI;
                            }
                            else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                            {
                                connectionState = NetworkCapabilities.TRANSPORT_CELLULAR;
                            }
                            else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                            {
                                connectionState = NetworkCapabilities.TRANSPORT_ETHERNET;
                            }
                            else
                            {
                                connectionState = 9;
                            }
                        }
                    }

                    if(connectionState >= 0)
                    {
                        String message = "Network connected : ";

                        if(connectionState == NetworkCapabilities.TRANSPORT_WIFI)
                        {
                            message += "WIFI";
                        }
                        else if(connectionState == NetworkCapabilities.TRANSPORT_CELLULAR)
                        {
                            message += "CELLULAR";
                        }
                        else if(connectionState == NetworkCapabilities.TRANSPORT_ETHERNET)
                        {
                            message += "ETHERNET";
                        }
                        else
                        {
                            message += "other";
                        }


                        Log.d("GT", message);


                        NetworkConnection = true;

                        Ready_Chatting();
                    }
                    else
                    {
                        NetworkConnection = false;

                        Log.d("GT", "Fail network checking");
                    }
                }

                @Override
                public void onLost(Network network) {

                    if(progressDialog != null)
                    {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    Toast.makeText(getApplicationContext(), "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();

                    NetworkConnection = false;

                    Log.d("GT", "Disconnect network");
                }
            };


            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
        else
        {
            networkInfo = connectivityManager.getActiveNetworkInfo();

            boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

            if(isConnected == false)
            {
                if(progressDialog != null)
                {
                    progressDialog.dismiss();
                    progressDialog = null;
                }


                NetworkConnection = false;

                Log.d("GT", "Disconnect network");

                Toast.makeText(this, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();


                return;
            }


            NetworkConnection = true;

            Log.d("GT", "Connected network");

            Toast.makeText(this, "인터넷 연결됨.", Toast.LENGTH_SHORT).show();

            Ready_Chatting();
        }
    }


    private void Ready_Chatting()
    {
        if(NetworkConnection == false)
        {
            if(progressDialog != null)
            {
                progressDialog.dismiss();
                progressDialog = null;
            }


            Toast.makeText(this, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();

            return;
        }


        if(FirstNetworkCheck == false)
        {
            FirstNetworkCheck = true;


            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Chat").child("Messages").child(DefineValue.Group_ID);


            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("GT", "Error getting data", task.getException());
                    }
                    else {
                        if(task.getResult().getValue() == null)
                        {
                            if(progressDialog != null)
                            {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        }
                    }
                }
            });


            UserUID = user.getUid();


            linearLayoutManager = new LinearLayoutManager(this);
            if(linearLayoutManager == null)
            {
                Log.d("GT", "LayoutManager is null");
            }
            linearLayoutManager.setStackFromEnd(true);


            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ChatRecyclerView.setLayoutManager(linearLayoutManager);


                    GroupChattingAdapter = new GroupChattingRecyclerViewAdapter(ChatList);
                    ChatRecyclerView.setAdapter(GroupChattingAdapter);


                    Update_Chat();
                }
            });
        }


        //Load_Chat();
        //Update_Chat();
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

                if(progressDialog != null && ChatList.isEmpty() == false)
                {
                    progressDialog.dismiss();
                    progressDialog = null;
                }


                Log.d("GT", "Chat child added");

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


                Log.d("GT", "Chat child changed");

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
            if(NetworkConnection == false)
            {
                Toast.makeText(this, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();

                return;
            }


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