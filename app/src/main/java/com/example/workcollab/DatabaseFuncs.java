package com.example.workcollab;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;

import com.example.workcollab.activities.SetupAccountActivity;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
import java.util.Objects;

public class DatabaseFuncs {
    //Initializations
    static boolean iHateThisFix = true;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage fbs = FirebaseStorage.getInstance();
    StorageReference reference = fbs.getReference();
    CollectionReference account = db.collection("Account");
    CollectionReference groups = db.collection("Groups");
    CollectionReference messages = db.collection("Messages");
    Map<String,Object> user;
    //Interfaces
    public interface TaskListener{
        void onTaskReceived(List<Map<String, Object>> tasks);
        void getDeadline(Timestamp timestamp);
    }
    public interface UpdateListener{
        void onUpdate(Map<String,Object> user);
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
        void basicListener();
    }
    //Methods
    public void DeleteAccount(String id, DeleteListener listener) {
        account.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
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
        void onUserFound(Map<String,Object>user);
    }
    public interface MembersListener {
        void onReceiveMembers(List<Map<String,Object>> members);
    }

    public interface DataListener {
        void onDataFound(Map<String,Object>user);

        void noDuplicateUser();
    }
    public interface MessageSentListener {
        void onMessageSent(String newId);
    }
    public interface GroupListener {
        void onReceive(List<Map<String,Object>> groups, List<Map<String,Object>> groupLeaders);

        void onReceive(List<Map<String,Object>> groups);

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
                    assert user != null;
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
                b.setText(R.string.submit);
                b.setBackgroundDrawable(AppCompatResources.getDrawable(c,R.drawable.textholder));
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
        account.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        user.put("Id", documentReference.getId());
                        listener.onUpdate(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    public void getMemberSubmissions(Map<String,Object>task, TaskListener listener){
        List<Map<String,Object>> taskDetails = new ArrayList<Map<String,Object>>();
        groups.document(
                String.valueOf(task.get("ParentId")))
                .collection("Tasks")
                .document(String.valueOf(task.get("Id")))
                .collection("Submitted")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                getUserById(document.getId(), new DataListener() {
                                    @Override
                                    public void onDataFound(Map<String,Object> user) {
                                        Map<String,Object> task2 = document.getData();
                                        assert task2 != null;
                                        task2.put("Profile",String.valueOf(user.get("Profile")));
                                        task2.put("Username",String.valueOf(user.get("Username")));
                                        taskDetails.add(task2);
                                        listener.onTaskReceived(taskDetails);

                                    }

                                    @Override
                                    public void noDuplicateUser() {

                                    }
                                });
                            }
                        }
                    }
                }
        );
    }
    public void downloadFile(String fileUrl, String fileName, Context context){

        File localFile = new File(String.valueOf(PublicMethods.getFilePath(Uri.parse(fileUrl),context)),  PublicMethods.getFileExtension(Uri.parse(fileUrl),fileName,context));
        fbs.getReferenceFromUrl(fileUrl).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "File downloaded successfully",Toast.LENGTH_SHORT).show();
                scanFile(context,localFile);
            }
        });
    }
    public void submitTask(Map<String,Object>user, Uri value, String groupId, String taskId, String taskName,CardView v, Context c, BasicListener listener){
        Map<String,Object> taskSub = new HashMap<>();

        reference.child(("Groups/"+groupId+"/SubmittedFiles/"+taskId+"/"+user.get("Id"))+"/"+taskName)
                .putFile(value,
                        new StorageMetadata.Builder()
                                .setContentType(String.valueOf(
                                        MimeTypeMap
                                                .getSingleton()
                                                .getExtensionFromMimeType(
                                                        c.getContentResolver()
                                                                .getType(value)
                                                )
                                        )
                                )
                                .build()
                        ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.child("Groups/"+groupId+"/SubmittedFiles/"+taskId+"/"+user.get("Id")+"/"+taskName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        taskSub.put("file",uri.toString());
                        taskSub.put("date",Timestamp.now());
                        taskSub.put("fileName",taskName);
                        taskSub.put("Profile",user.get("Profile"));
                        groups.document(groupId)
                                .collection("Tasks")
                                .document(taskId)
                                .collection("Submitted")
                                .document(String.valueOf(user.get("Id")))
                                .set(taskSub)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                listener.basicListener();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c,"Error Uploading Document",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private static void scanFile(Context context, File file) {
        // Use MediaScannerConnection to scan the file and make it available in the gallery
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "Scan completed: " + path);
            }
        });
    }
    public void saveProfile(Map<String,Object>user, Uri value, UpdateListener listener){
        reference.child("AccountProfiles/"+user.get("Id")+"/Profile.png").putFile(value)
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
                        reference.child("AccountProfiles/"+ user.get("Id") +"/Profile.png").getDownloadUrl().addOnSuccessListener(uri->{
                            updateAccount(String.valueOf(user.get("Email")), uri.toString(), "Profile", new UpdateListener() {
                                @Override
                                public void onUpdate(Map<String,Object>user) {
                                   listener.onUpdate(user);
                                }
                            });
                        });
                    }

                });
    }
    public void getGroupData(String groupId, DataListener listener){
        groups.document(groupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    listener.onDataFound(task.getResult().getData());
                }
            }
        });
    }
    public void updateGroup(Map<String,Object>group,String value, String condition, UpdateListener listener, int i){
        group.put(condition,value);
        groups.document(String.valueOf(group.get("Id"))).update(group).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    listener.onUpdate(group);
                }
            }
        });
    }
    public void updateGroup(Map<String,Object>group, String path, UpdateListener listener){
        group.put("GroupImage",path);
        groups.document(String.valueOf(group.get("Id"))).update(group).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
               if (task.isSuccessful()){
                   listener.onUpdate(group);
               }
            }
        });
    }
    public void saveGroupProfile(Map<String,Object>group, Uri value, UpdateListener listener){
        reference.child("GroupsProfiles/"+group.get("Id")+"/Profile.png").putFile(value)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ignored) {

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.child("GroupsProfiles/"+group.get("Id")+"/Profile.png").getDownloadUrl().addOnSuccessListener(uri->{
                            updateGroup(group, uri.toString(), new UpdateListener() {
                                @Override
                                public void onUpdate(Map<String,Object>user) {
                                    listener.onUpdate(user);
                                }
                            });
                        });
                    }

                });
    }
    public void createGroup(String name, List<String> Members,String profile, UpdateListener listener) {
        Map<String, Object> group = new HashMap<>();
        group.put("GroupName", name);
        group.put("Leaders", Members);
        group.put("GroupImage",profile);
        groups.add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                group.put("Id", documentReference.getId());
                listener.onUpdate(group);
            }
        });
    }
    public void createGroup(String name, List<String> Leaders, List<String> Members, Context c, Button b, String Profile, UpdateListener listener) {
        Map<String, Object> group = new HashMap<>();
        group.put("GroupName", name);
        group.put("Leaders", Leaders);
        group.put("Invites", Members);
        group.put("GroupImage", Profile);
        group.put("Members",new ArrayList<>());
        groups.add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                group.put("Id", documentReference.getId());
                group.put("isLeader",true);
                listener.onUpdate(group);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                b.setBackgroundDrawable(AppCompatResources.getDrawable(c,R.drawable.textholder));
                b.setText(R.string.create);
                b.setEnabled(true);
            }
        });
    }
    public void inviteMembers(String groupId, List<String> toAdd, BasicListener listener){
        for(String i: toAdd){
            groups.document(groupId).update("Invites",FieldValue.arrayUnion(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listener.basicListener();
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
                    List<Map<String,Object>> documentList = new ArrayList<>();
                    int i = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        i++;
                        user = new HashMap<>();
                        user.put("Id", doc.getId());
                        user.put("Username", String.valueOf(doc.get("Username")));
                        user.put("Email", String.valueOf(doc.get("Email")));
                        try {
                            user.put("Profile", String.valueOf(doc.get("Profile")));
                        } catch (Exception ignored) {
                        }
                        documentList.add(user);
                    }
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
                    List<Map<String,Object>> documentList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.getData();
                        user.put("GroupId",document.getId());
                        documentList.add(user);
                        }
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
                            DocumentReference docRef = account.document(document.getId());
                            Map<String, Object> user = new HashMap<>();
                            user.put(field, value);
                            docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"update work");
                                    InitDB(email, new DataListener() {
                                        @Override
                                        public void onDataFound(Map<String,Object>user) {
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
        db.collection("Groups")
                .document(groupId)
                .collection("Projects")
                .whereGreaterThanOrEqualTo("TaskDeadline", today)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                listener.getDeadline(doc.getTimestamp("TaskDeadline"));
                            }
                        }
                    }
                }
        );
    }
    public void updateEmail(String oldEmail, String newEmail, UpdateListener listener) {
        account.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (oldEmail.equals(document.get("Email"))) {
                            DocumentReference docRef = account.document(document.getId());
                            Map<String, Object> user = new HashMap<>();
                            user.put("Email", newEmail);


                            docRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"update work");
                                    InitDB(String.valueOf(user.get("Email")), new DataListener() {
                                        @Override
                                        public void onDataFound(Map<String,Object>user) {
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
        List<Map<String,Object>> documentList = new ArrayList<>();
        List<Map<String,Object>> leaderList = new ArrayList<>();
            groups
                .whereArrayContains("Members",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String,Object>a = document.getData();
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
                                                    Map<String,Object>a = document.getData();
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
        groups.whereArrayContains("Invites",id)
                .addSnapshotListener((value, error) -> {
                    List<Map<String,Object>> documentList = new ArrayList<>();
                    if (error != null) return;
                    assert value != null;
                    for (DocumentChange d : value.getDocumentChanges()) {
                        if (d.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot document = d.getDocument();
                            Map<String,Object>a = document.getData();
                            a.put("Id",document.getId());
                            documentList.add(a);
                        }
                    }
                    listener.onReceive(documentList);
                });
    }
    public void getMembers(String id, MembersListener listener){
        List<Map<String,Object>> ids = new ArrayList<>();
        groups.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        List<String> members = (List<String>) task.getResult().get("Members");
                        if (members!=null){
                            for(String id : members){
                                getUserById(id, new DataListener() {
                                    @Override
                                    public void onDataFound(Map<String,Object>user) {
                                        ids.add(user);
                                        listener.onReceiveMembers(ids);
                                    }

                                    @Override
                                    public void noDuplicateUser() {
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
    public void getTasks(String groupId, String groupName, Uri groupImage, TaskListener listener){
    List<Map<String,Object>> tasks = new ArrayList<>();
        groups.document(groupId).collection("Tasks").addSnapshotListener((value, error) -> {
            if (error != null) return;
            assert value != null;
            for (DocumentChange c :
                    value.getDocumentChanges()) {
                DocumentSnapshot d = c.getDocument();
                if (c.getType() == DocumentChange.Type.ADDED) {
                    Map<String, Object> taskMap= d.getData();
                    taskMap.put("Id",d.getId());
                    taskMap.put("GroupName", groupName);
                    taskMap.put("GroupImage", groupImage);
                    tasks.add(taskMap);
                }
            }
            listener.onTaskReceived(tasks);
        });

    }
    public void getTasks(String userId, TaskListener listener, boolean a){
        getJoinedGroups(userId, new GroupListener() {
            @Override
            public void onReceive(List<Map<String,Object>> groups, List<Map<String,Object>> groupLeaders) {
                for (Map<String,Object>g : groups){
                    DatabaseFuncs.this.groups.document(String.valueOf(g.get("Id"))).collection("Tasks").whereArrayContains("Assigned Members",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) return;
                            List<Map<String,Object>> tasks = new ArrayList<>();
                            assert value != null;
                            for (DocumentChange c :
                                    value.getDocumentChanges()) {
                                DocumentSnapshot d = c.getDocument();
                                if (c.getType() == DocumentChange.Type.ADDED) {
                                    Map<String, Object> taskMap= d.getData();
                                    taskMap.put("Id",d.getId());
                                    taskMap.put("GroupName", g.get("GroupName"));
                                    taskMap.put("GroupImage", g.get("GroupImage"));
                                    tasks.add(taskMap);
                                }
                            }
                            listener.onTaskReceived(tasks);
                        }
                    });
                }
            }

            @Override
            public void onReceive(List<Map<String,Object>> groups) {
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
                                    Map<String,Object>a = document.getData();
                                    a.put("Id",document.getId());
                                    dataListener.onDataFound(a);
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
        Map<String,Object> taskBuilder = new HashMap<>();
        taskBuilder.put("ParentId",groupId);
        taskBuilder.put("TaskName",taskName);
        taskBuilder.put("TaskDescription",taskDescription);
        taskBuilder.put("Assigned Members",members);
        taskBuilder.put("TaskCreated",new Timestamp(new Date()));
        taskBuilder.put("TaskDeadline",new Timestamp(new Date(taskDeadline)));

        groups.document(groupId).collection("Tasks").add(taskBuilder).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                listener.onCreateTaskListener();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                b.setEnabled(true);
                b.setText(R.string.submit);
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
        //
        //  <TODO>
        //      Limit the messages to minimize unnescessary data consumption
        //      This shit is not my original code bruh you do it
        //      I already fixed chat
        //   </TODO>
        //
        // TODO: 2nd chat problem, some messages dont get received

        messages.whereEqualTo("groupId", groupId)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        System.out.println("DEBUG Firestore error: " + error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        System.out.println("DEBUG No new messages detected.");
                        return;
                    }

                    List<Message> updatedMessages = new ArrayList<>();
                    List<Message> newMessages = new ArrayList<>();

                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        QueryDocumentSnapshot d = dc.getDocument();

                        if (userId.equals(d.get("senderId"))) continue;

                        Timestamp messageTimestamp = (Timestamp) Objects.requireNonNull(d.get("timestamp"));
                        Timestamp adjustedTimestamp = listener.getCurrentTimestamp();

                        if (messageTimestamp.getSeconds() > adjustedTimestamp.getSeconds() || (messageTimestamp.getSeconds() == adjustedTimestamp.getSeconds() && messageTimestamp.getNanoseconds() > adjustedTimestamp.getNanoseconds())) {
                            Message message = MessageCreator(d);
                            message.setReplyId(String.valueOf(d.get("replyId")));

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
                }
        );
    }
    private Message MessageCreator(DocumentSnapshot d){
        return new Message(
                d.getId(),
                String.valueOf(d.get("message")),
                String.valueOf(d.get("senderId")),
                String.valueOf(d.get("senderUsername")),
                String.valueOf(d.get("groupId")),
                Uri.parse(String.valueOf(d.get("file"))),
                String.valueOf(d.get("fileType")),
                (Timestamp) d.get("timestamp"));
    }
    public void setAllMessagesReceivedListener(String groupId, AllMessagesReceivedListener listener) {
        List<Message> messages = new ArrayList<>();
        this.messages
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot d: queryDocumentSnapshots) {
                Message message = MessageCreator(d);
                message.setReplyId(String.valueOf(d.get("replyId")));
                messages.add(message);
            }
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
    public void leaveGroup(String id, String groupId, BasicListener listener){
        groups.document(groupId).update("Members", FieldValue.arrayRemove(id)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.basicListener();
            }
        });
        groups.document(groupId).update("Leaders", FieldValue.arrayRemove(id)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.basicListener();
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
