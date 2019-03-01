/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vspeech;

import backend.AreaAnimation;
import backend.Spectrogram;
import backend.TimeScale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * for reading value from backend processing and storing values in variables
 * Input Call from Analyzer.java OutPut matrix of Vocl Tract, vector of
 * waveform,pitch,level, POA used in Analyzer.java
 */
public class VisualAction {

    Visual objVisual;
    private double[][] data_upper_x;
    private double[][] data_upper_y;
    private Scanner input;
    private double[][] data_spect;
    private Scanner input_new;
    private Scanner input_new_temp;
    public Vspeech vsts;
    public static int demo = 0;

    public VisualAction(Visual objVisual) {
        this.objVisual = objVisual;
        vsts = new Vspeech();
    }

    public void readFile() throws URISyntaxException {
        BufferedReader br = null;
        String path;
        if (vsts.jar) {
            path = "";
        } else {
            path = ".\\src\\praatchk\\";
        }
        try {
            /*   br = new BufferedReader(new FileReader(path + "waveform_time.txt"));
            StringBuilder sb = new StringBuilder();
            String line = "";
            ArrayList<Double> TempRefdata = new ArrayList<Double>();
            ArrayList<Double> Temp_data = new ArrayList<Double>();
            ArrayList<Double> Temp_upper_x = new ArrayList<Double>();
            ArrayList<Double> Temp_upper_y = new ArrayList<Double>();
            int count = 0;
            ArrayList<Double> Time_data = new ArrayList<Double>();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                Time_data.add(Double.parseDouble(line));
                Temp_data.add(Double.parseDouble(line));

                count++;

            }

            objVisual.setDataWave_x(Time_data);

            br = new BufferedReader(new FileReader(path + "waveform_values.txt"));
            sb = new StringBuilder();
            TempRefdata = new ArrayList<Double>();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            Double t = (1 / 10000.0);
            double last_temp = Temp_data.get(Temp_data.size() - 1);
            double time = (float) (last_temp + t);
            for (int i = TempRefdata.size() + 1; i < 10000; i++) {
                TempRefdata.add(0.0);
                Temp_data.add(time);
                time = time + t;
            }
            objVisual.setDataWave_y(TempRefdata);*/

 /*    br = new BufferedReader(new FileReader(path + "waveform_values.txt"));
            String line = "";
            ArrayList<Double> TempRefdata = new ArrayList<Double>();
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setSoundWave_1(TempRefdata);

            br = new BufferedReader(new FileReader(path + "sound_2.txt"));
            line = "";
            TempRefdata = new ArrayList<Double>();
            sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setSoundWave_2(TempRefdata);
            br = new BufferedReader(new FileReader(path + "sound_5.txt"));
            line = "";
            TempRefdata = new ArrayList<Double>();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setSoundWave_5(TempRefdata);
            br = new BufferedReader(new FileReader(path + "sound_10.txt"));
            line = "";
            TempRefdata = new ArrayList<Double>();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setSoundWave_10(TempRefdata);
            br = new BufferedReader(new FileReader(path + "sound_20.txt"));
            line = "";
            TempRefdata = new ArrayList<Double>();

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setSoundWave_20(TempRefdata);

            /* TempRefdata = new ArrayList<Double>();
            br = new BufferedReader(new FileReader(path + "intensity_values.txt"));
            sb = new StringBuilder();
            TempRefdata = new ArrayList<Double>();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setData_int(TempRefdata);

            br = new BufferedReader(new FileReader(path + "intensity_time.txt"));
            TempRefdata = new ArrayList<Double>();
            line = br.readLine();
            String[] result_time = line.split("\\s");
            for (int x = 0; x < result_time.length; x++) {
                if (!result_time[x].equals("")) {
                    TempRefdata.add(Double.parseDouble(result_time[x]));
                }
            }
            objVisual.setData_time(TempRefdata);

            /*   br = new BufferedReader(new FileReader(path + "pitch_values.txt"));
            TempRefdata = new ArrayList<Double>();
            line = br.readLine();
            String[] result_pitch = line.split("\\s");
            for (int x = 0; x < result_pitch.length; x++) {
                if (!result_pitch[x].equals("")) {
                    TempRefdata.add(Double.parseDouble(result_pitch[x]));
                }
            }
            objVisual.setData_pitchValue(TempRefdata);
            br = new BufferedReader(new FileReader(path + "pitch_time.txt"));
            TempRefdata = new ArrayList<Double>();
            line = br.readLine();
            String[] result_value = line.split("\\s");
            for (int x = 0; x < result_value.length; x++) {
                if (!result_value[x].equals("")) {
                    TempRefdata.add(Double.parseDouble(result_value[x]));
                }
            }
            objVisual.setData_pitch(TempRefdata);*/
 /* br = new BufferedReader(new FileReader(path + "mat_px_POA.txt"));
            sb = new StringBuilder();

            TempRefdata = new ArrayList<Double>();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }

            objVisual.setPoa_x(TempRefdata);
            br = new BufferedReader(new FileReader(path + "mat_py_POA.txt"));
            sb = new StringBuilder();

            TempRefdata = new ArrayList<Double>();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setPoa_y(TempRefdata);

            // read in the data
            String filename = path + "spectrogram_matrix.txt";
            input = new Scanner(new File(filename));
            int rows = 0;
            while (input.hasNextLine()) {
                ++rows;
                Scanner colReader = new Scanner(input.nextLine());
            }
            int columns = 0;
            input_new = new Scanner(new File(filename));

            String[] length = input_new.nextLine().trim().split("\\s+");
            for (int i = 0; i < length.length; i++) {
                columns++;
            }
            input_new_temp = new Scanner(new File(filename));
            data_spect = new double[rows][columns];
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    if (input_new_temp.hasNextDouble()) {
                        data_spect[i][j] = input_new_temp.nextDouble();
                    }
                }
            }
            objVisual.setSpecData(data_spect);

            // pre-read in the number of rows/columns
            input = new Scanner(new File(path + "areagram_matrix.txt"));
            int rows_area = 0;
            while (input.hasNextLine()) {
                ++rows_area;
                Scanner colReader = new Scanner(input.nextLine());
            }
            int columns_area = 0;
            input = new Scanner(new File(path + "areagram_matrix.txt"));
            String[] arealength = input.nextLine().trim().split("\\s+");
            for (int i = 0; i < arealength.length; i++) {
                columns_area++;
            }
            double[][] data_area = new double[rows_area][columns_area];
            input.close();

            // read in the data
            input = new Scanner(new File(path + "areagram_matrix.txt"));
            for (int i = 0; i < rows_area; ++i) {
                for (int j = 0; j < columns_area; ++j) {
                    if (input.hasNextDouble()) {
                        data_area[i][j] = input.nextDouble();
                    }
                }
            }
            objVisual.setAreaData(data_area);

            /*  if (demo == 0 || demo==1) {
                data_upper_x = new double[objVisual.getPoa_x().size()][200];
                data_upper_y = new double[objVisual.getPoa_x().size()][200];
                br = new BufferedReader(new FileReader(path + "mat_px.txt"));
                String line_jaw;
                int i = 0;
                while ((line_jaw = br.readLine()) != null) {
                    String[] result_mat = line_jaw.split("\\s");
                    for (int x = 0; x < result_mat.length; x++) {
                        if (!result_mat[x].equals("")) {
                            Temp_upper_x.add(Double.parseDouble(result_mat[x]));
                        }
                    }

                }
                for (int x = 0; x < objVisual.getPoa_x().size(); x++) {
                    for (int y = 0; y < 34; y++) {
                        data_upper_x[x][y] = Temp_upper_x.get(i++);
                    }
                }
                br = new BufferedReader(new FileReader(path + "mat_py.txt"));
                sb = new StringBuilder();
                i = 0;
                while ((line = br.readLine()) != null) {
                    String[] result_mat = line.split("\\s");
                    for (int x = 0; x < result_mat.length; x++) {
                        if (!result_mat[x].equals("")) {
                            Temp_upper_y.add(Double.parseDouble(result_mat[x]));
                        }
                    }

                }
                for (int x = 0; x < objVisual.getPoa_y().size(); x++) {
                    for (int y = 0; y < 34; y++) {
                        data_upper_y[x][y] = Temp_upper_y.get(i++);
                    }
                }
                demo++;
            } else {*/
            long startTime = System.nanoTime();

            ArrayList<Double> TempRefdata = new ArrayList<Double>();
            //          TempRefdata = timeAudio.Y[0];

            AreaAnimation areaAnim = new AreaAnimation();
            areaAnim.animation();
            Spectrogram spec = new Spectrogram();
            spec.specgram(areaAnim);

            objVisual.setSpecData(spec.b1);

            ArrayList<Double> Time_data = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.timesave.length; i++) {
                Time_data.add(areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.timesave[i]);
            }
            objVisual.setDataWave_x(Time_data);

            TempRefdata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.wavedata.size(); i++) {
                TempRefdata.add(areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pointDetect.wavedata.get(i));
            }
            objVisual.setDataWave_y(TempRefdata);

            TempRefdata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.tpp_12.size(); i++) {
                TempRefdata.add(areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.tpp_12.get(i));
            }
            objVisual.setData_pitch(TempRefdata);

            TempRefdata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pf_final.size(); i++) {
                TempRefdata.add(areaAnim.areaSpline.areaVal.vt_area.value.calcArea.pitchOrder.pf_final.get(i));
            }

            objVisual.setData_pitchValue(TempRefdata);

            //Intensity
            ArrayList<Double> TempLeveldata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.areaSpline.areaVal.vt_area.av_enful.length; i++) {
                TempLeveldata.add(areaAnim.areaSpline.areaVal.vt_area.av_enful[i]);
            }
            objVisual.setData_int(TempLeveldata);
            // System.out.println("temp= "+TempLeveldata+"size= "+TempLeveldata.size());

            TempRefdata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.areaSpline.areaVal.vt_area.t_en.size(); i++) {
                TempRefdata.add(areaAnim.areaSpline.areaVal.vt_area.t_en.get(i));
            }

            objVisual.setData_time(TempRefdata);

            //POA
            /*   br = new BufferedReader(new FileReader(path + "mat_px_POA.txt"));
            StringBuilder sb = new StringBuilder();

            TempRefdata = new ArrayList<Double>();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }

            objVisual.setPoa_x(TempRefdata);*/
 /*  br = new BufferedReader(new FileReader(path + "mat_py_POA.txt"));
            StringBuilder sb = new StringBuilder();

            TempRefdata = new ArrayList<Double>();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                TempRefdata.add(Double.parseDouble(line));
            }
            objVisual.setPoa_y(TempRefdata);*/
            TempRefdata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.H2_POA_pos.size(); i++) {
                TempRefdata.add(areaAnim.H2_POA_pos.get(i));
            }

            objVisual.setPoa_x(TempRefdata);

            TempRefdata = new ArrayList<Double>();
            for (int i = 0; i < areaAnim.K2_POA_pos.size(); i++) {
                TempRefdata.add(areaAnim.K2_POA_pos.get(i));
            }

            objVisual.setPoa_y(TempRefdata);
            data_upper_x = new double[areaAnim.mat_px.length][34];
            data_upper_y = new double[areaAnim.mat_px.length][34];

            for (int i = 0; i < data_upper_x.length; i++) {
                for (int j = 0; j < data_upper_x[0].length; j++) {
                    data_upper_x[i][j] = areaAnim.mat_px[i][j];
                    data_upper_y[i][j] = areaAnim.mat_py[i][j];

                }
            }
            objVisual.setMat_x(data_upper_x);
            objVisual.setMat_y(data_upper_y);

            objVisual.setAreaData(areaAnim.areaSpline.new_ag_q_spl);
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            System.out.println("Execution time in milliseconds : " + totalTime / 1000000);

            // objVisual.setAreaData(areaAnim.areaSpline.areaVal.vt_area.);
        } catch (Exception ex) {
            new newLogging("SEVERE", VisualAction.class.getName(), "readFile()", ex);
        }
    }

}
