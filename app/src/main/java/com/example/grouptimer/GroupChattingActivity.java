package com.example.grouptimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.grouptimer.Object.ChatMessage;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GroupChattingActivity extends AppCompatActivity implements View.OnClickListener {


    public static Context GroupChattingContext;


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
    NetworkInfo networkInfo = null;

    boolean NetworkConnection = false;
    boolean FirstNetworkCheck = false;


    ProgressDialog progressDialog = null;


    private long LastChatDate;


    private int GroupMemberCnt;


    public static int standardSize_X;
    public static int standardSize_Y;


    InputMethodManager inputManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatting);


        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        standardSize_X = (int) (size.x);
        standardSize_Y = (int) (size.y);
        //standardSize_X = (int) (size.x / density);
        //standardSize_Y = (int) (size.y / density);


        GroupChattingContext = this;


        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        ChatRecyclerView = (RecyclerView) findViewById(R.id.chattingRecyclerView);
        ChatEdit = (EditText) findViewById(R.id.chatEdit);
        ChatButton = (Button) findViewById(R.id.chatButton);


        ChatEdit.setOnClickListener(this);
        ChatButton.setOnClickListener(this);


        NetworkConnection = false;
        FirstNetworkCheck = false;

        LastChatDate = 0;

        GroupMemberCnt = 0;


        ChatList = new ArrayList<GroupChatRecyclerViewItem>();


        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        progressDialog = new ProgressDialog(GroupChattingActivity.this, R.style.ProgressDialogTheme);

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

                    ChatRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
                        {
                            if( (scrollY + 10) < oldScrollY )
                            {
                                if(inputManager.isActive() == true)
                                {
                                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                }
                            }
                        }
                    });


                    GroupChattingAdapter = new GroupChattingRecyclerViewAdapter(ChatList);
                    ChatRecyclerView.setAdapter(GroupChattingAdapter);


                    Update_Chat();
                }
            });
        }
    }


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

                if(snapshot.getValue() == null)
                {
                    return;
                }

                GroupChatRecyclerViewItem item = snapshot.getValue(GroupChatRecyclerViewItem.class);

                if(item == null)
                {
                    return;
                }


                Log.d("GT", item.Message);
                Log.d("GT", Long.toString(item.SendTime));
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



                {
                    String buffer;
                    long date;


                    buffer = Long.toString(item.SendTime);
                    buffer = buffer.substring(0, 8);

                    date = Long.parseLong(buffer);


                    if(LastChatDate != date)
                    {
                        GroupChatRecyclerViewItem dateItem = new GroupChatRecyclerViewItem(null, null, date, "");

                        dateItem.InvalidValue = -1;

                        LastChatDate = date;

                        ChatList.add(dateItem);
                    }
                }


                Log.d("GT", "Sender Check : " + item.SenderUID);


                ChatList.add(item);

                ListSize = ChatList.size();

                Sort_Chat_Message();

                GroupChattingAdapter.notifyItemRangeChanged(0, ListSize);

                ChatRecyclerView.scrollToPosition(ListSize - 1);


                if(progressDialog != null && ChatList.isEmpty() == false)
                {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
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


    private void Sort_Chat_Message()
    {
        for(int i = 1; i < ChatList.size(); i++)
        {
            for(int j = i ; j >= 1; j--)
            {
                if((ChatList.get(j).InvalidValue == 0) && (ChatList.get(j - 1).InvalidValue == 0))
                {
                    if(ChatList.get(j).SendTime < ChatList.get(j - 1).SendTime)
                    {
                        Log.d("GT", "j : " + ChatList.get(j).SendTime);
                        Log.d("GT", "j - 1: " + ChatList.get(j -1).SendTime);


                        Collections.swap(ChatList, j, j - 1);
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }


        for(int i = 0; i < ChatList.size(); i++)
        {
            Log.d("GT", "Time : " + ChatList.get(i).SendTime);
        }


        Log.d("GT", "Complete ChatList Sort");
    }


    private void Send_Chat(String message)
    {
        String senderUID;

        long sendTime;

        Map<String, Boolean> memberIndice;


        memberIndice = new HashMap<String, Boolean>();

        senderUID = user.getUid();
        sendTime = Generate_Time();



        FirebaseDatabase.getInstance().getReference().child("Groups").child(DefineValue.Group_ID).child("memberCnt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int value = snapshot.getValue(Integer.class);


                GroupMemberCnt = value;


                if(GroupMemberCnt != GroupTimeTableActivity.MemberIDList.size())
                {
                    Log.d("GT", "Invalid group member cnt");
                    Log.d("GT", "GroupMemberCnt : " + GroupMemberCnt + ", MemberIDList Size : " + GroupTimeTableActivity.MemberIDList.size());


                    ((GroupTimeTableActivity)GroupTimeTableActivity.context).Reload_GroupMemberList(DefineValue.Group_ID, GroupMemberCnt, message, senderUID, sendTime);
                }
                else
                {
                    ChatMessage chatMessage;


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


                    databaseReference.push().setValue(chatMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Log.d("GT", "Chat message send : Success");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void function(String message, String senderUID, long sendTime)
    {
        ChatMessage chatMessage;

        Map<String, Boolean> memberIndice;


        memberIndice = new HashMap<String, Boolean>();


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


        databaseReference.push().setValue(chatMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Log.d("GT", "Chat message send : Success");
            }
        });
    }


    private long Generate_Time()
    {
        long sendTime;

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREAN);


        sendTime = Long.parseLong(date.format(System.currentTimeMillis()));

        Log.d("GT", Long.toString(sendTime));


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