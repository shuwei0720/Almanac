package com.twm.gpsmapv2;

interface ITaskCallback {

    void actionPerformed(int actionId); 
    
    void isGpsProviderEnabled(boolean bEnable); 
    
    void gpsLocationManagerLatlng(String strProvider, double dLat, double dlng, float fAccuracy);
} 