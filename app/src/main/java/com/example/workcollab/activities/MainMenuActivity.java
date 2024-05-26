package com.example.workcollab.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.workcollab.databinding.ActivityMainMenuBinding;
import com.example.workcollab.databinding.DialogLogoutConfirmBinding;
import com.example.workcollab.fragments.AccountEditFragment;
import com.example.workcollab.fragments.AccountFragment;
import com.example.workcollab.fragments.AssignTaskFragment;
import com.example.workcollab.fragments.CreateGroupFragment;
import com.example.workcollab.fragments.GroupsFragment;
import com.example.workcollab.fragments.InvitesSubFragment;
import com.example.workcollab.fragments.JoinedGroupsSubFragment;
import com.example.workcollab.fragments.MainFragment;
import com.example.workcollab.fragments.ProfileAccountEditFragment;
import com.example.workcollab.fragments.SelectedGroupFragment;
import com.example.workcollab.fragments.SubmitTaskFragment;
import com.example.workcollab.fragments.TaskListFragment;
import com.example.workcollab.fragments.ViewMemberTasks;
import com.example.workcollab.fragments.YouFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

import java.io.InputStream;
import java.util.Map;
import java.util.Stack;

public class MainMenuActivity extends AppCompatActivity implements YouFragment.ButtonListeners, AccountEditFragment.UpdateListener, ProfileAccountEditFragment.ButtonListeners, AccountFragment.ButtonListeners, JoinedGroupsSubFragment.PositionListener, InvitesSubFragment.PositionListener, TaskListFragment.PositionListener, SubmitTaskFragment.onSubmitClick, ViewMemberTasks.PositionListener {
    ActivityMainMenuBinding b;
    DialogLogoutConfirmBinding bl;
    InputStream inputStream;
    ActivityResultLauncher<String> mGetCont;
    MainFragment mf;
    YouFragment sf;
    Map task;
    public static Stack<String> backFlow = new Stack<>();
    public static Map selectedgroup;
    int x = 1;

    public static String selected = "main";
    Bundle bu = new Bundle();

    public static Map user;

    DatabaseFuncs db = new DatabaseFuncs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backFlow.push("main");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 201);
        } else {
            Intent a = new Intent(this, NotifiationsService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Utils.isServiceRunning(this, NotifiationsService.class)) {
                startForegroundService(a);
            }

        }


        String email;
        System.out.println(getUserEmail() + "EmailBeLike2");
        if (!getUserEmail().equals("")) {
            email = getUserEmail();
        } else {
            email = bu.getString("user-email");
        }
        FirebaseApp.initializeApp(this);
        mGetCont=registerForActivityResult(new ActivityResultContracts.GetContent(), o ->{
            Intent intent = new Intent(MainMenuActivity.this, CropperActivity.class);
            intent.putExtra("DATA",o.toString());
            startActivityForResult(intent,101);
        });
        System.out.println(email + "whaiohgoeihaoieug aogoiaea");
        db.InitDB(email, new DatabaseFuncs.DataListener() {

            @Override
            public void onDataFound(Map user) {
                MainMenuActivity.user = user;
                if(selected.equals("main")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance()).commitAllowingStateLoss();
                    b.bottomNavView.setSelectedItemId(R.id.menu_home);
                    return;
                }
                if(selected.equals("groups")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance()).commitAllowingStateLoss();
                    b.bottomNavView.setSelectedItemId(R.id.menu_home);
                    return;
                }
                if(selected.equals("profile")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), YouFragment.newInstance()).commit();
                    b.bottomNavView.setSelectedItemId(R.id.menu_account);
                    return;
                }
                if(selected.equals("tasks")){
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), MainFragment.newInstance()).commitAllowingStateLoss();
                    b.bottomNavView.setSelectedItemId(R.id.menu_home);
                    return;
                }
                if (selected.equals("creategroups")) {
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), CreateGroupFragment.newInstance()).commit();
                }
                if (selected.equals("selectgroup")) {
                    getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(), SelectedGroupFragment.newInstance(selectedgroup)).commit();
                }
            }

            @Override
            public void noDuplicateUser() {

            }
        });
        b.btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.equals("groups")) {
                    backFlow.push("creategroups");
                    replaceFragment(CreateGroupFragment.newInstance(), "creategroups");
                }
                if(selected.equals("tasks")){
                    System.out.println("abs");
                    replaceFragment(AssignTaskFragment.newInstance(selectedgroup),"assignTask");
                }
            }
        });
        b.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int a = menuItem.getItemId();
                if(a == R.id.menu_home){
                    replaceFragment(MainFragment.newInstance(),"main");
                    backFlow.clear();
                    backFlow.push("main");
                } else if (a == R.id.menu_groups) {
                    replaceFragment(GroupsFragment.newInstance(),"groups");
                    backFlow.clear();
                    backFlow.push("groups");
                }else if (a == R.id.menu_tasks){
                    replaceFragment(TaskListFragment.newInstance(false),"tasks");
                }else if (a == R.id.menu_profile){
                    replaceFragment(YouFragment.newInstance(), "profile");
                    backFlow.clear();
                    backFlow.push("profile");
                }
                return true;
            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode+" "+requestCode);
        if (resultCode ==-1&&requestCode == 101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if(result!=null){
                resultUri = Uri.parse(result);

            }
            try{
               db.SaveProfile(user, resultUri, new DatabaseFuncs.UpdateListener() {
                   @Override
                   public void onUpdate(Map user) {
                       replaceFragment(ProfileAccountEditFragment.newInstance(user),"Profile");
                   }
               });

            } catch (Exception e){
                e.printStackTrace();
            }
        }
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
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogInPreferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("user-email").apply();
    }
    public void replaceFragment(Fragment fragment, String condition){
        if(!selected.equals(condition)){
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
        bl.Cancel.setOnClickListener(k->{
            dialog.cancel();
        });
        bl.Ok.setOnClickListener(k ->{
            dialog.cancel();
            Intent toLogIn = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(toLogIn);
            complete();
            finish();

        });
        dialog.show();


    }

    @Override
    public void onBackPressed() {
        if (false) {
            super.onBackPressed();
        }

        if (!backFlow.isEmpty()) {
            backFlow.pop();
        }

//        replaceFragment(MainFragment.newInstance(user), "main");
        if (backFlow.isEmpty()) {
            replaceFragment(new MainFragment(MainMenuActivity.user), "main");
        } else {
            Log.e("woah", backFlow.peek());
            replaceFragment(changeFragment(backFlow.peek()), backFlow.peek());
        }
    }

    @Override
    public void onPress() {
        mGetCont.launch("image/*");
    }

    @Override
    public void onLogOutPress() {
        logOutConfirm();
    }

    @Override
    public void onUpdatedEmail(Map user) {
        stayLogIn(user.get("Email").toString());
        MainMenuActivity.user = user;
    }

    @Override
    public void onPressChangePFP() {
        mGetCont.launch("image/*");
    }

    @Override
    public void onDeletedAccount() {
        complete();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void itemClicked(Map group) {
        JoinedGroupsSubFragment.PositionListener.super.itemClicked(group);
        backFlow.push("selectedgroup");
        getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),SelectedGroupFragment.newInstance(user,group)).commit();
        getSupportFragmentManager().beginTransaction().replace(b.frameFragment.getId(),SelectedGroupFragment.newInstance(group)).commit();
    }
    @Override
    public void taskItemClicked(Map task){

    }

    @Override
    public void onDeny(Map group) {
        InvitesSubFragment.PositionListener.super.onDeny(group);
    }

    @Override
    public void onAccept(Map group) {
        InvitesSubFragment.PositionListener.super.onAccept(group);
    }

    @Override
    public void onSubmitClick(Map task) {
        MainMenuActivity.this.task = task;
        System.out.println("ajieoghaoie");
        if (ContextCompat.checkSelfPermission(MainMenuActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("ajieoghaoie11");
            ActivityCompat.requestPermissions(MainMenuActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            System.out.println("ajieoghaoie23452");
            openFilePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // For .doc files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    public void viewMemberTask(Map task) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent a = new Intent(this, NotifiationsService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Utils.isServiceRunning(this, NotifiationsService.class)) {
                    startForegroundService(a);
                }
            }
        }
    }

    public Fragment changeFragment(String fragmentTag) {
        switch (fragmentTag) {
            case "main":
                return MainFragment.newInstance();
            case "groups":
                return GroupsFragment.newInstance(user);
            case "profile":
                return YouFragment.newInstance(user);
            case "creategroups":
                return CreateGroupFragment.newInstance(user);
            case "selectgroup":
                return SelectedGroupFragment.newInstance(user, selectedgroup);
        }
        return MainFragment.newInstance();
    }
}
