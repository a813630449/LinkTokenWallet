package com.xiaoliang.wallet.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoliang.wallet.R;
import com.xiaoliang.wallet.adapter.base.BaseListAdapter;
import com.xiaoliang.wallet.adapter.base.ViewHolder;
import com.xiaoliang.wallet.model.KeystoreInfoBean;
import com.xiaoliang.wallet.model.TransactionRecordsVo;
import com.xiaoliang.wallet.utils.ConstUtils.TimeUnit;
import com.xiaoliang.wallet.utils.ConvertUtils;
import com.xiaoliang.wallet.utils.TimeUtils;

public class TradeInfoAdapter extends BaseListAdapter<TransactionRecordsVo.Record> {
	private Context context;

	public TradeInfoAdapter(Context context, List<TransactionRecordsVo.Record> list) {
		super(context, list);
		this.context = context;
	}

	


	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tradeinfo_item, null);
		}
		TextView address_end_view=ViewHolder.get(convertView, R.id.address_end);
		TextView date_view=ViewHolder.get(convertView, R.id.date);
		TextView count_view=ViewHolder.get(convertView, R.id.count);
		String address=ConvertUtils.format2(list.get(position).getTradeAccount());
		String count=list.get(position).getShowAmount();
		String date=list.get(position).getTimestamp();
		date=TimeUtils.milliseconds2String(Long.valueOf(date)*1000);
		address_end_view.setText(address);
		date_view.setText(date);
		count_view.setText(count);
		return convertView;
	}

}
