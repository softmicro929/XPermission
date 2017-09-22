package softmicro.com.permissionactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.softmicro.xpermission.PermissionCallBack;
import com.softmicro.xpermission.PermissionConstants;
import com.softmicro.xpermission.XPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionCallBack {

    private Button bt_call;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setLayoutResourceID(R.layout.activity_main,this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        videoView.setVideoPath("http://60.205.114.50:29999/colorshow/aa.3gp");
//        videoView.start();
        bt_call = (Button) findViewById(R.id.button);
        //videoView = (VideoView) findViewById(R.id.videoview);
        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "打电话", Snackbar.LENGTH_SHORT).show();
                callPhone2();
            }
        });
    }
//
//    @Override
//    public void initView() {
//        bt_call = (Button) findViewById(R.id.button);
//        videoView = (VideoView) findViewById(R.id.videoview);
//    }
//
//    @Override
//    public void initEvent() {
//        bt_call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Snackbar.make(v, "打电话", Snackbar.LENGTH_SHORT).show();
//                callPhone2();
//            }
//        });
//    }
//
//    public void callPhone1() {
//        //,Manifest.permission.SYSTEM_ALERT_WINDOW
//        //Manifest.permission.CALL_PHONE,
//        //
//        doUnderPermissionTask(new String[]{Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.WRITE_SETTINGS}, new OnPermissionResult() {
//            @Override
//            public void onSuccess() {
//                doCall();
//            }
//
//            @Override
//            public void onFail() {
//                Snackbar.make(MainActivity.this.getWindow().getDecorView(), "onfail", Snackbar.LENGTH_SHORT).show();
//            }
//        });
//    }


    public void callPhone2(){
        String[] perms = new String[]{Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE};
        if(XPermission.hasPermission(this,perms)){
            doCall();
        }else{
            XPermission.requestPermission(this,perms,this);
        }
    }

    public void doCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "10086");
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_ACTIVITY)
            XPermission.onRequestPermissionsResult(requestCode,permissions,grantResults,this,this);
    }


    @Override
    public void onPermissionsGranted(List<String> perms) {
        Toast.makeText(this,"success",Toast.LENGTH_SHORT).show();
        doCall();
    }

    @Override
    public void onPermissionsReasoned(List<String> perms) {
        Toast.makeText(this,"reason",Toast.LENGTH_SHORT).show();
        XPermission.showReason(this,perms);
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        Toast.makeText(this,"deny",Toast.LENGTH_SHORT).show();
    }
}
