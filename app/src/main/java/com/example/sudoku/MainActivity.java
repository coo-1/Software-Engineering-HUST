package com.example.sudoku;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sudoku.adapter.ChessAdapter;
import com.example.sudoku.adapter.OnitemTextChangeListener;
import com.example.sudoku.entity.Chess;
import com.example.sudoku.entity.DLXSolver;
import com.example.sudoku.entity.Sudoku;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //创建数独
    Sudoku sudoku = new Sudoku();
    //创建求解器
    DLXSolver solver = new DLXSolver();

    //创建RecyclerView控件
    private RecyclerView myRecyclerView;
    //定义一个适配器
    private ChessAdapter myChessAdapter;
    //定义数组，存储数独数据
    private String cdata;
    //定义棋子列表,作为数据源
    private List<Chess> chesses = new ArrayList<Chess>();
    //定义reservedChesses
    private List<Chess> reservedChesses = new ArrayList<Chess>();

    //创建计时器
    private Chronometer myChronometer;

    //创建难度按钮
    private Button myBtnEasy,myBtnNormal,myBtnHard;
    //定义一个难度指示器,0代表Easy,1代表Normal,2代表Hard
    private int dltIndicator;

    //创建提示和提交按钮
    private Button myBtnHint,myBtnSubmit,myBtnRestart;

    //创建动画效果
    private Vibrator mVibrator;
    private TranslateAnimation alphaAnimation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //数独
        //设置默认难度（Easy）
        dltIndicator = getResources().getInteger(R.integer.difficulty_defalut);
        //获取新数独布局，初始化棋盘数据
        sudoku.setSudoku(dltIndicator+1);
        sudoku.runSudoku();
        cdata = sudoku.getIncomBoard();
        reservedChesses = Chess.deepcopy(initDataChesses());

        //棋盘
        //1.获取RecyclerView
        myRecyclerView = findViewById(R.id.board);
        //2.设置RecyclerView布局管理器
        myRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,9,GridLayoutManager.VERTICAL,false));
        //3.初始化数据适配器
        myChessAdapter = new ChessAdapter(chesses, reservedChesses ,MainActivity.this, new OnitemTextChangeListener() {
            @Override
            //设置数据监听器
            public void onItemTextChange(Button button, int position) {
                String number = button.getText().toString();
                if(button.getText().length()!=0) {
                    chesses.get(position).setValue(number.charAt(0));
                }else{
                    chesses.get(position).setValue('0');
                }
            }
        });
        //4.设置动画
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //5.设置适配器
        myRecyclerView.setAdapter(myChessAdapter);

        //动画效果
        alphaAnimation2 = new TranslateAnimation(-30, 0, 0, 0);
        alphaAnimation2.setDuration(40);
        alphaAnimation2.setRepeatCount(10);
        alphaAnimation2.setRepeatMode(Animation.REVERSE);
        mVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        //难度按钮
        //1.获取难度按钮
        myBtnEasy = findViewById(R.id.btn_easy);
        myBtnNormal = findViewById(R.id.btn_normal);
        myBtnHard = findViewById(R.id.btn_hard);
        //2.设置监听器
        myBtnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnTopButtonClick(0);
            }
        });
        myBtnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnTopButtonClick(1);
            }
        });
        myBtnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnTopButtonClick(2);
            }
        });
        //3.默认难度按钮设置为Activated
        setTopButtonActivatedState(dltIndicator,true);

        //功能按钮
        //1.获取功能按钮
        myBtnRestart = findViewById(R.id.btn_restart);
        myBtnHint = findViewById(R.id.btn_hint);
        myBtnSubmit = findViewById(R.id.btn_submit);
        //2.设置监听事件
        myBtnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnTopButtonClick(dltIndicator);
            }
        });
        myBtnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expectedAnswer = sudoku.getFullBoard();
                //填空
                for(int i=0;i<chesses.size();i++){
                    if(chesses.get(i).getValue()=='0'){
                        chesses.get(i).setValue(expectedAnswer.charAt(i));
                        reservedChesses.get(i).setValue(expectedAnswer.charAt(i));
                        break;
                    }
                }

                //提醒Adapter更新
                myChessAdapter.notifyReservedChessesChanged(reservedChesses);
            }
        });
        myBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expectedAnswer = sudoku.getFullBoard();
                String userAnswer = "";
                for(int i=0;i<chesses.size();i++){
                    userAnswer = userAnswer + chesses.get(i).getValue();
                }
                boolean result = true;
                //判断用户答案的正确性
                if(!userAnswer.equals(expectedAnswer))
                    result = false;

                if(!result){
                    solver.setDLXSolver(userAnswer);
                    if(solver.runDLXSolver().equals(userAnswer)) {
                        result = true;
                        solver.clearDLXSolver();
                    }
                }

                //返回结果
                if(result) {
                    //Toast.makeText(MainActivity.this, "答案正确！", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("congratulations!")

                            .setIcon(R.drawable.i2)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    builder1.create().show();
                }else {
                    Toast.makeText(MainActivity.this, "答案错误！", Toast.LENGTH_SHORT).show();
                    myRecyclerView.startAnimation(alphaAnimation2);
                    mVibrator.vibrate(new long[]{100,100,100,100},-1);
                }
            }
        });

        //获取计时器
        myChronometer = findViewById(R.id.chronometer);
        //设置计时器
        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.start();

    }


    private List<Chess> initDataChesses(){
        if(chesses.size()==0) {
            for (int i = 0; i < cdata.length(); i++) {
                Chess chess = new Chess(cdata.charAt(i));
                chesses.add(chess);
            }
        }
        else{
            for (int i = 0; i < cdata.length(); i++) {
                chesses.get(i).setValue(cdata.charAt(i));
            }
        }
        return chesses;
    }

    private void OnTopButtonClick(int dlt){
        setTopButtonActivatedState(dltIndicator,false);
        setTopButtonActivatedState(dlt,true);
        dltIndicator = dlt;

        //获取新数独布局，重置棋盘数据
        sudoku.setSudoku(dltIndicator+1);
        sudoku.runSudoku();
        cdata = sudoku.getIncomBoard();
        reservedChesses = Chess.deepcopy(initDataChesses());
        myChessAdapter.notifyReservedChessesChanged(reservedChesses);//提醒Adapter布局变更

        //重设计时器
        myChronometer.stop();
        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.start();
    }

    private void setTopButtonActivatedState(int dlt,boolean b){
        switch (dlt){
            case 0:
                myBtnEasy.setActivated(b);
                break;
            case 1:
                myBtnNormal.setActivated(b);
                break;
            case 2:
                myBtnHard.setActivated(b);
                break;
        }
    }


}