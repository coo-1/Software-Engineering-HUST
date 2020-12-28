package com.example.sudoku.entity;

/*数独棋子实体类
 */

import java.util.ArrayList;
import java.util.List;

public class Chess {
    char value;

    public Chess() {
    }

    public Chess(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    static public List<Chess> deepcopy(List<Chess> chesses){
        List<Chess> newChesses =  new ArrayList<Chess>();
        for(int i=0;i<chesses.size();i++){
            newChesses.add(new Chess(chesses.get(i).value));
        }
        return newChesses;
    }
}
