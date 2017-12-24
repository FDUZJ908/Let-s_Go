package util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.example.letsgo.R;

import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.picker.SinglePicker;
import layout.Fragment4;

import static util.Convert.age_;
import static util.Convert.constellation_;
import static util.Convert.gender_;

/**
 * Created by 11437 on 2017/12/22.
 */

public class Picker {
    public void onConstellationPicker(final Activity activity, final int pick) {
        SinglePicker<String> picker;
        if (pick==1){
        picker= new SinglePicker<String>(activity,gender_ );
        }
        else if(pick==2){
            picker=new SinglePicker<String>(activity,age_ );
        }
        else if(pick==3){
            picker=new SinglePicker<String>(activity,constellation_ );
        }
        else if(pick==4){
            picker= new SinglePicker<String>(activity,gender_ );
        }
        else return;
        picker.setCanLoop(false);//不禁用循环
        picker.setTopBackgroundColor(0xFFEEEEEE);
        picker.setTopHeight(50);
        picker.setTopLineColor(0xFF33B5E5);
        picker.setTopLineHeight(1);
        picker.setTitleText("请选择");
        picker.setTitleTextColor(0xFF999999);
        picker.setTitleTextSize(12);
        picker.setCancelTextColor(0xFF33B5E5);
        picker.setCancelTextSize(13);
        picker.setSubmitTextColor(0xFF33B5E5);
        picker.setSubmitTextSize(13);
        picker.setSelectedTextColor(0xFFEE0000);
        picker.setUnSelectedTextColor(0xFF999999);
        LineConfig config = new LineConfig();
        config.setColor(0xFFEE0000);//线颜色
        config.setAlpha(140);//线透明度
        config.setRatio((float) (1.0 / 8.0));//线比率
        picker.setLineConfig(config);
        picker.setItemWidth(200);
        picker.setBackgroundColor(0xFFE1E1E1);
        picker.setSelectedIndex(1);
        picker.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                switch (pick){
                    case 1:
                        ((Button)activity.findViewById( R.id.Gender)).setText(item);
                        break;
                    case 2:
                        ((Button)activity.findViewById( R.id.Age)).setText(item);
                        break;
                    case 3:
                        ((Button)activity.findViewById( R.id.Constellation)).setText(item);
                        break;
                    case 4:
                        ((Button)activity.findViewById(R.id.gender_r)).setText(item);
                    default:
                }

            }
        });
        picker.show();
    }
}
