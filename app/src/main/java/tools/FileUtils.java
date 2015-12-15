package tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by jsion on 15/11/24.
 */
public class FileUtils {


    public static void initData(String fileContent, String filePath, String fileName) {


        writeTxtToFile(fileContent, filePath, fileName);
    }

    public static boolean delFile(String strPath) {
        boolean filebool = false;
        File file = new File(strPath);
        if (file.exists() && file.isDirectory()) {
            if (file.listFiles().length == 0) {
                file.delete();
            } else {
                File[] ff = file.listFiles();
                for (int i = 0; i < ff.length; i++) {
                    if (ff[i].isDirectory()) {
                        delFile(strPath);
                    }
                    ff[i].delete();
                }

            }
        }
// file.delete();// 如果要把文件夹也删除。。就去掉注释。。
        filebool = true;
        return filebool;

    }


    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        String strFilePath = filePath + "/" + fileName;
        makeFilePath(filePath, fileName);
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }


            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            FileChannel fileChannel = raf.getChannel();
            fileChannel.truncate(0);
            long length = file.length();
            raf.seek(length);
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + "/" + fileName);
            if (!file.exists()) {
                file.delete();
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }


    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean fileIsExists(String filePath, String fileName) {
        try {
            File f = new File(filePath + "/" + fileName);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * 读取文件内容
     *
     * @param strFilePath
     * @return
     */
    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }
}
