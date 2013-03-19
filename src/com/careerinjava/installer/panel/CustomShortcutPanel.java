package com.careerinjava.installer.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.JScrollPane;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.MultiLineLabel;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.data.UninstallData;
import com.izforge.izpack.installer.event.InstallerListeners;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.shortcut.ShortcutData;
import com.izforge.izpack.panels.shortcut.ShortcutPanel;
import com.izforge.izpack.panels.shortcut.ShortcutPanelLogic;
import com.izforge.izpack.util.Housekeeper;
import com.izforge.izpack.util.OsVersion;
import com.izforge.izpack.util.PlatformModelMatcher;
import com.izforge.izpack.util.TargetFactory;

public class CustomShortcutPanel extends ShortcutPanel {
    private static final long serialVersionUID = -38011820064504878L;
    /**
     * The layout for this panel
     */
    private GridBagLayout layout;
    private ShortcutPanelLogic shortcutPanelLogic;
    /**
     * UI element for listing the intended shortcut targets
     */
    private JList targetList;
    private boolean shortcutLogicInitialized;
    /**
     * The logger.
     */
    private static final Logger logger = Logger.getLogger(ShortcutPanel.class.getName());
    /**
     * The contraints object to use whan creating the layout
     */
    private GridBagConstraints constraints;
    private List<String> shortcutsList;

    public CustomShortcutPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources,
            UninstallData uninstallData, Housekeeper housekeeper, TargetFactory factory, InstallerListeners listeners,
            PlatformModelMatcher matcher) {
        super(panel, parent, installData, resources, uninstallData, housekeeper, factory, listeners, matcher);
        layout = (GridBagLayout) super.getLayout();
        Object con = getLayoutHelper().getDefaultConstraints();
        if (con instanceof GridBagConstraints) {
            constraints = (GridBagConstraints) con;
        } else {
            con = new GridBagConstraints();
        }
        setLayout(super.getLayout());
        try {
            shortcutPanelLogic = new ShortcutPanelLogic(installData, resources, uninstallData, housekeeper, factory, listeners,
                    matcher);
            shortcutLogicInitialized = true;
        } catch (Exception exception) {
            logger.log(Level.WARNING, "Failed to initialise shortcuts: " + exception.getMessage(), exception);
            shortcutLogicInitialized = false;
        }
    }

    /**
     * Returns true when all selections have valid settings. This indicates that it is legal to proceed to the next panel.
     * 
     * @return true if it is legal to proceed to the next panel, otherwise false.
     */
    @Override
    public boolean isValidated() {
        shortcutPanelLogic.setCreateShortcuts(true);
        shortcutPanelLogic.setCreateDesktopShortcuts(true);
        try {
            shortcutPanelLogic.createAndRegisterShortcuts();
            Class<? extends ShortcutPanelLogic> panelLogicClass = shortcutPanelLogic.getClass();
            Field filesField = panelLogicClass.getDeclaredField("files");
            filesField.setAccessible(true);
            List<String> shortcutsCreated = (List<String>) filesField.get(shortcutPanelLogic);
            for (String shortcutCreated : shortcutsCreated) {
                File oldFile = new File(shortcutCreated);
                String parent2 = oldFile.getParent();
                logger.log(Level.WARNING, parent2);
                File newFile = new File(shortcutCreated.replace(parent2, installData.getVariable("INSTALL_PATH")));
                copyFile(oldFile, newFile);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return (true);
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            // previous code: destination.transferFrom(source, 0, source.size());
            // to avoid infinite loops, should be:
            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size)
                ;
        } finally {
            if (source != null) {
                source.close();
                sourceFile.delete();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * Called when the panel is shown to the user.
     */
    @Override
    public void panelActivate() {
        if (shortcutLogicInitialized && !OsVersion.IS_OSX) {
            layout = new GridBagLayout();
            constraints = new GridBagConstraints();
            setLayout(layout);
            MultiLineLabel apologyLabel = new MultiLineLabel(getI18nStringForClass("shortcuts"), 0, 0);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.insets = new Insets(5, 5, 5, 5);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.WEST;
            layout.addLayoutComponent(apologyLabel, constraints);
            add(apologyLabel);
            Vector<String> targets = new Vector<String>();
            shortcutsList = shortcutPanelLogic.getTargets();
            List<String> shortcutsInLocation = new ArrayList<String>();
            List<ShortcutData> shortcutsCreated = Collections.emptyList();
            Class<? extends ShortcutPanelLogic> panelLogicClass = shortcutPanelLogic.getClass();
            Field shortcutsField;
            try {
                shortcutsField = panelLogicClass.getDeclaredField("shortcuts");
                shortcutsField.setAccessible(true);
                shortcutsCreated = (List<ShortcutData>) shortcutsField.get(shortcutPanelLogic);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (ShortcutData sh : shortcutsCreated) {
                String target = sh.target.replace("$INSTALL_PATH", installData.getVariable("INSTALL_PATH"));
                String commandLine = sh.commandLine.replace("$INSTALL_PATH", installData.getVariable("INSTALL_PATH"));
                shortcutsInLocation.add(target + " " + commandLine);
            }
            if (shortcutLogicInitialized) {
                targets.addAll(shortcutsInLocation);
            }
            targetList = new JList(targets);
            JScrollPane scrollPane = new JScrollPane(targetList);
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.fill = GridBagConstraints.BOTH;
            layout.addLayoutComponent(scrollPane, constraints);
            add(scrollPane);
        } else {
            // Skip on OS X
            parent.skipPanel();
        }
    }
}
