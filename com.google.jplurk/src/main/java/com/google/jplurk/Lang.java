package com.google.jplurk;


/**
 *
 *  'en': 'English'
	'pt_BR': 'Português'
	'cn': '中文 (中国)'
	'ca': 'Català'
	'el': 'Ελληνικά'
	'dk': 'Dansk'
	'de': 'Deutsch'
	'es': 'Español'
	'sv': 'Svenska'
	'nb': 'Norsk bokmål'
	'hi': 'Hindi'
	'ro': 'Română'
	'hr': 'Hrvatski'
	'fr': 'Français'
	'ru': 'Pусский'
	'it': 'Italiano '
	'ja': '日本語'
	'he': 'עברית'
	'hu': 'Magyar'
	'ne': 'Nederlands'
	'th': 'ไทย'
	'ta_fp': 'Filipino'
	'in': 'Bahasa Indonesia'
	'pl': 'Polski'
	'ar': 'العربية'
	'fi': 'Finnish'
	'tr_ch': '中文 (繁體中文)'
	'tr': 'Türkçe'
	'ga': 'Gaeilge'
	'sk': 'Slovenský'
	'uk': 'українська'
	'fa': 'فارسی
 * @author qty
 *
 */
public enum Lang {

	en("en"), pt_BR("pt_BR"), cn("cn"), ca("ca"), el("el"), dk("dk"), de("de"), es("es"), sv("sv"), nb("nb"),
	hi("hi"), ro("ro"), hr("hr"), fr("fr"), ru("ru"), it("it"), ja("ja"), he("he"), hu("hu"), ne("ne"),
	th("th"), ta_fp("ta_fp"), in("in"), pl("pl"), ar("ar"), fi("fi"), tr_ch("tr_ch"), tr("tr"), ga("ga"),
	sk("sk"), uk("uk"), fa("fa")
	;

	private String lang;

	private Lang(String lang) {
		this.lang = lang;
	}

	@Override
	public String toString() {
		return lang;
	}

	public static boolean contains(String lang) {
		try {
			Lang.valueOf(lang);
		} catch (Exception e) {
			return false;
		}
		return true;
	}


}
