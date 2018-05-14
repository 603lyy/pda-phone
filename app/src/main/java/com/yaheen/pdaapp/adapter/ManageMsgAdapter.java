package com.yaheen.pdaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.adapter.base.CommonAdapter;
import com.yaheen.pdaapp.bean.MsgBean;

public class ManageMsgAdapter extends CommonAdapter<MsgBean> {

    public ManageMsgAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public int getCount() {
        return MsgBean.num;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_msg, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MsgBean data = getItem(0);
        if(data!=null){
            if(position==0){
                holder.tvDescribe.setText(R.string.msg_community_text);
                holder.etDetail.setText(data.getEntity().getCommunity());
            }else if(position==1){
                holder.tvDescribe.setText(R.string.msg_username_text);
                holder.etDetail.setText(data.getEntity().getUsername());
            }else if(position==2){
                holder.tvDescribe.setText(R.string.msg_id_text);
                holder.etDetail.setText(data.getEntity().getId());
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvDescribe;

        EditText etDetail;

        public ViewHolder(View view) {
            tvDescribe = view.findViewById(R.id.tv_describe);
            etDetail = view.findViewById(R.id.et_detail);
        }
    }
}
