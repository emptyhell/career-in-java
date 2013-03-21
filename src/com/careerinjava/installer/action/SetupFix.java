package com.careerinjava.installer.action;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

/**
 * Create setup batch script
 * 
 */
public class SetupFix {
    public void run(AbstractUIProcessHandler handler, String[] args) throws IOException {
        String envBatPath = args[0];
        File envBat = new File(envBatPath);
        FileUtils.writeStringToFile(envBat, args[1]);
    }
}
