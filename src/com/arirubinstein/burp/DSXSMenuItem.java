package com.arirubinstein.burp;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JOptionPane;
import java.util.regex.*;
import burp.*;

public class DSXSMenuItem implements IMenuItemHandler {
    private String getBody(String s){
        try {
            Pattern regex = Pattern.compile("^.\n(.*)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            Matcher regexMatcher = regex.matcher(s);
            while (regexMatcher.find()) {
                return regexMatcher.group(1);
            } 
        } catch (PatternSyntaxException ex) {
            return null;
        }
        return null;
    }
    private String getHeader(String h, String s){
        try {
        Pattern regex = Pattern.compile("^"+h+": (.+?)$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            Matcher regexMatcher = regex.matcher(s);
            while (regexMatcher.find()) {
                return regexMatcher.group(1).toString();
            } 
        } catch (PatternSyntaxException ex) {
            return null;
        }
        return null;

    }
    private void setClip(String s){
        StringSelection stringSelection = new StringSelection(s);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }
    private String cmdEscape(String s){
        return s.replace("!", "\\!").replace("\"", "\\\"");
    }
    private String stripForSqlMap(String s){
        return s.replace("(","").replace(")","").replace("'","");
    }

    public void menuItemClicked(String menuItemCaption, IHttpRequestResponse[] messageInfo){
        try {
            for (int i = 0; i < messageInfo.length; i++) {   
                byte[] request = messageInfo[i].getRequest();
                if (request != null) {
                    String req = new String(request);
                    String data = null;
                    String url = messageInfo[i].getUrl().toString();
                    String referer = getHeader("Referer", req);
                    String cookie = getHeader("Cookie", req);
                    String ua = getHeader("User-Agent", req);
                    String[] reqBrokenUp = req.split("\n");
                    if (req.startsWith("POST")){
                        //we need post parameters!
                        data = getBody(req);
                    } else {
                        System.out.println("Is Other");
                    }

                    StringBuffer cmdString = new StringBuffer("");
                    cmdString.append(" -u \""+cmdEscape(url)+"\"");
                    if (referer != null) cmdString.append(" --referer=\""+cmdEscape(referer)+"\"");
                    if (ua != null) cmdString.append(" --user-agent=\""+cmdEscape(ua)+"\"");
                    if (menuItemCaption.toLowerCase().indexOf("sqlmap") >= 0){
                        if (cookie != null) cmdString.append(" --cookie=\""+cmdEscape(stripForSqlMap(cookie))+"\"");
                    } else {
                        if (cookie != null) cmdString.append(" --cookie=\""+cmdEscape(cookie)+"\"");
                    }

                    if (data != null) cmdString.append(" --data=\""+cmdEscape(data)+"\"");
                    setClip(cmdString.toString());
                } else {
                    JOptionPane.showMessageDialog(null, "Couldn't find request, or there was an error parsing the request");
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
