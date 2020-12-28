/**
 * RecyclerView with support for grid animations.
 *
 * Based on:
 * https://gist.github.com/Musenkishi/8df1ab549857756098ba
 * Credit to Freddie (Musenkishi) Lust-Hed
 *
 * ...which in turn is based on the GridView implementation of attachLayoutParameters(...):
 * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/widget/GridView.java
 *
 */
package com.example.sudoku.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridRecycleView extends RecyclerView {

    public GridRecycleView(@NonNull Context context) {
        super(context);
    }

    public GridRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //将AnimationParameters设置为Grid样式
    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params,
                                                   int index, int count){
        LayoutManager layoutManager = getLayoutManager();
        if(getAdapter()!=null&& layoutManager instanceof GridLayoutManager) {
            //获取GridLayout动画参数
            GridLayoutAnimationController.AnimationParameters animParams = (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animParams==null){
                animParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animParams;
            }
            //设置GridLayout动画参数
            animParams.index = index;
            animParams.count = count;

            int colums =  ((GridLayoutManager) layoutManager).getSpanCount();
            animParams.columnsCount=colums;
            animParams.rowsCount = count/colums;

            int invertedIndex = count - 1 - index;
            animParams.column = colums - 1 - (invertedIndex%colums);
            animParams.row = animParams.rowsCount-1- (invertedIndex/colums);
        }else {
            //为其他布局时使用默认动画参数
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }

}
