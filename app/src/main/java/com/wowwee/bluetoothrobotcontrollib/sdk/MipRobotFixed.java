package com.wowwee.bluetoothrobotcontrollib.sdk;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.wowwee.bluetoothrobotcontrollib.BluetoothLeService;
import com.wowwee.bluetoothrobotcontrollib.MipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.RobotCommand;
import com.wowwee.bluetoothrobotcontrollib.util.AdRecord;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco Grubert on 11/25/15.
 */
public class MipRobotFixed extends MipRobot {

    public MipRobotFixed(BluetoothDevice pBluetoothDevice, List<AdRecord> pScanRecords, BluetoothLeService pBluetoothLeService) {
        super(pBluetoothDevice, pScanRecords, pBluetoothLeService);
    }

    // This adds the property change listener that is missing from WowWee's v2 implementation

    public void propertyChange(PropertyChangeEvent event) {
        String propertyName = event.getPropertyName();
        Log.d("BluetoothRobotPrivate", "Received propertyChange " + event.getPropertyName() + " nuvotonChipStatusKeyPathKVO true ?" + propertyName.equals("nuvotonChipStatus"));
        int sentdata;

        if (propertyName.equals("lastRobotCommand")) {
            RobotCommand sentdata1 = (RobotCommand) event.getNewValue();
            this.didReceiveRobotCommand(sentdata1);
        } else {
            super.propertyChange(event);
        }
    }

    // The code below adds the missing "radar" support

    public enum RadarMode {
        OFF(0), GESTURES(2), RANGE(4);

        RadarMode(int i) { value= (byte)i; }
        public byte value;
    }

    // Ideally this should be sent via the callback listener or broadcast receiver.
    // I am just putting it here for demo purposes.
    public ObstacleDistance detectedObject= ObstacleDistance.NONE;

    public enum ObstacleDistance {
        NONE, CLOSE, FAR;
    }


    protected void handleReceivedMipCommand(RobotCommand robotCommand) {
        if (robotCommand != null) {
            byte commandValue = robotCommand.getCmdByte();
            ArrayList dataArray = robotCommand.getDataArray();
            if (commandValue != MipCommandValues.kMipGetGameMode) {
                if (commandValue == MipCommandValues.kMipSetRadarModeOrRadarResponse) {
                    byte distance = ((Byte) dataArray.get(0)).byteValue();
                    switch (distance) {
                        case 2:
                            this.detectedObject = ObstacleDistance.FAR;
                            break;
                        case 3:
                            this.detectedObject = ObstacleDistance.CLOSE;
                            break;
                        default:
                            this.detectedObject = ObstacleDistance.NONE;
                            break;
                    }
                    return;
                }
            }
        }
        // MipRobot handles most other command callbacks
        super.handleReceivedMipCommand(robotCommand);
    }

    public void mipSetRadarMode(RadarMode mode) {
        this.sendRobotCommand(
                RobotCommand.create(MipCommandValues.kMipSetRadarModeOrRadarResponse, mode.value)
        );
    }

}
