package com.cp.springcamera.mapper;


import com.cp.springcamera.model.Camera;

public interface CameraMapper {
    void insert(Camera camera);

    int insertSelective(Camera record);

    Camera findbycamera(Integer cameraId);
}