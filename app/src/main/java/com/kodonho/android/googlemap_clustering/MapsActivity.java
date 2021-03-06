package com.kodonho.android.googlemap_clustering;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONException;

import java.io.InputStream;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setMap();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setMap();
    }

    private void setMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setCluster();
    }

    private void setCluster(){
        LatLng center = new LatLng(51.503186, -0.126446);
        float zoomLevel = 10f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,zoomLevel));

        clusterManager = new ClusterManager<Item>(this,mMap);
        // 모양바꾸기
        // DefaultClusterRenderer 를 상속받아서 Custom 클래스생성후에
        // clusterManager.setRenderer(커스텀클래스);

        // 클러스터(그룹)를 클릭했을 때 발생
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Item>() {
            @Override
            public boolean onClusterClick(Cluster<Item> cluster) {
                Toast.makeText(MapsActivity.this,"아이템개수="+cluster.getSize()
                        ,Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // 마커단위를 클릭했을 때 발생
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Item>() {
            @Override
            public boolean onClusterItemClick(Item item) {
                Toast.makeText(MapsActivity.this,"마커좌표="+item.getPosition()
                        ,Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        // 드래그가 완료된 시점에 동작하는 이벤트 리스너
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        try {
            readItems();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void readItems() throws JSONException{
        InputStream inputStream = getResources().openRawResource(R.raw.radar_search);
        List<Item> items = new ItemReader().read(inputStream);
        clusterManager.addItems(items);
    }

    private ClusterManager<Item> clusterManager;

}

class Item implements ClusterItem {
    private LatLng coord;
    public Item(double lat, double lng){
        coord = new LatLng(lat,lng);
    }

    @Override
    public LatLng getPosition(){
        return coord;
    }
}
