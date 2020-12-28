package com.example.sudoku.entity;

import java.util.Vector;

public class DLXSolver {		//基于Dancing Links X算法的数独求解器

    /* 常量 */
    final int N=81;							//数独大小为9x9
    final int kMaxNodes=1+81*4+9*9*9*4;		//结点数最大值
    final int kMaxColumns=400;				//表头结点数最大值
    final int kRow=100,kCol=200,kBox=300;	//行,列,宫 结点起始位置

    private class DLXNode{		//Dancing Links X算法中的结点
        int name;				//记录该结点所在列的表头结点在columns数组中的下标
        int size;				//记录这一列一共有多少结点,一般供表头结点使用
        DLXNode left;			//指向左边的结点
        DLXNode right;			//指向右边的结点
        DLXNode up;				//指向上面的结点
        DLXNode down;			//指向下面的结点
        DLXNode col;			//指向列表头结点
    }

    private DLXNode root;			//整个表的表头结点
    private int []inout;			//输入的数独
    private DLXNode []columns;		//记录每一列的表头结点
    private Vector<DLXNode>stack;	//用于存储选择的列
    private DLXNode []nodes;		//整个数独问题中生成的结点
    private int curNode;			//指示器,表示nodes数组中已经分配了的结点数目

    public DLXSolver() {}			//无参构造函数

    public void setDLXSolver(String s) {	//初始化
        curNode=0;							//nodes数组从头开始分配
        columns=new DLXNode[kMaxColumns];	//初始化columns数组
        stack=new Vector<>(100);			//初始化stack,容量为100
        nodes=new DLXNode[kMaxNodes];		//初始化nodes数组
        inout=new int[N];					//初始化inout数组
        for(int i=0;i<N;++i) {				//将数独字符串转换成整数并存放在数组中
            char c=s.charAt(i);
            inout[i]=Character.getNumericValue(c);
        }

        /*构建整个表的表头*/
        root=newColumn(0);					//初始化root
        root.left=root.right=root;

        /*构建整个问题矩阵的列表头结点*/
        boolean rows[][]=new boolean[9][10];	//0-9一共10个数字
        boolean cols[][]=new boolean[9][10];
        boolean boxes[][]=new boolean[9][10];

        for(int i=0;i<N;++i) {
            int row=i/9;			//获得第i个元素所在的行
            int col=i%9;			//获得第i个元素所在的列
            int box=row/3*3+col/3;	//获得第i个元素所在的宫
            int val=inout[i];		//获得第i个元素的值
            rows[row][val]=true;	//在第row行中val已被填写
            cols[col][val]=true;	//在第col列中val已被填写
            boxes[box][val]=true;	//在第box宫中val已被填写
        }

        //9x9的数独中每个格子是否被填写
        for(int i=0;i<N;++i)		//初始化问题矩阵1-81列的列表头结点
            if(inout[i]==0)			//第i格需要填写,不需要填写的各不纳入考虑
                appendColumn(i);

        //数独规则映射到精确覆盖问题中:行,列,宫各对应问题矩阵中的81列
        for(int i=0;i<9;++i)
            for(int v=1;v<10;++v) {	//已填入数字的各自不予考虑
                if(!rows[i][v])		//第i行可以填入v
                    appendColumn(getRowCol(i, v));
                if(!cols[i][v])		//第i列可以填入v
                    appendColumn(getColCol(i, v));
                if(!boxes[i][v])	//第i宫可以填入v
                    appendColumn(getBoxCol(i, v));
            }

        /*构建整个问题矩阵每行中的结点*/
        for(int i=0;i<N;++i) {
            if(inout[i]==0) {	//对未填写数字的格子构建问题矩阵的行
                int row=i/9;			//获得第i个元素所在的行
                int col=i%9;			//获得第i个元素所在的列
                int box=row/3*3+col/3;	//获得第i个元素所在的宫
                for(int v=1;v<10;++v)
                    if(!(rows[row][v]||cols[col][v]||boxes[box][v])) {
                        //第row行,第col列,第box宫中都可以填入v
                        /*四个结点对应着数独问题的四个约束条件*/
                        DLXNode n0=newRow(i);					//第i个空填入了数字v
                        DLXNode nr=newRow(getRowCol(row, v));	//在第row行填入了数字v
                        DLXNode nc=newRow(getColCol(col, v));	//在第col列填入了数字v
                        DLXNode nb=newRow(getBoxCol(box, v));	//在第box宫填入了数字v
                        putLeft(n0, nr);	//将nr,nc,nb添加到n0所在的行
                        putLeft(n0, nc);
                        putLeft(n0, nb);
                    }
            }
        }
    }

    public String runDLXSolver() {
        String tmpS="";
        //solve();
        if(!solve()) return tmpS;
        StringBuffer sb=new StringBuffer(tmpS);
        for(int i=0;i<81;++i)
            sb.append(inout[i]);
        return sb.toString();
    }

    public void clearDLXSolver(){
        inout=null;
        columns=null;
        nodes=null;
        stack.clear();
    }

    private void putLeft(DLXNode old,DLXNode nnew) {	//把nnew结点放到old结点的左边
        nnew.left=old.left;		//更新nnew结点的左右结点
        nnew.right=old;
        old.left.right=nnew;	//更新old结点的左右结点
        old.left=nnew;
    }
    private DLXNode newColumn(int n) {	//构建一个新的列结点
        DLXNode c=new DLXNode();	//创建一个结点
        c.left=c;
        c.right=c;
        c.up=c;
        c.down=c;
        c.col=c;
        c.name=n;					//n为该结点所在的列,0为root
        nodes[curNode++]=c;			//将结点记录到nodes数组中
        return c;
    }
    private void appendColumn(int n) {
        DLXNode c=newColumn(n);		//创建一个列结点
        putLeft(root, c);			//将列结点添加到root所在的行
        columns[n]=c;				//记录下表头结点
    }
    private void putUp(DLXNode old,DLXNode nnew) {	//把nnew结点放到old结点的上面,old结点为列表头结点
        nnew.up=old.up;		//更新nnew结点的上下结点
        nnew.down=old;
        old.up.down=nnew;	//更新old结点的上下结点
        old.up=nnew;
        old.size++;			//这一列新增加了一个结点
        nnew.col=old;		//指向列表头结点
    }
    private DLXNode newRow(int col) {	//构建问题矩阵中的行
        DLXNode r=new DLXNode();		//创建一个新的结点
        r.left=r;
        r.right=r;
        r.up=r;
        r.down=r;
        r.name=col;
        r.col=columns[col];				//指向结点所在列的列表头结点
        nodes[curNode++]=r;				//将结点记录到nodes数组中
        putUp(r.col, r);				//将结点r加入到对应的列表头结点所在的列
        return r;
    }
    private int getRowCol(int row,int val) {	//得到第row行的val对应列结点的下标
        return kRow+row*10+val;
    }
    private int getColCol(int col,int val) {	//得到第col列的val对应列结点的下标
        return kCol+col*10+val;
    }
    private int getBoxCol(int box,int val) {	//得到第box宫的val对应列结点的下标
        return kBox+box*10+val;
    }

    private DLXNode getMinColumn() {	//得到结点数最少的列
        DLXNode c=root.right;			//取第一个表头结点
        int minSize=c.size;				//最小结点数初始化
        if(minSize>1)					//多于1个时开始查找
            for(DLXNode cc=c.right;cc!=root;cc=cc.right)
                if(minSize>cc.size) {	//找到结点数较少的列
                    c=cc;				//更新拥有最少结点的列表头节点
                    minSize=cc.size;	//更新最小结点数
                    if(minSize<=1) break;	//结点数仅一个时肯定为最少的,结束循环
                }
        return c;
    }
    private void cover(DLXNode c) {		//将这一列及相关的行在问题矩阵中隐去
        c.right.left=c.left;		//隐去列表头结点
        c.left.right=c.right;
        for(DLXNode row=c.down;row!=c;row=row.down)	//遍历这一列上的所有结点
            for(DLXNode node=row.right;node!=row;node=node.right) {	//遍历结点所在行的所有结点
                node.down.up=node.up;		//将该结点隐去
                node.up.down=node.down;
                node.col.size--;			//该结点所在列的结点数减1
            }
    }
    private void uncover(DLXNode c) {	//恢复这一列及相关的行到问题矩阵中
        for(DLXNode row=c.up;row!=c;row=row.up)
            for(DLXNode node=row.left;node!=row;node=node.left) {
                node.col.size++;
                node.down.up=node;
                node.up.down=node;
            }
        c.right.left=c;
        c.left.right=c;
    }
    private boolean solve() {
        if(root.left==root) {		//递归出口,所有的列都已被覆盖
            for(int i=0;i<stack.size();++i) {
                DLXNode n=stack.elementAt(i);	//取出递归栈中的结点
                int cell=-1;
                int val=-1;
                while(cell==-1||val==-1) {
                    //在同一行中只有四个结点n0,nr,nc,nb;通过遍历这四个结点可以确定怎么填数字
                    //n0中记录的name是要填入的数据在inout数组中的下标
                    //在nr,nc,nb中任取一个即可知道在inout[cell]中要填写什么数字
                    if(n.name<100) cell=n.name;
                    else val=n.name%10;
                    n=n.right;
                }
                inout[cell]=val;
            }
            return true;
        }

        DLXNode col=getMinColumn();		//获取结点数最少的列,减少计算量
        cover(col);			//选中问题矩阵中的col列
        for(DLXNode row=col.down;row!=col;row=row.down) {
            stack.add(row);	//这一列上的结点入栈
            for(DLXNode node=row.right;node!=row;node=node.right)
                cover(node.col);		//选中col列中的结点所在行的其他结点所在的列
            if(solve()) return true;	//递归求解
            stack.remove(stack.lastElement());	//回溯,结点出栈
            for(DLXNode node=row.left;node!=row;node=node.left)
                uncover(node.col);		//恢复被选中的列
        }
        uncover(col);		//恢复选中的列
        return false;		//求解失败
    }
}
