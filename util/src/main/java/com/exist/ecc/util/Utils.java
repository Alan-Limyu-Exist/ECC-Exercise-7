package com.exist.ecc.util;

import java.util.Scanner;


public final class Utils {
    public static final Scanner SCANNER = new Scanner(System.in);
    public static final String DEFAULT_FILE_NAME = "DefaultSave.txt";

    private Utils() { }

    public static int getUnsignedIntInput(String message) {
        try {
            int input = Integer.parseInt(SCANNER.nextLine());

            if (input >= 0) {
                return input;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
         }

        System.out.println(message);
        return -1;
    }

    public static String getStringInput(String message) {
        System.out.print(message);
        return SCANNER.nextLine();
    }
}
