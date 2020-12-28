package com.example.sudoku.entity;

import java.util.Arrays;

public class Sudoku {			//创建数独棋盘
    private int dificulty;		//难度,对应全局挖空个数
    private int []sudoku;		//数独数组,基于此转换得到String
    private int []sequence;		//序列数组,记录生成的随机序列
    private String board;		//完整棋盘
    private String holes;		//挖空后的棋盘

    public Sudoku() {			//无参构造函数
        sequence=new int[81];
        sudoku=new int[81];
    }

    public void setSudoku(int dif) { dificulty=dif; }	//初始化数独难度

    public void runSudoku() {		//创建棋盘
        while(true) {				//检验生成的棋盘唯一性
            createFullBoard();		//完整棋盘
            createIncomBoard();		//挖空棋盘
            DLXSolver solver=new DLXSolver();
            solver.setDLXSolver(holes);
            if(board.equals(solver.runDLXSolver())){
                solver.clearDLXSolver();
                break;
            }
            solver.clearDLXSolver();
        }

    }

    public String getFullBoard() {	return board; }		//返回完整棋盘

    public String getIncomBoard() { return holes; }	//返回挖空棋盘

    public void printFullBoard() {	//打印完整棋盘
        System.out.println("Full Board:");
        for(int i=0;i<9;++i) {
            if(i%3==0) System.out.println("+-------+-------+-------+");
            System.out.print("| ");
            for(int j=0;j<9;++j) {
                char c=board.charAt(i*9+j);
                System.out.print(c);
                if(j%3==2) System.out.print(" | ");
                else System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("+-------+-------+-------+");
    }

    public void printIncomBoard() {	//打印挖空棋盘
        System.out.println("Incompleted Board:");
        for(int i=0;i<9;++i) {
            if(i%3==0) System.out.println("+-------+-------+-------+");
            System.out.print("| ");
            for(int j=0;j<9;++j) {
                char c=holes.charAt(i*9+j);
                if(c=='0') System.out.print(" ");
                else System.out.print(c);
                if(j%3==2) System.out.print(" | ");
                else System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("+-------+-------+-------+");
    }

    private void createRandomSeq() {	//生成随机序列
        for(int i=0;i<81;++i) sequence[i]=i;
        for(int i=1;i<=81;++i) {
            long randomNum=System.currentTimeMillis();
            int j=(int)(randomNum%i);
            int tmp=sequence[j];
            sequence[j]=sequence[i-1];
            sequence[i-1]=tmp;
        }
    }

    private void createFullBoard() {	//创建完整棋盘
        createRandomSeq();
        long randomNum=System.currentTimeMillis();
        int start=(int)(randomNum%81);
        int count=0;
        while(count<9) {	//在81个空格中随机选取9个格子依次填入1-9
            sudoku[sequence[start]]=++count;
            start=(start+1)%81;
        }
        //将棋盘信息数组转换为字符串
        String input=Arrays.toString(sudoku).replaceAll("[\\[\\]\\,\\s]", "");
        DLXSolver solver=new DLXSolver();	//使用数独求解器求解
        solver.setDLXSolver(input);

        board=solver.runDLXSolver();
        holes=board;

        for(int i=0;i<81;++i) {		//将完整棋盘转换为数组信息
            char c=board.charAt(i);
            sudoku[i]=Character.getNumericValue(c);
        }
    }

    private void createIncomBoard() {
        createRandomSeq();
        long randomNum=System.currentTimeMillis();
        int start=(int)(randomNum%81);
        int count=0;
        while(count<dificulty*10+20) {	//根据难度挖去对应数量的空格
            sudoku[sequence[start]]=0;
            ++count;
            start=(start+1)%81;
        }
        //将挖空后的棋盘信息数组转换为字符串
        holes=Arrays.toString(sudoku).replaceAll("[\\[\\]\\,\\s]", "");
        for (int i = 0; i < 81; i++)	//将棋盘信息清空
            sudoku[i]=0;
    }
}
