package com.google.jplurk.tool;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import com.google.jplurk.AbstractJPlurkSessionTestCase;
import com.google.jplurk.PlurkClient;
import com.google.jplurk.PlurkSettings;
import com.google.jplurk.exception.PlurkException;


public class CookieExportTool {
    public static void main(String[] args) throws Exception {
        exportCookieStore();
    }

    private static void exportCookieStore() throws Exception {
        PlurkSettings settings;
        try {
            settings = new PlurkSettings();
        } catch (PlurkException e) {
            JOptionPane.showConfirmDialog(null,
                    "cannot create jplurk settings.");
            return;
        }

        JSONObject result = new PlurkClient(settings).login(JOptionPane
                .showInputDialog("Please input your username"), JOptionPane
                .showInputDialog("Please input your password"));

        if (result == null) {
            JOptionPane.showMessageDialog(null,
                    "export cookie store is failure. Stop to export cookie");
            return;
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(AbstractJPlurkSessionTestCase.getCookieStore());
            fw.write(settings.exportCookies().toString());
            fw.close();
            JOptionPane.showMessageDialog(null,
                    "export cookie store to ~/.jplurkCookies");
        } catch (IOException e) {
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ignored) {
                } finally {
                    fw = null;
                }
            }
        }
    }

}
