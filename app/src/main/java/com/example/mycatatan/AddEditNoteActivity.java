package com.example.mycatatan;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private Button btnSave;
    private DatabaseReference databaseReference;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSave = findViewById(R.id.btnSave);
        databaseReference = FirebaseDatabase.getInstance().getReference("notes");

        // Get data from Intent for Edit mode
        noteId = getIntent().getStringExtra("noteId");
        if (noteId != null) {
            String title = getIntent().getStringExtra("noteTitle");
            String content = getIntent().getStringExtra("noteContent");
            etTitle.setText(title);
            etContent.setText(content);
        }

        btnSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Isi Semua Data Terlebih Dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noteId == null) {
            // Add new note if noteId is not provided
            noteId = databaseReference.push().getKey();
        }

        Note note = new Note(noteId, title, content);
        databaseReference.child(noteId).setValue(note)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Sukses Mencatat", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal Menyimpan", Toast.LENGTH_SHORT).show());
    }
}