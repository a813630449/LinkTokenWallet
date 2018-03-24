package com.xiaoliang.wallet.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;

import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.adapter.AccountAdapter;
import com.xiaoliang.wallet.model.Accounts;
import com.xiaoliang.wallet.model.KeystoreInfoBean;
import com.xiaoliang.wallet.model.WalletBalanceVo;
import com.xiaoliang.wallet.utils.AppUtils;
import com.xiaoliang.wallet.utils.FileUtils;
import com.xiaoliang.wallet.utils.JsonHelper;
import com.xiaoliang.wallet.utils.PathUtil;
import com.xiaoliang.wallet.utils.ToastUtil;
import com.xiaoliang.wallet.view.PopWindow;
import com.xiaoliang.wallet.wallet.WalletApi;
import com.xiaoliang.wallet.zxing.android.CaptureActivity;

public class MainActivity extends BaseActivity implements OnRefreshListener,
		OnClickListener, OnItemClickListener {

	private SwipeRefreshLayout mSwipeLayout;
	private ListView accountsListView;
	private AccountAdapter accountAdapter;
	private List<KeystoreInfoBean> accountModelList;
	private KeystoreInfoBean keystoreInfo;
	private TextView balance_show;
	
    private static final int SCANNING_CODE = 1;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    
    private long firstTime=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initListen();
		initdata();
		getNewVersion();
	}
	private void getNewVersion() {
		BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.forceUpdate(this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                //根据updateStatus来判断更新是否成功
            	int a=updateStatus;
            	if (updateStatus == 1) {
//					ToastUtil.showStringLong(MainActivity.this, "您当前已是最新版本！");
				}
            }
        });
	}
	private void start() {
		Uri uri = getIntent().getData();
		if(uri!=null){
			String path="";
			if ("file".equalsIgnoreCase(uri.getScheme())) {// 使用第三方应用打开
				path = uri.getPath();
			}
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {// 4.4以后
				path = PathUtil.getPath(this, uri);
//				path = PathUtil.getRealFilePath(this, uri);
			} else {// 4.4以下下系统调用方法
				path = PathUtil.getRealPathFromURI(this,uri);
			}
			String content = FileUtils.readFile2String(path, "UTF-8");
			keystoreInfo = JsonHelper.fromJson(content, KeystoreInfoBean.class);
			if (keystoreInfo == null) {
				ToastUtil.showStringLong(this, "您导入的不是钱包文件！");
				return;
			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							String address = keystoreInfo.getRealAddress();
							WalletBalanceVo balance = WalletApi
									.getBalance(address);
							if (balance.getBalance() != null) {
								keystoreInfo.setBalance(balance.getBalance());
								saveToFile(keystoreInfo);
							} else {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										ToastUtil.showStringLong(
														getApplicationContext(),
														"导入出错");
									}
								});
							}

						} catch (final Exception e) {
							// TODO Auto-generated catch block
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									ToastUtil.showStringLong(
											getApplicationContext(),
											"出现异常" + e.getLocalizedMessage());
								}
							});
						}

					}
				}).start();
			}
			getIntent().setData(null);
		}
		loadData();
	}

	private void initView() {
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		accountsListView = (ListView) findViewById(R.id.accountlist);
		balance_show = (TextView) findViewById(R.id.balance_show);
		initTitle(R.drawable.scan_icon, R.drawable.logo,
				R.drawable.mainpage_add);
	}

	@SuppressWarnings("deprecation")
	private void initdata() {
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		accountsListView.setFocusable(false);
		accountModelList = new ArrayList<KeystoreInfoBean>();
		accountAdapter = new AccountAdapter(this, accountModelList);
		accountsListView.setAdapter(accountAdapter);
	}

	private void initListen() {
		mSwipeLayout.setOnRefreshListener(this);
		accountsListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		start();
		super.onStart();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SCANNING_CODE) {
	            if (data != null) {
	                String content = data.getStringExtra(DECODED_CONTENT_KEY);
	                if(content.startsWith("0x") || content.length()==40){
	                	if(accountModelList.size()>0){
	                		String add=accountModelList.get(0).getRealAddress();
		                	Intent intent=new Intent(this, SendActivity.class);
		                	intent.putExtra("address", add);
		                	intent.putExtra("toaddress", content);
		                	startActivity(intent);
		                }
	                	
	                }else{
	                	ToastUtil.showStringLong(getApplicationContext(), "您扫描的不是钱包地址！");
	                }
	            }
	        }else if(requestCode == 10010){
	        	String path = "";	
				Uri uri = data.getData();
				if ("file".equalsIgnoreCase(uri.getScheme())) {// 使用第三方应用打开
					path = uri.getPath();
				}
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {// 4.4以后
					path = PathUtil.getPath(this, uri);
//					path = PathUtil.getRealFilePath(this, uri);
				} else {// 4.4以下下系统调用方法
					path = PathUtil.getRealPathFromURI(this,uri);
				}
				
				String content = FileUtils.readFile2String(path, "UTF-8");
				keystoreInfo = JsonHelper.fromJson(content, KeystoreInfoBean.class);
				if (keystoreInfo == null) {
					ToastUtil.showStringLong(this, "您导入的不是钱包文件！");
					return;
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								String address = keystoreInfo.getRealAddress();
								WalletBalanceVo balance = WalletApi
										.getBalance(address);
								if (balance.getBalance() != null) {
									keystoreInfo.setBalance(balance.getBalance());
									saveToFile(keystoreInfo);
								} else {
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											ToastUtil
													.showStringLong(
															getApplicationContext(),
															"导入出错");
										}
									});
								}

							} catch (final Exception e) {
								// TODO Auto-generated catch block
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										ToastUtil.showStringLong(
												getApplicationContext(),
												"出现异常" + e.getLocalizedMessage());
									}
								});
							}

						}
					}).start();

				}
	        }
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void saveToFile(KeystoreInfoBean keystoreInfo) {
		String basepath = Environment.getDataDirectory().getAbsolutePath();
		basepath = basepath + "/data/"
				+ AppUtils.getAppInfo(getApplicationContext()).getPackageName();
		String path = basepath + "/account";
		String filepath = path + "/account.json";
		boolean createDir = FileUtils.createOrExistsDir(path);
		boolean createFile = FileUtils.createOrExistsFile(filepath);
		List<KeystoreInfoBean> infoBeans = new ArrayList<KeystoreInfoBean>();
		if (createDir && createFile) {
			String content = FileUtils.readFile2String(filepath, "UTF-8");
			if (content == null || content.equals("")) {
				infoBeans.add(keystoreInfo);
				Accounts accounts = new Accounts();
				accounts.setInfoBeans(infoBeans);
				String json = JsonHelper.toJson(accounts);
				FileUtils.writeFileFromString(filepath, json, false);
			} else {
				Accounts accounts = JsonHelper
						.fromJson(content, Accounts.class);
				infoBeans = accounts.getInfoBeans();
				for (KeystoreInfoBean keystoreInfoBean : infoBeans) {
					if (keystoreInfoBean.getAddress().equals(
							keystoreInfo.getAddress())) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								ToastUtil.showStringLong(
										getApplicationContext(),
										"该钱包已导入，请勿重复导入！");
							}
						});

						return;
					}
				}
				infoBeans.add(0, keystoreInfo);
				accounts.setInfoBeans(infoBeans);
				String json = JsonHelper.toJson(accounts);
				FileUtils.writeFileFromString(filepath, json, false);
			}
			loadData();
		}

	}

	private void loadData() {
		String basepath = Environment.getDataDirectory().getAbsolutePath();
		basepath = basepath + "/data/"
				+ AppUtils.getAppInfo(getApplicationContext()).getPackageName();
		String path = basepath + "/account";
		String filepath = path + "/account.json";
		boolean createDir = FileUtils.createOrExistsDir(path);
		boolean createFile = FileUtils.createOrExistsFile(filepath);
		accountModelList.clear();
		if (createDir && createFile) {
			final String content = FileUtils.readFile2String(filepath, "UTF-8");
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Accounts accounts = JsonHelper.fromJson(content,
								Accounts.class);
						if(accounts!=null){
							List<KeystoreInfoBean> infoBeans = accounts
									.getInfoBeans();
							for (KeystoreInfoBean keystoreInfoBean : infoBeans) {
								String address = keystoreInfoBean.getRealAddress();
								WalletBalanceVo balance = WalletApi
										.getBalance(address);
								if (balance.getBalance() != null) {
									keystoreInfoBean.setBalance(balance
											.getBalance());
									accountModelList.add(keystoreInfoBean);
								}
							}
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									accountAdapter.notifyDataSetChanged();
									mSwipeLayout.setRefreshing(false);
									BigDecimal sum = new BigDecimal("0");
									for (KeystoreInfoBean keystoreInfoBean : accountModelList) {
										BigDecimal bigbalance = new BigDecimal(
												keystoreInfoBean.getBalance());
										sum = sum.add(bigbalance);
									}
									balance_show.setText(sum.toString());
								}
							});
						}
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mSwipeLayout.setRefreshing(false);
							}
						});
						
					} catch (final Exception e) {
						// TODO Auto-generated catch block
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mSwipeLayout.setRefreshing(false);
								ToastUtil.showStringLong(
										getApplicationContext(),
										"出现异常" + e.getLocalizedMessage());
							}
						});
					}
				}
			}).start();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String address = accountModelList.get(arg2).getRealAddress();
		Intent intent = new Intent(this, AccountActivity.class);
		intent.putExtra("address", address);
		startActivity(intent);
	}

	@Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if ((System.currentTimeMillis() - firstTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
	
	@Override
	protected void RightBtnClick() {
		// TODO Auto-generated method stub
		PopWindow popWindow = new PopWindow(this);
		popWindow.showPopupWindow(btn_right);
	}

	@Override
	protected void LeftBtnClick() {
		// TODO Auto-generated method stub
		if(accountModelList.size()>0){
			Intent intent = new Intent(MainActivity.this,
					CaptureActivity.class);
			startActivityForResult(intent, SCANNING_CODE);
		}else{
			ToastUtil.showStringLong(getApplicationContext(), "您还没有导入钱包文件");
		}
	}

	@Override
	protected void CenterBtnClick() {
		// TODO Auto-generated method stub

	}

}
