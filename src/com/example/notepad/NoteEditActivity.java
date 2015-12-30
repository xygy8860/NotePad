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
	
	private String retStrFormatNowDate =  "";//��������ʱ��
	private EditText title ; //��д�ı���ؼ�
	private EditText content ;//��д�����ݿؼ�
	private TextView titleLbl;//ֻ���ı���ؼ�
	private TextView contentLbl;//ֻ�������ݿؼ�
	private View view;//�ָ���Ч���ؼ�
	private MenuItem save;//���水ť
	private MenuItem edit;//�޸İ�ť
	private boolean isNew = true;//�ж��Ƿ��½���Ĭ��Ϊtrue
	private String idStr = "";//�������Ĳ���
	private boolean isLove = false;
	private boolean isSave = true;//�ж������Ƿ񱣴�
	private String titleStr = "";//����id�����ݿ��ѯ���ı���
	private String contentStr = "";//����id�����ݿ��ѯ��������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_edit);

		//���ݿؼ�id�õ��ؼ�
		title = (EditText)findViewById(R.id.title);
		content = (EditText)findViewById(R.id.content);
		titleLbl = (TextView)findViewById(R.id.titleLbl);
		contentLbl = (TextView)findViewById(R.id.contentLbl);
		view = (View)findViewById(R.id.viewNote);
		
		//�������ݿ��Զ��й���
		contentLbl.setMovementMethod(new ScrollingMovementMethod());
		
		//�õ�MainActivity�д��ݵĲ��������ݲ�����ͬȷ�����½������޸�		
		Intent intent = getIntent();
		idStr = intent.getStringExtra("id");
		if("".equals(idStr)){
			//�������Ϊ�գ����½�
		}
		else if("Love".equals(idStr)){
			title.setText("��������");
			content.setText("�Ұ�����^_^");
			isLove = false;
		}
		
		else{
			title.setVisibility(View.GONE);//���ñ���༭�򲻿ɼ�
			content.setVisibility(View.GONE);//�������ݱ༭�򲻿ɼ�
			view.setVisibility(View.VISIBLE);//���÷ָ��߿ɼ�
			//���������Ϊ�գ�����ݲ���id��ѯ���ݣ������������
			queryData(idStr);
			
			titleLbl.setText(titleStr);
			titleLbl.setGravity(Gravity.CENTER);
			contentLbl.setText(contentStr);
			
			isNew = false;//���½���Ϊ�޸�
		}
	}

	/**
	 * ���ذ�ť���������¼��������û����Ǳ���
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		String titleTemp = title.getText().toString();
		String contentTemp = content.getText().toString();
		
		if(isSave){
			//���isSaveΪ�棬���ж��Ƿ��½�
			//���Ϊ�½����ұ�������������ݣ������ѱ��棻�������ѱ���
			if(isNew && (!titleStr.equals(titleTemp) || !contentStr.equals(contentTemp))){
				saveMessage();
				return;
			}
		}
		//�������û�б��棬���������½�ʱ�����ݲ����ѱ��棬�޸�ʱ����δ�䶯�����ѱ���
		else{
			if(!"".equals(idStr)){
				queryData(idStr);
				//�޸�ʱ��������Ѿ��޸ģ����������ݱ���
				if(!titleStr.equals(titleTemp) || !contentStr.equals(contentTemp) ){
					saveMessage();
					return;
				}
			}
			//�½�ʱ���������������ʱ�����ѱ���
			else if(!"".equals(titleTemp) || !"".equals(contentTemp)){
				saveMessage();
				return;
			}
		}
		
		super.onBackPressed();
	}

	//��������δ���������
	private void saveMessage() {
		Toast.makeText(this, R.string.noSave, Toast.LENGTH_SHORT).show();
		isSave = true;
		isNew = false;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_edit, menu);
		
		//��ʼ��ѡ��ؼ���������findviewById�õ�
		save = (MenuItem)menu.findItem(R.id.save);
		edit = (MenuItem)menu.findItem(R.id.edit);
		//������½�������ʾ���水ť���༭��ť����
		if(isNew){
			//���༭ͼ���Ϊ����ͼ��
			editToSave();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		//��ȡ��ǰ����ı��������
		String titleText = title.getText().toString();
		String contentText = content.getText().toString();

		 Date nowTime = new Date(System.currentTimeMillis());
		 SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd HH:mm");
		 retStrFormatNowDate = sdFormatter.format(nowTime);
		
		int id = item.getItemId();
		//����������
		if (id == R.id.save) {
			//����Ϊ�����������
			if("".equals(titleText)){
				Toast.makeText(this, "����Ϊ��", Toast.LENGTH_SHORT).show();
				return true;
			}
			
			//������½�����������
			save(titleText, contentText);
			isSave = true;
			return true;
		}
		//����ǵ�������޸İ�ť
		else if(id == R.id.edit){
			isSave = false ;
			title.setVisibility(View.VISIBLE);//���ñ���༭��ɼ�
			content.setVisibility(View.VISIBLE);//�������ݱ༭��ɼ�
			view.setVisibility(View.GONE);//���÷ָ��߲��ɼ�

			//ֻ���ؼ������������
			contentLbl.setText("����");
			contentLbl.setMovementMethod(null);//���ù��������ٹ���
			titleLbl.setText(R.string.title);
			titleLbl.setGravity(Gravity.LEFT);
			
			//��ֻ���ؼ����ݴ��ݵ���д�ؼ�
			title.setText(titleStr);
			content.setText(contentStr);
			
			//���༭ͼ���Ϊ����ͼ��
			editToSave();
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ���ر༭��ť����ʾ���水ť
	 */
	private void editToSave() {
		edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		edit.setVisible(false);
		save.setVisible(true);
	}


	/**
	 * ���ݾ��������������
	 * @param titleText ����
	 * @param contentText ����
	 */
	private void save(String titleText, String contentText) {
		if(isNew){
			insertData(titleText, contentText);	
			isNew = false;
		}
		//�����½������������
		else{
			updateData(idStr, titleText, contentText);
		}
	}

	/**�������ݿ�����
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
		//��һ��������Ҫ���µı���
		//�ڶ���������һ��ContentValeus����
		//������������where�Ӿ�
		db.update("note", values, "id=?", new String[]{idStr});
		//�������³ɹ�����Ϣ
		Toast.makeText(this, R.string.updateOK, Toast.LENGTH_SHORT).show();
	}

	/**���ݱ���id��ѯ����
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

	/**�����ݿ��������
	 * @param titleText ����
	 * @param contentText  ����
	 */
	private void insertData(String titleText, String contentText) {
		//����ContentValues����
		ContentValues values = new ContentValues();

		//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
		//values.put("id", 1);//id������������Ҫ����
		values.put("title",titleText.toString());
		values.put("content", contentText.toString());
		values.put("createdDate", retStrFormatNowDate);
		values.put("modifiedDate", retStrFormatNowDate);
		
		//test_dbΪ���ݿ������
		DatabaseHelper dbHelper = new DatabaseHelper(NoteEditActivity.this,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
		db.insert("note", null, values);
		//�������³ɹ�
		Toast.makeText(this, R.string.saveOK, Toast.LENGTH_SHORT).show();
	}
}
