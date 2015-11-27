package bytecycle.com.miply;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.SeekBar;

import com.wowwee.bluetoothrobotcontrollib.MipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.MipRobotSound;
import com.wowwee.bluetoothrobotcontrollib.RobotCommand;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFixed;

import java.util.concurrent.TimeUnit;

/**
 * This is our data model, describing a single task in a sequence of steps.
 * Value stores additional data such as distance/angle/clip/color
 * Created by marco on 11/25/15.
 */
public class TaskItem {

        public TaskType taskType;
        public int value;
        public TaskItem (TaskType aTaskType) {
            taskType= aTaskType;
            switch (aTaskType) {
                case LEFT:
                    value= 90;
                    break;
                case RIGHT:
                    value= 90;
                    break;
                case COLOR:
                    value= 0xFFAAAAAA;
                    break;
                case DELAY:
                    value= 2;
                    break;
                default:
                    value= 20;
            }
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
                robot.mipDriveDistanceByCm(value);
                // Max: 1.7 seconds forward drive
                // Move Speed is 0..30
//              robot.mipDriveForwardForMilliseconds(1700,30);
                TimeUnit.SECONDS.sleep(5);
                break;
            case BACK:
                robot.mipDriveDistanceByCm(-value);
                TimeUnit.SECONDS.sleep(5);
                break;
            case LEFT:
                // speed= 0..24
                robot.mipTurnLeftByDegrees(90,20);
                TimeUnit.SECONDS.sleep(1);
                break;
            case RIGHT:
                robot.mipTurnRightByDegrees(90, 20);
                TimeUnit.SECONDS.sleep(1);
                break;
            case COLOR:
                 byte b= (byte) ((value >> 0) & 0xFF);
                 byte g= (byte) ((value >> 8) & 0xFF);
                 byte r= (byte) ((value >> 16) & 0xFF);
                robot.setMipChestRGBLedWithColor(r,g,b, (byte)0);
                TimeUnit.SECONDS.sleep(2);
                break;
            case SPEAKER:
                MipRobotFixed fixedBot= (MipRobotFixed) robot;
                fixedBot.mipSetRadarMode(MipRobotFixed.RadarMode.RANGE);
//                robot.mipPlaySound(new MipRobotSound((byte)99, (byte)0, (byte)100));
//                TimeUnit.SECONDS.sleep(1);
                break;
            case DELAY:
                TimeUnit.SECONDS.sleep(this.value);
                break;
        }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO: Set range/indents for the given seekbar
    public void configureSeekBar(SeekBar bar) {
        switch (taskType) {
            case LEFT:
                bar.setMax(180);
                break;
            case RIGHT:
                bar.setMax(180);
                break;
            case FORWARD:
                bar.setMax(200);
                break;
            case BACK:
                bar.setMax(200);
                break;
            case DELAY:
                bar.setMax(10);
                break;
            case SPEAKER:
                bar.setMax(100);
                break;
            default:
            ;
        }
        bar.setProgress(value);
    }


    public enum TaskType {
        FORWARD, BACK, LEFT, RIGHT, COLOR, SPEAKER, DELAY;
    }


}
