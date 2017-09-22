package softmicro.com.permissionactivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.softmicro.xpermission.PermissionCallBack;
import com.softmicro.xpermission.PermissionConstants;
import com.softmicro.xpermission.SpecialPermissionCallBack;
import com.softmicro.xpermission.XPermission;

import java.util.List;


/**
 * Created by wangshuainan on 2017/9/21.
 */

public class CallFragment extends Fragment implements PermissionCallBack,SpecialPermissionCallBack {

    private Button bt_call;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.callfragment, container, false);
        bt_call = (Button) view.findViewById(R.id.id_btn_call);
        bt_call.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),
                        "i am an Button in Fragment ! ",
                        Toast.LENGTH_SHORT).show();
                dotest();

            }
        });
        return view;
    }

    private void dotest() {
        String[] perms = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
        XPermission.requestSpecialPermission(CallFragment.this,perms,this);
        //requestPermissions(perms, PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_FRAGMENT);
//        XPermission.requestPermission(CallFragment.this, perms, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionConstants.REQUEST_CODE_DANGER_PERMISSION_IN_FRAGMENT)
            XPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, CallFragment.this,this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        XPermission.onRequestSpecialPermissionResult(this.getContext(),requestCode,this);
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {
        //Toast.makeText(getActivity(),"success in fragment",Toast.LENGTH_SHORT).show();
        //doCall();
    }

    @Override
    public void onPermissionsReasoned(List<String> perms) {
        Toast.makeText(getActivity(),"reason in fragment",Toast.LENGTH_SHORT).show();
        XPermission.showReason(getActivity(),perms);
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        //Toast.makeText(getActivity(),"deny in fragment",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpecialPermissionsGranted(List<String> perms) {
        Toast.makeText(getActivity(),"special permission grant in fragment",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpecialPermissionsDenied(List<String> perms) {
        Toast.makeText(getActivity(),"special permission fail in fragment",Toast.LENGTH_SHORT).show();
    }
}
