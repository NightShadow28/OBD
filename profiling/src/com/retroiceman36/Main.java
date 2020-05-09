package com.retroiceman36;

public class Main{

    final static double speedlimit = 0.5;  //insert from google API, street speed limit
    final static double speedCoefficient = 0.42;
    final static double accelerationCoefficient = 0.36;
    final static double retardationCoefficient = 0.22;
    final static int w1 = 1;
    final static int w2 = 2;
    final static int w3 = 3;
    final static int w4 = 3;
    final static int w5 = 5;

    static double max(double[] in){
        double large = in[0];
        for (int l = 0; l < in.length ; l++) {
            if(in[l]>large) large = in[l];
        }
        return large;
    }

    static double min(double[] in){
        double small = in[0];
        for (int l = 0; l < in.length ; l++) {
            if(in[l]<small) small = in[l];
        }
        return small;
    }

    static double[] relative(double[] arr, double[] ti, int n){
        double difference[] = new double[n-1];
        for(int i=0;i<n-2;i++){
            difference[i]= (arr[i+1]-arr[i])/ti[i+1];
        }
        return difference;
    }

    public static void main(String[] args) {
        //take longitude, latitude and height with time
        double[] latitude=
                {39.1423,
                        39.142327,
                        39.142504,
                        39.142437,
                        39.142349,
                        39.141617,
                        39.139169,
                        39.13618,
                        39.133269,
                        39.131086,
                        39.128873,
                        39.126514,
                        39.124429,
                        39.124571,
                        39.124831,
                        39.124486,
                        39.1248,
                        39.124803,
                        39.124821,
                        39.12493,
                        39.124967,
                        39.125398,
                        39.125527,
                        39.125528,
                        39.126032,
                        39.126139};
        double[] longitude={-84.534392,
                -84.534394,
                -84.534313,
                -84.53428,
                -84.533084,
                -84.531933,
                -84.533021,
                -84.532334,
                -84.532136,
                -84.531239,
                -84.532957,
                -84.533464,
                -84.533594,
                -84.536025,
                -84.540143,
                -84.544128,
                -84.546193,
                -84.546202,
                -84.546261,
                -84.54658,
                -84.546694,
                -84.547603,
                -84.547797,
                -84.547797,
                -84.548595,
                -84.548754};
        double[] time={0,
                2,
                10,
                37,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                18,
                1,
                3,
                11,
                4,
                18,
                10,
                15,
                17,
                43};
        double[] height =new double[longitude.length];


        for(int i = 0; i< latitude.length; i++){
            latitude[i]= (Math.toRadians(latitude[i]));
            longitude[i]= (Math.toRadians(longitude[i]));
        }
        double[] difflatitude = new double[latitude.length-1];
        double[] difflongitude = new double[longitude.length-1];
        double[] circum =new double[difflatitude.length];
        double[] lonLatDistance =new double[difflatitude.length];
        double[] distance =new double[difflatitude.length];


        for(int i = 0; i< longitude.length-1; i++){
            difflatitude[i]= latitude[i+1] - latitude[i];
            difflongitude[i]= longitude[i+1] - longitude[i];
            // Haversine formula
            circum[i]= Math.pow(Math.sin(difflatitude[i]/2),2) + Math.cos(latitude[i])*Math.cos(latitude[i+1])*Math.pow(Math.sin(difflongitude[i]/2),2);
            lonLatDistance[i]=2*Math.asin(Math.sqrt(circum[i]))*6371*1000; // in metres
            distance[i]=Math.sqrt(Math.pow(lonLatDistance[i],2)+Math.pow(height[i+1]-height[i],2));

            // System.out.printf("%.2f\n",circum[i]);
        }
        double totalDistance = 0;
        for (int j = 0; j < distance.length-1 ; j++) totalDistance += distance[j];

        //speed
        double [] speed = new double[distance.length];

        for(int i = 0; i< distance.length-1; i++){ speed[i]=Math.abs(distance[i]/ time[i+1]*3.6);
            //System.out.printf("%.2f\t\t%.2f\n", distance[i], speed[i]);
        }
        double [] diffSpeedLimit = new double[speed.length];
        for(int i = 0; i< speed.length-1; i++) diffSpeedLimit[i] = speed[i] - speedlimit;

        double[] speedMagnitude =new double[distance.length];

        for(int i = 0; i< diffSpeedLimit.length; i++){
            if (diffSpeedLimit[i]>=0 && diffSpeedLimit[i]<3) speedMagnitude[i]= w1;
            else if (diffSpeedLimit[i]>=3 && diffSpeedLimit[i]<10) speedMagnitude[i]= w2;
            else if (diffSpeedLimit[i]>=10 && diffSpeedLimit[i]<16) speedMagnitude[i]= w3;
            else if (diffSpeedLimit[i]>=16 && diffSpeedLimit[i]<22) speedMagnitude[i]= w4;
            else if (diffSpeedLimit[i]>=22 ) speedMagnitude[i]= w5;
        }
        double speedWeight =0;//
        double maxSpeed =max(diffSpeedLimit);
        for (int k = 0; k < diffSpeedLimit.length-1 ; k++) speedWeight += (diffSpeedLimit[k]/ maxSpeed) * speedMagnitude[k];


        //acceleration
        double [] acceleration;
        acceleration = relative(speed, time, speed.length);
        double[] accelerationMagnitude =new double[acceleration.length];
        double[] retardationMagnitude =new double[acceleration.length];
        for(int i = 0; i< acceleration.length-1; i++){
            if(acceleration[i]>=0) {
                if (acceleration[i] >= 0 && acceleration[i] < 1) accelerationMagnitude[i] = w1;
                else if (acceleration[i] >= 1 && acceleration[i] < 3) accelerationMagnitude[i] = w2;
                else if (acceleration[i] >= 3 && diffSpeedLimit[i] < 6) accelerationMagnitude[i] = w3;
                else if (acceleration[i] >= 6 && diffSpeedLimit[i] < 10) accelerationMagnitude[i] = w4;
                else if (acceleration[i] >= 10) accelerationMagnitude[i] = w5;
            }else{
                if (acceleration[i] <= 0 && acceleration[i] > -1) retardationMagnitude[i] = w1;
                else if (acceleration[i] <= -1 && acceleration[i] > -3) retardationMagnitude[i] = w2;
                else if (acceleration[i] <= -3 && diffSpeedLimit[i] > -6) retardationMagnitude[i] = w3;
                else if (acceleration[i] <= -6 && diffSpeedLimit[i] > -10) retardationMagnitude[i] = w4;
                else if (acceleration[i] <= -10) retardationMagnitude[i] = w5;
            }
        }
        double accelerationWeight =0;//new double[distance.length];
        double retardationWeight =0;//new double[distance.length];
        double maxAcceleration =max(acceleration);
        double minretardation =min(acceleration);
        for (int k = 0; k < diffSpeedLimit.length-1 ; k++){
            if(acceleration[k]>=0) accelerationWeight += (acceleration[k]/ maxAcceleration) * accelerationMagnitude[k];
            else retardationWeight += Math.abs((acceleration[k]/ minretardation) * retardationMagnitude[k]);
        }
        double score = (speedWeight * speedCoefficient + accelerationWeight * accelerationCoefficient + retardationWeight * retardationCoefficient)*100/ totalDistance;
    }
}
