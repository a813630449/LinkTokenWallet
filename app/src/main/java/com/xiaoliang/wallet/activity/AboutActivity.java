package com.xiaoliang.wallet.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.utils.AppUtils;
import com.xiaoliang.wallet.utils.ToastUtil;
import com.xiaoliang.wallet.view.PopWindow;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initView();
		initListen();
		initdata();
	}

	private void initView() {
		version=(TextView) findViewById(R.id.version);
		initTitle(R.drawable.back, "关于软件", R.drawable.more_icon);
	}

	private void initdata() {
		String ver=AppUtils.getAppInfo(this).getVersionName();
		version.setText("当前软件版本  V"+ver);
	}

	private void initListen() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
	}

	@Override
	protected void RightBtnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void LeftBtnClick() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected void CenterBtnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		if(!isExistMainActivity(MainActivity.class)){
			Intent intent=new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		super.onDestroy();
	}

	private boolean isExistMainActivity(Class<?> activity) {
		Intent intent = new Intent(this, activity);
		ComponentName cmpName = intent.resolveActivity(getPackageManager());
		boolean flag = false;
		if (cmpName != null) { // 说明系统中存在这个activity
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10); // 获取从栈顶开始往下查找的10个activity
			for (RunningTaskInfo taskInfo : taskInfoList) {
				if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
					flag = true;
					break; // 跳出循环，优化效率
				}
			}
		}
		return flag;
	}

	private void showMessage(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ToastUtil.showStringLong(getApplicationContext(), msg);
				dimissProgress();
			}
		});
	}
}
