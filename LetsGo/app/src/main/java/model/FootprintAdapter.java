package model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.letsgo.R;

import java.util.List;

/**
 * Created by 11437 on 2017/12/20.
 */
public class FootprintAdapter extends ArrayAdapter<Footprint> {
    private int resourceId;
    public FootprintAdapter(Context context, int textViewResourceId, List<Footprint> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View converView, ViewGroup parent){
        Footprint footprint=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView fContent=(TextView)view.findViewById(R.id.fContent);
        TextView fComment=(TextView)view.findViewById(R.id.fComment);
        fContent.setText(footprint.getContent());
        fComment.setText(footprint.getComment());
        return view;
    }
}

