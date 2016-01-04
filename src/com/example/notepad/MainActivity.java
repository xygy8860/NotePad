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


//actionBar����api11֮����ֵģ�����api������11֮��
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
		
		//����activity�ޱ��⣬�������setContentView֮ǰ
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getListView().setOnCreateContextMenuListener(this);//����viewΪ�ɳ���
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
	
	//activity�е�onResume����
	@Override
	protected void onResume() {
		
		queryData();//��ѯ���ݿ�����������,ˢ��ҳ��
		super.onResume();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ���������ͷ�ȫ�������Դ
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
					
					//װ��map����
					/*
					HashMap<String, Object> map = new HashMap<String,Object>();
					map.put("title", title);
					map.put("summary", summary);
					map.put("adViewNative", adViewNative);
					list.add(map);//��������
					
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
		// ��ʼ����ȫ�����
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
	 * dp ת px
	 * @param context �����Ķ���
	 * @param dpValue dp��ֵ
	 * @return
	 */
	public int dp2px(float dpValue){   
        final float scale = this.getResources().getDisplayMetrics().density;   
        return (int)(dpValue * scale + 0.5f);   
	}  
	
	/**
	 * ����
	 */
	private void adwo(){
		adView = new AdwoAdView(this,pid,false,20);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//�������ù����������Ļ��ʱ��������ڸ������м�
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		miniLayout.addView(adView,params);
	}
	
	private void waps(){
		// ���û������������ʱ�Ļص��������÷���������showBannerAd֮ǰ���ã�
		AppConnect.getInstance(this).setBannerAdNoDataListener(new AppListener() {
			@Override
			public void onBannerNoData() {
				Log.i("123", "banner������޿�������");
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
			//�����༭ҳ��
			startEditActivity("");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * listView�༭ѡ��
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.list_menu, menu); 
	}

	//ListViewѡ���
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO �Զ����ɵķ������
		AdapterView.AdapterContextMenuInfo info;
		int itemID;
		String str = null;
		try{
			info = (AdapterContextMenuInfo)item.getMenuInfo();
			TextView textView =(TextView)info.targetView.findViewById(R.id.id);
			str = textView.getText().toString();//��ȡ��ǰ��־��ID
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
		//ɾ����������
		if(itemID == R.id.deleteList){
			long id = Integer.parseInt(str);//��idת��Ϊ����
			if(id > 0){
				DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this,"test_db");
				SQLiteDatabase db = dbHelper.getWritableDatabase();//���ؿɶ������ݿ����
				db.delete("note", "id=?", new String[]{id + ""});//ɾ������
				queryData();//ˢ��ҳ��
			}
			
			Log.v("123","delete");
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * ListView�е��һ�����ݣ�����༭ҳ��
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO �Զ����ɵķ������
		
		String str = "";//������ʼ��

		//������findViewById��ͬ���˴�ǰ׺ΪV����Ϊ��ǰ��view
		TextView textView =(TextView)v.findViewById(R.id.id);
		TextView textNote =(TextView)v.findViewById(R.id.titleNote);
		
		//�����༭ҳ�沢���ݲ���
		str = textView.getText().toString();//���ݵĲ���id
		Log.v("123",  str + "");
		
		if(str.equals("adwo")){
			boolean flag = adViewNative.performClick();
			Log.v("123", flag + "");
		}else{
			startEditActivity(str); 
		}
	}
	
	public void onClickNewNote(View v) {
		//�����һ������������
		startEditActivity("");
	}
	
	/**�����༭ҳactivity������༭ҳ��
	 * @param id ���ݴ�����Ŀ��Id,Ҳ�����ݿ�ؼ���
	 */
	public void startEditActivity(String id) {
		Intent intent = new Intent();
	    intent.putExtra("id", id);  
	    intent.setClass(MainActivity.this, NoteEditActivity.class); 
	    MainActivity.this.startActivity(intent);
	}

	/**��ѯ���ݿ���������
	 * 
	 */
	public void queryData() {
		list = new ArrayList<HashMap<String,Object>>();//ʵ�����������
		//��ѯ���ݿ���������
		DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this,"test_db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();//���ؿɶ������ݿ����
		//���ݿ��ѯ��䣬�����α�
		Cursor cursor = db.query("note", new String[]{"id","title","content","modifiedDate"}, null, null, null, null, "id desc");
		//i��ʾ��ѯ����������Ŀ
		int i = cursor.getCount();
		HashMap[] map = new HashMap[i];//����hashmap�����飬����Ϊi
		
		//�α������ݿ���ѭ��ȡ����
		while(cursor.moveToNext()){
			i -= 1;//����Ӵ���С��ֱ��i=1
			//ȡ�������ֶε�ֵ
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String modifiedDate = cursor.getString(cursor.getColumnIndex("modifiedDate"));
			//װ��map����
			map[i] = new HashMap<String,String>();
			map[i].put("id", id);
			map[i].put("title", title);
			map[i].put("content", content);
			map[i].put("modifiedDate", modifiedDate);
			list.add(map[i]);//��������

			String name =  id + " " + title + " " + modifiedDate + " " ;
		}
		//SimpleAdapter���캯����ʼ��adapter
		/*SimpleAdapter adapter = new SimpleAdapter(this, list,R.layout.note,  
				new String[]{"id","title","content","modifiedDate"}, 
				new int[]{R.id.id,R.id.titleNote,R.id.contentNote,R.id.createdDate});*/
		adapter = new Adapter(list, this);
		//��listView��ʾ
		setListAdapter(adapter);
	}
}
