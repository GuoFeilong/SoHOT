package com.sohot.hot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.sohot.hot.MyConstant;

import tools.FileUtils;

public class CountDownService extends Service {
    private static final int MSG_SUM = 0x110;
    private String textConent;
    private long countDownTime;
    private Message msgToClient;

    public CountDownService() {
        textConent = FileUtils.ReadTxtFile(MyConstant.PHONE_SDCARD_PAHT + "/" + MyConstant.HOT_FLAG_FILE_NAME);
        Log.e("COUNT_DOWN_SERVIECE_没转换", "读取CountDownService: >>>>>>" + "[" + textConent.trim() + "]");
        String trim = textConent.trim();
        countDownTime = Long.parseLong(trim);
        Log.e("COUNT_DOWN_SERVIECE_转换", "读取trim: >>>>>>" + "[" + countDownTime + "]");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // 信使
    private Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgfromClient) {
            //返回给客户端的消息
            msgToClient = Message.obtain(msgfromClient);

            switch (msgfromClient.what) {
                //msg 客户端传来的消息
                case MSG_SUM:
                    msgToClient.what = MSG_SUM;
                    while (true) {
                        if (countDownTime <= 0) {
                            break;
                        } else {
                            try {
                                Thread.sleep(1000);
                                countDownTime--;
                                msgToClient.arg2 = (int) countDownTime;
                                msgfromClient.replyTo.send(msgToClient);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }


            }
            super.handleMessage(msgfromClient);
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
//        startService(new Intent(this, CountDownService.class));

        Log.e("COUNT_DOWN_SERVICE", "onDestroy: ");
        super.onDestroy();
    }

}

