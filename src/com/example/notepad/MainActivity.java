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

//actionBar����api11֮����ֵģ�����api������11֮��
public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getListView().setOnCreateContextMenuListener(this);//����viewΪ�ɳ���
	}
	
	//activity�е�onResume����
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		queryData();//��ѯ���ݿ�����������,ˢ��ҳ��
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
		// TODO �Զ����ɵķ������
		
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
		
//		if(id == 0){
//			startEditActivity(str); 
//			return;//����ǵ�һ�����ݣ����½��༭ҳ��
//		}
		
		
		//������findViewById��ͬ���˴�ǰ׺ΪV����Ϊ��ǰ��view
		TextView textView =(TextView)v.findViewById(R.id.id);
		TextView textNote =(TextView)v.findViewById(R.id.titleNote);
		
		String strNote = textNote.getText().toString();//���ݲ�������
		if(strNote.indexOf("��")!=-1 || strNote.indexOf("li")!=-1){
			Intent intent = new Intent();
		    intent.setClass(MainActivity.this, firstActivity.class); 
		    MainActivity.this.startActivity(intent);
		}
		else{
			//�����༭ҳ�沢���ݲ���
			str = textView.getText().toString();//���ݵĲ���id
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
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();//ʵ�����������

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
			Log.v("123", name);
		}
		//SimpleAdapter���캯����ʼ��adapter
		SimpleAdapter adapter = new SimpleAdapter(this, list,R.layout.note,  
				new String[]{"id","title","content","modifiedDate"}, 
				new int[]{R.id.id,R.id.titleNote,R.id.contentNote,R.id.createdDate});
		//��listView��ʾ
		setListAdapter(adapter);
	}
}
