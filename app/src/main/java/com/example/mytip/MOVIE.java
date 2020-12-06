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
                        int k=i;
                        if (array_word[++i] == '\n') {
                            if(array_word[++i]=='2') {
                                place="LOTTE CINEMA";
                                for(int j=k ;j>0;j--) {
                                    if (array_word[j] == '\n') {
                                        for (int l = j-1 ; l > 0; l--) {
                                            if (array_word[l] == '\n') {
                                                if ('0' <= array_word[--l] && array_word[l] <= '9') {
                                                    break;
                                                }else {
                                                    for (int m = l; m >0; m--) {
                                                        if (array_word[m] == '\n') {
                                                            l = m+1;
                                                            break;
                                                        }
                                                        else
                                                            title += array_word[m];
                                                    }
                                                }
                                            }else
                                                title += array_word[l];
                                        }
                                        break;
                                    }
                                }
                                title = (new StringBuffer(title)).reverse().toString();
                                for (int j = i; j < len; j++) {
                                    if (array_word[j] == '\n') {
                                        i = j;
                                        break;
                                    }
                                    date += array_word[j];
                                }
                                for (int j = i+1; j < len; j++) {
                                    if (array_word[j] == '\n') {
                                        i = j;
                                        break;
                                    }
                                    seating+=array_word[j];
                                }
                            }
                            else {
                                place="CGV";
                                for (int j = k + 2; j < len; j++) {
                                    if (array_word[j] == '\n') {
                                        k = j;
                                        break;
                                    }
                                    title += array_word[j];
                                }
                                for (int j = k + 1; j < len; j++) {
                                    if (array_word[j] == '\n') {
                                        k = j;
                                        break;
                                    }
                                }
                                for (int j = k + 1; j < len; j++) {
                                    if (array_word[j] == '\n') {
                                        k = j;
                                        break;
                                    }
                                    date += array_word[j];
                                }
                                for (int j = k + 1; j < len; j++) {
                                    if (array_word[j] == '\n') {
                                        break;
                                    }
                                    seating += array_word[j];
                                }
                            }
                        }
                    }
                }
            }

//            if(array_word[i] == 'C'){
//                if(array_word[++i] == 'G'){
//                    if(array_word[++i] == 'V'){
//                        if(array_word[++i] == ' '){
//                            place="CGV ";
//                            for(int j=i+1;j<len;j++){
//                                if(array_word[j]==' ' || array_word[j]=='\n') {
//                                    i = j;
//                                    break;
//                                }
//                                place+=array_word[j];
//                            }
//                        }
//                    }
//                }
//            }
        }


        //LOTTE
//        for (int i = 0; i < len; i++) {
//            if (array_word[i] == '관') {
//                if (array_word[++i] == '람') {
//                    if (array_word[++i] == '가') {
//                        for(int j=i ;j>0;j--) {
//                            if (array_word[j] == '\n') {
//                                for (int k = j - 1; k > 0; k--) {
//                                    if (array_word[k] == '\n')
//                                        break;
//                                    else
//                                        title += array_word[k];
//                                }
//                                break;
//                            }
//                        }
//                        title = (new StringBuffer(title)).reverse().toString();
//                        if (array_word[++i] == '\n') {
//                            for (int j = i+1; j < len; j++) {
//                                if (array_word[j] == '\n') {
//                                    i = j;
//                                    break;
//                                }
//                                date += array_word[j];
//                            }
//                            for (int j = i+1; j < len; j++) {
//                                if (array_word[j] == '\n') {
//                                    i = j;
//                                    break;
//                                }
//                                seating+=array_word[j];
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
    }
}
