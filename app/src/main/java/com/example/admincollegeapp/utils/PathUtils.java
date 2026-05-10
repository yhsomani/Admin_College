package com.example.admincollegeapp.utils;

public class PathUtils {

    // Private constructor to prevent instantiation
    private PathUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Sanitizes a filename to prevent path traversal attacks.
     * It replaces all non-alphanumeric characters (except spaces, hyphens and underscores) with underscores.
     *
     * @param filename The original filename or title.
     * @return A sanitized version of the filename.
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "default";
        }
        // Replace any character that is NOT a-z, A-Z, 0-9, space, hyphen, or underscore with '_'
        // Using \\- to ensure hyphen is treated literally and not as a range
        // Using ' ' (literal space) instead of \\s for stricter control
        String sanitized = filename.replaceAll("[^a-zA-Z0-9 \\-_]", "_");

        // If the resulting string contains no alphanumeric characters, return "default"
        if (sanitized.replaceAll("[_ \\-]", "").isEmpty()) {
            return "default";
        }

        return sanitized;
    }
}
