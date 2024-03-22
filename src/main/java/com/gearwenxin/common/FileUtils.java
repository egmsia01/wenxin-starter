package com.gearwenxin.common;

import java.io.File;

public class FileUtils {

    public static String getRootDir() {
        return System.getProperty("user.dir") + File.separator;
    }

}
