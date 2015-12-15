package tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 常用工具类
 *
 * @author Android_Tian
 */
public class Tools {

    /**
     * 保留小数点后几位
     *
     * @param a
     * @param num
     * @return
     */
    public static double getDoubleFormate(double a, int num) {
        double c = Math.pow(10, num);
        double b = (Math.round(a * c)) / (int) c;// (这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000)
        return b;
    }


    /**
     * 删除工程目录下指定文件
     */
    public static boolean deleteWithProject(Context context, String fileName) {
        if (null != context) {
            return context.deleteFile(fileName);
        }
        return false;
    }

    /**
     * Description:存储数据于工程目录
     */
    public static void writeData2Project(Context mContext, String fileName, byte[] data) {
        try {
            FileOutputStream outStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            outStream.write(data);
            outStream.flush();
            outStream.close();
            outStream = null;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * Description:读取数据于工程目录下
     *
     * @author MrJing
     */
    public static byte[] readData2Project(Context mContext, String fileName) {

        byte[] buffer = null;
        try {

            FileInputStream inStream = mContext.openFileInput(fileName);
            if (inStream != null) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                buffer = new byte[1024];
                int length = -1;
                while ((length = inStream.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
                stream.close();
                inStream.close();
                return stream.toByteArray();
            }
        } catch (Exception e) {
        }
        return buffer;
    }

    public static void writeObject(Context context, String key, Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream));
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byte[] buffer = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();

            Tools.deleteWithProject(context, key);
            Tools.writeData2Project(context, key, buffer);
        } catch (Exception e) {
        }
    }

    public static Object readObject(Context context, String key) {
        Object object = null;

        try {
            byte[] buffer = Tools.readData2Project(context, key);
            if (buffer != null) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
                object = objectInputStream.readObject();
                objectInputStream.close();
            }
        } catch (Exception e) {
        }

        return object;
    }

    private static long[] mHits = new long[2];

    public static void doublePressExit(Context context) {
        T.show(context, "双击退出SoHOT", 0);
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
            ((Activity) context).finish();
            T.hideToast();
        }
    }

    /**
     * s 必须是包含 字母数字特殊符号
     *
     * @param s
     * @return
     */
    public static boolean checkPassWord(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        return s.matches("^(?![a-zA-Z0-9]+$)(?![^a-zA-Z/D]+$)(?![^0-9/D]+$).{8,100}$");
    }


    /**
     * 判断新的用户名是否包含数字和字母
     *
     * @param newUserID
     * @return
     */
    public static boolean isValidFormater(String newUserID) {
        boolean isAalid = false;
        isAalid = newUserID.matches("(?![^a-zA-Z]+$)(?!\\D+$).+");
        return isAalid;
    }

    public static String getStringByMillisecond(final String strMillisecond) {
        Date date = new Date(Long.valueOf(strMillisecond));
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String time = format.format(date);
        return time;
    }

    /**
     * 格式化数据，保留小数点2位
     *
     * @param decimal
     * @return
     */
    public static String getStringDecimalFormat(double decimal) {
        String string = new DecimalFormat("#.00").format(decimal);
        return string;
    }

    /**
     * 获得当前的时间
     */
    public static String getCurrentTime() {
        return getCurrentTimeByMillisecond(String.valueOf(System.currentTimeMillis()));
    }

    /**
     * TimeByMillisecond
     */
    public static String getCurrentTimeByMillisecond(final String str) {
        Date date = new Date(Long.valueOf(str));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }

    /**
     * 字节数组转换成bitmap
     *
     * @param bytes
     * @param opts
     * @return
     */
    public static Bitmap getBitmapFromBytes(byte[] bytes, BitmapFactory.Options opts) {

        if (bytes != null)

            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else

                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;

    }

    /**
     * 关闭软件盘
     */
    public static void closeSoftKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * drawbale转字节数据
     *
     * @param drawable
     * @return
     */
    public static byte[] drawableToByte(Drawable drawable) {

        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            // 创建一个字节数组输出流,流的大小为size
            ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            // 将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }
        return null;

    }

    /**
     * 获取网络图片
     *
     * @param urlString
     * @return
     */
    public static Bitmap getHttpBitmap(String urlString) {
        URL url;
        Bitmap bitmap = null;

        try {
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            connection.setDoInput(true);
            connection.setUseCaches(true);

            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bitmap;

    }

    /**
     * bitmap 转字节数组
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String formatDouble2String(double value) {
        return String.format("%.2f", value);
    }


}
