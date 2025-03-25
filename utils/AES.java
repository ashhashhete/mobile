package com.igenesys.utils;

import java.security.Key;
import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AES {



	// private static SecretKeySpec secretKey;
	// private static byte[] key;

	private static String SECRET_KEY = "genesyssecurekey";

	public static String encrypt(String data) throws Exception {

		String encodedBase64Key = encodeKey(SECRET_KEY);

		Key key = generateKey(encodedBase64Key);
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(data.getBytes());
		String encryptedValue = Base64.getEncoder().encodeToString(encVal);
		return encryptedValue;
	}

	public static String decrypt(String strToDecrypt) {
		try {
			String encodedBase64Key = encodeKey(SECRET_KEY);
			Key key = generateKey(encodedBase64Key);

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	public static String getArabicDate(String date) {
//		logger.info("FUNCTION : userDataService  -> getArabicDate");
		LocalDate localDate = LocalDate.parse(date);
		HijrahDate hijrahDate = HijrahDate.from(localDate); // Hijrah-umalqura AH 1443-12-01
		String hDate = hijrahDate.toString();
		System.out.printf("localDateMonth: %s%n", LocalDate.from(hijrahDate).getMonth()); // 1 Dhuʻl-Hijjah, 1443 AH
		System.out.printf("localDate: %s%n", localDate);
		System.out.printf("hijrahDate: %s%n%n", hijrahDate);
		HijrahEra era = hijrahDate.getEra(); // HijrahEra.AH
		HijrahChronology chronology = hijrahDate.getChronology();// Hijrah-umalqura
		String[] hDate1 = hDate.split(chronology.toString() + " " + era.toString() + " ");
		String hijriDate = getHijriDate(hDate1[1].toString()) + " " + era;
		Map<String, String> data = new HashMap<>();
		data.put("gregorianDate", date);
		data.put("hijriDate", hDate1[1].toString());
		data.put("hijriDate_Month", hijriDate);
		data.put("calendarType", chronology.toString());
		System.out.printf("hijriDate_Month: %s%n%n", hijriDate);
		return hDate1[1].toString();

	}

	public static String getHijriDate(String hDate) {

		List<String> date = Arrays.asList(hDate.split("-"));
		String day = date.get(2);
		String monthvalue = date.get(1);
		String year = date.get(0);
		Map<String, String> monthNames = new HashMap<>();
		monthNames.put("01", "Muharram");
		monthNames.put("02", "Safar");
		monthNames.put("03", "Rabi Al-Awwal");
		monthNames.put("04", "Rabi Al-Thani");
		monthNames.put("05", "Jumada Al-Awwal");
		monthNames.put("06", "Jumada Al-Thani");
		monthNames.put("07", "Rajab");
		monthNames.put("08", "Sha`ban");
		monthNames.put("09", "Ramadan");
		monthNames.put("10", "Shawwal");
		monthNames.put("11", "Dhul-Qa`dah");
		monthNames.put("12", "Dhul-Hijjah");

		String reqDate = day + " " + monthNames.get(monthvalue) + ", " + year;

		return reqDate;
	}

	private static Key generateKey(String secret) throws Exception {
		byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
		Key key = new SecretKeySpec(decoded, "AES");
		return key;
	}

	public static String decodeKey(String str) {
		byte[] decoded = Base64.getDecoder().decode(str.getBytes());
		return new String(decoded);
	}

	public static String encodeKey(String str) {
		byte[] encoded = Base64.getEncoder().encode(str.getBytes());
		return new String(encoded);
	}

}