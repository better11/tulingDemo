package com.example.tulingdemo;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements HttpGetDataListner,android.view.View.OnClickListener{
	private HttpData httpData;
	private List<ListData> lists;
	private ListView lv;
	private EditText sendText;
	private Button send_btn;
	private String content_str;
	private TextAdapter adapter;
	private String [] welcome_array;
	private double currentTime,oldTime=0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();	
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	}
	private void initView(){
		lv=(ListView)findViewById(R.id.lv);
		sendText=(EditText)findViewById(R.id.sendText);
		send_btn=(Button)findViewById(R.id.send_btn);
		lists=new ArrayList<ListData>();
		send_btn.setOnClickListener(this);
		adapter=new TextAdapter(lists, this);
		lv.setAdapter(adapter);
		ListData listData;
		listData=new ListData(getRandomWelcomeTips(),ListData.RECEIVER,getTime());
		lists.add(listData);
	}
	
	private String getRandomWelcomeTips(){
		String welcome_tip=null;
		welcome_array=this.getResources().getStringArray(R.array.welcome_tips);
		int index=(int) (Math.random()*(welcome_array.length-1));
		welcome_tip=welcome_array[index];
		return welcome_tip;
	}
	@Override
	public void getDataUrl(String data) {
		parseText(data);
		
	}
	public void parseText(String str){
		try {
			JSONObject jb=new JSONObject(str);
			ListData listData;
			listData = new ListData(jb.getString("text"),ListData.RECEIVER,getTime());
			lists.add(listData);
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View v) {
		getTime();
		content_str=sendText.getText().toString();
		sendText.setText("");
		String dropk=content_str.replace(" ", "");
		String droph=dropk.replace("\n", "");
		content_str =droph;
		if (TextUtils.isEmpty(content_str))
		{
			Toast.makeText(MainActivity.this, "发送消息不能为空!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		ListData listData;
		listData = new ListData(content_str,ListData.SEND,getTime());
		lists.add(listData);
		if(lists.size()>30)
			for(int i=0;i<lists.size();i++)
				lists.remove(i);
		adapter.notifyDataSetChanged();

			httpData = (HttpData) new HttpData("http://www.tuling123.com/openapi/api?key=e8f2dbc185bde007d2831c4070c07eb6&info=" + content_str,
					this).execute();

	}
	private String getTime(){
		currentTime=System.currentTimeMillis();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat second=new SimpleDateFormat("HH:mm:ss");
		Date curDate=new Date();
		String str=format.format(curDate);
		String nStr=second.format(curDate);
		if(currentTime-oldTime>=1*100){
			if(oldTime==0) {
				oldTime = currentTime;
				return str;
			}
			oldTime=currentTime;
			return nStr;
		}else{
			return "";
		}
	}
	
}
