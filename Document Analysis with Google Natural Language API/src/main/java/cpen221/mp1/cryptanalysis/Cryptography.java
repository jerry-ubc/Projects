package cpen221.mp1.cryptanalysis;

import cpen221.mp1.Document;

public abstract class Cryptography {

    /**
     * Encrypt a document by replacing the i-th character, c_i, with
     * c_i + A \times \sin{(i \times 2\pi / P + \pi/4)}
     * where A is the amplitude and P is the period for the sine wave.
     * When encrypting text with multiple sentences, exactly one space
     * is used to separate sentences.
     *
     * @param doc the document to encrypt
     * @param length the number of characters to encrypt.
     *               If {@code length} is less than the number of
     *               characters in the document then only that many
     *               characters are encrypted.
     *               If {@code length} is greater than the number
     *               of characters in the document then additional
     *               ' ' are added at the end and encrypted.
     * @param period is the period of the sine wave used for encryption
     *               and {@code period} must be a factor of
     *               {@code length} other than 1 and {@code length} itself.
     * @param amplitude is the amplitude of the encrypting sine wave
     *                  and can be 64, 128, 256 or 512.
     * @return the encrypted array, with exactly one encrypted space
     *                  separating sentences.
     */
    public static int[] encrypt(Document doc, int length, int period, int amplitude) {
        // TODO: implement this method
        return null;
    }

    /**
     * Decrypt a text that has been encrypted using {@code Cryptography#encrypt}.
     * @param codedText the data to decrypt.
     * @return the decrypted text.
     */
    public static String decrypt(int[] codedText) {
        // TODO: implement this method
        return null;
    }

}