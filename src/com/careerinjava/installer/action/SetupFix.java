package com.careerinjava.installer.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

/**
 * Create setup batch script
 * 
 */
public class SetupFix {
    public void run(AbstractUIProcessHandler handler, String[] args) {
        String envBatPath = args[0];
        File envBat = new File(envBatPath);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(envBat));
            bw.write(args[1]);
        } catch (Exception e) {
            return;
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
