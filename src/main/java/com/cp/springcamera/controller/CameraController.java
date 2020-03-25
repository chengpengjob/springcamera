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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 播放海康视频
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
        GetSyncPipe getSyncPipe = new GetSyncPipe();
        Map<Integer, Object> map = new HashMap<>();

        /*String strDirPath = request.getSession().getServletContext().getRealPath("/");
        String fileDir = strDirPath.substring(0, strDirPath.indexOf("springcamera"));
        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraId);*/

        String fileDir = "E:/tomcat/apache-tomcat-8.5.43/webapps";
        //String fileDir = "/cp/apache-tomcat-9.0.27/webapps";
        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraId);

        if (!videoAddress.exists()) {
            videoAddress.mkdir();
        }
        File file = new File(videoAddress.getAbsolutePath());
        File[] listFiles = file.listFiles();

        if (listFiles.length < 1) {

            int num = 0;
            MidleCount.add(cameraId, num);
            List<String> list = new ArrayList<>();
            list.add("ffmpeg");
            list.add("-rtsp_transport");
            list.add("tcp");
            list.add("-i");
            list.add(address);
            list.add("-c:v");
            list.add("copy");
            list.add("-an");
            list.add("-ss");
            list.add("1");
            list.add("-map");
            list.add("0");
            list.add("-f");
            list.add("hls");
            list.add("-hls_flags");
            list.add("delete_segments+omit_endlist");
            list.add("-hls_allow_cache");
            list.add("0");
            list.add("-hls_segment_filename");
            list.add("output%03d.ts");
            list.add("playlist.m3u8");

            SyncPipe syncPipe = new SyncPipe(list, videoAddress);
            map.put(cameraId, syncPipe);

            //把对应线程根据cameraid存到midlemap中便于后期根据线程关闭视频
            for (Map.Entry<Integer, Object> entry : map.entrySet()) {
                MidleMap.getHashMap(entry.getKey(), entry.getValue());
            }
            syncPipe.start();
            getSyncPipe.setSyncPipe(syncPipe);
        } else {
            int countKey = cameraId;
            Integer integer = MidleCount.getMp().get(countKey);
            if (integer == null) {
                MidleCount.add(countKey, 0);
            } else {
                MidleCount.add(countKey, integer);
            }
        }
        return 1;

        //return camera.getCameraindexcode();
    }

    /**
     * 回放海康视频
     */
   /* @GetMapping("/camera/{cameraId}/Playback")
    public Integer PlaybackCamera(@PathVariable Integer cameraId, String beginTime, String endTime){

        //获取对应的CameraIndexCode
        Camera camera = cameraService.findCameracodex(cameraId);

        String address = cameraUtil.getPlaybackURLList(camera.getCameraIndexCode(), beginTime, endTime);

        GetSyncPipe getSyncPipe = new GetSyncPipe();
        Map<Integer, Object> map = new HashMap<>();

        *//*String strDirPath = request.getSession().getServletContext().getRealPath("/");
        String fileDir = strDirPath.substring(0, strDirPath.indexOf("iotcamera"));
        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraId);*//*

        String fileDir = "E:/tomcat/apache-tomcat-8.5.43/webapps";
        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/PlaybackGear" + cameraId);

        if (!videoAddress.exists()) {
            videoAddress.mkdir();
        }
        File file = new File(videoAddress.getAbsolutePath());
        File[] listFiles = file.listFiles();

        if (listFiles.length < 1) {

            int num = 0;
            MidleCount.add(cameraId, num);
            List<String> list = new ArrayList<>();
            list.add("ffmpeg");
            list.add("-rtsp_transport");
            list.add("tcp");
            list.add("-i");
            list.add(address);
            list.add("-c:v");
            list.add("copy");
            list.add("-an");
            list.add("-ss");
            list.add("1");
            list.add("-map");
            list.add("0");
            list.add("-f");
            list.add("hls");
            list.add("-hls_flags");
            list.add("delete_segments+omit_endlist");
            list.add("-hls_allow_cache");
            list.add("0");
            list.add("-hls_segment_filename");
            list.add("output%03d.ts");
            list.add("playlist.m3u8");

            SyncPipe syncPipe = new SyncPipe(list, videoAddress);
            map.put(cameraId, syncPipe);

            //把对应线程根据cameraid存到midlemap中便于后期根据线程关闭视频
            for (Map.Entry<Integer, Object> entry : map.entrySet()) {
                MidleMap.getHashMap(entry.getKey(), entry.getValue());
            }
            syncPipe.start();
            getSyncPipe.setSyncPipe(syncPipe);
        } else {
            int countKey = cameraId;
            Integer integer = MidleCount.getMp().get(countKey);
            if (integer == null) {
                MidleCount.add(countKey, 0);
            } else {
                MidleCount.add(countKey, integer);
            }
        }
        return 1;

    }*/
    /**
     * 关闭视频，防止一直在切片占用内存
     */
    @GetMapping("/camera/{cameraId}/teardown")
    public void teardownCamById(@PathVariable Integer cameraId){
        int p =MidleCount.reduce(cameraId);

        if(p == 0){
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
