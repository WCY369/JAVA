package myplayer;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import myplayer.MyWindow;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class PlayerMain {
    static MyWindow frame;

    public static void main(String[] args) {

        NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcLibraryName(), "player\\lib\\sdk\\lib");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        //创建异步窗口线程，关闭即销毁
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //创建窗口
                    frame = new MyWindow();
                    //窗口设置为可视
                    frame.setVisible(true);
                    //准备默认媒体
                    frame.getMediaPlayer().prepareMedia("");
                    new SwingWorker<String , Integer>() {

                        @Override
                        protected String doInBackground() throws Exception {
                            while(true){
                                //获取总时长
                                long total = frame.getMediaPlayer().getLength();
                                //获取当前时长
                                long curr = frame.getMediaPlayer().getTime();
                                //计算播放时长百分比
                                float percent = (float)curr/total;
                                publish((int)(percent*100));
                                //极短时间后再次获取时长
                                Thread.sleep(100);
                            }
                        }

                        protected void process(java.util.List<Integer> chunks) {

                            for (int v:chunks) {
                                //点击进度条，设置播放进度
                                frame.getProgressBar().setValue(v);
                            }
                        };

                    }.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //播放按钮
    public static void play(){
        frame.getMediaPlayer().play();
    }

    //暂停按钮
    public  static void  pause() {
        frame.getMediaPlayer().pause();
    }

    //停止按钮
    public static void stop(){
        frame.getMediaPlayer().stop();
    }

    //静音按钮
    public static void mute(){
        frame.getMediaPlayer().mute();
    }

    //跳转按钮
    public static void jumpTo(float to){
        frame.getMediaPlayer().setTime((long)(to*frame.getMediaPlayer().getLength()));
    }

    //音量按钮
    public static void setvol(int v){
        frame.getMediaPlayer().setVolume(v);
    }

    //打开文件
    public static void openVideo(){
        JFileChooser chooser = new JFileChooser();
        int v = chooser.showOpenDialog(null);
        if (v== JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            frame.getMediaPlayer().playMedia(file.getAbsolutePath());
        }
    }

    //退出
    public static void exit(){
        frame.getMediaPlayer().release();
        System.exit(0);
    }
}