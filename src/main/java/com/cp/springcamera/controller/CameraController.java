package com.cp.springcamera.controller;

import com.cp.springcamera.model.Camera;
import com.cp.springcamera.service.CameraService;
import com.cp.springcamera.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author feipeng
 * @site www.gcp168.cn
 * @create 2020-03-21 12:45
 */
@RestController
public class CameraController {

    @Autowired
    CameraService cameraService;

    CameraUtil cameraUtil = new CameraUtil();
    DataUtil dataUtil = new DataUtil();
    FfmpegUtil ffmpegUtil = new FfmpegUtil();

    /**
     * 播放海康视频
     *
     * @param cameraId
     * @param request
     * @return
     */
    @GetMapping("/camera/{cameraId}/play")
    public Integer playVideo(@PathVariable Integer cameraId, HttpServletRequest request) {
        cameraUtil.getCameraList();

        //获取对应的CameraIndexCode
        Camera camera = cameraService.findbycamera(cameraId);
        String CameraIndexCode = camera.getCameraindexcode();
        String address = cameraUtil.getPreviewURLsList(CameraIndexCode);

        //利用ffmpeg的方式把rtsp流转换成可播放的数据源
        return ffmpegUtil.slice(cameraId, address);

    }

    /**
     * 回放海康视频
     */
    @GetMapping("/camera/{cameraId}/{beginTime}/{endTime}/Playback")
    public Integer PlaybackCamera(@PathVariable Integer cameraId, @PathVariable String beginTime, @PathVariable String endTime) {
        String beginTimes = dataUtil.Stringios(beginTime);
        String endTimes = dataUtil.Stringios(endTime);

        //获取对应的CameraIndexCode
        Camera camera = cameraService.findbycamera(cameraId);
        String CameraIndexCode = camera.getCameraindexcode();

        //获取url地址
        String address = cameraUtil.getPlaybackURLList(CameraIndexCode, beginTimes, endTimes);

        //利用ffmpeg的方式来获取可播放的数据源
        return ffmpegUtil.slice(cameraId, address);
    }

    /**
     * 关闭视频，防止ffmpeg一直在转换数据源占用内存
     */
    @GetMapping("/camera/{cameraId}/teardown")
    public void teardownCamById(@PathVariable Integer cameraId) {
        int p = MidleCount.reduce(cameraId);

        if (p == 0) {
            SyncPipe syncPipe = (SyncPipe) MidleMap.getnum(cameraId);
            syncPipe.cancel();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //String fileDir = "F:/testcamera/deploy/tomcat/webapps";
                    //String fileDir = "C:/Program Files/Tomcat/apache-tomcat-8.5.39/webapps";
                    //File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraId);

                    String fileDir = "E:/tomcat/apache-tomcat-8.5.43/webapps";
                    File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraId);

                    String[] files = videoAddress.list();
                    for (String fileName : files) {
                        File deleteTs = new File(videoAddress.getAbsolutePath(), fileName);
                        try {
                            Files.deleteIfExists(deleteTs.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }
}
