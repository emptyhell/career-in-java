package com.careerinjava.installer.action;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class MacEclipseIniUpdater extends EclipseIniUpdater {
    public void run(AbstractUIProcessHandler handler, String[] args) throws IOException {
        String installationFolder = args[0];
        String javaPath = null;
        String softwaresPath = FilenameUtils.concat(installationFolder, "softwares");
        File softwares = new File(softwaresPath);
        Collection<File> eclipseIniFiles = FileUtils.listFiles(softwares, new NameFileFilter("eclipse.ini"),
                new PrefixFileFilter("eclips"));
        Collection<File> findJDKFiles = FileUtils.listFiles(softwares, new NameFileFilter("java"),
                FileFilterUtils.notFileFilter(new PrefixFileFilter("eclips")));
        if (eclipseIniFiles.isEmpty()) {
            throw new RuntimeException("Cannot find eclipse.ini file");
        }
        if (findJDKFiles.isEmpty()) {
            throw new RuntimeException("Cannot find JDK installation");
        }
        Iterator<File> it = findJDKFiles.iterator();
        while (it.hasNext()) {
            File candidate = it.next();
            if (candidate.getParent().endsWith("bin"))
                javaPath = candidate.getAbsolutePath();
        }
        if (javaPath == null) {
            throw new RuntimeException("Cannot find JDK installation");
        }
        File eclipseIni = eclipseIniFiles.iterator().next();
        updateEclipseIni(eclipseIni, javaPath);
    }
}
