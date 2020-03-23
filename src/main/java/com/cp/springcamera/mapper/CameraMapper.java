package com.cp.springcamera.mapper;


import com.cp.springcamera.model.Camera;

public interface CameraMapper {
    Camera insert(Camera camera);

    int insertSelective(Camera record);

    Camera findbycamera(Integer cameraId);
}