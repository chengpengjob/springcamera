package com.cp.springcamera.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ChengPeng
 * @create 2020-03-26 16:28
 */
public class FfmpegUtil {
    public Integer slice(Integer cameraId, String address ){
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
    }
}
