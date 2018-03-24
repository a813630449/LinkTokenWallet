package com.xiaoliang.wallet.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.adapter.TradeInfoAdapter;
import com.xiaoliang.wallet.model.TransactionRecordsVo;
import com.xiaoliang.wallet.model.WalletBalanceVo;
import com.xiaoliang.wallet.utils.BitmapUtil;
import com.xiaoliang.wallet.utils.ConvertUtils;
import com.xiaoliang.wallet.utils.ListViewUtil;
import com.xiaoliang.wallet.utils.ToastUtil;
import com.xiaoliang.wallet.view.DeletePopWindow;
import com.xiaoliang.wallet.view.PopWindow;
import com.xiaoliang.wallet.view.QRPopWindow;
import com.xiaoliang.wallet.wallet.WalletApi;

public class AccountActivity extends BaseActivity implements OnRefreshListener,
		OnClickListener {

	private SwipeRefreshLayout mSwipeLayout;
	private ListView tradeinfoListView;
	private TradeInfoAdapter tradeInfoAdapter;
	private List<TransactionRecordsVo.Record> tradeInfoList;
	private TextView balance, account;
	private String address;
	private Button send;
	private ImageView copy,qrview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		initView();
		initListen();
		initdata();
	}

	private void initView() {
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		tradeinfoListView = (ListView) findViewById(R.id.tradeinfoListView);
		balance = (TextView) findViewById(R.id.balance);
		account = (TextView) findViewById(R.id.account);
		send=(Button) findViewById(R.id.btn_send);
		copy=(ImageView) findViewById(R.id.copy);
		qrview=(ImageView) findViewById(R.id.qr);
		initTitle(R.drawable.back, "账户", R.drawable.more_icon);
	}

	private void loadData() {
		tradeInfoList.clear();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final WalletBalanceVo balancevo = WalletApi.getBalance(address);
				TransactionRecordsVo transactionRecords = WalletApi
						.getTransactionRecords(address);
				if (transactionRecords != null) {
					tradeInfoList.addAll(transactionRecords.getResult());
				}
				if (balancevo.getBalance() != null) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mSwipeLayout.setRefreshing(false);
							balance.setText(balancevo.getBalance());
							tradeinfoListView.setAdapter(tradeInfoAdapter);
							tradeInfoAdapter.notifyDataSetChanged();
						}
					});
				}
			}
		}).start();
	}

	@SuppressWarnings("deprecation")
	private void initdata() {
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		tradeinfoListView.setFocusable(false);
		tradeInfoList = new ArrayList<TransactionRecordsVo.Record>();
		tradeInfoAdapter = new TradeInfoAdapter(this, tradeInfoList);
		tradeinfoListView.setAdapter(tradeInfoAdapter);
		address = getIntent().getExtras().getString("address");
		account.setText(address);
	}
	@Override
	protected void onStart() {
		loadData();
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initListen() {
		mSwipeLayout.setOnRefreshListener(this);
		send.setOnClickListener(this);
		copy.setOnClickListener(this);
		qrview.setOnClickListener(this);
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
	public void onRefresh() {
		tradeInfoList.clear();
		loadData();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_send:
			Intent intent=new Intent(this, SendActivity.class);
			intent.putExtra("address", address);
			startActivity(intent);
			break;
		case R.id.copy:
			 // 从API11开始android推荐使用android.content.ClipboardManager
	        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
	        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
	        // 将文本内容放到系统剪贴板里。
	        cm.setText(address);
	        ToastUtil.showStringLong(getApplicationContext(), "复制地址成功！"+address);
	        break;
		case R.id.qr:
			try {
				Bitmap bitmap=BitmapUtil.createQRCode(address, ConvertUtils.dp2px(this, 100));
				QRPopWindow popWindow=new QRPopWindow(this,bitmap);
				popWindow.showAtLocation(qrview, Gravity.CENTER, 0, 0);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				ToastUtil.showStringLong(this, "生成二维码失败");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void RightBtnClick() {
		// TODO Auto-generated method stub
		DeletePopWindow popWindow = new DeletePopWindow(this,address);
		popWindow.showPopupWindow(btn_right);
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

}
