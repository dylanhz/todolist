package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);


        editText = findViewById(R.id.edit_text);
        spinner = findViewById(R.id.spinner);

        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                int prio = 0;
                Object tmp = spinner.getSelectedItem();
                if (tmp != null) {
                    switch (tmp.toString()) {
                        case "High":
                            prio = 2;
                            break;
                        case "Medium":
                            prio = 1;
                            break;
                        default:
                            prio = 0;
                            break;
                    }
                }
                boolean succeed = saveNote2Database(content.toString().trim(), prio);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content, int prio) {
        // TODO 插入一条新数据，返回是否插入成功 !done
        TodoDbHelper todoDbHelper = TodoDbHelper.getInstance(this);
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        long ts = System.currentTimeMillis();
        values.put(TodoContract.TodoEntry.COLUMN_1, ts);
        values.put(TodoContract.TodoEntry.COLUMN_2, ts);
        values.put(TodoContract.TodoEntry.COLUMN_3, State.TODO.intValue);
        values.put(TodoContract.TodoEntry.COLUMN_4, content);
        values.put(TodoContract.TodoEntry.COLUMN_5, prio);


        long res = db.insert(TodoContract.TodoEntry.TABLE_NAME, null, values);

        return res != -1;
    }
}
