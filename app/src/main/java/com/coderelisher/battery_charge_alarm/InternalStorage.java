package com.coderelisher.battery_charge_alarm;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalStorage {

        private Context context;

        public InternalStorage(Context context) {
            this.context = context;
        }

        // Method to write data to internal storage
        public void write(String fileName, String data) {
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Method to read data from internal storage
        public String read(String fileName) {
            FileInputStream fis = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                fis = context.openFileInput(fileName);
                int c;
                while ((c = fis.read()) != -1) {
                    stringBuilder.append((char) c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return stringBuilder.toString();
        }

}
