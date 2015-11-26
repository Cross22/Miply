package bytecycle.com.miply;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;

/**
 * Created by marco on 11/25/15.
 */
public class RobotTasks {
    public enum TaskType {
        FORWARD, BACK, LEFT, RIGHT, COLOR, SPEAKER, DELAY;
    }

    public class Item {
        public TaskType taskType;
        int value;
    }

    private final RobotListener mRobotListener;

    public RobotTasks(ListView listView, RobotListener robotListener) {
        mRobotListener= robotListener;
        int count= listView.getCount();
        for (int i=0; i<count; ++i) {
            ImageView v= (ImageView) listView.getItemAtPosition(i);
            v=v;
        }
        mRobotListener.mRobot.mipDriveDistanceByCm(20);
    }

    public void cancel() {

    }
}
