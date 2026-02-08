/*
 * This file is part of HTML To PDF Generator.
 *
 * Copyright (C) 2025 Gregory Echelberry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.htmltopdf.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that builds the encryption keys
 */
public class PdfR4V4Security {
    private static final Logger logger = LoggerFactory.getLogger(PdfR4V4Security.class);
    
    private static final byte[] PASSWORD_PADDING = new byte[] {
        (byte) 0x28, (byte) 0xBF, (byte) 0x4E, (byte) 0x5E,
        (byte) 0x4E, (byte) 0x75, (byte) 0x8A, (byte) 0x41,
        (byte) 0x64, (byte) 0x00, (byte) 0x4E, (byte) 0x56,
        (byte) 0xFF, (byte) 0xFA, (byte) 0x01, (byte) 0x08,
        (byte) 0x2E, (byte) 0x2E, (byte) 0x00, (byte) 0xB6,
        (byte) 0xD0, (byte) 0x2E, (byte) 0x3E, (byte) 0x80,
        (byte) 0x2F, (byte) 0x0C, (byte) 0xA9, (byte) 0xFE,
        (byte) 0x64, (byte) 0x53, (byte) 0x69, (byte) 0x7a
    };

    private static int KEY_LENGTH_BYTES = 16;

    /**
     * Compute the Owner password hash
     * @param ownerPassword String Owner Password
     * @param userPassword Stirng User Password
     * @return byte array of the owner hash value
     */
    public static byte[] computeOwnerValue(String ownerPassword, String userPassword) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(padPassword(ownerPassword));

            // 50 iterations (R > 3)
            for (int i = 0; i < 50; i++) {
                digest = md5.digest(digest);
            }

            byte[] key = Arrays.copyOf(digest, KEY_LENGTH_BYTES);
            Cipher rc4 = Cipher.getInstance("RC4");
            byte[] data = padPassword(userPassword);

            for (int i = 0; i < 20; i++) {
                byte[] iterKey = xorKey(key, i);
                rc4.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(iterKey, "RC4"));
                data = rc4.doFinal(data);
            }

            return data;
        } catch (NoSuchAlgorithmException noAlgorthim) {
            logger.error("No Such Algoritm", noAlgorthim);
            throw new RuntimeException(noAlgorthim);
        } catch (NoSuchPaddingException noPadding) {
            logger.error("No Such Padding", noPadding);
            throw new RuntimeException(noPadding);
        } catch (InvalidKeyException key) {
            logger.error("Invalid Key", key);
            throw new RuntimeException(key);
        } catch (IllegalBlockSizeException block) {
            logger.error("Illegal Block Sizse", block);
            throw new RuntimeException(block);
        } catch (BadPaddingException badPadding) {
            logger.error("Bad Padding", badPadding);
            throw new RuntimeException(badPadding);
        }
    }

    /**
     * Computes the Encryption key
     * @param userPassword String user password
     * @param ownerValue byte array of the owner password hash
     * @param permissions int of the permissions (16 bit)
     * @param documentId int of the persmissions (16 bit)
     * @param encryptMetaData boolean true to encrypt the meta data
     * @return byte array of the encryption key
     */
    public static byte[] computeEncryptionKey(String userPassword, byte[] ownerValue, int permissions, byte[] documentId, boolean encryptMetaData) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(padPassword(userPassword));
            md5.update(ownerValue);
            md5.update(intToLittleEndian(permissions));
            md5.update(documentId);

            if (!encryptMetaData) {
                md5.update(new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
            }
            byte[] hash = md5.digest();

            for (int i = 0; i < 50; i++) {
                hash = md5.digest(hash);
            }

            return Arrays.copyOf(hash, KEY_LENGTH_BYTES);
        } catch (NoSuchAlgorithmException noAlgorthim) {
            logger.error("No Such Algorithm", noAlgorthim);
            throw new RuntimeException(noAlgorthim);
        }
    }

    /**
     * Computes the user password hash
     * @param encryptionKey byte array of the encryption key
     * @param documentId byte array of the document id (16 bit)
     * @return
     */
    public static byte[] computeUserValue(byte[] encryptionKey, byte[] documentId) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(PASSWORD_PADDING);
            md5.update(documentId);

            byte[] hash = md5.digest();
            Cipher rc4 = Cipher.getInstance("RC4");
            byte[] data = hash;

            for (int i = 0; i < 20; i++) {
                byte[] iterKey = xorKey(encryptionKey, i);
                rc4.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(iterKey, "RC4"));
                data = rc4.doFinal(iterKey);
            }

            byte[] user = new byte[32];
            System.arraycopy(data, 0, user, 0, KEY_LENGTH_BYTES);
            Arrays.fill(user, 16, 32, (byte) 0);

            return user;
        } catch (NoSuchAlgorithmException noAlgorithm) {
            logger.error("No Such Algorithm", noAlgorithm);
            throw new RuntimeException(noAlgorithm);
        } catch (NoSuchPaddingException noPadding) {
            logger.error("No Such Padding", noPadding);
            throw new RuntimeException(noPadding);
        } catch (InvalidKeyException invalidKey) {
            logger.error("Invalid Key", invalidKey);
            throw new RuntimeException(invalidKey);
        } catch (IllegalBlockSizeException block) {
            logger.error("Illegal Block Sizse", block);
            throw new RuntimeException(block);
        } catch (BadPaddingException badPadding) {
            logger.error("Bad Padding", badPadding);
            throw new RuntimeException(badPadding);
        }
    }

    /**
     * Encrypt an object stream
     * @param encryptionKey byte array of the encryption key
     * @param objectNubmer int of the object Id
     * @param generationNumber int of the generation number, usually 0
     * @param plain byte array of the plain stream
     * @return byte array of the encrypted stream
     */
    public static byte[] encryptObject(byte[] encryptionKey, int objectNubmer, int generationNumber, byte[] plain) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encryptionKey);
            md5.update(intToLittleEndian(objectNubmer, 3));
            md5.update(intToLittleEndian(generationNumber, 2));
            md5.update(new byte[] {'s', 'A', 'l', 'T'});

            byte[] hash = md5.digest();
            int keyLength = Math.min(16, encryptionKey.length + 5);
            byte[] key = Arrays.copyOf(hash, keyLength);

            byte[] iv = new byte[16];

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(plain);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(iv);
            baos.write(encrypted);

            return baos.toByteArray();
        } catch (NoSuchAlgorithmException noAlgorithm) {
            logger.error("No Such Algorithm", noAlgorithm);
            throw new RuntimeException(noAlgorithm);
        } catch (NoSuchPaddingException noPadding) {
            logger.error("No Such Padding", noPadding);
            throw new RuntimeException(noPadding);
        } catch (InvalidKeyException invalidKey) {
            logger.error("Invalid Key", invalidKey);
            throw new RuntimeException(invalidKey);
        } catch (InvalidAlgorithmParameterException param) {
            logger.error("Invalid Algortigm Parameter", param);
            throw new RuntimeException(param);
        } catch (IllegalBlockSizeException block) {
            logger.error("Illegal Block Sizse", block);
            throw new RuntimeException(block);
        } catch (BadPaddingException badPadding) {
            logger.error("Bad Padding", badPadding);
            throw new RuntimeException(badPadding);
        } catch (IOException io) {
            logger.error("Error accessing stream", io);
            throw new RuntimeException(io);
        }
    }

    /**
     * Encrypts a String
     * @param encryptionKey byte array of the encryption key
     * @param objectNubmer int of the object Id
     * @param generationNumber int of the generation number, usually 0
     * @param plain byte array of the plain string
     * @return byte array of the encrypted string
     */
    public static byte[] encryptString(byte[] encryptionKey, int objectNubmer, int generationNumber, byte[] plain) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(encryptionKey);
            md5.update(intToLittleEndian(objectNubmer, 3));
            md5.update(intToLittleEndian(generationNumber, 2));
            md5.update(new byte[] {'s', 'A', 'l', 'T'});

            byte[] hash = md5.digest();
            byte[] key = Arrays.copyOf(hash, 16);

            byte[] iv = new byte[16];

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(plain);

            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

            return result;
        } catch (NoSuchAlgorithmException noAlgorithm) {
            logger.error("No Such Algorithm", noAlgorithm);
            throw new RuntimeException(noAlgorithm);
        } catch (NoSuchPaddingException noPadding) {
            logger.error("No Such Padding", noPadding);
            throw new RuntimeException(noPadding);
        } catch (InvalidKeyException invalidKey) {
            logger.error("Invalid Key", invalidKey);
            throw new RuntimeException(invalidKey);
        } catch (InvalidAlgorithmParameterException param) {
            logger.error("Invalid Algortigm Parameter", param);
            throw new RuntimeException(param);
        } catch (IllegalBlockSizeException block) {
            logger.error("Illegal Block Sizse", block);
            throw new RuntimeException(block);
        } catch (BadPaddingException badPadding) {
            logger.error("Bad Padding", badPadding);
            throw new RuntimeException(badPadding);
        }
    }

    /**
     * Pads the password using password padding
     * @param password String of the password
     * @return byte array of the padded password
     */
    private static byte[] padPassword(String password) {
        byte[] bytes = password != null ? password.getBytes(StandardCharsets.ISO_8859_1) : new byte[0];
        byte[] padded = new byte[32];
        int copyLength = Math.min(bytes.length, 32);
        System.arraycopy(bytes, 0, padded, 0, copyLength);
        System.arraycopy(PASSWORD_PADDING, 0, padded, copyLength, 32 - copyLength);

        return padded;
    }

    /**
     * Performs the XOR operation on the key
     * @param key byte array of the key
     * @param iteration int of the iteration
     * @return byte array of the updated key
     */
    private static byte[] xorKey(byte[] key, int iteration) {
        byte[] out = new byte[key.length];
        for (int i = 0; i < key.length; i++) {
            out[i] = (byte) (key[i] ^ iteration);
        }

        return out;
    }

    /**
     * Converts an int to Little Endian
     * @param value int of the value
     * @return byte array in Little Endian
     */
    private static byte[] intToLittleEndian(int value) {
        return new byte[] {
            (byte) (value),
            (byte) (value >> 8),
            (byte) (value >> 16),
            (byte) (value >> 24)
        };
    }

    /**
     * Converts an int to Little Endian
     * @param value int of the value
     * @param bytes int of the number of bytes
     * @return byte array in Little Endian
     */
    private static byte[] intToLittleEndian(int value, int bytes) {
        byte[] out = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            out[i] = (byte) (value >> (8 * i));
        }

        return out;
    }
}
