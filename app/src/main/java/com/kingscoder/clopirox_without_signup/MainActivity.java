package com.kingscoder.clopirox_without_signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private ImageView userImageView;
    private FirebaseUser currentUser;
    private TextView userEmailTV, userFullNameTV;
    private FirebaseFirestore mFireStore;
    private CardView medicineCardView, vaccinationCardView, ambulanceCardView, bmiCalCardView, consultantCardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView( 0);
        navigationView.getMenu().getItem(0).setChecked(true);

        userImageView = headerView.findViewById(R.id.main_user_imagview);
        userFullNameTV = headerView.findViewById(R.id.user_full_name_main_textview_navigation);
        userEmailTV = headerView.findViewById(R.id.user_email_textview_navigation);

        if (currentUser.getPhotoUrl() != null){
            Glide.with(this).load(currentUser.getPhotoUrl()).into(userImageView);
        }

        DocumentReference docRef = mFireStore.collection("USERS").document(currentUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String name = user.getFullName();
                userFullNameTV.setText(name);
            }
        });

        if (currentUser.getEmail() != null){
            userEmailTV.setText(currentUser.getEmail());
        }

        initialization();
    }

    private void initialization() {
        medicineCardView = findViewById(R.id.medicine_card_view);
        vaccinationCardView = findViewById(R.id.vaccination_card_view);
        ambulanceCardView = findViewById(R.id.ambulance_card_view);
        bmiCalCardView = findViewById(R.id.bmi_card_view);
        consultantCardView = findViewById(R.id.consultant_card_view);

        medicineCardView.setOnClickListener(this);
        vaccinationCardView.setOnClickListener(this);
        ambulanceCardView.setOnClickListener(this);
        bmiCalCardView.setOnClickListener(this);
        consultantCardView.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {

            return true;
        } else if (id == R.id.menu_notification){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout){
            if (mFirebaseAuth.getCurrentUser() != null){
                signOut();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.medicine_card_view:
                intent = new Intent(getApplicationContext(), MedicinePageActivity.class);
                startActivity(intent);
                break;
            case R.id.vaccination_card_view:
                intent = new Intent(getApplicationContext(), ViccineMainActivity.class);
                startActivity(intent);
                break;
            case R.id.ambulance_card_view:
                intent = new Intent(getApplicationContext(), EmbulenceActivity.class);
                startActivity(intent);
                break;
            case R.id.bmi_card_view:
                intent = new Intent(getApplicationContext(), BMIActivity.class);
                startActivity(intent);
                break;
            case R.id.consultant_card_view:
                intent = new Intent(getApplicationContext(), DoctorDetails.class);
                intent.putExtra("doc", "doc");
                startActivity(intent);
                break;
        }
    }
}
