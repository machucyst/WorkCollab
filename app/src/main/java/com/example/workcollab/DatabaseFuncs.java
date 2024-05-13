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
import com.google.firebase.firestore.DocumentChange;
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
    CollectionReference account = db.collection("Account");
    CollectionReference groups = db.collection("Groups");
    CollectionReference messages = db.collection("Messages");
    Map user;
    public interface UpdateListener{
        void onUpdate(Map user);
    }

    public void DeleteAccount(String id, DeleteListener listener) {
        account.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("bai bai");
                listener.onDelete();
            }
        });
    }

    public interface DeleteListener {
        void onDelete();
    }

    public interface DataListener {
        void onDataFound(Map user);

        void noDuplicateUser();
    }
    public interface MessageSentListener {
        void onMessageSent(String newId);
    }
    public void CreateAccount(String username, String password, String email, String ContactNumber, UpdateListener listener) {
        Map<String, Object> user = new HashMap<>();
        user.put("Username", username);
        user.put("Password", password);
        user.put("Email", email);
        user.put("ContactNumber", ContactNumber);
        account.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        user.put("Id", documentReference.getId());
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
    public void SaveProfile(Map user, Uri value, UpdateListener listener){
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
                        reference.child("AccountProfiles/"+user.get("Id").toString()+"/Profile.png").getDownloadUrl().addOnSuccessListener(uri->{
                            UpdateAccount(user.get("Email").toString(), uri.toString(), "Profile", new UpdateListener() {
                                @Override
                                public void onUpdate(Map user) {
                                   listener.onUpdate(user);
                                }
                            });
                        });
                    }

                });
    }

    public void CreateGroup(String name, List<String> Members, UpdateListener listener) {
        Map<String, Object> group = new HashMap<>();
        group.put("GroupName", name);
        group.put("Leaders", Members);
        groups.add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                group.put("Id", documentReference.getId());
                listener.onUpdate(group);
            }
        });
    }

    public void CreateGroup(String name, List<String> Leaders, List<String> Members, UpdateListener listener) {
        Map<String, Object> group = new HashMap<>();
        group.put("GroupName", name);
        group.put("Leaders", Leaders);
        group.put("Invites", Members);
        groups.add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                group.put("Id", documentReference.getId());
                listener.onUpdate(group);
            }
        });
    }

    public void GetUsers(GroupListener listener) {
        account.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> user = new HashMap<>();
                    List<Map> documentList = new ArrayList<>();
                    int i = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        i++;
                        System.out.println(doc.toString() + i);
                        user = new HashMap<>();
                        user.put("Id", doc.getId());
                        user.put("Username", doc.get("Username").toString());
                        user.put("Email", doc.get("Email").toString());
                        try {
                            user.put("Profile", doc.get("Profile").toString());
                        } catch (Exception ex) {
                            System.out.println("no profile lmao");
                        }
                        documentList.add(user);
                    }
                    System.out.println("list" + documentList);
                    listener.onReceive(documentList);
                }
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

    public void UpdateAccount(String email, String value, String field, UpdateListener listener) {
        account.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (email.equals(document.get("Email"))) {
                            System.out.println(email);
                            DocumentReference docRef = account.document(document.getId());
                            System.out.println(document.getId() + "id shit or some shit");
                            Map<String, Object> user = new HashMap<>();
                            user.put(field, value);
                            docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"update work");
                                    InitDB(email, new DataListener() {
                                        @Override
                                        public void onDataFound(Map user) {
                                            try {
                                                user.put("Id", document.getId());
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

    public void GetDeadlines(String groupId, GroupListener listener) {
        Date today = new Date();
        System.out.println(today);
        db.collection("Groups").document(groupId).collection("Projects").whereGreaterThanOrEqualTo("TaskDeadline", today).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        System.out.println(doc.get("TaskDeadline"));
                        listener.getDeadline(doc.getTimestamp("TaskDeadline"));
                    }
                }
            }
        });
    }

    public void UpdateEmail(String oldEmail, String newEmail, UpdateListener listener) {
        account.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (oldEmail.equals(document.get("Email"))) {
                            DocumentReference docRef = account.document(document.getId());
                            System.out.println(document.getId()+"id shit or some shit");
                            Map<String, Object> user = new HashMap<>();
                            user.put("Email", newEmail);


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

    public void GetJoinedGroups(String id, GroupListener listener){
        List<Map> documentList = new ArrayList<>();
        List<Map> leaderList = new ArrayList<>();
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
                            groups
                                    .whereArrayContains("Leaders", id)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Map a = document.getData();
                                                    a.put("Id", document.getId());
                                                    leaderList.add(a);
                                                }
                                                listener.onReceive(documentList, leaderList);

                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public interface GroupListener {
        void onReceive(List<Map> groups, List<Map> groupLeaders);

        void onReceive(List<Map> groups);

        void getDeadline(Timestamp timestamp);
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

    public void getUserById(String id, DataListener dataListener) {
        final String[] b = {""};
        account.document(id).get().addOnCompleteListener(task -> {
            DocumentSnapshot d = task.getResult();
            Map<String, Object> user = d.getData();
            user.put("Id", d.getId());
            dataListener.onDataFound(user);
        });
    }

    public void sendMessage(Message message, Uri attachedFile, MessageSentListener listener) {
        StorageReference messagesFiles = reference.child("Messages" + message.getId());



        if (attachedFile == null) {
            message.setFile(Uri.parse(""));
            messages.add(message.toMap()).addOnCompleteListener(task -> {
                listener.onMessageSent(task.getResult().getId());
            });
            return;
        }


        messagesFiles.putFile(attachedFile).addOnSuccessListener(taskSnapshot -> messagesFiles.getDownloadUrl().addOnSuccessListener(uri -> {
            message.setFile(uri);
            messages.add(message.toMap()).addOnCompleteListener(task -> {
                listener.onMessageSent(task.getResult().getId());
            });
        }));
    }
    public interface MessagesReceivedListener {
        void onMessageReceived(List<Message> newMessages, List<Message> updatedMessages);
        Timestamp getCurrentTimestamp();
    }

    public void setReceivedMessagesListener(String userId, String groupId, MessagesReceivedListener listener) {
        messages.whereEqualTo("groupId", groupId).addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) return;

            List<Message> newMessages = new ArrayList<>();
            List<Message> updatedMessages = new ArrayList<>();

            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                QueryDocumentSnapshot d = dc.getDocument();
                if (userId.equals(d.getId())) continue;
                if (((Timestamp)d.get("timestamp")).compareTo(listener.getCurrentTimestamp()) > 0 ) {
                    Message message = new Message(d.getId(), d.get("message").toString(), d.get("senderId").toString(), d.get("senderUsername").toString(), d.get("groupId").toString(), Uri.parse(d.get("file").toString()), d.get("fileType").toString(), (Timestamp) d.get("timestamp"));
                    message.setReplyId(d.get("replyId").toString());
                    switch (dc.getType()) {
                        case ADDED:
                            newMessages.add(message);
                            break;
                        case MODIFIED:
                            updatedMessages.add(message);
                            break;
                    }
                }
            }

            listener.onMessageReceived(newMessages, updatedMessages);
        });
    }
    public interface AllMessagesReceivedListener {
        void sendAllReceivedMessages(List<Message> messages);
    }

    public void setAllMessagesReceivedListener(String groupId, AllMessagesReceivedListener listener) {
        List<Message> messages = new ArrayList<>();
        Log.e("uwuwu", "uwuwu");
        this.messages
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot d: queryDocumentSnapshots) {
                Message message = new Message(d.getId(), d.get("message").toString(), d.get("senderId").toString(), d.get("senderUsername").toString(), d.get("groupId").toString(), Uri.parse(d.get("file").toString()), d.get("fileType").toString(), (Timestamp) d.get("timestamp"));
                message.setReplyId(d.get("replyId").toString());
                messages.add(message);
            }

            Log.e("textest", "wadadawdawdawd");

            listener.sendAllReceivedMessages(messages);
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("uwu", e.toString());
                    }
                });
    }
}


