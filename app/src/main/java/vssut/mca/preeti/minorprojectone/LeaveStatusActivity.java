package vssut.mca.preeti.minorprojectone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LeaveStatusActivity extends Activity {

    TextView tvStatus, tvName, tvBranch, tvRollNo, tvRoomNo, tvReason, tvAddress, tvLeave, tvReturn, tvPersonal, tvGuardian, tvTravel, tvApproveDate;
    ImageView imgStatus;
    SQLiteDatabase dbInfo;
    private String userid, timestamp, leavedate;
    SharedPreferences status;
    SharedPreferences.Editor editorStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_status);
        findIDs();
        status = getSharedPreferences("mystatus", MODE_PRIVATE);
        editorStatus = status.edit();
        setupTextViews();
    }

    protected void findIDs() {
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvName = (TextView) findViewById(R.id.tvName);
        tvBranch = (TextView) findViewById(R.id.tvBranch);
        tvRollNo = (TextView) findViewById(R.id.tvRollNo);
        tvRoomNo = (TextView) findViewById(R.id.tvRoomNo);
        tvReason = (TextView) findViewById(R.id.tvReason);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvLeave = (TextView) findViewById(R.id.tvLeaveDateTime);
        tvReturn = (TextView) findViewById(R.id.tvReturnDateTime);
        tvPersonal = (TextView) findViewById(R.id.tvPersonalNo);
        tvGuardian = (TextView) findViewById(R.id.tvGuardianNo);
        tvTravel = (TextView) findViewById(R.id.tvTravelMode);
        tvApproveDate = (TextView) findViewById(R.id.tvApprovalDate);
        imgStatus = (ImageView) findViewById(R.id.imageView);
    }

    protected void setupTextViews() {

        if (status.getString("status", "noKey").equals("noKey"))

        {

        }
        else
            {
                if (HasLeaveDatePassed())
                {
                    originalViews();
                    clearDatabase();
                    Toast.makeText(this, "Data RESET Complete", Toast.LENGTH_LONG).show();
                }
                else
                {
                    dbInfo = openOrCreateDatabase("request", MODE_PRIVATE, null);
                    Cursor cursor = dbInfo.rawQuery("select * from leave_info", null);

                    while (cursor.moveToNext()) {
                        tvName.setText(tvName.getText().toString().concat(cursor.getString(0)));
                        tvBranch.setText(tvBranch.getText().toString().concat(cursor.getString(1)));
                        tvRollNo.setText(tvRollNo.getText().toString().concat(cursor.getString(2)));
                        tvRoomNo.setText(tvRoomNo.getText().toString().concat(cursor.getString(3)));
                        tvReason.setText(tvReason.getText().toString().concat(cursor.getString(4)));
                        tvAddress.setText(tvAddress.getText().toString().concat(cursor.getString(5)));
                        tvLeave.setText(tvLeave.getText().toString().concat(", ").concat(cursor.getString(6)));
                        tvReturn.setText(tvReturn.getText().toString().concat(", ").concat(cursor.getString(7)));
                        tvPersonal.setText(tvPersonal.getText().toString().concat(cursor.getString(8)));
                        tvGuardian.setText(tvGuardian.getText().toString().concat(cursor.getString(9)));
                        tvTravel.setText(tvTravel.getText().toString().concat(cursor.getString(10)));
                        userid = cursor.getString(11);
                        if (!userid.isEmpty()) {
                            // imgStatus.setBackground();
                            imgStatus.setBackgroundColor(Color.YELLOW);
                            tvStatus.setText("Request Pending.");
                        }
                    }

                    knowLeaveStatus();
                }

        }


    }

    public void knowLeaveStatus() {
        try {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("approved");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            if (key.equals(userid)) {
                                timestamp = child.getValue(Approved.class).getTimestamp();
                                tvStatus.setText("Request Approved.");
                                tvApproveDate.setText("DT:" + timestamp);
                                imgStatus.setImageResource(R.drawable.ic_green_tick);
                                imgStatus.setBackgroundColor(Color.WHITE);
                                Toast.makeText(LeaveStatusActivity.this, "KEY FOUND :]", Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }
                    }
                    else
                        {
                        Toast.makeText(LeaveStatusActivity.this, "No requests approved yet :(", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LeaveStatusActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();

                }
            };
            databaseReference.addValueEventListener(eventListener);
        } catch (Exception e) {
            Toast.makeText(this, "Error while connecting to Firebase.Try again after some time or check your Internet Connection", Toast.LENGTH_LONG).show();

        }

    }

    private void clearDatabase() {

            try {
                SQLiteDatabase dbRequest = openOrCreateDatabase("request", MODE_PRIVATE, null);
                dbRequest.execSQL("delete from leave_info ");
                dbRequest.close();
                editorStatus.remove("status");
                editorStatus.remove("leavedate");
                editorStatus.commit();
                // originalViews();
                Toast.makeText(this, "Successful to clear Database", Toast.LENGTH_LONG).show();


            } catch (Exception e) {
                Toast.makeText(this, "Failed to clear Databases", Toast.LENGTH_LONG).show();

            }

    }

    private boolean HasLeaveDatePassed()
    {
        boolean b=false;
        if (status.getString("leavedate","NoLeaveDate").equals("NoLeaveDate"))
        {
            //Do nothing
        }
        else
        {
            String timestamp = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
            String leavedate=status.getString("leavedate",timestamp);
            int subTimeStamp = Integer.parseInt(timestamp.substring(0, 2));
            int subLeaveDate = Integer.parseInt(leavedate.substring(0, 2));
            if (subTimeStamp > subLeaveDate)
                b=true;
        }

        return b;
    }

        private void originalViews()
        {
            tvName.setText("Name:");
            tvBranch.setText("Branch:");
            tvRollNo.setText("RollNo:");
            tvRoomNo.setText("RoomNo:");
            tvReason.setText("Reason:");
            tvAddress.setText("DestinationAddress:");
            tvLeave.setText("Leave DateTime:");
            tvReturn.setText("Return DateTime:");
            tvPersonal.setText("Personal No:");
            tvGuardian.setText("Guardian No:");
            tvTravel.setText("Travel Mode:");
            tvStatus.setText("No Pending Request.");
            tvApproveDate.setText("Dt:");
            imgStatus.setImageResource(R.drawable.ic_info);
            imgStatus.setBackgroundColor(Color.parseColor("#FF00574B"));

        }

}
