package com.twm.gpsmapv2;

import com.twm.gpsmapv2.ITaskCallback;  

interface ITaskBinder {   
    boolean isTaskRunning();   
    void stopRunningTask();   
    void registerCallback(ITaskCallback cb);   
    void unregisterCallback(ITaskCallback cb);   
}  