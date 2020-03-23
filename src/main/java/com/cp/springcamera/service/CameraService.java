package com.cp.springcamera.service;

import com.cp.springcamera.mapper.CameraMapper;
import com.cp.springcamera.model.Camera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author feipeng
 * @site www.gcp168.cn
 * @create 2020-03-21 11:09
 */
@Service
public class CameraService {
    @Autowired
    CameraMapper cameraMapper;

    public Camera add(Camera camera){
        return cameraMapper.insert(camera);
    }

    public Camera findbycamera(Integer cameraId){
        Camera camera = cameraMapper.findbycamera(cameraId);
        return camera;

    }
}
