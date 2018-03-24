package com.xiaoliang.wallet.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.activity.BaseActivity;
import com.xiaoliang.wallet.adapter.base.BaseListAdapter;
import com.xiaoliang.wallet.adapter.base.ViewHolder;
import com.xiaoliang.wallet.model.KeystoreInfoBean;
import com.xiaoliang.wallet.utils.BitmapUtil;
import com.xiaoliang.wallet.utils.ConvertUtils;
import com.xiaoliang.wallet.utils.ToastUtil;
import com.xiaoliang.wallet.view.QRPopWindow;

public class AccountAdapter extends BaseListAdapter<KeystoreInfoBean> {
	private Context context;

	public AccountAdapter(Context context, List<KeystoreInfoBean> list) {
		super(context, list);
		this.context = context;
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.account_item, null);
		}
		TextView balance_view=ViewHolder.get(convertView, R.id.balance);
		TextView account_view=ViewHolder.get(convertView, R.id.account);
		final ImageView qrview=ViewHolder.get(convertView, R.id.qr);
		String balance=list.get(position).getBalance();
		final String address=list.get(position).getAddress();
		String account=ConvertUtils.format(address);
		balance_view.setText(balance);
		account_view.setText(account);
		qrview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					Bitmap bitmap=BitmapUtil.createQRCode(address, ConvertUtils.dp2px(context, 100));
					QRPopWindow popWindow=new QRPopWindow((BaseActivity)context,bitmap);
					popWindow.showAtLocation(qrview, Gravity.CENTER, 0, 0);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					ToastUtil.showStringLong(context, "生成二维码失败");
				}
			}
		});
		return convertView;
	}

}
