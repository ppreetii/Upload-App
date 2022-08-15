package vssut.mca.preeti.minorprojectone;

public class Firebase
{
   String name,branch,rollno,roomno,phoneNo,guardianNo,reason,leaveDate,leaveTime,returnDate,returnTime,travelMode;
    Firebase()
    {
        // Default constructor required for calls to DataSnapshot.getValue(Firebase.class)

    }
    Firebase(String name,String branch,String rollno,String roomno,String phoneNo,String guardianNo,String reason,String leaveDate,String leaveTime,String returnDate,String returnTime,String travelMode)
    {
        this.name=name;
        this.branch=branch;
        this.rollno=rollno;
        this.roomno=roomno;
        this.phoneNo=phoneNo;
        this.guardianNo=guardianNo;
        this.reason=reason;
        this.leaveDate=leaveDate;
        this.leaveTime=leaveTime;
        this.returnDate=returnDate;
        this.returnTime=returnTime;
        this.travelMode=travelMode;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public String getRollno() {
        return rollno;
    }

    public String getRoomno() {
        return roomno;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getGuardianNo() {
        return guardianNo;
    }

    public String getReason() {
        return reason;
    }

    public String getLeaveDate() {
        return leaveDate;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getTravelMode() {
        return travelMode;
    }

}
