package org.ai.carp.controller.util;

import org.ai.carp.controller.exceptions.InvalidRequestException;
import org.bson.types.Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveUtils {

    public static Binary convertSubmission(String encoded, String entryName) {
        if (encoded.length() > 87500) {
            throw new InvalidRequestException("Archive size exceeds 64KB!");
        }
        ZipInputStream zis = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(encoded);
            zis = new ZipInputStream(new ByteArrayInputStream(bytes));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    return new Binary(bytes);
                }
            }
            throw new InvalidRequestException("No " + entryName + " found!");
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid base64 data!");
        } catch (IOException e) {
            throw new InvalidRequestException("Invalid zip archive!");
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
