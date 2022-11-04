package com.example.grouptimer;

        import androidx.appcompat.app.AppCompatActivity;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.List;

public class MakeGroupActivity extends AppCompatActivity{

    private TextView selectTextView;
    private ListView selectListView;
    private Button selectButton;
    private List<String> purposeList; //selectListView 에 들어갈 값을 넣어두는 List
    private Button makeGroupButton;
    private EditText groupNameEditText;
    private EditText howManyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_group);

        selectTextView = (TextView) findViewById(R.id.selectTextView);
        selectListView = (ListView) findViewById(R.id.selectListView);
        selectButton = (Button) findViewById(R.id.selectButton);
        makeGroupButton = (Button) findViewById(R.id.makeGroup);

        purposeList = new ArrayList<>();
        purposeList.add("약속1");
        purposeList.add("약속2");
        purposeList.add("약속3");

        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, purposeList);
        selectListView.setAdapter(adpater);

        selectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String data = (String) adapterView.getItemAtPosition(position);
                selectTextView.setText(data);
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == selectButton) {
                    if(selectListView.getVisibility() == View.VISIBLE){
                        System.out.println(selectListView.getVisibility());
                        selectListView.setVisibility(View.INVISIBLE);
                        System.out.println(selectListView.getVisibility());
                    }
                    selectListView.setVisibility(View.VISIBLE);
                }
            }
        });
//
//        makeGroupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String groupName = groupNameEditText.getText().toString();
//                int groupNum = Integer.parseInt(howManyEditText.getText().toString());
//
//            }
//        });
//
    }

}