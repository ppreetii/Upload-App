package vssut.mca.preeti.minorprojectone;

public class Approved {
    String timestamp;
    Approved()
    {

    }
    Approved(String timestamp)
    {
        //this.key=key;
        this.timestamp=timestamp;

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
