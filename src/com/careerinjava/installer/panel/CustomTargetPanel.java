package com.careerinjava.installer.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.handler.AbstractUIHandler;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.target.TargetPanel;
import com.izforge.izpack.util.Platform;

public class CustomTargetPanel extends TargetPanel {
    private static final long serialVersionUID = 4155471671560191335L;
    /**
     * Target panel directory variable name.
     */
    private static final String TARGET_PANEL_DIR = "TargetPanel.dir";
    /**
     * Target panel directory variable prefix.
     */
    private static final String PREFIX = TARGET_PANEL_DIR + ".";

    /**
     * Constructs a <tt>TargetPanel</tt>.
     * 
     * @param panel the panel meta-data
     * @param parent the parent window
     * @param installData the installation data
     * @param resources the resources
     * @param log the log
     */
    public CustomTargetPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources, Log log) {
        super(panel, parent, installData, resources, log);
    }

    /**
     * Indicates whether the panel has been validated or not.
     * 
     * @return Whether the panel has been validated or not.
     */
    @Override
    public boolean isValidated() {
        String actualPath = pathSelectionPanel.getPath();
        String defaultPath = getTargetPanelDir(installData);
        boolean result = true;
        if (!defaultPath.equalsIgnoreCase(actualPath)) {
            int choice = askQuestion(getI18nStringForClass("warningtitle"), getI18nStringForClass("warning"),
                    AbstractUIHandler.CHOICES_YES_NO, AbstractUIHandler.ANSWER_NO);
            if (choice == AbstractUIHandler.ANSWER_NO) {
                result = false;
            }
        }
        if (!result) {
            pathSelectionPanel.setPath(defaultPath);
        }
        result = super.isValidated();
        return result;
    }

    private static String getTargetPanelDir(InstallData installData) {
        Platform platform = installData.getPlatform();
        String path = null;
        if (platform.getSymbolicName() != null) {
            path = installData.getVariable(PREFIX + platform.getSymbolicName().toLowerCase());
        }
        if (path == null) {
            path = getTargetPanelDir(installData, platform.getName());
        }
        if (path == null) {
            path = installData.getVariable(TARGET_PANEL_DIR);
        }
        return path;
    }

    /**
     * Returns the installation path for the specified platform name.
     * <p/>
     * This looks for a variable named {@code TargetPanel.dir.<platform name>}. If none is found, it searches the parent
     * platforms, in a breadth-first manner.
     * 
     * @param installData the installation data
     * @param name the platform name
     * @return the default path, or {@code null} if none is found
     */
    private static String getTargetPanelDir(InstallData installData, Platform.Name name) {
        String path = null;
        List<Platform.Name> queue = new ArrayList<Platform.Name>();
        queue.add(name);
        while (!queue.isEmpty()) {
            name = queue.remove(0);
            path = installData.getVariable(PREFIX + name.toString().toLowerCase());
            if (path != null) {
                break;
            }
            Collections.addAll(queue, name.getParents());
        }
        return path;
    }
}
