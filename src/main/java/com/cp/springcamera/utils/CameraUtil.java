package com.cp.springcamera.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cp.springcamera.model.Camera;
import com.cp.springcamera.service.CameraService;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author feipeng
 * @site www.gcp168.cn
 * @create 2020-03-21 12:16
 */
@Component
public class CameraUtil {

    static {
        ArtemisConfig.host = "112.84.178.103:444";
        ArtemisConfig.appKey = "24101667";
        ArtemisConfig.appSecret = "1WWlprPgNzzFpK0lsMKF";
    }

    private static final String ARTEMIS_PATH = "/artemis";

    @Autowired
    private CameraService cameraService;

    private static CameraUtil cameraUtil;

    @PostConstruct
    public void init(){
        cameraUtil = this;
        cameraUtil.cameraService = this.cameraService;
    }


    /**
     * 定时任务，每次全量更新
     */
   /* @Scheduled(cron = "0/5 * * * * ? *")
    public void toRun() {
        getCameraList();
    }
*/

    /**
     * 根据海康API获取所有监控存到本地数据库
     */
    public void getCameraList() {

        final String getRootApi = ARTEMIS_PATH + "/api/resource/v1/cameras";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getRootApi);
            }
        };

        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo", 1);
        jsonBody.put("pageSize", 600);
        String body = jsonBody.toJSONString();

        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType);

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObjects = jsonObject.getJSONObject("data");

        //获取list下面数据
        JSONArray jsonArray = jsonObjects.getJSONArray("list");

        Camera camera = new Camera();
        JSONObject two = null;
        //遍历返回的数据存到数据库中
        for (int i = 0; i < jsonArray.size(); i++) {
            two = jsonArray.getJSONObject(i);
            int cp = i+ 1;
            //防止重复数据插入数据库中
            //|| !(two.get("cameraName").toString()).equals(((cameraUtil.cameraService).findbycamera(i)).getCameraname())
            if(((cameraUtil.cameraService).findbycamera(cp)) == null){
                camera.setCameraid(cp);
                camera.setCameraindexcode(two.get("cameraIndexCode").toString());
                camera.setCameraname(two.get("cameraName").toString());

                camera.setRecordlocation("null");
                camera.setRecordlocationname("null");
                camera.setStatusname("null");
                camera.setStatus("1");
                cameraUtil.cameraService.add(camera);

                /*if((two.get("recordLocation").toString()) != null){
                    camera.setRecordlocation(two.get("recordLocation").toString());
                }else{
                    camera.setRecordlocation("null");
                }if((two.get("recordLocationName").toString()) != null){
                    camera.setRecordlocationname(two.get("recordLocationName").toString());
                }else{
                    camera.setRecordlocationname("null");
                }if((two.get("status").toString()) != null){
                    camera.setStatus(two.get("status").toString());
                }else{
                    camera.setStatus("1");
                }if((two.get("statusName").toString()) != null){
                    camera.setStatusname(two.get("statusName").toString());
                }else{
                    camera.setStatusname("null");
                }*/
            }else {
               return;
            }
        }
    }

    /**
     * 获取视频业务（即可播放的rtsp地址）
     */
    public String getPreviewURLsList(String CameraIndexCode) {

        //设置接口的URI地址
        final String getRootApi = ARTEMIS_PATH + "/api/video/v1/cameras/previewURLs";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getRootApi);//根据现场环境部署确认是http还是https
            }
        };

        //设置参数提交方式
        String contentType = "application/json";

        //封装请求参数
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", CameraIndexCode);
        jsonBody.put("expand", "streamform=rtp");
        jsonBody.put("streamType", "0");
        jsonBody.put("protocol", "rtsp");
        jsonBody.put("transmode", "1");
        String body = jsonBody.toJSONString();


        //调用接口
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType);

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObjects = jsonObject.getJSONObject("data");
        Object o = jsonObjects.getString("url");
        String rtsp = o.toString();

        return rtsp;
    }

    /**
     * 获取回放视频业务（即可播放的rtsp地址）
     */
    public String getPlaybackURLList(String CameraIndexCode, String beginTime, String endTime) {
        final String getRootApi = ARTEMIS_PATH + "/api/video/v1/cameras/playbackURLs";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getRootApi);
            }
        };

        //设置参数提交方式
        String contentType = "application/json";

        //封装请求参数
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", CameraIndexCode);
        jsonBody.put("beginTime", beginTime);
        jsonBody.put("endTime", endTime);
        String body = jsonBody.toJSONString();

        //调用接口
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType);

        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObjects = jsonObject.getJSONObject("data");
        Object o = jsonObjects.getString("url");
        String rtsp = o.toString();

        return rtsp;
    }

    /*public static void main(String[] args) {
        String list = getCameraList();

    }*/
}
