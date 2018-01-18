package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.donkingliang.labels.LabelsView;
import com.example.letsgo.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.picker.*;
import model.Account;
import model.responseAccount;
import model.responseSearch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.Picker;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;
import static com.example.letsgo.MainActivity.myNickname;
import static com.example.letsgo.MainActivity.myTags;
import static com.example.letsgo.MainActivity.myToken;
import static com.example.letsgo.MainActivity.myUserid;
import static com.example.letsgo.R.drawable.tag;
import static util.Convert.ageIntToString;
import static util.Convert.chaIntListToString;
import static util.Convert.chaIntToString;
import static util.Convert.character_;
import static util.Convert.conIntToString;
import static util.Convert.genderIntToString;
import static util.Convert.genderStringToInt;
import static util.Convert.generateTags;
import static util.Convert.intIntListToString;
import static util.Convert.intIntToString;
import static util.Convert.interest_;
import static util.httpUtil.sendHttpPost;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment4.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment4 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button buttonEdit;
    private EditText Nickname;
    private Button Gender;
    private Button buttonAge;
    private Button buttonConstellation;
    private LabelsView Interest;
    private LabelsView Character;
    private Gson gson = new Gson();
    private String responseData;
    private responseAccount mResponseAccount;
    private responseAccount mPostAccount;
    private Picker picker = new Picker();
    private ArrayList<Integer> A1;
    private ArrayList<Integer> A2;

    public static final int GETACCOUNT = 5;
    public static final int POSTACCOUNT = 6;

    public Fragment4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment4.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment4 newInstance(String param1, String param2) {
        Fragment4 fragment = new Fragment4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    public void initViews() {
        buttonEdit = getView().findViewById(R.id.buttonEdit);
        buttonAge = getView().findViewById(R.id.Age);
        buttonConstellation = getView().findViewById(R.id.Constellation);
        Nickname = getView().findViewById(R.id.Nickname);
        Gender = getView().findViewById(R.id.Gender);
        Interest = getView().findViewById(R.id.Interest);
        Character = getView().findViewById(R.id.Character);
        GetAccount();
        setUneditable();
    }

    public void setUneditable() {
        if (A1 != null) {
            Interest.setLabels(intIntListToString(A1));
        }
        if (A2 != null){
            Character.setLabels(chaIntListToString(A2));
        }
        Interest.setLabelBackgroundResource(tag);
        Character.setLabelBackgroundResource(tag);
        buttonEdit.setEnabled(true);
        buttonEdit.setText("编辑个人信息");
        buttonEdit.setOnClickListener(EditListener);

        Nickname.setEnabled(false);

        buttonAge.setEnabled(false);
        buttonConstellation.setEnabled(false);
        Gender.setEnabled(false);
        Interest.setEnabled(false);
        Character.setEnabled(false);

        buttonAge.setOnClickListener(null);
        buttonConstellation.setOnClickListener(null);
        Gender.setOnClickListener(null);
    }

    public void setEditable() {
        buttonEdit.setText("完成修改");
        buttonEdit.setOnClickListener(PostListener);

        Nickname.setEnabled(true);

        buttonAge.setEnabled(true);
        buttonConstellation.setEnabled(true);
        Gender.setEnabled(true);
        Interest.setEnabled(true);
        Character.setEnabled(true);

        buttonAge.setOnClickListener(AgeListener);
        buttonConstellation.setOnClickListener(ConstellationListener);
        Gender.setOnClickListener(GenderListener);

        Interest.setLabels(new ArrayList<String>(Arrays.asList(interest_)));
        Interest.setSelectType(LabelsView.SelectType.MULTI);
        Interest.setOnLabelSelectChangeListener(InterestSelectListener);
        Interest.setLabelBackgroundResource(R.color.colorWhite);
        Character.setLabels(new ArrayList<String>(Arrays.asList(character_)));
        Character.setSelectType(LabelsView.SelectType.MULTI);
        Character.setOnLabelSelectChangeListener(CharacterSelectListener);
        Character.setLabelBackgroundResource(R.color.colorWhite);
    }

    protected LabelsView.OnLabelSelectChangeListener InterestSelectListener = new LabelsView.OnLabelSelectChangeListener() {
        @Override
        public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
            if (isSelect) {
                //Log.d("**InterestSelect",labelText+":被选中:坐标是"+String.valueOf(position));
                label.setBackground(ContextCompat.getDrawable(getContext(),tag));
            }
            else {
                //Log.d("**InterestSelect",labelText+":被取消选中:坐标是"+String.valueOf(position));
                label.setBackground(null);
            }
        }
    };

    protected LabelsView.OnLabelSelectChangeListener CharacterSelectListener = new LabelsView.OnLabelSelectChangeListener() {
        @Override
        public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
            if (isSelect) {
                //Log.d("**CharacterSelect",labelText+":被选中:坐标是"+String.valueOf(position));
                label.setBackground(ContextCompat.getDrawable(getContext(),tag));
            }
            else {
                //Log.d("**CharacterSelect",labelText+":被取消选中:坐标是"+String.valueOf(position));
                label.setBackground(null);
            }
        }
    };

    protected View.OnClickListener EditListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setEditable();
        }
    };

    protected View.OnClickListener PostListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            A1 = Interest.getSelectLabels();
            A2 = Character.getSelectLabels();
            PostAccount();
            setUneditable();
        }
    };

    protected View.OnClickListener GenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picker.onConstellationPicker(getActivity(), 1);
        }
    };
    protected View.OnClickListener AgeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picker.onConstellationPicker(getActivity(), 2);
        }
    };
    protected View.OnClickListener ConstellationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picker.onConstellationPicker(getActivity(), 3);
        }
    };


    protected void GetAccount() {
        Account account = new Account(myUserid, myToken);
        sendHttpPost("https://shiftlin.top/cgi-bin/Account", gson.toJson(account), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                //Log.d("******",responseData);
                Message message = new Message();
                message.what = GETACCOUNT;
                message.obj = responseData;
                handler.sendMessage(message);
            }
        });
    }

    protected void UpdateAccount() {
        Nickname.setText(mResponseAccount.getNickname());
        Gender.setText(genderIntToString(mResponseAccount.getGender()));
        buttonConstellation.setText(conIntToString(mResponseAccount.getTags()));
        buttonAge.setText(ageIntToString(mResponseAccount.getTags()));
        //Log.d("**************",String.valueOf(mResponseAccount.getTags()));
        Interest.setLabels(intIntToString(mResponseAccount.getTags()));
        Character.setLabels(chaIntToString(mResponseAccount.getTags()));
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mResponseAccount = gson.fromJson(msg.obj.toString(), responseAccount.class);
            switch (msg.what) {
                case GETACCOUNT:
                    if (mResponseAccount.getStatus().equals("ERROR")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("获取个人信息错误")
                                .setMessage(mResponseAccount.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        myNickname = mResponseAccount.getNickname();
                        myTags=mResponseAccount.getTags();
                        UpdateAccount();
                    }
                    break;
                case POSTACCOUNT:
                    if (mResponseAccount.getStatus().equals("ERROR")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("更新个人信息错误")
                                .setMessage(mResponseAccount.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        //更新成功
                        Log.d("返回tag",String.valueOf(mResponseAccount.getTags()));
                        myTags=mResponseAccount.getTags();
                    }
                default:
                    break;
            }
        }
    };

    protected void PostAccount() {
        // tags to do
        long tags = generateTags(buttonAge.getText().toString(), buttonConstellation.getText().toString(), A1, A2);
        Log.d("发送tag",String.valueOf(tags));
        mPostAccount = new responseAccount(myUserid, myToken, Nickname.getText().toString(), genderStringToInt(Gender.getText().toString()), tags);
        sendHttpPost("https://shiftlin.top/cgi-bin/Account", gson.toJson(mPostAccount), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                Message message = new Message();
                message.what = POSTACCOUNT;
                message.obj = responseData;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment4, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
