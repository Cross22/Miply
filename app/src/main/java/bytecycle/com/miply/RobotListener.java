package bytecycle.com.miply;

import android.graphics.Color;
import android.util.Log;

import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFixed;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by marco on 11/25/15.
 */
public class RobotListener implements MipRobotFixed.MipRobotInterfaceFixed {
    final String TAG= "RobotListener";
    protected MainActivity mParent;

    public MipRobot mRobot;

    RobotListener(MainActivity parent) {
        mParent= parent;
    }

    void log(String msg, MipRobot robot) {
        Log.d(TAG, robot.getName() + " " +msg);
    }

    @Override
    //  @Override
    public void mipDeviceReady(MipRobot sender) {
        final MipRobot robot = sender;
        mRobot= sender;
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParent.mListView.setBackgroundColor(Color.GREEN);
                log("Connected!", robot);
            }
        });
    }

    @Override
    public void mipDeviceDisconnected(MipRobot mipRobot) {
        final MipRobot robot = mipRobot;
        mRobot= null;
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParent.mListView.setBackgroundColor(Color.RED);
                log("Disconnected!", robot);
            }
        });
    }

    @Override
    public void mipRobotDidReceiveBatteryLevelReading(MipRobot mipRobot, int i) {
        Log.d(TAG, "Battery status: "+i);
    }

    @Override
    public void mipRobotDidReceivePosition(MipRobot mipRobot, byte b) {
        Log.d(TAG, "Position status: "+b);
    }

    @Override
    public void mipRobotDidReceiveHardwareVersion(int major, int minor) {
        Log.d(TAG, "HW Version: "+major + "." + minor);
    }

    @Override
    public void mipRobotDidReceiveSoftwareVersion(Date date, int i) {
        Log.d(TAG, "SW Version: "+date);
    }

    @Override
    public void mipRobotDidReceiveVolumeLevel(int level) {
        Log.d(TAG, "Volume level: "+level);
    }

    @Override
    public void mipRobotDidReceiveIRCommand(ArrayList<Byte> arrayList, int i) {
        Log.d(TAG, "IR Command? "+i);
    }

    @Override
    public void mipRobotDidReceiveWeightReading(byte b, boolean b1) {
        Log.d(TAG, "mipRobotDidReceiveWeightReading : "+b + "," + b1);
    }

    @Override
    public void mipRobotIsCurrentlyInBootloader(MipRobot mipRobot, boolean b) {
        Log.d(TAG, "inBootloader : "+b);
    }

    @Override
    public void mipRobotDidReceiveRangeReading(MipRobotFixed.ObstacleDistance distance) {
        Log.d(TAG, "Distance reading : "+distance.toString() );
    }
}
