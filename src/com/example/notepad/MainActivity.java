package com.example.notepad;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.notepad.db.DatabaseHelper;

//actionBar是在api11之后出现的，所以api必须在11之上
public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getListView().setOnCreateContextMenuListener(this);//设置view为可长按
	}
	
	//activity中的onResume方法
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		queryData();//查询数据库中所有数据,刷新页面
		super.onResume();
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
		// TODO 自动生成的方法存根
		
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
		
//		if(id == 0){
//			startEditActivity(str); 
//			return;//如果是第一行数据，则新建编辑页面
//		}
		
		
		//与以往findViewById不同，此处前缀为V，即为当前的view
		TextView textView =(TextView)v.findViewById(R.id.id);
		TextView textNote =(TextView)v.findViewById(R.id.titleNote);
		
		String strNote = textNote.getText().toString();//传递参数标题
		if(strNote.indexOf("莉")!=-1 || strNote.indexOf("li")!=-1){
			Intent intent = new Intent();
		    intent.setClass(MainActivity.this, firstActivity.class); 
		    MainActivity.this.startActivity(intent);
		}
		else{
			//启动编辑页面并传递参数
			str = textView.getText().toString();//传递的参数id
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
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();//实例化链表对象

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
			Log.v("123", name);
		}
		//SimpleAdapter构造函数初始化adapter
		SimpleAdapter adapter = new SimpleAdapter(this, list,R.layout.note,  
				new String[]{"id","title","content","modifiedDate"}, 
				new int[]{R.id.id,R.id.titleNote,R.id.contentNote,R.id.createdDate});
		//将listView显示
		setListAdapter(adapter);
	}
}
