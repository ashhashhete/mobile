package com.igenesys.utils;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtilsTest {


    private static void doCryptoInAES(int cipherMode, String key, File inputFile,
                                 File outputFile){
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (Exception ex) {
            ex.getCause();
        }
    }

    private static void doCryptoInBlowFish(int cipherMode,String KEY,File inputFile,File outputFile) throws CryptoException{
        String ALGORITHM = "Blowfish";
        String MODE = "Blowfish/CBC/PKCS5Padding";
        String IV = "!a3edr45";

        try {
            Key secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(cipherMode, secretKey, new IvParameterSpec(IV.getBytes()));

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            ex.fillInStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
    public static void encryptFileinAES(File f,int name) throws CryptoException {
        if(name==1){
            doCryptoInAES(Cipher.ENCRYPT_MODE,"ak$#54%^RtF%g^Hf",f,f);
        }else if(name==2){
            doCryptoInAES(Cipher.DECRYPT_MODE,"ak$#54%^RtF%g^Hf",f,f);
        }
//

    }
    public void encryptFileinBlowfish(File f,String name) throws CryptoException {
//        final File saveFile = new File(Environment.getExternalStorageDirectory(), "intruder.jpg");
        doCryptoInBlowFish(Cipher.ENCRYPT_MODE,"SAMPLEKEY",f,f);
        doCryptoInBlowFish(Cipher.DECRYPT_MODE,"SAMPLEKEY",f,f);

    }
}