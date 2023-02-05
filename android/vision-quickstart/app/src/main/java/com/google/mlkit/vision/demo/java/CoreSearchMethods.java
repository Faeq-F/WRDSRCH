package com.google.mlkit.vision.demo.java;

import android.util.Log;

import com.google.mlkit.vision.demo.java.textdetector.TextGraphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CoreSearchMethods {

    public static void main(String[] args) {
        String[] word_src = {"HELLO", "SHARE", "SPICE", "SHARK", "BUTLR", "SILVE", "YOUNG"} ;
        String[] vert = {"XLXXX", "XLXXX", "XEXXX", "XHXXX", "XXXXX"} ;
        String[] diag = {"XHXXX", "XXEXX", "XXXYX", "XXXXX", "XXXXX"} ;

        findHorz(word_src, "ARE") ;
        findVert(vert, "HELL") ;
        findDiag(diag, "HELL") ;




    }


    public static HashMap<String, int[]> searchWords(String[] word_block, String[] words) {
        for (int i = 0; i <= words.length; i++) {
            words[i] = words[i].replace('|', 'I').replaceAll("\\s", "");
        }
        HashMap<String, int[]> solutions = new HashMap<String, int[]>() ;

        int[] sols_coords ;



        for (String word : words) {

            // search horizontally

            sols_coords = findHorz(word_block, word) ;

            if (sols_coords != null) {

                solutions.put(word, sols_coords) ;

                continue ;

            }



            // search vertically

            sols_coords = findVert(word_block, word) ;

            if (sols_coords != null) {

                solutions.put(word, sols_coords) ;

                continue ;

            }

        }



        return solutions ;

    }




    // Input: normal word search block and word to find
    // Return:  coords of start of word, first number being the array in the bigger array,
    //          seconds number being the index within the string itself.
    public static int[] findHorz(String[] word_block, String word) {
        int[] coords_normal = findWordOnBlock(word_block, word) ;

        if (coords_normal[1] != -1 ) {
            return coords_normal ;
        } else {
            int[] coords_reversed = findWordOnBlock(word_block, inverseString(word)) ;

            coords_reversed[0] = coords_reversed[0] - word.length() ;

            if (coords_reversed[1] != -1) return (coords_reversed) ;
        }

        return null ;
    }

    // Input: normal word search block and word to find
    // Return:  coords of start of word, first number being the array in the bigger array,
    //          seconds number being the index within the string itself.
    public static int[] findVert(String[] word_block, String word) {
        String[] horz_word_block = rowToCol(word_block) ;

        int[] coords_normal = findWordOnBlock(horz_word_block, word) ;

        if (coords_normal[1] != -1) {
            coords_normal[0] ^= coords_normal[1] ;
            coords_normal[1] ^= coords_normal[0] ;
            coords_normal[0] ^= coords_normal[1] ;

            return coords_normal ;
        } else {
            int[] coords_reversed = findWordOnBlock(horz_word_block, inverseString(word)) ;

            if (coords_reversed[1] != -1) {
                coords_reversed[0] ^= coords_reversed[1] ;
                coords_reversed[1] ^= coords_reversed[0] ;
                coords_reversed[0] ^= coords_reversed[1] ;

                return coords_reversed ;
            }

        }

        return null ;

    }

    // Input: normal word search block and word to find
    // Return:  coords of start of word, first number being the array in the bigger array,
    //          seconds number being the index within the string itself.
    // NOT DONE!!!
    public static int[] findDiag(String[] word_block, String sword) {
        String[] horz_word_block = horzToDiag(word_block);

        System.out.println(Arrays.toString(horz_word_block));

        // int start = word_block.length, end = word_block[0].length() ;
        int y_len = word_block.length;
        int x_len = word_block[0].length();

        ArrayList<ArrayList<int[]>> list = new ArrayList<ArrayList<int[]>>();

        for (int i = 0; i < (x_len + (y_len - 1)); ++i) {
            list.add(new ArrayList<int[]>());
        }
        ;

        int x_start = 0, y_start = 0;
        int tmp_y;
        int x_axis_len = word_block[0].length() - 1;
        int y_axis_len = word_block.length - 1;
        int x_i_start, x_end = x_axis_len;

        int[] tmp_array = {0, 0};

        while (true) {
            if (x_start < 0) {
                x_start = 0;
                y_start += 1;
            }

            tmp_y = y_start;
            // tmp_str = "" ;

            for (x_i_start = x_start; x_i_start < x_end + 1; ++x_i_start) {
                System.out.println(Arrays.toString(list.toArray()));
                if (tmp_y >= y_axis_len) {
                    tmp_array[0] = tmp_y;
                    tmp_array[1] = x_i_start;

                    list.get(tmp_y).add(x_i_start, tmp_array);
                    // tmp_str += word_block[tmp_y].charAt(x_i_start) ;
                    break;
                }

                // tmp_str += word_block[tmp_y].charAt(x_i_start) ;

                list.get(tmp_y).add(x_i_start, tmp_array);

                if (tmp_y < y_axis_len) {
                    tmp_y += 1;
                }
            }

            // list.add( tmp_str ) ;

            x_start -= 1;

            if (x_start < 0 && y_start == y_axis_len) {
                break;
            }
            ;
        }
        ;

        return null;
    }

    // Input: Diagonilized word search block, length and height of original word search block
    // Output: Normalized word search block.
    public static String[] diagToHorz(String[] word_block, int length, int height) {
        int start = 0 ;
        ArrayList<String> list = new ArrayList<String>() ;

        for (int rows_num = 0 ; rows_num < height ; ++rows_num) {
            list.add("") ;
        }

        int i, j ;

        for (i=start ; i<length ; ++i) {
            for (j=start ; j<=i ; ++j) {
                list.set(j, list.get(j) + word_block[i].charAt(j)) ;
            }

        }

        int tmp_start = start +1 ;
        int tmp_end = length ;

        int list_len = length ;
        int k, tmp_i ;

        while (true) {
            if (tmp_end==height) break ;

            tmp_i = 0 ;
            for (i=tmp_start ; i<=tmp_end ; ++i) {
                list.set(i, list.get(i) + word_block[list_len].charAt(tmp_i)) ;
                ++tmp_i ;
            }

            ++tmp_end ; ++tmp_start ;
            ++list_len ;
        }

        for (j=tmp_start ; j<height ; ++j) {
            tmp_i = 0 ;
            for (k=j ; k<height ; ++k) {
                list.set(k, list.get(k) + word_block[list_len].charAt(tmp_i)) ;
                ++tmp_i ;
            }

            ++list_len ;
        }

        return inverseBlock(list) ;
    }

    // Input: Normal word search block
    // Output: Diagonalized word search block
    public static String[] horzToDiag(String[] word_block) {
        int y_axis_len = word_block.length -1 ; // both already in list format (-1 len)
        int x_axis_len = word_block[0].length() -1 ;

        ArrayList<String> list = new ArrayList<String>() ;

        int y_start = 0 ;
        int x_start = x_axis_len ;
        int x_end = x_axis_len ;

        int x_i_start ;

        int tmp_y ;

        String tmp_str = "" ;

        while (true) {
            if (x_start < 0) {
                x_start = 0 ;
                y_start += 1 ;
            }

            tmp_y = y_start ;
            tmp_str = "" ;

            for (x_i_start=x_start ; x_i_start<x_end+1 ; ++x_i_start) {
                if (tmp_y >= y_axis_len) {
                    tmp_str += word_block[tmp_y].charAt(x_i_start) ;
                    break ;
                }

                tmp_str += word_block[tmp_y].charAt(x_i_start) ;

                if (tmp_y < y_axis_len) {
                    tmp_y += 1 ;
                }
            }

            list.add( tmp_str ) ;

            x_start -= 1 ;

            if (x_start < 0 && y_start == y_axis_len) {
                break ;
            } ;
        } ;

        return list.toArray(new String[0]) ;
    }

    // Input: Normal word search block
    // Output: Columnized word search block, i.e., rows become columns and columns become rows
    public static String[] rowToCol(String[] word_block) {
        int row_len = word_block.length ;
        int col_len = word_block[0].length() ;

        ArrayList<String> list = new ArrayList<String>() ;

        String tmp = "" ;

        for (int i=0 ; i<col_len ; ++i)
        {
            for (int j=0 ; j<row_len ; ++j)
            {
                tmp += word_block[j].charAt(i) ;
            }

            list.add(tmp) ;

            tmp = "" ;

        }

        return list.toArray(new String[0]) ;
    }

    // Input: word search block as an ArrayList (dynamic and such)
    // Output:  inversed block, i.e., each string within the ArrayList
    //          is in reverse order, NOT the order of the Strings themselves.
    public static String[] inverseBlock(ArrayList<String> word_block) {
        ArrayList<String> list = new ArrayList<String>() ;

        for (String line : word_block) {
            list.add(inverseString(line)) ;
        }

        return list.toArray(new String[0]) ;
    }

    // Input: word search block as a String array
    // Output:  inversed block, i.e., each string within the ArrayList
    //          is in reverse order, NOT the order of the Strings themselves.
    public static String[] inverseBlock(String[] word_block) {
        ArrayList<String> list = new ArrayList<String>() ;

        for (String line : word_block) {
            list.add(inverseString(line)) ;
        }

        return list.toArray(new String[0]) ;
    }

    // Input: String
    // Output: String in reverse order, last letter being the first, etc...
    public static String inverseString(String word) {
        String tmp = "" ;

        for (int i=word.length()-1 ; i>=0 ; --i) {
            tmp += word.charAt(i) ;
        }

        return tmp ;
    }

    // Input: word search block in any format, horz, diag or normal
    // Output: coords relative to format of word search block, first number being the
    //          the array number within the String array, and second the index within the string itself
    public static int[] findWordOnBlock(String[] word_block, String word) {
        int[] pos = {0, 0};

        for (String line : word_block) {
            pos[1] = line.indexOf(word);

            if (pos[1] != -1) return pos;

            ++pos[0];
        }
        ;

        return pos;
    }

}