package com.example.admincollegeapp.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class PathUtilsTest {

    @Test
    public void sanitizeFilename_withPathTraversal_sanitizesCorrectly() {
        assertEquals("______etc_passwd", PathUtils.sanitizeFilename("../../etc/passwd"));
        assertEquals("___test", PathUtils.sanitizeFilename("../test"));
    }

    @Test
    public void sanitizeFilename_withSpecialCharacters_sanitizesCorrectly() {
        assertEquals("test_file_123", PathUtils.sanitizeFilename("test!file@123"));
        assertEquals("file name-with_special_chars", PathUtils.sanitizeFilename("file name-with_special#chars"));
    }

    @Test
    public void sanitizeFilename_withNormalInput_remainsSame() {
        assertEquals("Normal Filename 123", PathUtils.sanitizeFilename("Normal Filename 123"));
        assertEquals("my-file_name", PathUtils.sanitizeFilename("my-file_name"));
    }

    @Test
    public void sanitizeFilename_withEmptyOrNullInput_returnsDefault() {
        assertEquals("default", PathUtils.sanitizeFilename(""));
        assertEquals("default", PathUtils.sanitizeFilename(null));
        assertEquals("default", PathUtils.sanitizeFilename("   "));
        assertEquals("default", PathUtils.sanitizeFilename("!!!"));
    }
}
