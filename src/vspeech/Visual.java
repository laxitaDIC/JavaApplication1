/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Laxita
 */
public class Visual {

    private ArrayList<Double> dataWave_x;
    private ArrayList<Double> dataWave_y;
    private File file;
    private ArrayList<Double> data_pitchValue;
    private ArrayList<Double> data_pitch;
    private ArrayList<Double> data_time;
    private ArrayList<Double> data_int;
    private double[][] mat_x;
    private double[][] mat_y;
    private ArrayList<Double> poa_x;
    private ArrayList<Double> poa_y;
    private File reffile;
    private double specData[][];
    private double areaData[][];
    private ArrayList<Double> soundWave_2;
    private ArrayList<Double> soundWave_5;
    private ArrayList<Double> soundWave_10;
   private ArrayList<Double> soundWave_20;
   private ArrayList<Double> soundWave_1;

   public ArrayList<Double> getPoa_x() {
        return poa_x;
    }

    public void setPoa_x(ArrayList<Double> poa_x) {
        this.poa_x = poa_x;
    }

    public ArrayList<Double> getPoa_y() {
        return poa_y;
    }

    public void setPoa_y(ArrayList<Double> poa_y) {
        this.poa_y = poa_y;
    }

    public double[][] getSpecData() {
        return specData;
    }

    public void setSpecData(double[][] specData) {
        this.specData = specData;
    }

    public double[][] getAreaData() {
        return areaData;
    }

    public void setAreaData(double[][] areaData) {
        this.areaData = areaData;
    }

    public double[][] getMat_x() {
        return mat_x;
    }

    public void setMat_x(double[][] mat_x) {
        this.mat_x = mat_x;
    }

    public double[][] getMat_y() {
        return mat_y;
    }

    public void setMat_y(double[][] mat_y) {
        this.mat_y = mat_y;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getReffile() {
        return reffile;
    }

    public void setReffile(File reffile) {
        this.reffile = reffile;
    }

    public ArrayList<Double> getDataWave_x() {
        return dataWave_x;
    }

    public void setDataWave_x(ArrayList<Double> dataWave_x) {
        this.dataWave_x = dataWave_x;
    }

    public ArrayList<Double> getDataWave_y() {
        return dataWave_y;
    }

    public void setDataWave_y(ArrayList<Double> dataWave_y) {
        this.dataWave_y = dataWave_y;
    }

     public ArrayList<Double> getSoundWave_1() {
        return soundWave_1;
    }

    public void setSoundWave_1(ArrayList<Double> soundWave_1) {
        this.soundWave_1 = soundWave_1;
    } 
    
      public ArrayList<Double> getSoundWave_2() {
        return soundWave_2;
    }

    public void setSoundWave_2(ArrayList<Double> soundWave_2) {
        this.soundWave_2 = soundWave_2;
    } 
    
    public ArrayList<Double> getSoundWave_5() {
        return soundWave_5;
    }

    public void setSoundWave_5(ArrayList<Double> soundWave_5) {
        this.soundWave_5 = soundWave_5;
    } 
    
    public ArrayList<Double> getSoundWave_10() {
        return soundWave_10;
    }

    public void setSoundWave_10(ArrayList<Double> soundWave_10) {
        this.soundWave_10 = soundWave_10;
    }
    
    public ArrayList<Double> getSoundWave_20() {
        return soundWave_20;
    }

    public void setSoundWave_20(ArrayList<Double> soundWave_20) {
        this.soundWave_20 = soundWave_20;
    }
    
    public File getStudentFile() {
        return file;
    }

    public void setStudentFile(File file) {
        this.file = file;
    }

    public ArrayList<Double> getData_pitchValue() {
        return data_pitchValue;
    }

    public void setData_pitchValue(ArrayList<Double> data_pitchValue) {
        this.data_pitchValue = data_pitchValue;
    }

    public ArrayList<Double> getData_pitch() {
        return data_pitch;
    }

    public void setData_pitch(ArrayList<Double> data_pitch) {
        this.data_pitch = data_pitch;
    }

    public ArrayList<Double> getData_time() {
        return data_time;
    }

    public void setData_time(ArrayList<Double> data_time) {
        this.data_time = data_time;
    }

    public ArrayList<Double> getData_int() {
        return data_int;
    }

    public void setData_int(ArrayList<Double> data_int) {
        this.data_int = data_int;
    }

}
