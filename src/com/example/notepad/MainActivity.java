package com.example.notepad;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import cn.waps.AppConnect;
import cn.waps.AppListener;

import com.example.notepad.db.DatabaseHelper;
import com.sixth.adwoad.AdwoAdView;
import com.sixth.adwoad.ErrorCode;
import com.sixth.adwoad.NativeAdListener;
import com.sixth.adwoad.NativeAdView;


//actionBar是在api11之后出现的，所以api必须在11之上
public class MainActivity extends ListActivity {

	private LinearLayout miniLayout;
	private AdwoAdView adView;
	private LayoutParams params;
	//private String pid = "7ae18260fdf1487bb79930924347f411";
	private String pid = "2b8dbd92edd74a97b3ba6b0189bef125";
	private NativeAdView adViewNative;
	private ArrayList<HashMap<String, Object>> list;
	private Adapter adapter;
	private RelativeLayout adwoLayout;
	private View view;
	private String retStrFormatNowDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//设置activity无标题，必须放在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getListView().setOnCreateContextMenuListener(this);//设置view为可长按
        miniLayout =(LinearLayout)findViewById(R.id.AdLinearLayout);
        adwoLayout =(RelativeLayout)findViewById(R.id.adwo);
        
        Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd HH:mm");
		retStrFormatNowDate = sdFormatter.format(nowTime);
        
        AppConnect.getInstance("4a331c09a698bb447542d737ca288368","360", this);
        waps();
        //adwo();
        adwoNative();
	}
	
	//activity中的onResume方法
	@Override
	protected void onResume() {
		
		queryData();//查询数据库中所有数据,刷新页面
		super.onResume();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 请在这里释放全屏广告资源
		if (adViewNative != null) {
			adViewNative = null;
		}
	}

	private void adwoNative(){
		adViewNative = new NativeAdView(this,pid,true,new NativeAdListener() {
			
			@Override
			public void onReceiveAd(String arg0) {
				
				try{
					JSONObject json = new JSONObject(arg0);
					String title = json.getString("title");
					String summary = json.getString("summary");
					
					Log.v("123",  " onReceiveAd:"+title + ":" + summary);
					
					//装入map数组
					/*
					HashMap<String, Object> map = new HashMap<String,Object>();
					map.put("title", title);
					map.put("summary", summary);
					map.put("adViewNative", adViewNative);
					list.add(map);//载入链表
					
					adapter.notifyDataSetChanged();*/
					
					if(list.size() > 1){
						TextView id = (TextView)view.findViewById(R.id.id);
						TextView titleTxt = (TextView)view.findViewById(R.id.titleNote);
						TextView content = (TextView)view.findViewById(R.id.contentNote);
						TextView date = (TextView) view.findViewById(R.id.createdDate);
						TextView zanzhu = (TextView) view.findViewById(R.id.zanzhu);
						
						id.setText("adwo");
						titleTxt.setText(title);
						content.setText(summary);
						date.setText(retStrFormatNowDate);
						
						zanzhu.setVisibility(View.VISIBLE);
						adwoLayout.setVisibility(View.VISIBLE);
					}
				}catch(Exception e){
					
				}
			}
			
			@Override
			public void onPresentScreen() {
				
			}
			
			@Override
			public void onFailedToReceiveAd(View arg0, ErrorCode arg1) {
				Log.v("123",  " failed:"+arg1);
				adViewNative.prepareAd();
			}
			
			@Override
			public void onDismissScreen() {
				
			}
		});
		// 开始请求全屏广告
		adViewNative.prepareAd();
		
		ImageView imageView = new ImageView(this);
		view = LayoutInflater.from(this).inflate(R.layout.note, null);
		LayoutParams params1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, dp2px(60));
		adViewNative.setLayoutParams(params1);
		
		adwoLayout.addView(imageView);
		adwoLayout.addView(view);
		adwoLayout.addView(adViewNative);
		adwoLayout.setVisibility(View.GONE);
	}
	
	/**
	 * dp 转 px
	 * @param context 上下文对象
	 * @param dpValue dp的值
	 * @return
	 */
	public int dp2px(float dpValue){   
        final float scale = this.getResources().getDisplayMetrics().density;   
        return (int)(dpValue * scale + 0.5f);   
	}  
	
	/**
	 * 安沃
	 */
	private void adwo(){
		adView = new AdwoAdView(this,pid,false,20);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//当不设置广告条充满屏幕宽时建议放置在父容器中间
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		miniLayout.addView(adView,params);
	}
	
	private void waps(){
		// 设置互动广告无数据时的回调监听（该方法必须在showBannerAd之前调用）
		AppConnect.getInstance(this).setBannerAdNoDataListener(new AppListener() {
			@Override
			public void onBannerNoData() {
				Log.i("123", "banner广告暂无可用数据");
			}
		});
		AppConnect.getInstance(this).showBannerAd(this, miniLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_add) {
			//启动编辑页面
			startEditActivity("");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * listView编辑选项
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.list_menu, menu); 
	}

	//ListView选项触发
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO 自动生成的方法存根
		AdapterView.AdapterContextMenuInfo info;
		int itemID;
		String str = null;
		try{
			info = (AdapterContextMenuInfo)item.getMenuInfo();
			TextView textView =(TextView)info.targetView.findViewById(R.id.id);
			str = textView.getText().toString();//获取当前日志的ID
			if("id".equals(str)){
				Toast.makeText(this, R.string.deleteFail, Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		itemID = item.getItemId();
		//删除本行数据
		if(itemID == R.id.deleteList){
			long id = Integer.parseInt(str);//将id转换为整型
			if(id > 0){
				DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this,"test_db");
				SQLiteDatabase db = dbHelper.getWritableDatabase();//返回可读的数据库对象
				db.delete("note", "id=?", new String[]{id + ""});//删除数据
				queryData();//刷新页面
			}
			
			Log.v("123","delete");
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * ListView中点击一条数据，进入编辑页面
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO 自动生成的方法存根
		
		String str = "";//参数初始化

		//与以往findViewById不同，此处前缀为V，即为当前的view
		TextView textView =(TextView)v.findViewById(R.id.id);
		TextView textNote =(TextView)v.findViewById(R.id.titleNote);
		
		//启动编辑页面并传递参数
		str = textView.getText().toString();//传递的参数id
		Log.v("123",  str + "");
		
		if(str.equals("adwo")){
			boolean flag = adViewNative.performClick();
			Log.v("123", flag + "");
		}else{
			startEditActivity(str); 
		}
	}
	
	public void onClickNewNote(View v) {
		//点击第一行数据则新增
		startEditActivity("");
	}
	
	/**启动编辑页activity，进入编辑页面
	 * @param id 数据存在条目的Id,也即数据库关键字
	 */
	public void startEditActivity(String id) {
		Intent intent = new Intent();
	    intent.putExtra("id", id);  
	    intent.setClass(MainActivity.this, NoteEditActivity.class); 
	    MainActivity.this.startActivity(intent);
	}

	/**查询数据库所有数据
	 * 
	 */
	public void queryData() {
		list = new ArrayList<HashMap<String,Object>>();//实例化链表对象
		//查询数据库所有数据
		DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this,"test_db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();//返回可读的数据库对象
		//数据库查询语句，返回游标
		Cursor cursor = db.query("note", new String[]{"id","title","content","modifiedDate"}, null, null, null, null, "id desc");
		//i表示查询到的数据条目
		int i = cursor.getCount();
		HashMap[] map = new HashMap[i];//生成hashmap的数组，容量为i
		
		//游标在数据库中循环取数据
		while(cursor.moveToNext()){
			i -= 1;//数组从大往小，直到i=1
			//取出各个字段的值
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String modifiedDate = cursor.getString(cursor.getColumnIndex("modifiedDate"));
			//装入map数组
			map[i] = new HashMap<String,String>();
			map[i].put("id", id);
			map[i].put("title", title);
			map[i].put("content", content);
			map[i].put("modifiedDate", modifiedDate);
			list.add(map[i]);//载入链表

			String name =  id + " " + title + " " + modifiedDate + " " ;
		}
		//SimpleAdapter构造函数初始化adapter
		/*SimpleAdapter adapter = new SimpleAdapter(this, list,R.layout.note,  
				new String[]{"id","title","content","modifiedDate"}, 
				new int[]{R.id.id,R.id.titleNote,R.id.contentNote,R.id.createdDate});*/
		adapter = new Adapter(list, this);
		//将listView显示
		setListAdapter(adapter);
	}
}
