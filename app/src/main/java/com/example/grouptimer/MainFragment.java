package com.example.grouptimer;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFragment extends Fragment {
//    private static final String TAG = "MainFragment";

    RecyclerView recyclerView;
    NoteAdapter adapter;
    Context context;

    SwipeRefreshLayout swipeRefreshLayout;

    public static int toDoListCnt;
    private int toDoListPlusButtonBefore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //fragment_main에 인플레이션을 함
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        initUI(rootView);

        loadNoteListData();


        //당겨서 새로고침
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNoteListData();
                Log.d("GT", "refresh");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void initUI(ViewGroup rootView){

        //recyclerView연결
        recyclerView = rootView.findViewById(R.id.toDoListrecyclerView);

        //LinearLayoutManager을 이용하여 recyclerView설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //어댑터 연결
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

    }

    public void loadNoteListData(){

        GroupToDoListActivity.toDoListArrayList.clear();


        //_id, TODO가 담겨질 배열 생성
        ArrayList<Note> items = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference()
                .child("Groups").child(DefineValue.Group_ID).child("ToDoListCnt")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.getValue(Integer.class) != null) {
                            toDoListCnt = dataSnapshot.getValue(Integer.class);

                            toDoListPlusButtonBefore = toDoListCnt;


                            //firebase 에서 ToDoList 에 있는 값을 가져와서 toDoListArrayList 에 저장
                            for (int i = 0; i < toDoListCnt; i++) {

                                int outerIndex = i;
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Groups").child(DefineValue.Group_ID).child("ToDoList").child(String.valueOf(i))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String toDoList = dataSnapshot.getValue(String.class);

                                                items.add(new Note(outerIndex, toDoList));

                                                if(outerIndex == (toDoListCnt - 1))
                                                {
                                                    for(int k = 0; k < toDoListCnt; k++)
                                                    {
                                                        GroupToDoListActivity.toDoListArrayList.add(items.get(k).getTodo());
                                                    }


                                                    //어댑터에 연결 및 데이터셋 변경
                                                    adapter.setItems(items);
                                                    //adapter.notifyItemRangeChanged(0, toDoListCnt);
                                                    adapter.notifyDataSetChanged();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            return ;
        }

    }
