package com.my.Quran;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import android.database.*;
import android.database.sqlite.*;
import android.content.ClipboardManager;
public class ViewActivity extends Activity {
    private HashMap<String, Object> sorah = new HashMap<>();
    private ArrayList<HashMap<String, Object>> ayat = new ArrayList<>();
    private LinearLayout linear1;
    private ListView listview1;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.view);
        initialize(_savedInstanceState);

        // إنشاء قاعدة البيانات
        db = openOrCreateDatabase("BookmarksDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks (surah_id TEXT, aya_id TEXT)");

        initializeLogic();
    }

    private void initialize(Bundle _savedInstanceState) {
        linear1 = findViewById(R.id.linear1);
        listview1 = findViewById(R.id.listview1);
    }

    private void initializeLogic() {
        sorah = new Gson().fromJson(getIntent().getStringExtra("sorah"), new TypeToken<HashMap<String, Object>>(){}.getType());
        if (!sorah.isEmpty()) {
            setTitle("سورة ".concat(sorah.get("name").toString()));
            ayat = new Gson().fromJson(sorah.get("verses").toString(), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
            listview1.setAdapter(new Listview1Adapter(ayat));
            ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();

            // التحقق من وجود آية مرجعية والانتقال إليها
            final String bookmarkPosition = getIntent().getStringExtra("bookmark_position");
            if (bookmarkPosition != null) {
                listview1.post(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < ayat.size(); i++) {
								if (ayat.get(i).get("id").toString().equals(bookmarkPosition)) {
									listview1.setSelection(i);
									break;
								}
							}
						}
					});
            }
        }
    }

    public class Listview1Adapter extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;

        public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }

        @Override
        public View getView(final int _position, View _v, ViewGroup _container) {
            LayoutInflater _inflater = getLayoutInflater();
            View _view = _v;
            if (_view == null) {
                _view = _inflater.inflate(R.layout.aya, null);
            }

            final LinearLayout linear1 = _view.findViewById(R.id.linear1);
            final TextView num = _view.findViewById(R.id.num);
            final TextView aya = _view.findViewById(R.id.aya);
            final ImageView bookmarkIcon = _view.findViewById(R.id.bookmark_icon);
			final ImageView copyicon = _view.findViewById(R.id.iconcopy);
			final ImageView img_shar = _view.findViewById(R.id.img_shar);
            num.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/sultani.ttf"), 0);    

            // تعيين النصوص
            num.setText(String.valueOf((long)(Double.parseDouble(_data.get(_position).get("id").toString()))));
			//اضافة التجويد

           /* aya.setText(TajweedQuran.getTajweed(_data.get(_position).get("text").toString()
												.replace("ۭ", "ۢ")
												.replace("ٗ", "ً")
												.replace("ٖ", "ٍ")
												.replace("ٞ", "ٌ")
												.replace("ِۢ", "ٍۢ")
												.replace("ُۢ", "ٌۢ")
												.replace("َۢ", "ًۢ").replace("ـٰ","\u0670")));//replace("ـَٔ","ئَ").replace("ـُٔ","ئُ").replace("ـًٔ","ئً")));
				*/								
            //بدون تجويد
			aya.setText(_data.get(_position).get("text").toString().replace("بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ","بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ \n"));
			
			//aya.setGravity(Gravity.CENTER);
			aya.setTextSize(2,25.0f);
			aya.setPadding(1, 5, -1, 5);
			Display defaultDisplay = ((WindowManager)getApplicationContext().getSystemService("window")).getDefaultDisplay();
			 Point point = new Point();
			 defaultDisplay.getSize(point);
			 int height = (point.x * 80) /100;
			 int width = (point.x * 20) / 100;
			 int y = point.y;
			 aya.setWidth(width);
			 //aya.setHeight(height);
			// aya.setY(y);
			


			
			aya.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/hafs_v20.ttf"),0);
			

			///نسخ الاية
			copyicon.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						//نسخ نص الاية
						((ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard","".concat(aya.getText().toString().concat("\n").concat("سورة ".concat(sorah.get("name").toString().concat("\n").concat("اية".concat(_data.get((int)_position).get("id").toString())))))));
						
					}
					
				
			});
			//مشاركة النص للاية 
			img_shar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						Intent i = new Intent(android.content.Intent.ACTION_SEND); i.setType("text/plain"); i.putExtra(android.content.Intent.EXTRA_TEXT, aya.getText().toString()); startActivity(Intent.createChooser(i,""));
					}
				});
			 
            // التحقق من وجود العلامة المرجعية
            String currentSurahId = sorah.get("id").toString();
            String currentAyaId = _data.get(_position).get("id").toString();

            if (isBookmarked(currentSurahId, currentAyaId)) {
                bookmarkIcon.setImageResource(R.drawable.ic_bookmark_filled);
            } else {
                bookmarkIcon.setImageResource(R.drawable.ic_bookmark_border);
            }

            // إضافة مستمع النقر على أيقونة المفضلة
            bookmarkIcon.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String surahId = sorah.get("id").toString();
						String ayaId = _data.get(_position).get("id").toString();

						if (isBookmarked(surahId, ayaId)) {
							// إزالة العلامة المرجعية
							db.delete("bookmarks", "surah_id = ? AND aya_id = ?", 
									  new String[]{surahId, ayaId});
							bookmarkIcon.setImageResource(R.drawable.ic_bookmark_border);
							Toast.makeText(ViewActivity.this, "تم إزالة العلامة المرجعية", 
										   Toast.LENGTH_SHORT).show();
						} else {
							// إضافة علامة مرجعية
							ContentValues values = new ContentValues();
							values.put("surah_id", surahId);
							values.put("aya_id", ayaId);
							db.insert("bookmarks", null, values);
							bookmarkIcon.setImageResource(R.drawable.ic_bookmark_filled);
							Toast.makeText(ViewActivity.this, "تمت إضافة العلامة المرجعية", 
										   Toast.LENGTH_SHORT).show();
						}
					}
				});

            // تعيين الخلفية
            if (_position % 2 == 0) {
                _view.setBackgroundColor(Color.parseColor("#FDF7E6"));
            } else {
                _view.setBackgroundColor(Color.parseColor("#F3EAD6"));
            }

            return _view;
        }
    }

    // دالة للتحقق من وجود العلامة المرجعية
    private boolean isBookmarked(String surahId, String ayaId) {
        try {
            Cursor cursor = db.rawQuery(
                "SELECT * FROM bookmarks WHERE surah_id = ? AND aya_id = ?",
                new String[]{surahId, ayaId}
            );
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
 
