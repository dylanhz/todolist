package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    private static final int REQUEST_CODE_SORT = 1003;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        }, this);
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        TodoDbHelper.getInstance(this).close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingActivity.class),
                        REQUEST_CODE_SORT);
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_ADD || requestCode == REQUEST_CODE_SORT)
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans !done
        TodoDbHelper todoDbHelper = TodoDbHelper.getInstance(this);
        SQLiteDatabase db = todoDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TodoContract.TodoEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List noteList = new ArrayList();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_1));
            Note note = new Note(id);
            long ts = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_2));
            note.setDate(new Date(ts));
            int state = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_3));
            note.setState(State.from(state));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_4));
            note.setContent(content);
            noteList.add(note);
        }
        cursor.close();
        return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据 !done
        TodoDbHelper todoDbHelper = TodoDbHelper.getInstance(this);
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        String selection = TodoContract.TodoEntry.COLUMN_1 + " = ?";
        String[] selectionArgs = {String.valueOf(note.id)};
        int deletedRows = db.delete(TodoContract.TodoEntry.TABLE_NAME, selection, selectionArgs);

        if (deletedRows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void updateNode(Note note) {
        // TODO 更新数据 !done
        TodoDbHelper todoDbHelper = TodoDbHelper.getInstance(this);
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_3, note.getState().intValue);

        String selection = TodoContract.TodoEntry.COLUMN_1 + " = ?";
        String[] selectionArgs = {String.valueOf(note.id)};
        int updatedRows = db.update(TodoContract.TodoEntry.TABLE_NAME, values, selection, selectionArgs);

        if (updatedRows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

}
