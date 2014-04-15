package de.hft_stuttgart.swp2.render;

import java.io.File;

import javax.swing.filechooser.FileFilter;




/* ImageFilter.java is used by FileChooserDemo2.java. */
public class FileChooserGmlFilter extends FileFilter {

    //Accept all directories and all gml files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.gml)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "GML-Files";
    }
}