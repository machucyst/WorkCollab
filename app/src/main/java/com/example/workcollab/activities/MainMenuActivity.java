package com.example.workcollab.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.workcollab.DatabaseFuncs;
import com.example.workcollab.NotifiationsService;
import com.example.workcollab.R;
import com.example.workcollab.Utils;
import com.example.workcollab.adapters.DeadlinesAdapter;
import com.example.workcollab.adapters.GroupMembersAdapter;
import com.example.workcollab.databinding.ActivityMainMenuBinding;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.fragments.AccountEditFragment;
import com.example.workcollab.fragments.AccountFragment;
import com.example.workcollab.fragments.AppearanceFragment;
import com.example.workcollab.fragments.BottomDialogCreateFragment;
import com.example.workcollab.fragments.BottomDialogViewProfileFragment;
import com.example.workcollab.fragments.CreateGroupFragment;
import com.example.workcollab.fragments.GroupsFragment;
import com.example.workcollab.fragments.InvitesSubFragment;
import com.example.workcollab.fragments.JoinedGroupsSubFragment;
import com.example.workcollab.fragments.MainFragment;
import com.example.workcollab.fragments.SelectedGroupFragment;
import com.example.workcollab.fragments.SelectedGroupSettingsFragment;
import com.example.workcollab.fragments.SubmitTaskFragment;
import com.example.workcollab.fragments.TaskListFragment;
import com.example.workcollab.fragments.ViewMemberTasks;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class MainMenuActivity extends AppCompatActivity implements DeadlinesAdapter.ClickItemListener,GroupMembersAdapter.PositionListener,SelectedGroupSettingsFragment.GroupPFP,AccountEditFragment.UpdateListener, AccountFragment.ButtonListeners, JoinedGroupsSubFragment.PositionListener, InvitesSubFragment.PositionListener, TaskListFragment.PositionListener, SubmitTaskFragment.onSubmitClick, ViewMemberTasks.PositionListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 32, PICK_FILE_REQUEST = 123;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB

    ActivityMainMenuBinding b;
    DialogLogoutConfirmBinding bl;
    static boolean activityRunning = false;
    Map<String,Object> task;
    static int index;
    public static Stack<String> backFlow = new Stack<>();
    public static Map<String,Object> selectedGroup;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static String selected = "main";
    Bundle bu = new Bundle();
    public static Map<String,Object> user;

    DatabaseFuncs db = new DatabaseFuncs();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Notifications stuff ask Plant
        backFlow.push("main");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 201);
        } else {
            Intent a = new Intent(this, NotifiationsService.class);
            if (!Utils.isServiceRunning(this, NotifiationsService.class)) {
                startForegroundService(a);
            }

        }


        String email;
        if (!getUserEmail().isEmpty()) {
            email = getUserEmail();
        } else {
            email = bu.getString("user-email");
        }
        FirebaseApp.initializeApp(this);
        db.InitDB(email, new DatabaseFuncs.DataListener() {

            @Override
            public void onDataFound(Map<String,Object> user) {
                MainMenuActivity.user = user;
                    reload();
                }

            @Override
            public void noDuplicateUser() {

            }
        });

        // Add Groups || Tasks Button
        b.btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialogCreateFragment dba = new BottomDialogCreateFragment();
                dba.show(getSupportFragmentManager(),new BottomDialogCreateFragment().getTag());
            }
        });
        // Bottom Nav View Button functions
        b.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                navItemReselect(menuItem);
                return false;
            }

        });

        b.bottomNavView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                navItemReselect(menuItem);
            }
        });
    }
    private void navItemReselect(MenuItem menuItem){
        int a = menuItem.getItemId();
        if(a == R.id.menu_home){
            replaceFragment(MainFragment.newInstance(),"main");
            backFlow.clear();
            backFlow.push("main");
        } else if (a == R.id.menu_groups) {
            replaceFragment(GroupsFragment.newInstance(false),"groups");
            backFlow.clear();
            backFlow.push("groups");
        }else if (a == R.id.menu_tasks){
            backFlow.clear();
            backFlow.push("tasks");
            replaceFragment(TaskListFragment.newInstance(false),"tasks");
        }else if (a == R.id.menu_profile){
            replaceFragment(AccountFragment.newInstance(), "account");
            backFlow.clear();
            backFlow.push("profile");
        }
    }

    // Image Editor Cropper Code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + " " + requestCode);
        switch (requestCode) {
            case 101:
                if (data != null) {
                    Uri sourceUri = data.getData();

                    // Destination URI
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "IMG_" + System.currentTimeMillis()));

                    // Start UCrop activity
                    UCrop.Options options = new UCrop.Options();
                    options.setContrastEnabled(false);
                    options.setBrightnessEnabled(false);
                    options.setFreeStyleCropEnabled(false);
                    options.setSaturationEnabled(false);
                    options.setSharpnessEnabled(false);
                    options.setShowCropGrid(false);
                    assert sourceUri != null;
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(450, 450)
                            .withOptions(options)
                            .start(this);

                }
                break;
            case UCrop.REQUEST_CROP:
            if (resultCode == RESULT_OK) {
                assert data != null;
                final Uri resultUri = UCrop.getOutput(data);
                try {
                    switch (index){
                        case 10:
                        db.saveProfile(user, resultUri, new DatabaseFuncs.UpdateListener() {
                            @Override
                            public void onUpdate(Map<String,Object>  user) {
                                MainMenuActivity.user = user;
                                replaceFragment(AccountFragment.newInstance(), "Profile");
                            }
                        });
                        break;
                        case 20:
                            db.saveGroupProfile(selectedGroup, resultUri, new DatabaseFuncs.UpdateListener() {
                                @Override
                                public void onUpdate(Map<String,Object> user) {
                                    replaceFragment(SelectedGroupFragment.newInstance(user),"selectedgroup");
                                }
                            });
                            break;
                    }

                } catch (Exception ignored) {}

            } else if (resultCode == UCrop.RESULT_ERROR) {
                assert data != null;
                final Throwable cropError = UCrop.getError(data);
                // Handle the error
            }
            break;
            case PICK_FILE_REQUEST:
                if (data != null) {
                    if(getFileSize(data.getData())<=MAX_FILE_SIZE){
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_fragment,SubmitTaskFragment.newInstance(MainMenuActivity.this.task, Objects.requireNonNull(data.getData())))
                                .commit();
                    }else{
                        Toast.makeText(this,"File size too large (20 MB Only)",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            case RESULT_CANCELED:
                reload();
                break;
        }
    }
    private long getFileSize(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        long size = cursor.getLong(sizeIndex);
        cursor.close();
        return size;
    }
    private String getUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user-email", "");
    }
    public void stayLogIn(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }
    void complete(){
        //Clear auto login
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("user-email").apply();
        FirebaseAuth.getInstance().signOut();
    }
    public void replaceFragment(Fragment fragment, String condition){
        //Change Display
        String waa = "";
        if (!backFlow.isEmpty()) {
            waa = backFlow.peek();
        }
        if(!selected.equals(condition) || !waa.equals(condition)){
            selected = condition;
            getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),fragment).addToBackStack(null).commit();
        }
    }
    public void logOutConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);

        // Inflate the layout for the dialog
        bl = DialogLogoutConfirmBinding.inflate(getLayoutInflater().from(this));
        builder.setView(bl.getRoot());
        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        bl.Cancel.setOnClickListener(k->{
            dialog.cancel();
        });
        bl.Ok.setOnClickListener(k ->{
            dialog.cancel();
            Intent toLogIn = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(toLogIn);
            complete();
            finish();
            selected="main";

        });
        dialog.show();


    }

    @Override
    public void onBackPressed() {


        try{
            if (!backFlow.isEmpty()) {
                backFlow.pop();
            }
            if (backFlow.isEmpty()) {
                replaceFragment(MainFragment.newInstance(), "main");
            } else {
                Log.e("woah", backFlow.peek());
                replaceFragment(changeFragment(backFlow.peek()), backFlow.peek());
            }
        } catch (Exception e){
            super.onBackPressed();

        }
    }
    @Override
    public void onLogOutPress() {
        logOutConfirm();
    }

    @Override
    public void onUpdatedEmail(Map<String,Object> user) {
        stayLogIn(String.valueOf(user.get("Email")));
        MainMenuActivity.user = user;
    }
    @Override
    public void onPressChangePFP() {
        index = 10;
        Intent intent = new Intent();
        intent.setType("image/*");  // For .doc files
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 101);
    }
    @Override
    public void onDeletedAccount() {
        complete();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void itemClicked(Map<String,Object>  group) {
        JoinedGroupsSubFragment.PositionListener.super.itemClicked(group);
        backFlow.push("selectedgroup");
        getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),SelectedGroupFragment.newInstance(group)).commit();
    }
    @Override
    public void onDeny(Map<String,Object>  group) {
        InvitesSubFragment.PositionListener.super.onDeny(group);
    }

    @Override
    public void onAccept(Map<String,Object>  group) {
        InvitesSubFragment.PositionListener.super.onAccept(group);
    }

    @Override
    public void onSubmitClick(Map<String,Object>  task) {
        MainMenuActivity.this.task = task;
        openFilePicker();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void openFilePicker(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }
    public Fragment changeFragment(String fragmentTag) {
        switch (fragmentTag) {
            case "main":
                return MainFragment.newInstance();
            case "groups":
                return GroupsFragment.newInstance(false);
            case "profile":
                return AccountFragment.newInstance();
            case "creategroups":
                return CreateGroupFragment.newInstance();
            case "selectgroup":
                return SelectedGroupFragment.newInstance(selectedGroup);
            case "appearance":
                return AppearanceFragment.newInstance();
            case "tasks":
                return TaskListFragment.newInstance(false);
        }
        return MainFragment.newInstance();
    }

    public void viewMemberTask(Map<String,Object> task) {
        //TODO: yeah idk
    }
    public void reload(){
    //In case of Activity crash/changes reload fragment
        switch (selected) {
            case "main":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance()).commitAllowingStateLoss();
                b.bottomNavView.setSelectedItemId(R.id.menu_home);
                return;
            case "groups":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), GroupsFragment.newInstance(false)).commitAllowingStateLoss();
                b.bottomNavView.setSelectedItemId(R.id.menu_home);
                return;
            case "account":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), AccountFragment.newInstance()).commit();
                b.bottomNavView.setSelectedItemId(R.id.menu_profile);
                return;
            case "creategroups":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), CreateGroupFragment.newInstance()).commit();
                return;
            case "selectgroup":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), SelectedGroupFragment.newInstance(selectedGroup)).commit();
                return;
            case "appearance":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), AppearanceFragment.newInstance()).commit();
                return;
            case "tasks":
                getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), TaskListFragment.newInstance(false)).commit();
                break;
//        if(selected.equals("tasks")){
//            getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance()).commitAllowingStateLoss();
//            b.bottomNavView.setSelectedItemId(R.id.menu_tasks);
//            return;
//        }
//              >  idk why theres 2 tasks
//              >  im very confused
        }
    }

    @Override
    public void onGroupChanged() {
        index = 20;
        Intent intent = new Intent();
        intent.setType("image/*");  // For .doc files
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 101);
    }

    @Override
    public void otherOne() {
        //TODO:???????????????????????
        getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),MainFragment.newInstance()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bu.getBoolean("notStayLog")){
            mAuth.signOut();
        }
    }

    public static boolean isActivityRunning() {
        return activityRunning;
    }

    @Override
    public void onMemberClicked(Map<String,Object> user) {
        BottomDialogViewProfileFragment bd = new BottomDialogViewProfileFragment(String.valueOf(user.get("Id")));
        bd.show(getSupportFragmentManager(),new BottomDialogViewProfileFragment(String.valueOf(user.get("Id"))).getTag());
    }

    @Override
    public void onItemClick(int position, Map<String,Object> task) {
        replaceFragment(SubmitTaskFragment.newInstance(task),"taskselected");
    }
}
