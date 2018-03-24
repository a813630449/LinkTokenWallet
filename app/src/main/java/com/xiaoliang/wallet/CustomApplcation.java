package com.xiaoliang.wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.xiaoliang.wallet.utils.AbDes;
import com.xiaoliang.wallet.utils.ToastUtil;

/**
 * 自定义全局Applcation类
 * 
 * @ClassName: CustomApplcation
 * @Description: TODO
 * @author smile
 * @date 2014-5-19 下午3:25:00
 */
public class CustomApplcation extends Application {

	public static CustomApplcation mInstance;
	private static Context context;
	private static String APPID = "584a1d783f59d2581cd12c0d5d5cfeb8";
	private String ip;
	private int port;

	@Override
	public void onCreate() {
		super.onCreate();
		// 崩溃记录
//		 CrashHandler crashHandler = CrashHandler.getInstance();
//		 crashHandler.init(getApplicationContext());
		context = getApplicationContext();
		mInstance = this;
		Bmob.initialize(context, APPID);
		//生成数据库表
		//BmobUpdateAgent.initAppVersion();
		initUmeng();
		getProxy();
	}
    private void getProxy() {
		HttpUtils httpUtils=new HttpUtils();
		String url="http://api.jdszm.cn/proxy/ip.txt";
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				ToastUtil.showStringLong(getApplicationContext(), "程序初始化失败，请重新打开尝试");
			}

			@Override
			public void onSuccess(ResponseInfo<String> res) {
				try {
					String result=res.result;
					result=AbDes.decrypt(result);
					String ip=result.split("-")[0];
					String port=result.split("-")[1];
					setIp(ip);
					setPort(Integer.parseInt(port));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyyMMdd");
		return sdf.format(Calendar.getInstance().getTime());
	}
    public String getimei(Context context){
        TelephonyManager tm = (TelephonyManager) this.getSystemService(context.TELEPHONY_SERVICE);
        String imei=tm.getDeviceId();
		if(imei==null){
			return "00000000000000000";
		}else{
			return tm.getDeviceId();
		}
    }
	
    public void initUmeng(){
        MobclickAgent.setScenarioType(context, EScenarioType.E_UM_NORMAL);
        MobclickAgent.openActivityDurationTrack(false);
    }
	public static CustomApplcation getInstance() {
		return mInstance;
	}

	public static Context getContext() {
		return context;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

}
