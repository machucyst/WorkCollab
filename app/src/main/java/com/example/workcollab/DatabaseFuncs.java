package com.example.workcollab;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFuncs {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage fbs = FirebaseStorage.getInstance();
    StorageReference reference = fbs.getReference();
    DocumentReference docAccRef = db.document("Account");

    CollectionReference account = db.collection("Account");
    CollectionReference groups = db.collection("Groups");
    Map user;
    public interface UpdateListener{
        void onUpdate(Map user);
    }
    public interface GroupListener{
        void onReceive(List<Map> groups);
        void getDeadline(Timestamp timestamp);
    }
    public interface DataListener {
        void onDataFound(Map user);
        void noDuplicateUser();
    }
    public void SaveProfile(Map user, Uri value){
        reference.child("AccountProfiles/"+user.get("Id").toString()+"/Profile.png").putFile(value)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.child("Account/"+user.get("Id").toString()+"/Profile").getDownloadUrl().addOnSuccessListener(uri->{
                            UpdateAccount(user.get("Email").toString(), uri.toString(), "Profile", new UpdateListener() {
                                @Override
                                public void onUpdate(Map user) {
                                    System.out.println("Saved Profile");
                                }
                            });
                        });
                    }

                });
    }
    public void CreateAccount(String username, String password, String email, String ContactNumber, UpdateListener listener) {
        Map<String, Object> user = new HashMap<>();
        user.put("Username", username);
        user.put("Password", password);
        user.put("Email", email);
        user.put("ContactNumber", ContactNumber);
        List nolist = new ArrayList();
        user.put("Groups", nolist);
        account.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.onUpdate(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }
    public void GetProjects(String userId, GroupListener listener){
        groups.whereArrayContains("Members",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> user = new HashMap<>();
                    List<Map> documentList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.getData();
                        user.put("GroupId",document.getId());
                        System.out.println(user);
                        documentList.add(user);
                        }
                    System.out.println(documentList);
                    listener.onReceive(documentList);
                    }
            }
        });
    }
    public void GetDeadlines(String groupId,GroupListener listener){
        Date today = new Date();
        System.out.println(today);
        db.collection("Groups").document(groupId).collection("Projects").whereGreaterThanOrEqualTo("TaskDeadline",today).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        System.out.println(doc.get("TaskDeadline"));
                            listener.getDeadline(doc.getTimestamp("TaskDeadline"));
                    }
                }
            }
        });
    }
    public void UpdateEmail(String oldEmail, String newEmail, UpdateListener listener){
        account.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (oldEmail.equals(document.get("Email"))){
                            DocumentReference docRef = account.document(document.getId());
                            Map<String, Object> user = new HashMap<>();
                            user.put("Email",newEmail);


                            docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"update work");
                                        InitDB(user.get("Email").toString(), new DataListener() {
                                            @Override
                                            public void onDataFound(Map user) {
                                                try {
                                                    listener.onUpdate(user);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                                DatabaseFuncs.this.user = user;
                                            }

                                            @Override
                                            public void noDuplicateUser() {

                                            }
                                        });

                                }

                            });



                        }
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());

                }
            }
        });
    }
    public void UpdateAccount(String email,String value,String field,UpdateListener listener){
       account.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (email.equals(document.get("Email"))){
                            System.out.println(email);
                            DocumentReference docRef = account.document(document.getId());
                            System.out.println(document.getId()+"id shit or some shit");
                            Map<String, Object> user = new HashMap<>();
                            user.put(field,value);
                            docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"update work");
                                    InitDB(email, new DataListener() {
                                        @Override
                                        public void onDataFound(Map user) {
                                            try {
                                                listener.onUpdate(user);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                            DatabaseFuncs.this.user = user;
                                        }

                                        @Override
                                        public void noDuplicateUser() {

                                        }
                                    });

                                }
                            });

                        }
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());

                }
            }
        });

    }

    public void GetJoinedGroups(String id, GroupListener listener){
        List<Map> documentList = new ArrayList<>();
            groups
                .whereArrayContains("Members",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map a = document.getData();
                                    a.put("Id",document.getId());
                                    documentList.add(a);
                            }
                            listener.onReceive(documentList);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void InitDB(String email, DataListener dataListener) {
        final String[] b = {""};
        db.collection("Account")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map a = document.getData();
                                    a.put("Id",document.getId());
                                    dataListener.onDataFound(a);
                                    System.out.println(a);
                                    return;
                            }
                            dataListener.noDuplicateUser();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            dataListener.onDataFound(null);
                        }
                    }
                });
    }
}


