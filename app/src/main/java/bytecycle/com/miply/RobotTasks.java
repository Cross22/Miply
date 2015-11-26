package bytecycle.com.miply;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by marco on 11/25/15.
 */
public class RobotTasks {

    private ExecutorService mExecutor= Executors.newSingleThreadExecutor();

    private final RobotListener mRobotListener;

    private ArrayList<TaskItem> mItemList;

    public RobotTasks(ListView listView, RobotListener robotListener) {
        mRobotListener= robotListener;

        // copy from listview to internal list for later processing
        int count= listView.getCount();
        mItemList= new ArrayList<>(count);

        for (int i=0; i<count; ++i) {
            TaskItem item= (TaskItem) listView.getItemAtPosition(i);
            mItemList.add(item);
        }
    }

    public void execute() {
        if (mRobotListener==null || mRobotListener.mRobot==null)
            return;

        // 0..7
//        mRobotListener.mRobot.setMipVolumeLevel((byte) 3);

        for (final TaskItem item : mItemList) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    item.execute(mRobotListener.mRobot);
                }
            });
        }
//        mRobotListener.mRobot.readMipStatus();
    }

    public void cancel() {
        mExecutor.shutdownNow();
        mItemList= null;
    }
}
