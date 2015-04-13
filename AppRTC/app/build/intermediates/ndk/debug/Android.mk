LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_SRC_FILES := \
	C:\Users\davizen\AndroidstudioProjects\android\app\src\main\jni\Android.mk \

LOCAL_C_INCLUDES += C:\Users\davizen\AndroidstudioProjects\android\app\src\main\jni
LOCAL_C_INCLUDES += C:\Users\davizen\AndroidstudioProjects\android\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
