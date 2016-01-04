package com.example.notepad;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.waps.AppConnect;
import cn.waps.AppListener;

import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private boolean isNew = true;//�ж��Ƿ��½���Ĭ��Ϊtrue
	private String idStr = "";//�������Ĳ���
	private boolean isEdit = true;//�ж������Ƿ񱣴�
	private String titleStr = "";//����id�����ݿ��ѯ���ı���
	private String contentStr = "";//����id�����ݿ��ѯ��������
	
	private ImageView imgButton;
	private String titleText;
	private String contentText;
	private LinearLayout miniLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//����activity�ޱ��⣬�������setContentView֮ǰ
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.activity_note_edit);
		
		waps();

		//���ݿؼ�id�õ��ؼ�
		title = (EditText)findViewById(R.id.title);
		content = (EditText)findViewById(R.id.content);
		titleLbl = (TextView)findViewById(R.id.titleLbl);
		contentLbl = (TextView)findViewById(R.id.contentLbl);
		view = (View)findViewById(R.id.viewNote);
		imgButton = (ImageView) findViewById(R.id.newButton);
		
		//�������ݿ��Զ��й���
		contentLbl.setMovementMethod(new ScrollingMovementMethod());
		
		//�õ�MainActivity�д��ݵĲ��������ݲ�����ͬȷ�����½������޸�		
		Intent intent = getIntent();
		idStr = intent.getStringExtra("id");
		if("".equals(idStr)){
			//�½�
			imgButton.setBackgroundResource(R.drawable.edit);
			isEdit = true;
		}
		else{
			// �Ķ�
			imgButton.setBackgroundResource(R.drawable.read);
			
			title.setVisibility(View.GONE);//���ñ���༭�򲻿ɼ�
			content.setVisibility(View.GONE);//�������ݱ༭�򲻿ɼ�
			view.setVisibility(View.VISIBLE);//���÷ָ��߿ɼ�
			//���������Ϊ�գ�����ݲ���id��ѯ���ݣ������������
			queryData(idStr);
			
			titleLbl.setText(titleStr);
			titleLbl.setGravity(Gravity.CENTER);
			contentLbl.setText(contentStr);
			
			isNew = false;//���½���Ϊ�޸�
			isEdit = false;
		}
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("MM-dd HH:mm");
		retStrFormatNowDate = sdFormatter.format(nowTime);
		
		imgButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �޸Ľ��� --> �Ķ�����
				if(isEdit){
					//��ȡ��ǰ����ı��������
					titleText = title.getText().toString();
					contentText = content.getText().toString();
					//����Ϊ�����������
					if("".equals(titleText)){
						Toast.makeText(getApplicationContext(), "���ⲻ��Ϊ��", Toast.LENGTH_SHORT).show();
					}else{
						imgButton.setBackgroundResource(R.drawable.read);
						//������½�����������
						save(titleText, contentText);
						isEdit = false;
						
						title.setVisibility(View.GONE);//���ñ���༭�򲻿ɼ�
						content.setVisibility(View.GONE);//�������ݱ༭�򲻿ɼ�
						view.setVisibility(View.VISIBLE);//���÷ָ��߿ɼ�
						//���������Ϊ�գ�����ݲ���id��ѯ���ݣ������������
						queryData(idStr);
						
						titleLbl.setText(titleStr);
						titleLbl.setGravity(Gravity.CENTER);
						contentLbl.setText(contentStr);
					}
				}
				// �Ķ�����--> �޸Ľ���
				else{
					//��ȡ��ǰ����ı��������
					titleText = titleLbl.getText().toString();
					contentText = contentLbl.getText().toString();
					
					if("".equals(titleText)){
						Toast.makeText(getApplicationContext(), "���ⲻ��Ϊ��", Toast.LENGTH_SHORT).show();
					}else{
						imgButton.setBackgroundResource(R.drawable.edit);

						isEdit = true ;
						title.setVisibility(View.VISIBLE);//���ñ���༭��ɼ�
						content.setVisibility(View.VISIBLE);//�������ݱ༭��ɼ�
						view.setVisibility(View.GONE);//���÷ָ��߲��ɼ�

						//ֻ���ؼ������������
						contentLbl.setText("����");
						contentLbl.setMovementMethod(null);//���ù��������ٹ���
						titleLbl.setText("����");
						//titleLbl.setGravity(Gravity.LEFT);
						
						//��ֻ���ؼ����ݴ��ݵ���д�ؼ�
						title.setText(titleStr);
						content.setText(contentStr);
					}
				}
			}
		});
	}
	
	/**
	 * ����
	 */
	private void waps(){
		miniLayout =(LinearLayout)findViewById(R.id.miniAdLinearLayout);
		/*AppConnect.getInstance(this).setAdForeColor(getResources().getColor(R.color.green));
		AppConnect.getInstance(this).showMiniAd(this, miniLayout, 10); //Ĭ��10 ���л�һ�ι��
*/		
		// ���û������������ʱ�Ļص��������÷���������showBannerAd֮ǰ���ã�
		AppConnect.getInstance(this).setBannerAdNoDataListener(new AppListener() {
			@Override
			public void onBannerNoData() {
				Log.i("123", "banner������޿�������");
			}
		});
		AppConnect.getInstance(this).showBannerAd(this, miniLayout);
	}

	/**
	 * ���ذ�ť���������¼��������û����Ǳ���
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		String titleTemp = title.getText().toString();
		String contentTemp = content.getText().toString();
		
		if(isEdit){
			//���isSaveΪ�棬���ж��Ƿ��½�
			//���Ϊ�½����ұ�������������ݣ������ѱ��棻�������ѱ���
			if(isNew && titleStr.equals(titleTemp) && contentStr.equals(contentTemp)){
				super.onBackPressed();
			}else{
				if(!isNew){
					// �������û�иı䣬Ҳ������
					queryData(idStr);
					if(titleStr.equals(titleTemp) && contentStr.equals(contentTemp)){
						super.onBackPressed();
					}
				}
				
				MyDialog dialog = new MyDialog(this);
				dialog.setTitle("��û�б��棬ȷ���˳���");
				dialog.setPosBtnOnClickListener(new MyDialog.OnClickListener() {
					@Override
					public void click(View v) {
						finish();
					}
				});
			}
		}else{
			super.onBackPressed();
		}
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
