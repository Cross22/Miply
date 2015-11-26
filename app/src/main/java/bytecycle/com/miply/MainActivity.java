package bytecycle.com.miply;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFinder;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageAdapter mAdapter;
    DragSortListView mListView;
    private BluetoothAdapter mBluetoothAdapter;

    protected void makeDraggable(int viewId) {
        final ImageView iv = (ImageView) findViewById(viewId);
        // Long click listener to enable dragging. However, long press takes too long
        // so we also enable single click below
        iv.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ShadowBuilder shadow = new ShadowBuilder(iv);
                        view.startDrag(null, shadow, iv, 0);
                        return false;
                    }
                }
        );
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertViewIntoList(view, -1,-1); // single click to add to bottom of list
            }
        });
    }

    // DragSortListView requires some extensive configuration to work right..
    protected void configureDragListView() {
        // Set Listview backing store (it's an array of views)
        mAdapter = new ImageAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    View item = mAdapter.getItem(from);
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

        // Set BluetoothAdapter to MipRobotFinder
        MipRobotFinder.getInstance().setBluetoothAdapter(mBluetoothAdapter);

        // Set Context to MipRobotFinder
        MipRobotFinder finder = MipRobotFinder.getInstance();
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
        makeDraggable(R.id.imgUp);
        makeDraggable(R.id.imgDown);
        makeDraggable(R.id.imgLeft);
        makeDraggable(R.id.imgRight);
        makeDraggable(R.id.imgFill);
        makeDraggable(R.id.imgPause);
        makeDraggable(R.id.imgSpeech);


        // Action templates can be dragged onto the list view
        mListView = (DragSortListView) findViewById(R.id.listView);
        mListView.setOnDragListener(new DragTarget());

        configureDragListView();

        configureBleService();
    }

    void insertViewIntoList(View v, int x, int y) {
        int row= mListView.pointToPosition(x,y);
        if (row < 0)
            row = mListView.getCount(); // append
        mAdapter.insert(v, (int)row);
    }

    protected class DragTarget implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    return true;

                case DragEvent.ACTION_DROP:
                    insertViewIntoList((View) event.getLocalState(), (int) event.getX(), (int) event.getY());

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    protected RobotTasks mTasks;

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
                return true;
            case R.id.action_settings:
                //TODO:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            MipRobotFinder.getInstance().scanForMips();
        } else {
            MipRobotFinder.getInstance().stopScanForMips();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.registerReceiver(mMipFinderBroadcastReceiver, MipRobotFinder.getMipRobotFinderIntentFilter());
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Log.e("Miply","No bluetooth!");
//                TextView noBtText = (TextView)this.findViewById(R.id.no_bt_text);
//                noBtText.setVisibility(View.VISIBLE);
            }
        }

        // Search for mip
        MipRobotFinder.getInstance().clearFoundMipList();

        // When doing a search, turn off any existing searches and try again
        scanLeDevice(false);
        scanLeDevice(true);
    }

    protected RobotListener mRobotListener= new RobotListener(this);

    protected final BroadcastReceiver mMipFinderBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MipRobotFinder.MipRobotFinder_MipFound.equals(action)) {
                // Connect to mip
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        List<MipRobot> mipFoundList = MipRobotFinder.getInstance().getMipsFoundList();
                        if (mipFoundList != null && mipFoundList.size() > 0){
                            MipRobot selectedMipRobot = mipFoundList.get(0);
                            if (selectedMipRobot != null){
                                connectToMip(selectedMipRobot);
                            }
                        }
                    }
                }, 3000);

            }
        }
    };

    private void connectToMip(final MipRobot mip) {
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
