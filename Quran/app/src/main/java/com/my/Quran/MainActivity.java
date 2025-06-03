package com.my.Quran;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import android.database.sqlite.*;
import android.database.*;

public class MainActivity extends Activity {
	private SQLiteDatabase db;
	private String json = "";
	private HashMap<String, Object> trans = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> quran_list = new ArrayList<>();
	
	private LinearLayout linear1;
	private ListView listview1;
	private LinearLayout empty;
	private ImageView empty_img;
	private TextView empty_text;
	// قائمة الألوان 
	private static final int[] COLORS = {
		Color.parseColor("#30FF5733"), // برتقالي محمر
		Color.parseColor("#3033FF57"), // أخضر زاهي
		Color.parseColor("#305733FF"), // أزرق بنفسجي
		Color.parseColor("#30FFD700"), // ذهبي
		Color.parseColor("#3000CED1"), // تركوازي
		Color.parseColor("#30DC143C"), // قرمزي
		Color.parseColor("#308A2BE2"), // بنفسجي
		Color.parseColor("#3020B2AA"), // أخضر مزرق
		Color.parseColor("#30FF6347"), // طماطمي
		Color.parseColor("#304682B4")  // أزرق معدني
	};
	private Intent i = new Intent();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		// إنشاء قاعدة البيانات
        db = openOrCreateDatabase("BookmarksDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks (surah_id TEXT, aya_id TEXT)");
        
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		listview1 = findViewById(R.id.listview1);
		empty = findViewById(R.id.empty);
		empty_img = findViewById(R.id.empty_img);
		empty_text = findViewById(R.id.empty_text);
		
	}
	
	private void initializeLogic() {
		empty_text.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/tajawal_medium.ttf"), 0);
		try {
			  java.io.InputStream is = this.getAssets().open("QuranJson.json");
			           int size = is.available();
			           byte[] buffer = new byte[size];
			           is.read(buffer);
			           is.close();
			           json = new String(buffer, "UTF-8");
			 
			quran_list = new Gson().fromJson(json, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
			if (quran_list.size() > 0) {
				listview1.setAdapter(new Listview1Adapter(quran_list));
				((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
			}
			empty.setVisibility(View.GONE);
		} catch(Exception e) {
			
			listview1.setVisibility(View.GONE);
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
				_view = _inflater.inflate(R.layout.sorah, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final TextView name = _view.findViewById(R.id.name);
			final TextView info = _view.findViewById(R.id.info);
			final ImageView bookmarkIndicator = _view.findViewById(R.id.bookmark_indicator);
			name.setText("سورة ".concat(_data.get(_position).get("name").toString()));
            info.setText(_data.get(_position).get("type").toString().concat(" - عدد آياتها : ".concat(String.valueOf((long)(Double.parseDouble(_data.get(_position).get("total_verses").toString()))))));
            name.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/tajawal_medium.ttf"), 0);
            info.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/tajawal_regular.ttf"), 0);

            // التحقق من وجود علامات مرجعية في السورة
            String surahId = _data.get(_position).get("id").toString();
            if (hasSurahBookmarks(surahId)) {
                bookmarkIndicator.setVisibility(View.VISIBLE);
            } else {
                bookmarkIndicator.setVisibility(View.GONE);
            }
			bookmarkIndicator.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						String surahId = _data.get(_position).get("id").toString();

						try {
							Cursor cursor = db.rawQuery(
								"SELECT aya_id FROM bookmarks WHERE surah_id = ? ORDER BY aya_id ASC LIMIT 1",
								new String[]{surahId}
							);

							if (cursor.moveToFirst()) {
								String firstBookmarkedAyah = cursor.getString(cursor.getColumnIndex("aya_id"));
								cursor.close();

								trans = _data.get(_position);
								i.setClass(getApplicationContext(), ViewActivity.class);
								i.putExtra("sorah", new Gson().toJson(trans));
								i.putExtra("bookmark_position", firstBookmarkedAyah);
								startActivity(i);
							}
							cursor.close();
						} catch (Exception e) {
							Toast.makeText(MainActivity.this, "حدث خطأ في قراءة العلامات المرجعية", Toast.LENGTH_SHORT).show();
						}
					}
				});

			linear1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						trans = _data.get(_position);
						i.setClass(getApplicationContext(), ViewActivity.class);
						i.putExtra("sorah", new Gson().toJson(trans));
						startActivity(i);
					}
				});
		

// تعيين لون لكل عنصر بشكل دوري
		int colorIndex = _position % COLORS.length;
		_view.setBackgroundColor(COLORS[colorIndex]);
            return _view;
        }
    }

    // دالة للتحقق من وجود علامات مرجعية في السورة
    private boolean hasSurahBookmarks(String surahId) {
        try {
            Cursor cursor = db.rawQuery(
                "SELECT * FROM bookmarks WHERE surah_id = ?",
                new String[]{surahId}
            );
            boolean hasBookmarks = cursor.getCount() > 0;
            cursor.close();
            return hasBookmarks;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تحديث القائمة عند العودة للصفحة الرئيسية
        if (listview1.getAdapter() != null) {
            ((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    
	
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}
