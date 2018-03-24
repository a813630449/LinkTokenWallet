package com.xiaoliang.wallet.view;

import java.math.BigDecimal;
import java.util.List;

import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.activity.AboutActivity;
import com.xiaoliang.wallet.model.Accounts;
import com.xiaoliang.wallet.model.KeystoreInfoBean;
import com.xiaoliang.wallet.model.WalletBalanceVo;
import com.xiaoliang.wallet.utils.AppUtils;
import com.xiaoliang.wallet.utils.FileUtils;
import com.xiaoliang.wallet.utils.JsonHelper;
import com.xiaoliang.wallet.utils.ScreenUtils;
import com.xiaoliang.wallet.utils.SizeUtils;
import com.xiaoliang.wallet.utils.ToastUtil;
import com.xiaoliang.wallet.wallet.WalletApi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

/**
 * <p>Title:PopWindow</p>
 * <p>Description: 自定义PopupWindow</p>
 * @author syz
 * @date 2016-3-14
 */
public class DeletePopWindow extends PopupWindow{
	private View conentView;
	public DeletePopWindow(final Activity context,final String address){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.delete_popup_window, null);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(SizeUtils.dp2px(context, 130));
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);
		
		conentView.findViewById(R.id.deleteaccount).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//do something you need here
				String basepath = Environment.getDataDirectory().getAbsolutePath();
				basepath = basepath + "/data/"
						+ AppUtils.getAppInfo(context).getPackageName();
				String path = basepath + "/account";
				String filepath = path + "/account.json";
				boolean createDir = FileUtils.createOrExistsDir(path);
				boolean createFile = FileUtils.createOrExistsFile(filepath);
				if (createDir && createFile) {
					try {
						final String content = FileUtils.readFile2String(filepath, "UTF-8");
						Accounts accounts = JsonHelper.fromJson(content,Accounts.class);
						if(accounts!=null){
							List<KeystoreInfoBean> infoBeans = accounts
									.getInfoBeans();
							for (KeystoreInfoBean keystoreInfoBean : infoBeans) {
								String add = keystoreInfoBean.getRealAddress();
								if(address.equals(add)){
									accounts.getInfoBeans().remove(keystoreInfoBean);
									String json = JsonHelper.toJson(accounts);
									FileUtils.writeFileFromString(filepath, json, false);
									ToastUtil.showStringLong(context, "删除成功");
									context.finish();
									return;
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						ToastUtil.showStringLong(context, "删除失败");
						e.printStackTrace();
					}
				}
				ToastUtil.showStringLong(context, "删除失败");
				DeletePopWindow.this.dismiss();
			}
		});
	}
	
	
	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 5);
		} else {
			this.dismiss();
		}
	}
	
	
	/****************
	*
	* 发起添加群流程。群号：链克助手 咨询&amp;反馈(638901126) 的 key 为： y_eVbqeyx1ELK2fC6CtQFgqBqkZ8mWMI
	* 调用 joinQQGroup(y_eVbqeyx1ELK2fC6CtQFgqBqkZ8mWMI) 即可发起手Q客户端申请加群 链克助手 咨询&amp;反馈(638901126)
	*
	* @param key 由官网生成的key
	* @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	******************/
	public boolean joinQQGroup(String key,Context context) {
	    Intent intent = new Intent();
	    intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
	   // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	    try {
	    	context.startActivity(intent);
	        return true;
	    } catch (Exception e) {
	        // 未安装手Q或安装的版本不支持
	        return false;
	    }
	}

	
}
