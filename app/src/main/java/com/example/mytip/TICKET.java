package com.example.mytip;

import android.util.Log;

public class TICKET {

    String title="";
    String date="";
    String place="";
    String seating="";
    public TICKET(String message) {
        char[] array_word = new char[message.length()];
        for (int i = 0; i < array_word.length; i++) { //스트링을 한글자씩 끊어 배열에 저장
            array_word[i] = message.charAt(i);
        }
        int len = array_word.length;

        boolean melon = false;
        for (int i = 0; i < len; i++) {
            if (array_word[i] != '\n') {
                title += array_word[i];
            } else {
                Log.d("date", "제목은 " + title);
                break;
            }
        }
        for (int i = 0; i < len; i++) {
            if (i < len - 6) {
                if (array_word[++i] == 'm') {
                    if (array_word[++i] == 'e') {
                        if (array_word[++i] == 'l') {
                            if (array_word[++i] == 'o') {
                                if (array_word[++i] == 'n') {
                                    melon = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        boolean ilsi2 = true;
        if (!melon) {
            for (int i = 0; i < len; i++) {
                if (array_word[i] == '일' && ilsi2) {
                    if (array_word[i + 1] == ' ')
                        i++;
                    if (array_word[++i] == '시') {
                        i = i + 2;
                        if (array_word[i] == ':')
                            i++;
                        for (int j = i; j < len; j++) {
                            if (array_word[j] != '\n') {
                                date += array_word[j];
                            } else {
                                Log.d("date", "일시는 " + date);
                                ilsi2 = false;
                                break;
                            }
                        }
                        continue;
                    }
                }
                if (array_word[i] == '장') {
                    if (array_word[i + 1] == ' ')
                        i++;
                    if (array_word[++i] == '소') {
                        i = i + 2;
                        if (array_word[i] == ':')
                            i++;
                        for (int j = i; j < len; j++) {
                            if (array_word[j] != '\n') {
                                place += array_word[j];
                            } else {
                                Log.d("date", "장소는 " + place);
                                break;
                            }
                        }
                        continue;
                    }
                }

            }
        }
        //멜론티켓
        if (melon) {
            for (int i = 0; i < len; i++) {
                if (array_word[i] == '\n') {
                    for (int j = i + 1; j < len; j++) {
                        if (array_word[j] != '\n') {
                            date += array_word[j];
                        } else {
                            for (int k = j + 1; k < len; k++) {
                                if (array_word[k] != '\n') {
                                    place += array_word[k];
                                } else
                                    break;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }

        //좌석
        String ch = "", bl = "", gu = "", ye = "", bu = "";
        String seat = "";
        for (int i = 0; i < len; i++) {
            if (array_word[i] == '층') {
                for (int j = i; j > 0; j--) {
                    if (array_word[j] == ' ' || array_word[j] == '\n')
                        break;
                    ch += array_word[j];
                }
                seating += (new StringBuffer(ch)).reverse().toString() + " ";
                continue;
            }
            if (array_word[i] == '블') {
                if (array_word[i + 1] == '럭') {
                    for (int j = i + 1; j > 0; j--) {
                        if (array_word[j] == ' ' || array_word[j] == '\n')
                            break;
                        bl += array_word[j];
                    }
                    seating += (new StringBuffer(bl)).reverse().toString() + " ";
                    continue;
                }
            }
            if (array_word[i] == '구') {
                if (array_word[i + 1] == '역') {
                    for (int j = i + 1; j > 0; j--) {
                        if (array_word[j] == ' ' || array_word[j] == '\n')
                            break;
                        gu += array_word[j];
                    }
                    seating += (new StringBuffer(gu)).reverse().toString() + " ";
                    continue;
                }
            }
            if (array_word[i] == '열') {
                for (int j = i; j > 0; j--) {
                    if (array_word[j] == ' ' || array_word[j] == '\n')
                        break;
                    ye += array_word[j];
                }
                seating += (new StringBuffer(ye)).reverse().toString() + " ";
                continue;
            }
            if (array_word[i] == '번') {
                if (array_word[i + 1] != ' ' && array_word[i + 1] != '\n')
                    continue;
                for (int j = i; j > 0; j--) {
                    if (array_word[j] == ' ' || array_word[j] == '\n')
                        break;
                    bu += array_word[j];
                }
                seating += (new StringBuffer(bu)).reverse().toString() + " ";
                continue;
            }
        }
    }
}
