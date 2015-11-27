package bytecycle.com.miply;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFinderFixed;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFixed;

import java.util.List;
import java.util.ResourceBundle;

public class MainActivity extends AppCompatActivity {
    // Listview backing adapter
    protected TaskAdapter mAdapter;
    DragSortListView mListView;
    protected BluetoothAdapter mBluetoothAdapter;

    // Action bar menu
    protected Menu mMenu;

    // currently active task list
    protected RobotTasks mTasks;



    protected void makeClickable(int viewId, final TaskItem.TaskType taskType) {
        final ImageView iv = (ImageView) findViewById(viewId);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertItemIntoList(new TaskItem(taskType), -1, -1); // single click to add to bottom of list
            }
        });
    }

    // DragSortListView requires some extensive configuration to work right..
    protected void configureDragListView() {
        // Set Listview backing store (it's an array of views)
        mAdapter = new TaskAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    TaskItem item = mAdapter.getItem(from);
                    mAdapter.remove(item);
                    mAdapter.insert(item, to);
                }
            }
        });

        mListView.setRemoveListener(new DragSortListView.RemoveListener() {
                                        @Override
                                        public void remove(int which) {
                                            mAdapter.remove(mAdapter.getItem(which));
                                        }
                                    }
        );

        DragSortController controller= new DragSortController(mListView);
        controller.setRemoveEnabled(true);
        controller.setRemoveMode(1);
        controller.setSortEnabled(true);
        controller.setDragInitMode(1);
        mListView.setFloatViewManager(controller);
        mListView.setOnTouchListener(controller);
        mListView.setDragEnabled(true);

        // Icon in the list row that serves as handle for dragging the row
        // defaults to entire row if not set
        controller.setDragHandleId(R.id.draghandle);
    }

    protected void configureBleService() {
        final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Set BluetoothAdapter to MipRobotFinderFixed
        MipRobotFinderFixed.getInstance().setBluetoothAdapter(mBluetoothAdapter);

        // Set Context to MipRobotFinderFixed
        MipRobotFinderFixed finder = MipRobotFinderFixed.getInstance();
        finder.setApplicationContext(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android support toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Action templates in bottom shelf should be draggable
        makeClickable(R.id.imgUp, TaskItem.TaskType.FORWARD);
        makeClickable(R.id.imgDown, TaskItem.TaskType.BACK);
        makeClickable(R.id.imgLeft, TaskItem.TaskType.LEFT);
        makeClickable(R.id.imgRight, TaskItem.TaskType.RIGHT);
        makeClickable(R.id.imgFill, TaskItem.TaskType.COLOR);
        makeClickable(R.id.imgPause, TaskItem.TaskType.DELAY);
        makeClickable(R.id.imgSpeech, TaskItem.TaskType.SPEAKER);

        // Action templates can be dragged onto the list view
        mListView = (DragSortListView) findViewById(R.id.listView);
        configureDragListView();
        configureBleService();
    }

    void insertItemIntoList(TaskItem item, int x, int y) {
        int row= mListView.pointToPosition(x,y);
        if (row < 0)
            row = mListView.getCount(); // append
        mAdapter.insert(item, (int)row);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    public void enableRunButton(boolean enable) {
        MenuItem item= mMenu.findItem(R.id.action_run);
        item.setEnabled(enable);
        if (enable) {
            item.setIcon(R.drawable.ic_play_circle_filled_white_48dp);
        } else {
            item.setIcon(R.drawable.ic_not_interested_white_48dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Return true for any custom action we are handling
        switch (item.getItemId()) {
            case R.id.action_run:
                if (mTasks!=null) {
                    mTasks.cancel();
                    mTasks= null;
                }
                mTasks= new RobotTasks(mListView, mRobotListener);
                mTasks.execute();
                return true;
            //TODO:
//            case R.id.action_settings:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            MipRobotFinderFixed.getInstance().scanForMips();
        } else {
            MipRobotFinderFixed.getInstance().stopScanForMips();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.registerReceiver(mMipFinderBroadcastReceiver, MipRobotFinderFixed.getMipRobotFinderIntentFilter());
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Log.e("Miply","No bluetooth!");
//                TextView noBtText = (TextView)this.findViewById(R.id.no_bt_text);
//                noBtText.setVisibility(View.VISIBLE);
            }
        }

        // Search for mip
        MipRobotFinderFixed.getInstance().clearFoundMipList();

        // When doing a search, turn off any existing searches and try again
        scanLeDevice(false);
        scanLeDevice(true);
    }

    protected RobotListener mRobotListener= new RobotListener(this);

    protected final BroadcastReceiver mMipFinderBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MipRobotFinderFixed.MipRobotFinder_MipFound.equals(action)) {
                // Connect to mip
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        List<MipRobotFixed> mipFoundList = MipRobotFinderFixed.getInstance().getMipsFoundList();
                        if (mipFoundList != null && mipFoundList.size() > 0){
                            MipRobotFixed selectedMipRobot = mipFoundList.get(0);
                            if (selectedMipRobot != null){
                                connectToMip(selectedMipRobot);
                            }
                        }
                    }
                }, 3000);

            }
        }
    };

    private void connectToMip(final MipRobotFixed mip) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mip.setCallbackInterface(mRobotListener);
                mip.connect(MainActivity.this.getApplicationContext());
//                TextView connectionView = (TextView)MainMenuActivity.this.findViewById(R.id.connect_text);
                Log.d("Miply","Connecting to: "+mip.getName());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
