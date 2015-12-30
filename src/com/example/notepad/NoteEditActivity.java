package com.example.notepad;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

import com.example.notepad.MainActivity;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.app.ListActivity;
import android.content.ClipData.Item;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notepad.db.DatabaseHelper;

public class NoteEditActivity extends ActionBarActivity {
	
	private String retStrFormatNowDate =  "";//现在日期时间
	private EditText title ; //可写的标题控件
	private EditText content ;//可写的内容控件
	private TextView titleLbl;//只读的标题控件
	private TextView contentLbl;//只读的内容控件
	private View view;//分割线效果控件
	private MenuItem save;//保存按钮
	private MenuItem edit;//修改按钮
	private boolean isNew = true;//判断是否新建，默认为true
	private String idStr = "";//传递来的参数
	private boolean isLove = false;
	private boolean isSave = true;//判断数据是否保存
	private String titleStr = "";//根据id从数据库查询出的标题
	private String contentStr = "";//根据id从数据库查询出的内容

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_edit);

		//根据控件id得到控件
		title = (EditText)findViewById(R.id.title);
		content = (EditText)findViewById(R.id.content);
		titleLbl = (TextView)findViewById(R.id.titleLbl);
		contentLbl = (TextView)findViewById(R.id.contentLbl);
		view = (View)findViewById(R.id.viewNote);
		
		//设置内容可以多行滚动
		contentLbl.setMovementMethod(new ScrollingMovementMethod());
		
		//得到MainActivity中传递的参数，根据参数不同确定是新建还是修改		
		Intent intent = getIntent();
		idStr = intent.getStringExtra("id");
		if("".equals(idStr)){
			//如果参数为空，则新建
		}
		else if("Love".equals(idStr)){
			title.setText("爱情宣言");
			content.setText("我爱莉莉^_^");
			isLove = false;
		}
		
		else{
			title.setVisibility(View.GONE);//设置标题编辑框不可见
			content.setVisibility(View.GONE);//设置内容编辑框不可见
			view.setVisibility(View.VISIBLE);//设置分割线可见
			//如果参数不为空，则根据参数id查询数据，填充标题和内容
			queryData(idStr);
			
			titleLbl.setText(titleStr);
			titleLbl.setGravity(Gravity.CENTER);
			contentLbl.setText(contentStr);
			
			isNew = false;//将新建改为修改
		}
	}

	/**
	 * 返回按钮触发保存事件，以免用户忘记保存
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		String titleTemp = title.getText().toString();
		String contentTemp = content.getText().toString();
		
		if(isSave){
			//如果isSave为真，则判断是否新建
			//如果为新建，且标题或内容有内容，则提醒保存；否则不提醒保存
			if(isNew && (!titleStr.equals(titleTemp) || !contentStr.equals(contentTemp))){
				saveMessage();
				return;
			}
		}
		//如果数据没有保存，则分情况，新建时无数据不提醒保存，修改时数据未变动不提醒保存
		else{
			if(!"".equals(idStr)){
				queryData(idStr);
				//修改时如果数据已经修改，则提醒数据保存
				if(!titleStr.equals(titleTemp) || !contentStr.equals(contentTemp) ){
					saveMessage();
					return;
				}
			}
			//新建时标题或内容有数据时，提醒保存
			else if(!"".equals(titleTemp) || !"".equals(contentTemp)){
				saveMessage();
				return;
			}
		}
		
		super.onBackPressed();
	}

	//弹出数据未保存的提醒
	private void saveMessage() {
		Toast.makeText(this, R.string.noSave, Toast.LENGTH_SHORT).show();
		isSave = true;
		isNew = false;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_edit, menu);
		
		//初始化选项控件，不能用findviewById得到
		save = (MenuItem)menu.findItem(R.id.save);
		edit = (MenuItem)menu.findItem(R.id.edit);
		//如果是新建，则显示保存按钮，编辑按钮隐藏
		if(isNew){
			//将编辑图标变为保存图标
			editToSave();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		//获取当前输入的标题和内容
		String titleText = title.getText().toString();
		String contentText = content.getText().toString();

		 Date nowTime = new Date(System.currentTimeMillis());
		 SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd HH:mm");
		 retStrFormatNowDate = sdFormatter.format(nowTime);
		
		int id = item.getItemId();
		//如果点击保存
		if (id == R.id.save) {
			//标题为空则给予提醒
			if("".equals(titleText)){
				Toast.makeText(this, "标题为空", Toast.LENGTH_SHORT).show();
				return true;
			}
			
			//如果是新建，插入数据
			save(titleText, contentText);
			isSave = true;
			return true;
		}
		//如果是点击的是修改按钮
		else if(id == R.id.edit){
			isSave = false ;
			title.setVisibility(View.VISIBLE);//设置标题编辑框可见
			content.setVisibility(View.VISIBLE);//设置内容编辑框可见
			view.setVisibility(View.GONE);//设置分割线不可见

			//只读控件重新填充文字
			contentLbl.setText("内容");
			contentLbl.setMovementMethod(null);//设置滚动条不再滚动
			titleLbl.setText(R.string.title);
			titleLbl.setGravity(Gravity.LEFT);
			
			//将只读控件内容传递到可写控件
			title.setText(titleStr);
			content.setText(contentStr);
			
			//将编辑图标变为保存图标
			editToSave();
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 隐藏编辑按钮，显示保存按钮
	 */
	private void editToSave() {
		edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		edit.setVisible(false);
		save.setVisible(true);
	}


	/**
	 * 根据具体情况保存或更新
	 * @param titleText 标题
	 * @param contentText 内容
	 */
	private void save(String titleText, String contentText) {
		if(isNew){
			insertData(titleText, contentText);	
			isNew = false;
		}
		//不是新建，则更新数据
		else{
			updateData(idStr, titleText, contentText);
		}
	}

	/**更新数据库数据
	 * @param titleText
	 * @param contentText
	 */
	private void updateData(String idStr, String titleText, String contentText) {
		DatabaseHelper dbHelper = new DatabaseHelper(NoteEditActivity.this,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("title", titleText);
		values.put("content", contentText);
		values.put("modifiedDate", retStrFormatNowDate);
		//第一个参数是要更新的表名
		//第二个参数是一个ContentValeus对象
		//第三个参数是where子句
		db.update("note", values, "id=?", new String[]{idStr});
		//弹出更新成功的消息
		Toast.makeText(this, R.string.updateOK, Toast.LENGTH_SHORT).show();
	}

	/**根据标题id查询数据
	 * @param titleId
	 */
	private Cursor queryData(String titleId) {

		DatabaseHelper dbHelper = new DatabaseHelper(NoteEditActivity.this,"test_db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("note", new String[]{"title","content"}, "id=?", new String[]{titleId.toString()}, null, null, null);
		while(cursor.moveToNext()){
			titleStr = cursor.getString(cursor.getColumnIndex("title"));
			contentStr = cursor.getString(cursor.getColumnIndex("content"));
		}
		return cursor;
	}

	/**向数据库插入数据
	 * @param titleText 标题
	 * @param contentText  内容
	 */
	private void insertData(String titleText, String contentText) {
		//生成ContentValues对象
		ContentValues values = new ContentValues();

		//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		//values.put("id", 1);//id自增长，不需要插入
		values.put("title",titleText.toString());
		values.put("content", contentText.toString());
		values.put("createdDate", retStrFormatNowDate);
		values.put("modifiedDate", retStrFormatNowDate);
		
		//test_db为数据库的名字
		DatabaseHelper dbHelper = new DatabaseHelper(NoteEditActivity.this,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//调用insert方法，就可以将数据插入到数据库当中
		db.insert("note", null, values);
		//弹出更新成功
		Toast.makeText(this, R.string.saveOK, Toast.LENGTH_SHORT).show();
	}
}
