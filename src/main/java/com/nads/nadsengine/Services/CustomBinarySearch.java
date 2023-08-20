package com.nads.nadsengine.Services;

import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBObject;

public class CustomBinarySearch {

    public List<Integer> binarySearchName(List<BasicDBObject> input, String matcher, int n_hasil, String column_name,
            int threshold) {
        int left = 0;
        int right = input.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = sumHuruf(input.get(mid).getString(column_name), matcher, threshold);
            // System.out.println(left + " " + right + " " + mid);
            // System.out.println("midVal: " + midVal);
            // if (right - left <= n_hasil) {
            // return List.of(left, right);
            // }
            if (midVal > 0) {
                right = mid - 1;
            }
            if (midVal < 0) {
                left = mid + 1;
            }
            if (midVal == 0) {
                return List.of(mid - n_hasil < 0 ? 0 : mid - n_hasil,
                        mid + n_hasil > input.size() ? input.size() : mid + n_hasil);
            }
        }
        return List.of(left, right);
    }

    public List<Integer> binarySearchCoba(String[] nama_input, String matcher, int n_hasil) {
        int left = 0;
        int right = nama_input.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = sumHuruf(nama_input[mid], matcher, 3);
            // System.out.println(left + " " + right + " " + mid);
            // System.out.println("midVal: " + midVal);
            if (right - left <= n_hasil) {
                return List.of(left, right);
            }
            if (midVal > 0) {
                right = mid - 1;
            }
            if (midVal < 0) {
                left = mid + 1;
            }
            if (midVal == 0) {
                return List.of(mid - n_hasil, mid + n_hasil);
            }
        }
        return List.of(-1, -1);
    }

    public int sumHuruf(String input, String matcher, int treshold) {
        System.out.println("input: " + input);
        int hasil = 0;
        if (treshold > input.length()) {
            treshold = input.length();
        }
        if (treshold > matcher.length()) {
            treshold = matcher.length();
        }

        char[] huruf = { ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        for (int i = 0; i < treshold; i++) {
            int input_val = Arrays.binarySearch(huruf, input.toLowerCase().charAt(i));
            int matcher_val = Arrays.binarySearch(huruf, matcher.toLowerCase().charAt(i));
            if (input_val > matcher_val) {
                hasil = 1;
                return hasil;
            }
            if (input_val < matcher_val) {
                hasil = -1;
                return hasil;
            }
            if (input_val == matcher_val) {
                hasil = 0;
            }
        }
        return hasil;
    }

}
