package layout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.BoolRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.letsgo.MainActivity;
import com.example.letsgo.R;
import com.example.letsgo.RegisterActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.logging.LogRecord;

import model.User;
import model.responseRegister;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import util.httpUtil;

import static android.content.Context.MODE_PRIVATE;
import static com.example.letsgo.MainActivity.myToken;
import static com.example.letsgo.MainActivity.myUserid;
import static util.httpUtil.sendHttpPost;
import static util.httpUtil.sendHttpRequest;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String responseData;
    private Button buttonLogin;
    private Button buttonRegister;
    private EditText account;
    private EditText password;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Gson gson = new Gson();
    private responseRegister responseRegister;
    private User user;

    private OnFragmentInteractionListener mListener;

    public static final int GETLOGIN=1;

    private Handler handler=new Handler() {
        public void handleMessage(Message msg){
            responseRegister=gson.fromJson(msg.obj.toString(),responseRegister.class);
            switch (msg.what){
                case GETLOGIN:
                    if(responseRegister.getStatus().equals("ERROR")){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("登录错误")
                                .setMessage(responseRegister.getMessage())
                                .setPositiveButton("确定",null)
                                .show();
                    }
                    else{
                        editor=sp.edit();
                        editor.putString("UserName",account.getText().toString());
                        editor.putString("UserPsw",password.getText().toString());
                        editor.putString("UserToken",responseRegister.getToken());
                        editor.commit();
                        ((MainActivity)getActivity()).SetLogIn(user.getUserid(),responseRegister.getToken());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public Fragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_fragment1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews(){
        buttonLogin = getView().findViewById(R.id.buttonLogin);
        buttonRegister = getView().findViewById(R.id.buttonRegister);
        account = getView().findViewById(R.id.account);
        password = getView().findViewById(R.id.password);
        sp=getContext().getSharedPreferences("UserInfo",MODE_PRIVATE);
        if(sp.getString("UserName",null)!=null && sp.getString("UserPsw",null)!=null && sp.getString("UserToken",null)!=null){
            ((MainActivity)getActivity()).SetLogIn(sp.getString("UserName",null),sp.getString("UserToken",null));
        }
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=new User(account.getText().toString(),password.getText().toString());
                sendHttpPost("https://shiftlin.top/cgi-bin/Login",gson.toJson(user), new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        responseData = response.body().string();
                        Message message=new Message();
                        message.what=GETLOGIN;
                        message.obj=responseData;
                        handler.sendMessage(message);
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i=new Intent(getActivity(), RegisterActivity.class);
                startActivity(i);
            }
        });
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
