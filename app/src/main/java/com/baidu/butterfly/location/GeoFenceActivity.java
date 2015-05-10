package com.baidu.butterfly.location;



import java.util.ArrayList;
import java.util.List;

import com.baidu.butterfly.R;
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.LocationClient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GeoFenceActivity extends Activity {
    private LocationClient mLocationClient;
    private GeofenceClient mGeofenceClient;
    private AddGeofenceListener listener;
    private Button addGeoFence, removeGeoFence;
    private EditText geoID, geoLontitude, geoLatitude, duration;
    private ListView geoFenceList;
    private StringBuffer buffer;
    private TextView logMsg;
    private List<String> getIDList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Geofence fence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
        mGeofenceClient = ((LocationApplication) getApplication()).mGeofenceClient;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getIDList);
        geoID = (EditText) findViewById(R.id.geoid);
        geoLontitude = (EditText) findViewById(R.id.geolontitude);
        geoLatitude = (EditText) findViewById(R.id.geolatitude);
        duration = (EditText) findViewById(R.id.geoduration);
        addGeoFence = (Button) findViewById(R.id.addfence);
        removeGeoFence = (Button) findViewById(R.id.removefence);
        logMsg = (TextView) findViewById(R.id.geofencelog);
        geoFenceList = (ListView)findViewById(R.id.geolist);
        ((LocationApplication) getApplication()).logMsg = logMsg;
        listener = new AddGeofenceListener();
        geoFenceList.setAdapter(adapter);
        fence = new Geofence();
        mGeofenceClient.registerGeofenceTriggerListener(fence);
    }
    public Handler MessageHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            logMsg.setText(msg.getData().getString("msg"));

            adapter.notifyDataSetChanged();
        }

    };
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        addGeoFence.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                double longtitude =Double.valueOf(geoLontitude.getText().toString());
                double latotide =Double.valueOf(geoLatitude.getText().toString());
                BDGeofence fence = new BDGeofence.Builder().setGeofenceId(geoID.getText().toString()).
                        setCircularRegion(longtitude,latotide, BDGeofence.RADIUS_TYPE_SMALL).
                        setExpirationDruation(10L * (3600 * 1000)).
                        setCoordType(BDGeofence.COORD_TYPE_BD09LL).
                        build();
                mGeofenceClient.setInterval(199009999);
                mGeofenceClient.addBDGeofence(fence, listener);
                Toast.makeText(GeoFenceActivity.this, "���ڴ���Χ��...", Toast.LENGTH_SHORT).show();
            }
        });
        removeGeoFence.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                List<String> fences= new ArrayList<String>();
                fences.add(geoID.getText().toString());
                mGeofenceClient.removeBDGeofences(fences, new RemoveFenceListener());
            }
        });
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    public class AddGeofenceListener implements OnAddBDGeofencesResultListener {

        @Override
        public void onAddBDGeofencesResult(int statusCode, String geofenceId) {
            try {
                if (statusCode == BDLocationStatusCodes.SUCCESS) {
                    // ������ʵ�ִ���Χ���ɹ��Ĺ����߼�
                    Message msg = MessageHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "Χ��" + geofenceId + "���ӳɹ�");
                    msg.setData(bundle);
                    MessageHandler.sendMessage(msg);
                    Toast.makeText(GeoFenceActivity.this, "Χ��" + geofenceId + "���ӳɹ�", Toast.LENGTH_SHORT).show();
                    if (mGeofenceClient != null) {
                        setData(geofenceId);
                        // �����ӵ���Χ���ɹ��󣬿�������Χ�����񣬶Ա��δ����ɹ����ѽ���ĵ���Χ��������ʵʱ������
                        mGeofenceClient.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public class Geofence implements OnGeofenceTriggerListener {

        @Override
        public void onGeofenceTrigger(String arg0) {
            // TODO Auto-generated method stub

            String temp = logMsg.getText().toString();
            temp+="\n����Χ��"+ arg0;
            Message msg = MessageHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("msg", temp);
            msg.setData(bundle);
            MessageHandler.sendMessage(msg);
        }

        @Override
        public void onGeofenceExit(String arg0) {
            // TODO Auto-generated method stub
            String temp = logMsg.getText().toString();
            temp+="\n�˳�Χ��"+ arg0;
            Message msg = MessageHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("msg", temp);
            msg.setData(bundle);
            MessageHandler.sendMessage(msg);
        }

    }
    private void setData(String str){
        getIDList.add(str);
    }
    public class RemoveFenceListener implements OnRemoveBDGeofencesResultListener {
        @Override
        public void onRemoveBDGeofencesByRequestIdsResult(int statusCode, String[] geofenceRequestIds) {
            if (statusCode == BDLocationStatusCodes.SUCCESS){
                Message msg = MessageHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("msg", "Χ��"  + "ɾ���ɹ�");
                msg.setData(bundle);
                MessageHandler.sendMessage(msg);
                for(int i=0;i<geofenceRequestIds.length;i++){
                    if(getIDList.contains(geofenceRequestIds[i])){
                        getIDList.remove(geofenceRequestIds[i]);
                    }
                }
            }}}
}