package com.example.mytip;

import android.util.Log;

public class MOVIE {
    String title="";
    String date="";
    String place="";
    String seating="";
    public MOVIE(String message) {
        char[] array_word = new char[message.length()];
        for (int i = 0; i < array_word.length; i++) { //스트링을 한글자씩 끊어 배열에 저장
            array_word[i] = message.charAt(i);
        }
        int len = array_word.length-5;

        for (int i = 0; i < len; i++) {
            if (array_word[i] == '관') {
                if (array_word[++i] == '람') {
                    if (array_word[++i] == '가') {
                        if (array_word[++i] == '\n') {
                            for (int j = i+1; j < len; j++) {
                                if (array_word[j] == '\n') {
                                    i = j;
                                    break;
                                }
                                title += array_word[j];
                            }
                            for (int j = i+1; j < len; j++) {
                                if (array_word[j] == '\n') {
                                    i = j;
                                    break;
                                }
                            }
                            for (int j = i+1; j < len; j++) {
                                if (array_word[j] == '\n') {
                                    i = j;
                                    break;
                                }
                                date+=array_word[j];
                            }
                            for (int j = i+1; j < len; j++) {
                                if (array_word[j] == '\n') {
                                    i = j;
                                    break;
                                }
                                seating+=array_word[j];
                            }
                        }
                    }
                }
            }
            if(array_word[i] == 'C'){
                if(array_word[++i] == 'G'){
                    if(array_word[++i] == 'V'){
                        if(array_word[++i] == ' '){
                            place="CGV ";
                            for(int j=i+1;j<len;j++){
                                if(array_word[j]==' ' || array_word[j]=='\n') {
                                    i = j;
                                    break;
                                }
                                place+=array_word[j];
                            }
                        }
                    }
                }
            }
        }
    }
}
