package model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letsgo.R;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 11437 on 2017/12/20.
 */
public class FootprintAdapter extends ArrayAdapter<Footprint> {
    private int resourceId;
    private List<Footprint> FootprintList = new ArrayList<>();
    private List<Attitude> attitudeList = new ArrayList<>();
    private Context context;

    public List<Attitude> getAttitudeList() {
        return this.attitudeList;
    }

    public FootprintAdapter(Context context, int textViewResourceId, List<Footprint> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        FootprintList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext();
        }
        final Footprint footprint = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.fContent = (TextView) view.findViewById(R.id.fContent);
            viewHolder.fNickname = (TextView) view.findViewById(R.id.fNickname);
            viewHolder.fLike = (Button) view.findViewById(R.id.fLike);
            viewHolder.fDislike = (Button) view.findViewById(R.id.fDislike);
            viewHolder.fTime = (TextView) view.findViewById(R.id.fTime);
            viewHolder.fPostid = footprint.getPostid();
            viewHolder.fAttitude = footprint.getAttitude();
            if (viewHolder.fAttitude == 0) {
                viewHolder.flag1 = 0;
                viewHolder.flag2 = 0;
            } else if (viewHolder.fAttitude == 2) {
                viewHolder.flag2 = 1;
                viewHolder.flag1 = 0;
                viewHolder.fLike.setEnabled(false);
                viewHolder.fDislike.setBackground(ContextCompat.getDrawable(context,R.drawable.dislike1));
            } else if (viewHolder.fAttitude == 1) {
                viewHolder.fLike.setBackground(ContextCompat.getDrawable(context, R.drawable.like1));
                viewHolder.flag1 = 1;
                viewHolder.flag2 = 0;
                viewHolder.fDislike.setEnabled(false);
            }
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.fContent.setText(footprint.getContent());
        viewHolder.fLike.setText(footprint.getLike());
        viewHolder.fDislike.setText(footprint.getDislike());
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        viewHolder.fTime.setText(dateFormat.format(1000*(long)footprint.getTimestamp()));
        viewHolder.fNickname.setText(footprint.getNickname());

        //设置tag标记
        //viewHolder.fLike.setTag(R.id.btn1,position);
        //viewHolder.fDislike.setTag(R.id.btn2,position);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == viewHolder.fLike) {
                    if (viewHolder.flag1 == 0) {
                        // 无->点赞
                        viewHolder.fLike.setText(String.valueOf(Integer.parseInt(viewHolder.fLike.getText().toString()) + 1));
                        viewHolder.fLike.setBackground(ContextCompat.getDrawable(context, R.drawable.like1));
                        viewHolder.flag1 = 1;
                        viewHolder.fDislike.setEnabled(false);
                        ChangeList(footprint.getPostid(), 1);
                    } else {
                        //点赞->无
                        viewHolder.fLike.setText(String.valueOf(Integer.parseInt(viewHolder.fLike.getText().toString()) - 1));
                        viewHolder.fLike.setBackground(ContextCompat.getDrawable(context, R.drawable.like0));
                        viewHolder.flag1 = 0;
                        viewHolder.fDislike.setEnabled(true);
                        ChangeList(footprint.getPostid(), -1);
                    }
                } else if (view == viewHolder.fDislike) {
                    if (viewHolder.flag2 == 0) {
                        //无->反对
                        viewHolder.fDislike.setText(String.valueOf(Integer.parseInt(viewHolder.fDislike.getText().toString()) + 1));
                        viewHolder.fDislike.setBackground(ContextCompat.getDrawable(context,R.drawable.dislike1));
                        viewHolder.flag2 = 1;
                        viewHolder.fLike.setEnabled(false);
                        ChangeList(footprint.getPostid(), 2);
                    } else {
                        //反对->无
                        viewHolder.fDislike.setText(String.valueOf(Integer.parseInt(viewHolder.fDislike.getText().toString()) - 1));
                        viewHolder.fDislike.setBackground(ContextCompat.getDrawable(context,R.drawable.dislike0));
                        viewHolder.flag2 = 0;
                        viewHolder.fLike.setEnabled(true);
                        ChangeList(footprint.getPostid(), -2);
                    }
                }
            }
        };

        viewHolder.fLike.setOnClickListener(listener);
        viewHolder.fDislike.setOnClickListener(listener);

        return view;
    }

    class ViewHolder {
        TextView fContent;
        TextView fNickname;
        Button fLike;
        Button fDislike;
        TextView fTime;
        int fPostid;
        int fAttitude;
        int flag1;
        int flag2;
    }

    protected void ChangeList(int postid, int attitude) {
        attitudeList.remove(new Attitude(postid, 1));
        attitudeList.remove(new Attitude(postid, -1));
        attitudeList.remove(new Attitude(postid, 2));
        attitudeList.remove(new Attitude(postid, -2));
        if (attitude != 0) {
            attitudeList.add(new Attitude(postid, attitude));
        }
    }
}

