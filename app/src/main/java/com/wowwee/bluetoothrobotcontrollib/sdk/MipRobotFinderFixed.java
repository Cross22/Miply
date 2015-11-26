
package com.wowwee.bluetoothrobotcontrollib.sdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import com.wowwee.bluetoothrobotcontrollib.BluetoothLeService;
import com.wowwee.bluetoothrobotcontrollib.BluetoothRobot;
import com.wowwee.bluetoothrobotcontrollib.BluetoothRobotFinder;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFixed;
import com.wowwee.bluetoothrobotcontrollib.util.AdRecord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MipRobotFinderFixed extends BluetoothRobotFinder {
    private static MipRobotFinderFixed instance = null;
    public static final int MRFScanOptionMask_ShowAllDevices = 0;
    public static final int MRFScanOptionMask_FilterByProductId = 1;
    public static final int MRFScanOptionMask_FilterByServices = 2;
    public static final int MRFScanOptionMask_FilterByDeviceName = 4;
    public static final String MipRobotFinder_MipFound = "com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipFound";
    public static final String MipRobotFinder_MipListCleared = "com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipListCleared";
    public static final String MipRobotFinder_BluetoothError = "com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_BluetoothError";
    public static final String MipRobotFinder_MipPairedFound = "com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipPairedFound";
    private int mScanOptionsFlagMask = 1;
    private List<MipRobotFixed> mMipsFound = new ArrayList();
    protected List<MipRobotFixed> mMipsConnected = new ArrayList();
    private ArrayList<BluetoothDevice> pairedDeviceList;
    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            MipRobotFinderFixed.this.handleFoundBluetoothDevice(device, scanRecord, rssi);
        }
    };

    protected MipRobotFinderFixed() {
    }

    public static MipRobotFinderFixed getInstance() {
        if(instance == null) {
            instance = new MipRobotFinderFixed();
        }

        return instance;
    }

    public static IntentFilter getMipRobotFinderIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipFound");
        intentFilter.addAction("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipListCleared");
        intentFilter.addAction("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_BluetoothError");
        intentFilter.addAction("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipPairedFound");
        return intentFilter;
    }

    protected void mipRobotDidConnect(MipRobotFixed mip) {
        if(mip != null && !this.mMipsConnected.contains(mip)) {
            this.mMipsConnected.add(mip);
        }

    }

    protected void mipRobotDidDisconnect(MipRobotFixed mip) {
        if(mip != null) {
            this.mMipsConnected.remove(mip);
            this.mMipsFound.remove(mip);
        }

    }

    public void scanForMips() {
        this.startScan();
    }

    public void scanForMipsForDuration(int pSeconds) {
        this.startScan();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                MipRobotFinderFixed.this.stopScanForMips();
            }
        }, (long)(pSeconds * 1000));
    }

    public void stopScanForMips() {
        if(this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }

    }

    public MipRobotFixed firstConnectedMip() {
        return this.mMipsConnected.size() > 0?(MipRobotFixed)this.mMipsConnected.get(0):null;
    }

    public void clearFoundMipList() {
        ArrayList mipsToRemove = new ArrayList();
        Iterator var3 = this.mMipsFound.iterator();

        MipRobotFixed intent;
        while(var3.hasNext()) {
            intent = (MipRobotFixed)var3.next();
            if(!this.mMipsConnected.contains(intent)) {
                mipsToRemove.add(intent);
            }
        }

        var3 = mipsToRemove.iterator();

        while(var3.hasNext()) {
            intent = (MipRobotFixed)var3.next();
            this.mMipsFound.remove(intent);
        }

        Intent intent1 = new Intent("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipListCleared");
        this.mContext.sendBroadcast(intent1);
    }

    public MipRobotFixed findMip(BluetoothDevice pDevice) {
        Iterator var3 = this.mMipsFound.iterator();

        while(var3.hasNext()) {
            MipRobotFixed mip = (MipRobotFixed)var3.next();
            if(mip.getBluetoothDevice().equals(pDevice)) {
                return mip;
            }
        }

        return null;
    }

    public List<MipRobotFixed> getMipsFoundList() {
        return this.mMipsFound;
    }

    public List<MipRobotFixed> getMipsConnected() {
        return this.mMipsConnected;
    }

    private void startScan() {
        if(this.mBluetoothAdapter != null) {
            boolean filterByServices = (this.mScanOptionsFlagMask & 2) != 0;
            if(filterByServices) {
                UUID[] services = BluetoothRobot.getAdvertisedServiceUUIDs();
                this.mBluetoothAdapter.startLeScan(services, this.mLeScanCallback);
            } else {
                this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            }

            this.getPairedMip();
        } else {
            Log.e("BLERobotControlLib", "Could not start scan, bluetooth adapter is null. Your device may not support Bluetooth LE");
        }

    }

    private void getPairedMip() {
        if(this.mBluetoothAdapter != null) {
            Set paired = this.mBluetoothAdapter.getBondedDevices();
            if(paired != null && paired.size() != 0) {
                ArrayList list = new ArrayList();
                list.addAll(paired);
                this.pairedDeviceList = new ArrayList();
                Iterator intent = paired.iterator();

                while(intent.hasNext()) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice)intent.next();
                    if(bluetoothDevice.getName() != null && bluetoothDevice.getType() == 2 && bluetoothDevice.getName().toLowerCase().contains("mip")) {
                        this.pairedDeviceList.add(bluetoothDevice);
                    }
                }

                if(this.pairedDeviceList.size() > 0) {
                    Intent intent1 = new Intent("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipPairedFound");
                    intent1.putExtra("PairedBluetoothDevices", this.pairedDeviceList);
                    this.mContext.sendBroadcast(intent1);
                }
            }
        }

    }

    private void handleFoundBluetoothDevice(BluetoothDevice device, byte[] advertisingData, int rssi) {
        if(!this.isBluetoothDeviceExist(device)) {
            List records = AdRecord.parseScanRecord(advertisingData);
            MipRobotFixed robot = new MipRobotFixed(device, records, (BluetoothLeService)null);
            robot.debugLog();
            boolean filterByProductId = (this.mScanOptionsFlagMask & 1) != 0;
            if(filterByProductId && robot.getProductId() != 5) {
                return;
            }

            boolean filterByDeviceName = (this.mScanOptionsFlagMask & 4) != 0;
            if(filterByDeviceName && !robot.getName().contains("WW-MIP")) {
                return;
            }

            this.mMipsFound.add(robot);
            Intent intent = new Intent("com.wowwee.bluetoothrobotcontrollib.MipRobotFinder_MipFound");
            intent.putExtra("BluetoothDevice", robot.getBluetoothDevice());
            this.mContext.sendBroadcast(intent);
        }

    }

    private boolean isBluetoothDeviceExist(BluetoothDevice pDevice) {
        Iterator var3 = this.mMipsFound.iterator();

        while(var3.hasNext()) {
            MipRobotFixed mip = (MipRobotFixed)var3.next();
            if(mip.getBluetoothDevice().equals(pDevice)) {
                return true;
            }
        }

        return false;
    }
}
