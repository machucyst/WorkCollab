package com.example.workcollab;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFuncs {
    //Initializations
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage fbs = FirebaseStorage.getInstance();
    FirebaseAuth fauth = FirebaseAuth.getInstance();
    StorageReference reference = fbs.getReference();
    CollectionReference account = db.collection("Account");
    CollectionReference groups = db.collection("Groups");
    CollectionReference messages = db.collection("Messages");
    Map user;
    //Interfaces
    public interface TaskListener{
        void onTaskRecieved(List<Map> tasks);

        void getDeadline(Timestamp timestamp);
    }
    public interface UpdateListener{
        void onUpdate(Map user);
    }
    public interface EmailAuthListener{
        void changeLayout(boolean test);
    }
    public interface OptionListener{
        void onOptionPicked();
    }
    public interface CreateTaskListener{
        void onCreateTaskListener();
    }
    public interface BasicListener{
        void BasicListener();
    }
    //Methods
    public void DeleteAccount(String id, DeleteListener listener) {
        account.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("bai bai");
                listener.onDelete();
            }
        });
    }
    public interface MessagesReceivedListener {
        void onMessageReceived(List<Message> newMessages, List<Message> updatedMessages);
        Timestamp getCurrentTimestamp();
    }
    public interface DeleteListener {
        void onDelete();
    }
    public interface UserListener{
        void onUserFound(Map user);
    }
    public interface MembersListener {
        void onReceiveMembers(List<Map> members);
    }

    public interface DataListener {
        void onDataFound(Map user);

        void noDuplicateUser();
    }
    public interface MessageSentListener {
        void onMessageSent(String newId);
    }
    public interface GroupListener {
        void onReceive(List<Map> groups, List<Map> groupLeaders);

        void onReceive(List<Map> groups);

        void getDeadline(Timestamp timestamp);
    }
    public interface AllMessagesReceivedListener {
        void sendAllReceivedMessages(List<Message> messages);
    }
    public void registerAccount(FirebaseAuth auth, String email, String password, Context c, Button b, EmailAuthListener listener){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(c,"A verification email has been sent. Please check your email",Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.changeLayout(true);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c,"Failed Registering Account",Toast.LENGTH_SHORT).show();
                b.setEnabled(true);
            }
        });
    }
    public void createAccount(String username, String password, String email, String ContactNumber, UpdateListener listener) {
        Map<String,Object> user = new HashMap<>();
        user.put("Username", username);
        user.put("Password", password);
        user.put("Email", email);
        user.put("ContactNumber", ContactNumber);
        System.out.println(user);
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
    public void getMemberSubmissions(Map task, TaskListener listener){
        List<Map> taskDetails = new ArrayList<>();
        groups.document(task.get("ParentId").toString()).collection("Tasks").document(task.get("Id").toString()).collection("Submitted").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()) {
                        getUserById(document.getId(), new DataListener() {
                            @Override
                            public void onDataFound(Map user) {
                                Map task2 = document.getData();
                                System.out.println("seojaehg"+document.getData());
                                task2.put("Profile",user.get("Profile").toString());
                                task2.put("Username",user.get("Username").toString());
                                taskDetails.add(task2);
                                listener.onTaskRecieved(taskDetails);

                            }

                            @Override
                            public void noDuplicateUser() {

                            }
                        });
                    }
                }
            }
        });
    }
    public void downloadFile(String fileUrl, String fileCreator, Context context){
        File localFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileCreator);
        fbs.getReferenceFromUrl(fileUrl).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Let me go to sleep",Toast.LENGTH_SHORT).show();
                System.out.println(localFile.getAbsoluteFile());
            }
        });
    }
    public void submitTask(Map user, Uri value, String groupId, String taskId, ImageView v, Context c, BasicListener listener){
        Map<String,Object> taskSub = new HashMap<>();

        reference.child("Groups/"+groupId+"/SubmittedFiles/"+taskId+"/"+user.get("Id").toString()+"/Task*").putFile(value, new StorageMetadata.Builder().setContentType(String.valueOf(MimeTypeMap.getSingleton().getExtensionFromMimeType(c.getContentResolver().getType(value)))).build()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child("Groups/"+groupId+"/SubmittedFiles/"+taskId+"/"+user.get("Id").toString()+"/Task*").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        taskSub.put("file",uri.toString());
                        taskSub.put("date",Timestamp.now());
                        taskSub.put("fileCreator",user.get("Username"));
                        taskSub.put("Profile",user.get("Profile"));
                        groups.document(groupId).collection("Tasks").document(taskId).collection("Submitted").document(user.get("Id").toString()).set(taskSub).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                listener.BasicListener();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c,"Error Uploading Document",Toast.LENGTH_SHORT);
                v.setBackground(AppCompatResources.getDrawable(c,R.drawable.textholder));
                v.setEnabled(true);
            }
        });

    }
    public void saveProfile(Map user, Uri value, UpdateListener listener){
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
                            updateAccount(user.get("Email").toString(), uri.toString(), "Profile", new UpdateListener() {
                                @Override
                                public void onUpdate(Map user) {
                                   listener.onUpdate(user);
                                }
                            });
                        });
                    }

                });
    }
    public void createGroup(String name, List<String> Members, UpdateListener listener) {
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
    public void createGroup(String name, List<String> Leaders, List<String> Members, Context c, Button b, UpdateListener listener) {
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                b.setBackgroundDrawable(AppCompatResources.getDrawable(c,R.drawable.textholder));
                b.setText("Submit");
                b.setEnabled(true);
            }
        });
    }
    public void inviteMembers(String groupId, List<String> toAdd, BasicListener listener){
        for(String i: toAdd){
            groups.document(groupId).update("Invites",FieldValue.arrayUnion(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listener.BasicListener();
                }
            });
        }

    }
    public void getUsers(GroupListener listener) {
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
    public void getProjects(String userId, GroupListener listener){
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
    public void updateAccount(String email, String value, String field, UpdateListener listener) {
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
    public void getDeadlines(String groupId, GroupListener listener) {
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
    public void updateEmail(String oldEmail, String newEmail, UpdateListener listener) {
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
    public void getJoinedGroups(String id, GroupListener listener){
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
                                    a.put("isLeader",false);
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
                                                    a.put("isLeader",true);
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
    public void getInvites(String id, GroupListener listener){
        List<Map> documentList = new ArrayList<>();
        groups.whereArrayContains("Invites",id)
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
    public void getMembers(String id, MembersListener listener){
        List<Map> ids = new ArrayList<>();
        groups.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        List<String> members = (List<String>) task.getResult().get("Members");
                        System.out.println(members + "membercheck");
                        if (members!=null){
                            for(String id : members){
                                getUserById(id, new DataListener() {
                                    @Override
                                    public void onDataFound(Map user) {
                                        System.out.println(user+"326523");
                                        ids.add(user);
                                        listener.onReceiveMembers(ids);
                                    }

                                    @Override
                                    public void noDuplicateUser() {
                                        System.out.println("lmao error");
                                    }
                                });
                            }

                        }
                    }

                }
            }
        });
    }
    public void getMembers(String id, UserListener listener) {
        account.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult().exists()){
                        listener.onUserFound(task.getResult().getData());
                    }
                }
            }
        });
    }
    public void getTasks(String groupId, TaskListener listener){
    List<Map> tasks = new ArrayList<>();
        groups.document(groupId).collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        Map<String, Object> taskMap = doc.getData();
                        taskMap.put("Id",doc.getId());
                        tasks.add(taskMap);
                    }
                    listener.onTaskRecieved(tasks);
                }
            }
        });

    }
    public void getTasks(String userId, TaskListener listener, boolean a){
        List<Map> tasks = new ArrayList<>();
        Log.e("wawa", "woah");
        getJoinedGroups(userId, new GroupListener() {
            @Override
            public void onReceive(List<Map> groups, List<Map> groupLeaders) {
                for (Map g : groups){
                    System.out.println(g.get("Id").toString()+"id funny");
                    DatabaseFuncs.this.groups.document(g.get("Id").toString()).collection("Tasks").whereArrayContains("Assigned Members",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                Log.e("wawa", "whee");
                                for (DocumentSnapshot doc : task.getResult()){
                                    System.out.println(doc.getData());
                                    Map<String, Object> taskMap = doc.getData();
                                    taskMap.put("Id",doc.getId());
                                    taskMap.put("GroupName", g.get("GroupName"));
                                    taskMap.put("GroupImage", g.get("GroupImage"));
                                    tasks.add(taskMap);
                                }
                            }
                        listener.onTaskRecieved(tasks);
                        }
                    });
                }
            }

            @Override
            public void onReceive(List<Map> groups) {
            }

            @Override
            public void getDeadline(Timestamp timestamp) {

            }
        });


    }
    public void InitDB(String email, DataListener dataListener){
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
    public void createTask(String groupId, List<String> members,String taskName, String taskDescription, long taskDeadline, Button b, Context c,  CreateTaskListener listener){
        Map taskBuilder = new HashMap<>();
        taskBuilder.put("ParentId",groupId);
        taskBuilder.put("TaskName",taskName);
        taskBuilder.put("TaskDescription",taskDescription);
        taskBuilder.put("Assigned Members",members);
        taskBuilder.put("TaskCreated",new Timestamp(new Date()));
        taskBuilder.put("TaskDeadline",new Timestamp(new Date(taskDeadline)));

        groups.document(groupId).collection("Tasks").add(taskBuilder).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("ypee");
                listener.onCreateTaskListener();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                b.setEnabled(true);
                b.setText("Submit");
                b.setBackgroundDrawable(AppCompatResources.getDrawable(c,R.drawable.textholder));
            }
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
    public void denyInvite(String id,String groupId, OptionListener listener){
        groups.document(groupId).update("Invites", FieldValue.arrayRemove(id)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onOptionPicked();
            }
        });
    }
    public void acceptInvite(String id,String groupId, OptionListener listener){
        groups.document(groupId).update("Invites", FieldValue.arrayRemove(id)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        groups.document(groupId).update("Members",FieldValue.arrayUnion(id)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onOptionPicked();
            }
        });
    }
}


