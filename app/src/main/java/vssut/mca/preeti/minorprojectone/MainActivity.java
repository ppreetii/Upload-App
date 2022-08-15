package vssut.mca.preeti.minorprojectone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private DatabaseReference myref;
    String name, branch,rollno,roomno,phoneno,guardianno,reason,address,leavedate,leavetime,returndate,returntime,travelmode;
    EditText ettName, etBranch, etRollNo,etRoomNo, etPhoneNo,etGuardian,etReason,etAddress,etLeaveDate,etLeaveTime,etReturnDate,etReturnTime,etTravelMode;
    Button btnStatus, btnSubmit,btnAutoFill;
    SharedPreferences state,status,spleavedate;
    SharedPreferences.Editor editor,editorStatus,editorLeaveDate;
    SQLiteDatabase database,dbInfo;
    String uid=""; String returnDateTime;String leaveDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this sharedpreference value determines if this first time use, if yes, store data in sqlite DB to be used for Autofill option
        //here, DB=information, table=details
        state=getSharedPreferences("myPref",MODE_PRIVATE);
        editor=state.edit();
        //this sharedpreference value is used by leaveStatusActivity to get leavedate , so that we can reset views if current date exceeds leaveDate
        status=getSharedPreferences("mystatus",MODE_PRIVATE);
        editorStatus=status.edit();

        findIdOfView();// this function sets up all views of our UI
        btnSubmit.setOnClickListener(new myclick());//event listeners for all three buttons which will be triggered on click
        btnStatus.setOnClickListener(new myclick());
        btnAutoFill.setOnClickListener(new myclick());
        //leave_date=status.getString("status",null);


    }

    protected void findIdOfView()
    {
        ettName=(EditText)findViewById(R.id.etName);
        etBranch=(EditText)findViewById(R.id.etBranch);
        etRollNo=(EditText)findViewById(R.id.etRollNo);
        etRoomNo=(EditText)findViewById(R.id.etRoomNo);
        etPhoneNo=(EditText)findViewById(R.id.etPhoneNo);
        etGuardian=(EditText)findViewById(R.id.etGuardian);
        etReason=(EditText)findViewById(R.id.etReason);
        etAddress=(EditText)findViewById(R.id.etAddress);
        etLeaveDate=(EditText)findViewById(R.id.etLeaveDate);
        etLeaveTime=(EditText)findViewById(R.id.etLeaveTime);
        etReturnDate=(EditText)findViewById(R.id.etReturnDate);
        etReturnTime=(EditText)findViewById(R.id.etReturnTime);
        etTravelMode=(EditText)findViewById(R.id.etTravelMode);
        btnStatus=(Button)findViewById(R.id.btnStatus);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnAutoFill=(Button)findViewById(R.id.btnAutoFillForm);

    }


    public class myclick implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {

            if (v==btnSubmit)
            {
                //1.get all values from EditTexts to get entire request information
                getStrings();
                //2. Check value of STATE sharedpreference.If it is not set, that means the user is interacting with app for first time.
                //Hence,saveToSQlite() is called so that STATE value can chaange from default value "no"
                //Our database name=information.Inside it ,a table "details" is created, which stores info about request.
                //This info is used to setup textviews for common information sent out each time,i.e, autofill feature uses it
                if (state.getString("STATE","no").equals("no"))
                    saveToSQLite();
                //3. Now uid value is set to unique key of request made.If it is not empty, then we have already sent a request.
                //If it is empty, we push request information as object to firebase DB and get unique generated key during the process inside uid.
                //This uid value is the way to find if our request has been approved or not.
                if (status.getString("status","noKey").equals("noKey"))
                {
                    //to prevent multiple request
                    sendRequest();// this method sends our request to firebase DB for approval
                    requestInformation();//this store request info in addition to uid value + changing value for STATUS sharedpreference with leavedate
                    //this info is used in LeaveStatusActivity to setup request info.Since this info might change frequently,so a seperate table has been made.

                }

                else
                {
                    Toast.makeText(getApplicationContext(),"Request already sent.",Toast.LENGTH_LONG).show();
                }

            }
            else if (v==btnAutoFill)
            {
                autoFill();//status sharedpreference value checked . If value is yes, data is retrieved from "details' table of "information" DB.
                //If not, data is stored
            }
            else if (v==btnStatus)
            {
                startActivity(new Intent(MainActivity.this,LeaveStatusActivity.class));

            }

        }

        protected void autoFill()
        {
            if (state.getString("STATE","no").equals("yes"))//check if any information has been stored before
            {
                database=openOrCreateDatabase("information",MODE_PRIVATE,null);
                Cursor cursor=database.rawQuery("select * from details",null);
                while(cursor.moveToNext())
                {
                    ettName.setText(cursor.getString(0));
                    etBranch.setText(cursor.getString(1));
                    etRollNo.setText(cursor.getString(2));
                    etRoomNo.setText(cursor.getString(3));
                    etPhoneNo.setText(cursor.getString(4));
                    etGuardian.setText(cursor.getString(5));
                    etAddress.setText(cursor.getString(6));
                    etTravelMode.setText(cursor.getString(7));
                }

                database.close();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"No Data stored yet.",Toast.LENGTH_LONG).show();
            }


        }
        protected void saveToSQLite()
        {
            database=openOrCreateDatabase("information",MODE_PRIVATE,null);
            database.execSQL("create table if not exists details  (name varchar(30), branch varchar(10), rollno varchar(12) ,roomno varchar(5),phoneno varchar(11),guardian varchar(11),address varchar(50),travelmode varchar(10))");
            getStrings(); // all variables will be initialised
            database.execSQL("insert into details values ('"+name+"','"+branch+"','"+rollno+"','"+roomno+"','"+phoneno+"','"+guardianno+"','"+address+"','"+travelmode+"')");
            editor.putString("STATE","yes");
            editor.commit();
           // database.close();
        }
        protected void getStrings()
        {
            name=ettName.getText().toString();
            branch=etBranch.getText().toString();
            rollno=etRollNo.getText().toString();
            roomno=etRoomNo.getText().toString();
            phoneno=etPhoneNo.getText().toString();
            guardianno=etGuardian.getText().toString();
            reason=etReason.getText().toString();
            address=etAddress.getText().toString();
            leavedate=etLeaveDate.getText().toString();
            leavetime=etLeaveTime.getText().toString();
            returndate=etReturnDate.getText().toString();
            returntime=etReturnTime.getText().toString();
            travelmode=etTravelMode.getText().toString();
            returnDateTime=returndate.concat(returntime);
            leaveDateTime=leavedate.concat(leavetime);
           //requestInformation();

        }

        public void sendRequest()
        {
            Firebase obj=new Firebase(name,branch,rollno,roomno,phoneno,guardianno,reason,leavedate,leavetime,returndate,returntime,travelmode);
            try {

                myref = FirebaseDatabase.getInstance().getReference().child("request");
                uid = myref.push().getKey();
                myref.child(uid).setValue(obj);
                Toast.makeText(getApplicationContext(), "Request successfully sent !!!", Toast.LENGTH_LONG).show();
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(),"Failed to send request!!",Toast.LENGTH_LONG).show();
            }
        }
        protected void requestInformation()
        {
            dbInfo=openOrCreateDatabase("request",MODE_PRIVATE,null);
            dbInfo.execSQL("create table if not exists leave_info (name varchar(30), branch varchar(10), rollno varchar(12),roomno varchar(5),reason varchar(30),address varchar(50),leaveDateTime varchar(20),returnDateTime varchar(20),phoneno varchar(11),guardian varchar(11),travelmode varchar(10),keyvalue varchar(30))");
            dbInfo.execSQL("insert into leave_info values ('"+name+"','"+branch+"','"+rollno+"','"+roomno+"','"+reason+"','"+address+"','"+leaveDateTime+"','"+returnDateTime+"','"+phoneno+"','"+guardianno+"','"+travelmode+"','"+uid+"')");
            editorStatus.putString("status",uid);
            editorStatus.putString("leavedate",leavedate);
            editorStatus.commit();

        }

    }
}
