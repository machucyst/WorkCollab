package com.example.workcollab;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFuncs {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> user = new HashMap<>();
    CollectionReference account = db.collection("account");

    public void CreateAccount(String username, String password, String email, int ContactNumber, String[] groups) {
        user.put("Username", username);
        user.put("Password", password);
        user.put("Email", email);
        user.put("ContactNumber", ContactNumber);
        user.put("Groups", Arrays.asList(groups));
        account.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void InitDB() {


    }

    ;

    public void InitDB(String email, Listener listener) {
        final String[] b = {""};
        db.collection("account")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.w(TAG, document.getId());
                                listener.onReceive(document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onReceive(null);
                        }
                    }
                });
    }

    public interface Listener{
        void onReceive(Map User);
    }
    public String getUserName(String email){
        final String[] a = {""};
        db.collection("account")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                a[0] =  (String) document.get("Username");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        return a[0];

    }
    public String getUserPass(String email){
        final String[] a = new String[1];
        db.collection("account")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                             a[0] = document.getString("Email");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        System.out.println(a[0]);
        return a[0];

    }
    public void getDB() {
        db.collection("account/User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d(TAG, documentSnapshot.getId());
                            }
                        } else {
                            Log.w(TAG, "Erra");
                        }
                    }
                });
    }
}
