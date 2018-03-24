package com.xiaoliang.wallet.activity;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SignatureException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.ethereum.core.Transaction;
import org.ethereum.util.Unit;
import org.ethereum.util.Utils;
import org.ethereum.wallet.CommonWallet;
import org.ethereum.wallet.Wallet;
import org.spongycastle.util.encoders.Hex;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoliang.wallet.CustomApplcation;
import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.model.Accounts;
import com.xiaoliang.wallet.model.KeystoreInfoBean;
import com.xiaoliang.wallet.model.TransactionCountVo;
import com.xiaoliang.wallet.model.TransactionVo;
import com.xiaoliang.wallet.model.WalletBalanceVo;
import com.xiaoliang.wallet.utils.AppUtils;
import com.xiaoliang.wallet.utils.ConvertUtils;
import com.xiaoliang.wallet.utils.FileUtils;
import com.xiaoliang.wallet.utils.JsonHelper;
import com.xiaoliang.wallet.utils.ToastUtil;
import com.xiaoliang.wallet.wallet.WalletApi;
import com.xiaoliang.wallet.zxing.android.CaptureActivity;

public class SendActivity extends BaseActivity implements
		OnClickListener {

	private TextView balance, account;
	private EditText txt_address,txt_count,txt_password;
	private String address;
	private Button btn_send;
	private ImageView scan;
	
    static KeystoreInfoBean keystoreInfo;
    static Wallet wallet;
    static AtomicLong txInd;
    static double tradeAmount;
    static String toAddress;
    static String password;
    
    private static final int SCANNING_CODE = 1;
    private static final String DECODED_CONTENT_KEY = "codedContent";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		initView();
		initListen();
		initdata();
	}

	private void initView() {
		balance=(TextView) findViewById(R.id.balance);
		account=(TextView) findViewById(R.id.account);
		txt_address=(EditText) findViewById(R.id.address);
		txt_count=(EditText) findViewById(R.id.count);
		txt_password=(EditText) findViewById(R.id.password);
		btn_send=(Button) findViewById(R.id.btn_send);
		scan=(ImageView) findViewById(R.id.scan);
		initTitle(R.drawable.back, "转账", R.drawable.more_icon);
	}


	private void initdata() {
		address=getIntent().getExtras().getString("address");
		toAddress=getIntent().getExtras().getString("toaddress");
		account.setText(ConvertUtils.format(address));
		txt_address.setText(toAddress);
		loadData();
	}
	private void loadData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final WalletBalanceVo balancevo = WalletApi.getBalance(address);
				
				if (balancevo.getBalance() != null) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							balance.setText(balancevo.getBalance());
						}
					});
				}
			}
		}).start();
	}
	private void initListen() {
		btn_send.setOnClickListener(this);
		scan.setOnClickListener(this);
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
		switch (view.getId()) {
		case R.id.btn_send:
			new SendfTask().execute("");
			break;
		case R.id.scan:
			Intent intent = new Intent(SendActivity.this,
	                CaptureActivity.class);
	        startActivityForResult(intent, SCANNING_CODE);
	        break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SCANNING_CODE) {
	            if (data != null) {
	                String content = data.getStringExtra(DECODED_CONTENT_KEY);
	                if(content.startsWith("0x") || content.length()==40){
	                	txt_address.setText(content);
	                }else{
	                	ToastUtil.showStringLong(getApplicationContext(), "您扫描的不是钱包地址！");
	                }
	            }
	        }
		}
	}
	
	private void send() {
		try {
			toAddress=txt_address.getText().toString();
			tradeAmount=Double.parseDouble(txt_count.getText().toString());
			password=txt_password.getText().toString();
			if(TextUtils.isEmpty(toAddress)){
				showMessage("收款账户为空");
				return;
			}
			if(tradeAmount==0){
				showMessage("请输入转账链克数量");
				return;
			}
			if(TextUtils.isEmpty(password)){
				showMessage("钱包密码为空");
				return;
			}
			String basepath=Environment.getDataDirectory().getAbsolutePath();
			basepath=basepath+"/data/"+AppUtils.getAppInfo(getApplicationContext()).getPackageName();
			String filepath=basepath+"/account/account.json";
			String content=FileUtils.readFile2String(filepath, "UTF-8");
			Accounts accounts=JsonHelper.fromJson(content, Accounts.class);
			List<KeystoreInfoBean> infoBeans=accounts.getInfoBeans();
			for (KeystoreInfoBean keystoreInfoBean : infoBeans) {
				String realaddress=keystoreInfoBean.getRealAddress();
				if(realaddress.equals(address)){
					keystoreInfo = keystoreInfoBean;
				}
			}
			if(keystoreInfo==null){
				showMessage("未找到钱包文件!");
				return;
			}
			try {
				wallet = CommonWallet.fromV3(JsonHelper.toJson(keystoreInfo), password);
				step1_getTransactionCount();
			    step2_sendRawTransaction();
			} catch (GeneralSecurityException e) {
				showMessage("钱包密码错误");
			}
			if (wallet == null) {
				showMessage("钱包密码错误");
				return;
		    }
		} catch (Exception e) {
			showMessage("发生异常"+e.getLocalizedMessage());
		}
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
	
	/**
     * 第一步，获取交易次数
     */
    private void step1_getTransactionCount() {
        TransactionCountVo transactionCount = WalletApi.getTransactionCount(keystoreInfo.getRealAddress());
        BigInteger count = Utils.toBigNumber(transactionCount.getResult());
        txInd = new AtomicLong(count.intValue());
    }
    
    /**
     * 第二步，发送交易信息
     */
    private void step2_sendRawTransaction() {
        BigInteger nonce = BigInteger.valueOf(txInd.getAndIncrement());


        //以下两组数据参考   https://github.com/ImbaQ/MyLinkToken_js/blob/master/js/app.js  57行
        BigInteger gasLimit = new BigInteger("186a0", 16);
        BigInteger gasPrice = new BigInteger("174876e800", 16);

        BigInteger amount = Unit.valueOf(Unit.ether.toString()).toWei(String.valueOf(tradeAmount));

        Transaction tx = Transaction.create(toAddress.replace("0x", ""), amount, nonce, gasPrice, gasLimit, null);
        try {
            tx.sign(wallet);
        } catch (SignatureException e) {
        	showMessage("签名失败");
            System.exit(0);
        }
        byte[] encoded = tx.getEncoded();
        
        String ip=CustomApplcation.getInstance().getIp();
        int port=CustomApplcation.getInstance().getPort();
        if(ip==null || ip.equals("")){
        	ip="64.137.224.136";
        	port=3128;
        }
        WalletApi.sendRawTransactionByProxy("0x" + Hex.toHexString(encoded),ip,port,new WalletApi.TransactionCallback() {
            @Override
            public void onSuccess(final TransactionVo transactionVo) {
                if (transactionVo == null) {
                	showMessage("交易失败");
                    return;
                }
                if (TextUtils.isEmpty(transactionVo.getResult())) {
                	showMessage("交易失败，失败信息信息：" + transactionVo.getError().getMessage());
                    return;
                }
                showMessage("交易成功, 交易hash：" + transactionVo.getResult());
                finish();
            }

            @Override
            public void onFailed() {
            	showMessage("交易失败");
            }
        });
    }

    private void showMessage(final String msg){
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ToastUtil.showStringLong(getApplicationContext(), msg);
				dimissProgress();
			}
		});
    }
    
    /* 
     * 异步任务执行转账
     * */
    public class SendfTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        	showProgress("转账中...");
        };
        protected String doInBackground(String... params) {
        	send();
			return null; 
        }
        @Override
        protected void onPostExecute(String result) { 
            super.onPostExecute(result);
        }        
    }
    
}
