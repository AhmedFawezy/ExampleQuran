package com.my.Quran;
/*
هذا الكلاس من اعداد سيف مجاهدي الاقصي 

*/
public class TajweedQuran
{
	public static final java.util.regex.Matcher gunnahmatcher = java.util.regex.Pattern.compile("[ن|م]ّ").matcher("");
	public static final java.util.regex.Matcher iqlabmmatcher = java.util.regex.Pattern.compile("[ۭۢ][ْۡاى]?[ۛۚۗۖۙۘ]? ?ب").matcher("");
	public static final java.util.regex.Matcher qalqalamatcher = java.util.regex.Pattern.compile("[قطبجد](ْ|ۡ|[^ه]?[^هىا]?[^هىا]$)").matcher("");
	public static final java.util.regex.Matcher  idhghammatcher= java.util.regex.Pattern.compile("([نًٌٍ][ْۡاى]?[ۛۚۗۖۙۘ]? [نميو]ّ?)|م[ْۛۚۗۖۙۘۡ]? م").matcher("");
	public static final java.util.regex.Matcher idhghammatcherwihtoutgunnah = java.util.regex.Pattern.compile("[نًٌٍ][ْۡاى]?[ۛۚۗۖۙۘ]? [رل]").matcher("");
	public static final java.util.regex.Matcher ikhfamatcher = java.util.regex.Pattern.compile("([نًٌٍ][ْۡاى]?[ۛۚۗۖۙۘ]? ?[صذثكجشقسدطزفتضظک])|م[ْۡ]? ?ب").matcher("");
	public static int[] colors;
    public static android.text.Spannable getTajweed(String s) {
		colors = new int[6];
		colors[0]=(android.graphics.Color.argb(255,209,106,0));//R.color.color_ghunna
		colors[1]=(android.graphics.Color.argb(255,0,120,0));//R.color.color_qalqala
		colors[2]=(android.graphics.Color.argb(255,0,75,214));//R.color.color_iqlab
		colors[3]=(android.graphics.Color.argb(255,185,84,200));//R.color.color_idgham
		colors[4]=(android.graphics.Color.argb(255,0,193,193));// R.color.color_idghamwo
		colors[5]=(android.graphics.Color.argb(255,182,0,0));//R.color.color_ikhfa 
		//s=s.replace("ۭ", "ۢ").replace("ٗ", "ً").replace("ٖ", "ٍ").replace("ٞ", "ٌ").replace("ِۢ", "ٍۢ").replace("ُۢ", "ٌۢ").replace("َۢ", "ًۢ");//.replace("\t","").replace("ـٰ","\u0670").replace("قَوَّـٰمُونَ","قَوَّٰمُونَ");//replace("ـَٔ","ئَ").replace("ـُٔ","ئُ").replace("ـًٔ","ئً");

        android.text.Spannable text = new android.text.SpannableString(s);
        gunnahmatcher.reset(s);
        while (gunnahmatcher.find()) {
            text.setSpan(new android.text.style.ForegroundColorSpan(colors[0]), gunnahmatcher.start(), gunnahmatcher.end() + 1, 0);
        }
        qalqalamatcher.reset(s);
        while (qalqalamatcher.find()) {
            text.setSpan(new android.text.style.ForegroundColorSpan(colors[1]), qalqalamatcher.start(), qalqalamatcher.end(), 0);
        }
        iqlabmmatcher.reset(s);
        while (iqlabmmatcher.find()) {
            android.util.Log.d("iqlab Found text " + iqlabmmatcher.group(), "starting at " + iqlabmmatcher.start() + " and ending at " + iqlabmmatcher.end());
            text.setSpan(new android.text.style.ForegroundColorSpan(colors[2]), getIqlabStart(s, iqlabmmatcher.start()), iqlabmmatcher.end() + 1, 0);
        }
        idhghammatcher.reset(s);
        while (idhghammatcher.find()) {
            text.setSpan(new android.text.style.ForegroundColorSpan(colors[3]), getStart(s, idhghammatcher.start()), getEnd(s, idhghammatcher.end()), 0);
        }
        idhghammatcherwihtoutgunnah.reset(s);
        while (idhghammatcherwihtoutgunnah.find()) {
            text.setSpan(new android.text.style.ForegroundColorSpan(colors[4]), getStart(s, idhghammatcherwihtoutgunnah.start()), getEnd(s, idhghammatcherwihtoutgunnah.end()), 0);
        }
        ikhfamatcher.reset(s);
        while (ikhfamatcher.find()) {
            text.setSpan(new android.text.style.ForegroundColorSpan(colors[5]), getStart(s, ikhfamatcher.start()), getEnd(s, ikhfamatcher.end()), 0);
        }
        return text;
    }

    private static int getIqlabStart(String m, int start) {
		char ch = m.charAt(start - 1);
		if (ch == 'ً' | ch == 'ٌ' | ch == 'ٍ') {
			if (m.charAt(start - 2) == 'ّ')
				return start - 3;
			return start - 2;
		}
		return start - 1;
	}
	private static int getEnd(String m, int end) {
		if (m.charAt(end) == 'ّ') {
			if (m.charAt(end + 2) == 'ٰ') {// standing fathah
				return end + 3;
			}
			return end + 2;
		}
		if (m.charAt(end + 1) == 'ٰ' || m.charAt(end + 1) == 'ّ') {// standing fathah
			return end + 2;
		}
		return end + 1;
	}
	private static int getStart(String m, int start) {
		char ch = m.charAt(start);
		if (ch == 'ً' | ch == 'ٌ' | ch == 'ٍ') {
			if (m.charAt(start - 1) == 'ّ') {
				return start - 2;
			}
			return start - 1;
		}
		return start;
	}

}

