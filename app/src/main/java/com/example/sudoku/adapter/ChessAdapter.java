package com.example.sudoku.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sudoku.R;
import com.example.sudoku.entity.Chess;

import java.util.ArrayList;
import java.util.List;

/*自定义的适配器*/
public class ChessAdapter extends RecyclerView.Adapter<ChessAdapter.MyViewHolder>{
    //数独数据
    private List<Chess> cdata = new ArrayList<Chess>();
    //上下文
    private Context context;
    //弹框选项
    private  String options[] =new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9","清空"};
    //弹框选项的记录
    private int option;
    //重新初始化的按钮个数
    private List<Chess> reservedChesses;
    //定义监听
    private OnitemTextChangeListener myOnitemTextChangeListener;

    //构造方法
    public ChessAdapter(List<Chess> cdata, List<Chess> rsvChesses,Context context, OnitemTextChangeListener onitemTextChangeListener) {
        this.cdata = cdata;
        this.context = context;
        this.myOnitemTextChangeListener = onitemTextChangeListener;
        this.reservedChesses = rsvChesses;
    }

    @NonNull
    @Override
    //返回一个自定义的ViewHolder
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //填充布局，获取列表项布局文件
        View itemView = LayoutInflater.from(context).inflate(R.layout.boarditem,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);

        //设置View中数据变化事件
        if(myOnitemTextChangeListener!=null){
            myViewHolder.mybutton.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    myOnitemTextChangeListener.onItemTextChange(myViewHolder.mybutton,myViewHolder.getAdapterPosition());
                }
            });
        }

        return myViewHolder;
    }

    @Override
    //填充onCreateViewHolder返回的holder中的控件
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //获取数独数据
        Chess chess = cdata.get(position);
        if(chess.getValue()!='0') {
            if(reservedChesses.get(position).getValue()!='0')
                holder.mybutton.setEnabled(false);
            else holder.mybutton.setEnabled(true);
            holder.mybutton.setText(chess.getValue()+"");

        } else{
            holder.mybutton.setText("");
            holder.mybutton.setEnabled(true);
        }
    }

    @Override
    //返回数独棋子个数
    public int getItemCount() {
        return cdata.size();
    }

    public void notifyReservedChessesChanged(List<Chess> rsvChesses){
        reservedChesses = rsvChesses;
        super.notifyDataSetChanged();
    }

    //定义内部类MyviewHolder
    class MyViewHolder extends RecyclerView.ViewHolder{
        //定义对应的网格项
        private Button mybutton;
        //构造方法
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取对应的列表项
            mybutton = (Button) itemView.findViewById(R.id.cbutton);
            mybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("请选择一项")
                            .setSingleChoiceItems(options, option, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    option = which;
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (options[option] == "清空") mybutton.setText("");
                                    else mybutton.setText(options[option]);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
            });
        }
    }
}
