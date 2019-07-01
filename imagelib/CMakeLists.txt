cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -lz")

include_directories(${pathToOpenCv}/sdk/native/jni/include)
## join opencv lib
#add_library(lib_opencv STATIC IMPORTED)
#set_target_properties(lib_opencv
#        PROPERTIES IMPORTED_LOCATION
#        ${pathToOpenCv}/sdk/native/libs/${ANDROID_ABI}/libopencv_java3.so)
add_library(calib3d STATIC IMPORTED)
set_target_properties(calib3d
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_calib3d.a)

add_library(core STATIC IMPORTED)
set_target_properties(core
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_core.a)


add_library(dnn STATIC IMPORTED)
set_target_properties(dnn
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_dnn.a)
add_library(features2d STATIC IMPORTED)
set_target_properties(features2d
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_features2d.a)

add_library(flann STATIC IMPORTED)
set_target_properties(flann
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_flann.a)

add_library(highgui STATIC IMPORTED)
set_target_properties(highgui
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_highgui.a)

add_library(imgcodecs STATIC IMPORTED)
set_target_properties(imgcodecs
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_imgcodecs.a)

add_library(imgproc STATIC IMPORTED)
set_target_properties(imgproc
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_imgproc.a)

add_library(ml STATIC IMPORTED)
set_target_properties(ml
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_ml.a)

add_library(objdetect STATIC IMPORTED)
set_target_properties(objdetect
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_objdetect.a)

add_library(photo STATIC IMPORTED)
set_target_properties(photo
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_photo.a)

add_library(shape STATIC IMPORTED)
set_target_properties(shape
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_shape.a)

add_library(stitching STATIC IMPORTED)
set_target_properties(stitching
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_stitching.a)

add_library(superres STATIC IMPORTED)
set_target_properties(superres
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_superres.a)

add_library(video STATIC IMPORTED)
set_target_properties(video
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_video.a)

add_library(videoio STATIC IMPORTED)
set_target_properties(videoio
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_videoio.a)

add_library(videostab STATIC IMPORTED)
set_target_properties(videostab
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/staticlibs/${ANDROID_ABI}/libopencv_videostab.a)
########################### 3rd party ###############

add_library(libcpufeatures STATIC IMPORTED)
set_target_properties(libcpufeatures
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/libcpufeatures.a)

add_library(libIlmImf STATIC IMPORTED)
set_target_properties(libIlmImf
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/libIlmImf.a)

add_library(liblibjasper STATIC IMPORTED)
set_target_properties(liblibjasper
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/liblibjasper.a)

add_library(liblibjpeg-turbo STATIC IMPORTED)
set_target_properties(liblibjpeg-turbo
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/liblibjpeg-turbo.a)

add_library(liblibpng STATIC IMPORTED)
set_target_properties(liblibpng
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/liblibpng.a)

add_library(liblibprotobuf STATIC IMPORTED)
set_target_properties(liblibprotobuf
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/liblibprotobuf.a)

add_library(liblibtiff STATIC IMPORTED)
set_target_properties(liblibtiff
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/liblibtiff.a)

add_library(liblibwebp STATIC IMPORTED)
set_target_properties(liblibwebp
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/liblibwebp.a)

add_library(libtbb STATIC IMPORTED)
set_target_properties(libtbb
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/libtbb.a)

add_library(libtegra_hal STATIC IMPORTED)
set_target_properties(libtegra_hal
        PROPERTIES IMPORTED_LOCATION
        ${pathToOpenCv}/sdk/native/3rdparty/libs/${ANDROID_ABI}/libtegra_hal.a)


# join log lib
find_library(lib_log log)

# add target lib (live-detect)
add_library(mx-image SHARED
        src/main/cpp/native-lib.cpp

        )
target_link_libraries(mx-image

        stitching
        features2d
        flann
        imgcodecs
        imgproc
        core
        calib3d

        dnn
        highgui
        ml
        objdetect
        photo
        shape
        superres
        video
        videoio
        videostab

        # 3rd party

        libIlmImf
        libcpufeatures
        liblibjasper
        liblibjpeg-turbo
        liblibpng
        liblibprotobuf
        liblibtiff
        liblibwebp
        libtbb
        libtegra_hal

        ${lib_log})
