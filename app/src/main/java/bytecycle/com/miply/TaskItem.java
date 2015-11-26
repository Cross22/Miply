package bytecycle.com.miply;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.wowwee.bluetoothrobotcontrollib.MipRobotSound;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;

import java.util.concurrent.TimeUnit;

/**
 * This is our data model, describing a single task in a sequence of steps.
 * Value stores additional data such as distance/angle/clip/color
 * Created by marco on 11/25/15.
 */
public class TaskItem {

    public enum TaskType {
        FORWARD, BACK, LEFT, RIGHT, COLOR, SPEAKER, DELAY;
    }

        public TaskType taskType;
        public int value;

        public TaskItem (TaskType aTaskType) {
            taskType= aTaskType;
            value= 20;
        }

        public Drawable getDrawable(Context c) {
            int resId=0;
            switch (taskType) {
                case FORWARD:
                    resId= R.drawable.up_icon;
                    break;
                case BACK:
                    resId= R.drawable.down_icon;
                    break;
                case LEFT:
                    resId= R.drawable.left_icon;
                    break;
                case RIGHT:
                    resId= R.drawable.right_icon;
                    break;
                case COLOR:
                    resId= R.drawable.fill_icon;
                    break;
                case SPEAKER:
                    resId= R.drawable.speech_icon;
                    break;
                case DELAY:
                    resId= R.drawable.pause_icon;
                    break;
            }
            return ContextCompat.getDrawable(c, resId);
        }

    public void execute(MipRobot robot) {
        try {
        switch (taskType) {
            case FORWARD:
                // -127..127 for cm values
                // Move Speed is 0..30
                robot.mipDriveDistanceByCm(255);
                // Max: 1.7 seconds forward drive
//                robot.mipDriveForwardForMilliseconds(200*7,30);
                TimeUnit.SECONDS.sleep(5);
                break;
            case BACK:
                robot.mipDriveDistanceByCm(-value);
                TimeUnit.SECONDS.sleep(5);
                break;
            case LEFT:
                // speed= 0..24
                robot.mipTurnLeftByDegrees(90,20);
                TimeUnit.SECONDS.sleep(2);
                break;
            case RIGHT:
                robot.mipTurnRightByDegrees(90, 20);
                TimeUnit.SECONDS.sleep(2);
                break;
            case COLOR:
                final byte maxb= 127;
                final byte minb= 0;
                robot.setMipChestRGBLedWithColor(maxb, minb, minb, minb);
                TimeUnit.SECONDS.sleep(1);
                break;
            case SPEAKER:
                robot.mipPlaySound(new MipRobotSound((byte)99, (byte)0, (byte)100));
                TimeUnit.SECONDS.sleep(1);
                break;
            case DELAY:
                TimeUnit.SECONDS.sleep(this.value);
                break;
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
