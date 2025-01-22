package com.example.mycatatan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddNote;
    private DatabaseReference databaseReference;
    private ArrayList<Note> noteList;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddNote = findViewById(R.id.fabAddNote);
        databaseReference = FirebaseDatabase.getInstance().getReference("notes");
        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList, new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onEdit(Note note) {
                // Navigasi ke Activity untuk Edit
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra("noteId", note.getId());
                intent.putExtra("noteTitle", note.getTitle());
                intent.putExtra("noteContent", note.getContent());
                startActivity(intent);
            }

            @Override
            public void onDelete(Note note) {
                deleteNote(note);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            startActivity(intent);
        });

        loadNotes();
    }

    private void loadNotes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    noteList.add(note);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal Menampilkan Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNote(Note note) {
        if (note != null) {
            databaseReference.child(note.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Catatan Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Gagal Menghapus Catatn", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}