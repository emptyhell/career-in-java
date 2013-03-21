package com.careerinjava.installer.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class EclipseIniUpdater {
    protected void updateEclipseIni(File eclipseIni, String pathToJava) throws IOException {
        List<String> eclipseIniLines = FileUtils.readLines(eclipseIni);
        List<String> newContentForIniFile = new ArrayList<String>();
        boolean vmAdded = false;
        for (int i = 0; i < eclipseIniLines.size(); i++) {
            String line = eclipseIniLines.get(i);
            if (line.equals("-vm")) {
                newContentForIniFile.add(line);
                newContentForIniFile.add(pathToJava);
                i++;
                vmAdded = true;
            } else if (line.startsWith("-vmargs") && !vmAdded) {
                newContentForIniFile.add("-vm");
                newContentForIniFile.add(pathToJava);
                newContentForIniFile.add(line);
                vmAdded = true;
            } else {
                newContentForIniFile.add(line);
            }
        }
        FileUtils.writeLines(eclipseIni, newContentForIniFile);
    }
}
