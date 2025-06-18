/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication1;

import java.util.Scanner;

/**
 *
 * @author farid
 */
public class JavaApplication1 {

 // Left rotate operation
    static int leftRotate(int x, int c) {
        return (x << c) | (x >>> (32 - c));
    }

    public static String md5(String input) {
        // Step 1: Constants
        int[] s = {
            7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
            5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
            6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
        };

        int[] K = new int[64];
        for (int i = 0; i < 64; i++) {
            K[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
        }

        // Step 2: Initialize variables
        int A = 0x67452301;
        int B = 0xefcdab89;
        int C = 0x98badcfe;
        int D = 0x10325476;

        // Step 3: Padding
        byte[] msgBytes = input.getBytes();
        int origLenBits = msgBytes.length * 8;

        int paddingLength = (56 - (msgBytes.length + 1) % 64 + 64) % 64;
        byte[] padded = new byte[msgBytes.length + 1 + paddingLength + 8];

        System.arraycopy(msgBytes, 0, padded, 0, msgBytes.length);
        padded[msgBytes.length] = (byte) 0x80;

        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) ((origLenBits >>> (8 * i)) & 0xFF);
        }

        // Step 4: Process blocks
        for (int i = 0; i < padded.length / 64; i++) {
            int[] M = new int[16];
            for (int j = 0; j < 64; j += 4) {
                M[j / 4] = (padded[i * 64 + j] & 0xff) |
                           ((padded[i * 64 + j + 1] & 0xff) << 8) |
                           ((padded[i * 64 + j + 2] & 0xff) << 16) |
                           ((padded[i * 64 + j + 3] & 0xff) << 24);
            }

            int a = A, b = B, c = C, d = D;

            for (int j = 0; j < 64; j++) {
                int F, g;
                if (j < 16) {
                    F = (b & c) | (~b & d);
                    g = j;
                } else if (j < 32) {
                    F = (d & b) | (~d & c);
                    g = (5 * j + 1) % 16;
                } else if (j < 48) {
                    F = b ^ c ^ d;
                    g = (3 * j + 5) % 16;
                } else {
                    F = c ^ (b | ~d);
                    g = (7 * j) % 16;
                }

                int temp = d;
                d = c;
                c = b;
                b = b + leftRotate(a + F + K[j] + M[g], s[j]);
                a = temp;
            }

            A += a;
            B += b;
            C += c;
            D += d;
        }

        return toHex(A) + toHex(B) + toHex(C) + toHex(D);
    }

    static String toHex(int n) {
        return String.format("%08x", n);
    }

    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your words : ");
        String words = input.nextLine();
        System.out.println("MD5 of \"" + words + "\" is:");
        System.out.println(md5(words));
    }

}

