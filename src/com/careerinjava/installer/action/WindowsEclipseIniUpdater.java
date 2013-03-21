package com.careerinjava.installer.action;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class WindowsEclipseIniUpdater extends EclipseIniUpdater {
    public void run(AbstractUIProcessHandler handler, String[] args) throws IOException {
        String installationFolder = args[0];
        String softwaresPath = FilenameUtils.concat(installationFolder, "softwares");
        File softwares = new File(softwaresPath);
        Collection<File> eclipseIniFiles = FileUtils.listFiles(softwares, new NameFileFilter("eclipse.ini"),
                new PrefixFileFilter("eclips"));
        Collection<File> findJDKFiles = FileUtils.listFiles(softwares, new NameFileFilter("jvm.dll"),
                FileFilterUtils.or(new NameFileFilter(new String[] { "server", "jre", "bin" }), new PrefixFileFilter("j")));
        if (eclipseIniFiles.isEmpty()) {
            throw new RuntimeException("cannot find eclipse.ini file");
        }
        if (findJDKFiles.isEmpty()) {
            throw new RuntimeException("cannot find JDK installation");
        }
        File eclipseIni = eclipseIniFiles.iterator().next();
        updateEclipseIni(eclipseIni, findJDKFiles.iterator().next().getAbsolutePath());
    }
}
