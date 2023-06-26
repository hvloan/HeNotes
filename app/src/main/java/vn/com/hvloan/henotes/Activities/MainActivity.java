package vn.com.hvloan.henotes.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vn.com.hvloan.henotes.Adapter.NoteAdapter;
import vn.com.hvloan.henotes.Database.RoomDB;
import vn.com.hvloan.henotes.Events.NoteClickListener;
import vn.com.hvloan.henotes.Models.Note;
import vn.com.hvloan.henotes.R;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView rcvNotes;
    Toolbar toolbarHome;
    NoteAdapter noteAdapter;
    List<Note> listNotes = new ArrayList<>();
    RoomDB roomDB;
    FloatingActionButton fabAdd;

    Note selectedNote;


    private String passwordNote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
        setupToolbarHome();
        updateRcvNotes(listNotes);
        actionComponent();
    }

    private void setupToolbarHome() {
        setSupportActionBar(toolbarHome);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void actionComponent() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startForResultAdd.launch(new Intent(MainActivity.this, NoteActivity.class));
            }
        });
    }

    ActivityResultLauncher<Intent> startForResultAdd = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Note newNote = (Note) result.getData().getSerializableExtra("note");
                        roomDB.noteDAO().insert(newNote);
                        reloadNote();
                        noteAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

    void reloadNote() {
        listNotes.clear();
        listNotes.addAll(roomDB.noteDAO().getAllNotes());
        //sort pinned note
        Collections.sort(listNotes, new Comparator<Note>() {
            @Override
            public int compare(Note noteOne, Note noteTwo) {
                return Boolean.compare(noteOne.isPinned(), noteTwo.isPinned());
            }
        });
        Collections.reverse(listNotes);
    }

    ActivityResultLauncher<Intent> startForResultUpdate = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Note newNote = (Note) result.getData().getSerializableExtra("note");
                        roomDB.noteDAO().update(newNote.getId(), newNote.getTitle(), newNote.getContent(), newNote.getColor());
                        reloadNote();
                        noteAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

    private void updateRcvNotes(List<Note> listNotes) {
        rcvNotes.setHasFixedSize(true);
        rcvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
        noteAdapter = new NoteAdapter(MainActivity.this, listNotes, noteClickListener);
        rcvNotes.setAdapter(noteAdapter);
    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Note note) {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra("old_note", note);
            startForResultUpdate.launch(intent);
        }

        @Override
        public void onLongClick(Note note, CardView cardView) {
            selectedNote = note;
            showPopUpMenu(cardView);
        }
    };

    private void showPopUpMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu_note);
        popupMenu.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_pin:
                if (selectedNote.isPinned()) {
                    roomDB.noteDAO().updatePinNote(selectedNote.getId(), false);
                    Toast.makeText(this, "UnPinned successfully!", Toast.LENGTH_LONG).show();
                } else {
                    roomDB.noteDAO().updatePinNote(selectedNote.getId(), true);
                    Toast.makeText(this, "Pinned successfully!", Toast.LENGTH_LONG).show();
                }
                reloadNote();
                noteAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_delete:
                roomDB.noteDAO().delete(selectedNote);
                listNotes.remove(selectedNote);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Delete note " + selectedNote.getContent() + " success!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_lock:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Input your password");
                alertDialog.setMessage("Input your password to show/hide this note.");
                EditText editText = new EditText(this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                alertDialog.setView(editText);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passwordNote = editText.getText().toString();
                        if (selectedNote.isHide()) {
                            if (selectedNote.getPassword().equals(passwordNote)) {
                                roomDB.noteDAO().updateLockNote(selectedNote.getId(), "", false);
                                Toast.makeText(MainActivity.this, "This note is visible.", Toast.LENGTH_SHORT).show();
                                reloadNote();
                                noteAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Password is not valid.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        } else {
                            roomDB.noteDAO().updateLockNote(selectedNote.getId(), passwordNote, true);
                            reloadNote();
                            noteAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            default:
                return false;
        }
    }

    private void initComponent() {
        rcvNotes = findViewById(R.id.rcv_home);
        toolbarHome = findViewById(R.id.toolbar_home);
        roomDB = RoomDB.getInstance(this);
        selectedNote = new Note();
        if (roomDB.noteDAO().getAllNotes().isEmpty()) {
            listNotes = new ArrayList<>();
        } else {
            reloadNote();
        }
        fabAdd = findViewById(R.id.fab_add);
    }
}