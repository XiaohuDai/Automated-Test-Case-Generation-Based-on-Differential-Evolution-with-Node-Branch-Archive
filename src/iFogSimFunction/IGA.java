package iFogSimFunction;

import java.io.File;
import java.util.Date;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * 6个benchmark函数：1)transmit ;2)send ;3)processTupleArrival ;4)executeTuple ;5)checkCloudletCompletion ;6)getRsultantTuples
 * (函数序号，输入变量维度，存在路径数目，可行路径数目)
 * transmit(1,3,2,1) 100%; send(2,2,9,5)66%; processEvent(3,7,9,6)100%; executeTuple(4,7,5,3)100%; 
 * checkCloudletCompletion(5,5,6,3)100%; getResultantTuple(6,8,7,4)100%
 */

public class IGA {

	private static final int RUN = 30;
	private static final int pop_num = 50;
	private static final int fir_num = 2;
	private static final int sec_num = 24;
	private static final int K = 10;
	private static final double alpha = 0.001;
	private static final double PXOVER = 0.8 ;
    private static final double PMUTATION = 0.1 ;
    
	private static final int fun_num = 6;
	private static final int R = 8;
	private static final int PATHNUM = 7;
	private static final int NODENUM = 4;
	
	static boolean[][] visit = new boolean[NODENUM][4]; 
	//private static final int CONDNUM = 4;
	private static final double MAXFIT = 1/alpha;
	private static final int MCN = 300000;
	private static final int col = 4;
	
	/*PATH记录不同路径的分支节点走向*/
	private static String[] PATH = new String[PATHNUM];
	
	private static int[][] convergence  = new int[RUN][PATHNUM];
	
	private static int[][] x = new int[pop_num][R] ;  //当前种群
	private static int[][] mid = new int[pop_num][R] ;  //中间种群
	private static int[][] newpop = new int[pop_num][R] ;  //新种群，由精英个体、父代、子代构成
	private static int obj;
	static double start;            //运行开始时间，完成时间
	static double finish;
	static double[] runtime = new double[RUN];
	static float[] coverage = new float[RUN];
	static int[] Cycle = new int[RUN];
	static int[] case_num = new int[RUN];
	
	public static void main(String[] args){
		/*输入变量参数上下界*/
		int[] lb = new int[R];
		int[] ub = new int[R];
		
		if(fun_num==1){
			for(int i=0;i<R;i++)
			{
				lb[i] = 0;    //初始化上下界
				ub[i] = 255;
			}
		}
		if(fun_num==2){
			for(int i=0;i<R;i++)
			{
				lb[i] = -100;    //初始化上下界
				ub[i] = 100;
			}
		}
		if(fun_num==3){
			lb[0]=0;ub[0]=100;
			lb[1]=0;ub[1]=3;
			lb[2]=-1;ub[2]=10000;
			lb[3]=0;ub[3]=1;
			lb[4]=0;ub[4]=10000;
			lb[5]=0;ub[5]=9999;
			lb[6]=-1;ub[6]=100000;
		}
		if(fun_num==4){
			lb[0]=1;ub[0]=3;
			for(int i=1;i<R;i++)
			{
				lb[i] = 0;    //初始化上下界
				ub[i] = 255;
			}
		}
		if(fun_num==5){
			lb[0]=0;ub[0]=1;
			for(int i=1;i<R-1;i++)
			{
				lb[i] = 0;    //初始化上下界
				ub[i] = 255;
			}
			lb[R-1]=0;ub[R-1]=1;
		}
		if(fun_num==6){
			for(int i=0;i<6;i++)
			{
				lb[i] = 0;    //初始化上下界
				ub[i] = 255;
			}
			lb[6]=0;ub[6]=1;
			lb[7]=1;lb[7]=3;
		}
		
		switch(fun_num){
		case 1:
			PATH[0] = "0";
			PATH[1] = "1";
			break;
		case 2:
			PATH[0] = "0    ";
			PATH[1] = "100  ";
			PATH[2] = "1010 ";
			PATH[3] = "10110";
			PATH[4] = "10111";
			PATH[5] = "110  ";
			PATH[6] = "1110 ";
			PATH[7] = "11110";
			PATH[8] = "11111";
			break;
		case 3:
			PATH[0] = "0     ";
			PATH[1] = "10    ";
			PATH[2] = "110   ";
			PATH[3] = "111 00";
			PATH[4] = "111 01";
			PATH[5] = "111 1 ";
			PATH[6] = "12 0  ";
			PATH[7] = "12 1  ";
			PATH[8] = "13    ";
			break;
		case 4:
			PATH[0] = "000";
			PATH[1] = "001";
			PATH[2] = "010";
			PATH[3] = "011";
			PATH[4] = "1  ";
			break;
		case 5:
			PATH[0] = "000";
			PATH[1] = "001";
			PATH[2] = "010";
			PATH[3] = "011";
			PATH[4] = "1 0";
			PATH[5] = "1 1";
			break;
		case 6:
			PATH[0] = "0000";
			PATH[1] = "0001";
			PATH[2] = "001 ";
			PATH[3] = "0100";
			PATH[4] = "0101";
			PATH[5] = "011 ";
			PATH[6] = "1   ";
			break;
		}
		
		/*进行RUN次重复试验*/
		for(int run=0;run<RUN;run++)
		{
			//visit initialize
			for(int i=0;i<NODENUM;i++)
				for(int j=0;j<4;j++)
					visit[i][j] = false;
			
			boolean[] statu = new boolean[PATHNUM];
			int[][] solution = new int[PATHNUM][R];
			double[] fitness_x = new double[pop_num];
			double[] fitness_newpop = new double[pop_num];
			double[] p_relate = new double[pop_num];
			double[] p_relate2 = new double[pop_num];
			double[] p_accumulate = new double[pop_num];
			double[] p_accumulate2 = new double[pop_num];
			int Path;
			obj = 0 ;
			
			Date mydate = new Date();
			start = mydate.getTime();
			
			for(int i = 0 ; i < pop_num ; i++ )           //step 1:初始化
			{
				for(int j = 0 ; j < R ; j++ )
				{
					double r0 = Math.random() ;
					x[i][j] =  (int)Math.round( lb[j] + r0 * ( ub[j] - lb[j]) ) ;
				}
				case_num[run] = case_num[run] + 1;
				
				Path = pathnum(x[i] , fun_num) ;
				if( !statu[Path] )
				{
					for(int j = 0 ; j < R ; j++)
						solution[Path][j] = x[i][j];
					statu[Path] = true ;               //标记路径Path是否已找到覆盖它的用例
					convergence[run][obj] = case_num[run] + 1;
					obj++ ;                            //已覆盖的路径数
					nodeiscoverage(x[i] , fun_num) ;   //标记已被覆盖的分支
				}
				
				if(obj == PATHNUM)
					break ;	
			}
						
            Cycle[run] = 1 ;     
			
			while(case_num[run] <= MCN && obj < PATHNUM )     //循环终止条件  case_num[run] <= MCN &&
			{
				int[] indexsort = new int[pop_num];			
				indexsort = selectsort(fitness_x) ;   //适应值从高到低排序对应的序号
				
				for(int  i = 0 ; i < pop_num ; i++ )
					for(int j = 0 ; j < R ; j++ )
					    mid[i][j] = x[indexsort[i]][j] ; //数组mid[]是排好序的数组x[]     
				
				for(int i = 0 ; i < fir_num ; i++)    //精英选择，选出前fir_num个直接复制到新种群，剩下的作为父代
					for(int j = 0 ; j < R ; j++ )
					    newpop[i][j] = mid[i][j] ;
				
				for(int i = fir_num ; i < pop_num ; i++)    //把除去精英个体的剩余个体复制会数组x[][]
					for(int j = 0 ; j < R ; j++ )
					    x[i-fir_num][j] = mid[i][j] ;
				for(int i = 0 ; i < pop_num-fir_num ; i++)            //计算对应目标路径r0的适应值
					fitness_x[i] = benchmarkfunction(x[i], fun_num, -1) ;
				
				int sumfit = 0 ;
				for(int i = 0 ; i < pop_num-fir_num ; i++)
					sumfit += fitness_x[i] ;		     //计算适应值和		
				for(int i =0 ; i < pop_num-fir_num ; i++)
					p_relate[i] = fitness_x[i]/sumfit ;  //计算相关概率
				p_accumulate[0] = p_relate[0] ;
				for(int i = 1 ; i < pop_num-fir_num ; i++)
					p_accumulate[i] = p_accumulate[i-1] + p_relate[i] ;  //计算累积概率
				for(int t = 0 ; t < sec_num ; t++)   //轮盘选择,被选中的用例编号赋给s
				{
					int s = 0  ;   
					double r3 = Math.random() ;
					if(r3 <= p_accumulate[0])          
						s = 0 ;
					else 					
						for(int i = 1 ; i < pop_num-fir_num ; i++)
							if(r3 > p_accumulate[i-1] && r3 <= p_accumulate[i])
							{
								s=i ;
							    break ;
							}
					for(int j = 0 ; j < R ; j ++)
						mid[t][j] = x[s][j];                
				}//轮盘赌选择，产生父代
				
				for(int i = 0 ; i < sec_num ; i++)
					for(int j = 0 ; j < R ; j ++)
					{
					    newpop[i+fir_num][j] = mid[i][j];  //父代复制到新种群
					    x[i][j] = mid[i][j];  //父代复制到x[][]
					}
				
				int t = 0 ;
				while( t < sec_num ) 
				{	
					double r1 = Math.random() ;
    				if (r1 < PXOVER )
    					Xover(t , t+1);					
					t=t+2;
				}//crossover
				
				for(int i = 0 ; i < sec_num ; i++)
				{
    				for(int j = 0 ; j < R ; j++)
    				{
    				     double r2 = Math.random();
    				     if(r2 < PMUTATION)
    				    	 x[i][j] = (int)Math.round(Math.abs (Math.random() * (ub[j] - lb[j]) + lb[j])) ;
    				     else
    				     {
    				    	 if(x[i][j] > ub[j] || x[i][j] < lb[j]) 
    							{
    							   double r01 = Math.random() ;
    							   x[i][j] = (int)Math.round( lb[j] + r01 * ( ub[j] - lb[j]) ) ;
    							}	
    				     }
    			    }//mutation
    				
    				case_num[run] = case_num[run] + 1 ;
    				
    				Path = pathnum(x[i] , fun_num) ;
					if( !statu[Path] )
					{
						for(int j = 0 ; j < R ; j++)
							solution[Path][j] = x[i][j];
						statu[Path] = true ;           
						convergence[run][obj] = case_num[run] + 1;
						obj++ ;                            
						nodeiscoverage(x[i] , fun_num) ;   
					}
					
					if(obj == PATHNUM)
						break ;
				
				}
				for(int i = 0 ; i < sec_num ; i++)
					for(int j = 0 ; j < R ; j ++)
					    newpop[i+fir_num+sec_num][j] = x[i][j];  //子代复制到新种群
				
				for(int i = 0 ; i < pop_num ; i++)
					for(int j = 0 ; j < R ; j ++)
					    x[i][j] = newpop[i][j];  //newpop[]复制到x[][]
				for(int i = 0 ; i < pop_num ; i++)            //计算对应目标路径的适应值
					fitness_x[i] = benchmarkfunction(x[i], fun_num, -1) ;
				
				for (int i = 0 ; i < pop_num ; i++)         //把可用的用例存储起来
				{
					Path = pathnum(x[i] , fun_num) ;
					if( !statu[Path] )
					{
						for(int j = 0 ; j < R ; j++)
							solution[Path][j] = x[i][j];
						statu[Path] = true ;  
						convergence[run][obj] = case_num[run] + 1;
						obj++ ;                            
						nodeiscoverage(x[i] , fun_num) ;   
					}
					if(obj == PATHNUM)
						break ;
				}
//					if(fitness_x[i] == MAXFIT)
//					{							
//						for(int j = 0 ; j < R ; j++)
//							solution[rands][j] = x[i][j];
//						statu[rands] = true ;             //标志路径k是否已找到覆盖它的用例
//						obj++ ;                       //已覆盖的路径数
//						break ;
//					}
//										
				
															
				Cycle[run]++ ;    
			}
			
			Date mydate2 = new Date();
			finish = mydate2.getTime();
			runtime[run] = finish - start;
			System.out.println();
			System.out.println("运行时间="+runtime[run]+"ms");             //输出运行时间
			System.out.println("NO. of cycles=" + (Cycle[run]-1));        //输出Number of Cycle 
  			coverage[run] = obj*100/PATHNUM;
			System.out.println("路径覆盖率=" + coverage[run] +"%");
			System.out.println("最优解为：");
			 
			for( int k = 0; k < PATHNUM ; k++ )     //输出路径覆盖情况：覆盖路径的测试用例以及未覆盖的路径
			{
				if(statu[k])
				{
				    System.out.print("path"+k+":"); 
				    for(int j = 0; j < R ; j++ )
					     System.out.print(solution[k][j]+" ");
                    System.out.println();
				}
				else
					System.out.println("path"+k+"没被覆盖."); 
			}
			System.out.println("case_num["+run+"] = " + case_num[run] ); 	

//			//输出infection矩阵
//			System.out.println("\n"+"infection矩阵:");
//			for(int r=0;r<R;r++){
//				for(int n=0;n<NODENUM;n++)
//					System.out.print(infection[r][n]+"  ");
//				System.out.println();
//			}
			if(obj < PATHNUM) {
				 for(int n= obj;n<PATHNUM;n++) {
			    	 convergence[run][n] = 300000;
			     }
			}
		}
		
		double time_sum = 0 , time_average, coverage_sum = 0 , coverage_average , cycle_sum = 0,cycle_average,case_average ;
		int case_sum = 0 ;
		for ( int run = 0 ; run < RUN ; run++)
		{
		     time_sum = time_sum + runtime[run] ;
		     coverage_sum = coverage_sum + coverage[run] ;
		     cycle_sum = cycle_sum + (Cycle[run]-1) ;
		     case_sum = case_sum + case_num[run] ;
		}
		time_average = time_sum / RUN ;
		coverage_average = coverage_sum / RUN ;
		cycle_average = cycle_sum / RUN ;
		case_average = case_sum / RUN ;
		
//
		int[] convergence_sum = new int[PATHNUM];
		int[] convergence_average = new int[PATHNUM];
		for ( int run = 0 ; run < RUN ; run++)
		{
		     for(int n=0;n<PATHNUM;n++) {
		    	 convergence_sum[n] += convergence[run][n];
		     }
		}			
		for(int n=0;n<PATHNUM;n++) {
			convergence_average[n] = convergence_sum[n] / RUN;
	    }
		System.out.println("\n"+"收敛速度:");
		for(int n=0;n<PATHNUM;n++) {
			System.out.println(convergence_average[n]);
		}					
		System.out.println();
//		
		
		System.out.println("time_sum = " + time_sum + "ms");
		System.out.println("time_average = " + time_average + "ms");
		System.out.println("cycle_sum = " + cycle_sum );
		System.out.println("cycle_average = " + cycle_average );
		System.out.println("case_sum = " + case_sum );
		System.out.println("case_average = " + case_average );
		System.out.println("coverage_sum = " + coverage_sum + "%");
		System.out.println("coverage_average = " + coverage_average + "%");	
		System.out.println("standar = " + getStandardDevition(case_num , RUN));	
		
		int[] a = new int[RUN] ;
		for(int run = 0 ; run < RUN ; run++)
			a[run] = case_num[run] ;
		for(int run = 0 ; run < RUN ; run++)
		{
			int min = run;
			for(int k = run+1 ; k < RUN ; k++)
				if(a[min] > a[k])
					min = k ;
			if(run != min)
			{
				int tmp = a[min] ;
				a[min] = a[run] ;
				a[run] = tmp ;
			}
		}
		
		try  //将数据导出到Excel文档中
		{
			File file = new java.io.File("D:\\Desktop Files\\Postgraduate\\科研论文\\Experimental\\节点分支存档 VS RP-DE\\iFogSimFunction" + fun_num +".xls");
			Workbook book = Workbook.getWorkbook(file);
			WritableWorkbook wbook = Workbook.createWorkbook(file,book);
			WritableSheet sheet = wbook.getSheet(0);       //写入数据sheet
			
			for(int run=0;run<RUN;run++)
			{
				int q = run + 1;
				jxl.write.Number number;
				if(case_num[run] >= 300000) {
					number = new jxl.write.Number(col, q, 300000); 
				}else {
					number = new jxl.write.Number(col, q,case_num[run]); 
				}
				
				//jxl.write.Number number2 = new jxl.write.Number(col, q+RUN+10,coverage[run]); 
				sheet.addCell(number); 
				//sheet.addCell(number2);
			}
			
//			double case_ave = getAverage(case_num , RUN);
//			jxl.write.Number number1 = new jxl.write.Number(col,RUN+4,case_ave); 
//			sheet.addCell(number1);
//			double case_std = getStandardDevition(case_num , RUN);
//			jxl.write.Number number2 = new jxl.write.Number(col,RUN+5,case_std); 
//			sheet.addCell(number2);
			 		
			wbook.write(); 	
			wbook.close();
			
		}catch(Exception e)
		{
			System.out.println(e);
		}	
	}

	public static void nodeiscoverage(int[] x , int func_num)  //visit[][2]:To sign whether the 'Yes' branch or the 'No' branch of each node has been covered with test case (that we have obtained).   
	{
		if(func_num == 1)
		{
			char tuple[]= new char[R];
			for(int i=0;i<R;i++)
				tuple[i] = (char) x[i];
			
			if(tuple[0]=='O'&&tuple[1]=='l'&&tuple[2]=='d')
				visit[0][0] = true;
			else
				visit[0][1] = true;
		}
		if(func_num == 2)
		{
			int entityId = x[0];
			double delay = x[1];
			
			if(entityId<0)
			{
				visit[0][0] = true;
				visit[3][0] = true;
			}
			else
			{
				visit[0][1] = true;
				visit[3][1] = true;
			}
			if(delay<0)
				visit[1][0] = true;
			else
				visit[1][1] = true;
			if(entityId==100)
				visit[2][0] = true;
			else
				visit[2][1] = true;
			if(entityId!=1)
				visit[4][0] = true;
			else
				visit[4][1] = true;
		}
		if(func_num == 3)
		{
			int eventTime = x[0];
			int type = x[1];
			int dest = x[2];
			int state = x[3];
			int p = x[4];
			int tag = x[5];
			int src = x[6];
			
			if(eventTime<50) visit[0][0] = true;
			else visit[0][1] = true;
			
			if(type==0) visit[1][0] = true;
			else if(type==1) visit[1][1] = true;
			else if(type==2) visit[1][2] = true;
			else visit[1][3] = true;
			
			if(dest<0) visit[2][0] = true;
			else visit[2][1] = true;
			
			if(src<0) visit[3][0] = true;
			else visit[3][1] = true;
			
			if(state==1) visit[4][0] = true;
			else visit[4][1] = true;
			
			if(p==0||tag==9999) visit[5][0] = true;
			else visit[5][1] = true;
		}
		if(func_num == 4)
		{
			int Direction = x[0];
			String map1 = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			String map2 = String.valueOf((char)x[4])+String.valueOf((char)x[5])+String.valueOf((char)x[6]);
			
			if(Direction==1)
				visit[0][0] = true;
			else 
				visit[0][1] = true;
			
			if(map1.equals("001")||map1.equals("002")||map1.equals("003"))
				visit[1][0] = true;
			else
				visit[1][1] = true;
			
			if(map2.equals("004")||map2.equals("005")||map2.equals("006"))
				visit[2][0] = true;
			else
				visit[2][1] = true;
		}
		if(func_num == 5)
		{
			boolean isFinished;
			boolean cloudletCompleted;
			if(x[0]==0) isFinished=true;
			else isFinished=false;
			if(x[R-1]==0) cloudletCompleted=true;
			else cloudletCompleted=false;
			String cl = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
		
			if(isFinished)
				visit[0][0] = true;
			else visit[0][1] = true;
			
			if(cl.equals("001")||cl.equals("002")||cl.equals("003"))
				visit[1][0] = true;
			else visit[1][1] = true;
			
			if(cloudletCompleted)
				visit[2][0] = true;
			else visit[2][1] = true;
		}
		if(func_num == 6)
		{
			String edge = String.valueOf((char)x[0])+String.valueOf((char)x[1])+String.valueOf((char)x[2]); 
			String pair = String.valueOf((char)x[3])+String.valueOf((char)x[4])+String.valueOf((char)x[5]); 
			boolean canSelect;
			if(x[6]==0) canSelect = true;
			else canSelect = false;
			
			if(edge.equals("mod"))
				visit[0][0] = true;
			else visit[0][1] = true;
			
			if(pair.equals("001")||pair.equals("002")||pair.equals("003"))
				visit[1][0] = true;
			else visit[1][1] = true;
			
			if(canSelect) visit[2][0] = true;
			else visit[2][1] = true;
			
			if(x[7]==2) visit[3][0] = true;
			else visit[3][1] = true;
		}
	}
	
	public static int pathnum(int[] x , int func_num)
	{
		int path = -1;
		
		if(func_num == 1)
		{
			char tuple[]= new char[R];
			for(int i=0;i<R;i++)
				tuple[i] = (char) x[i];
			
			if(tuple[0]=='O'&&tuple[1]=='l'&&tuple[2]=='d')
				path = 0;
			else
				path = 1;
		}
		if(func_num == 2)
		{
			int entityId = x[0];
			double delay = x[1];
			
			if(entityId<0)
			{
				path = 0;
			}else{
				if(delay<0)
				{
					delay = 0;
					if(delay>=100)
						path = 1;
					else if(entityId<0)
						path = 2;
					else if(entityId!=1)
						path = 3;
					else 
						path = 4;
						
				}
				else{
					if(delay>=100)
						path = 5;
					else if(entityId<0)
						path = 6;
					else if(entityId!=1)
						path = 7;
					else 
						path = 8;
				}
			}
		}
		if(func_num == 3)
		{
			int eventTime = x[0];
			int type = x[1];
			int dest = x[2];
			int state = x[3];
			int p = x[4];
			int tag = x[5];
			int src = x[6];
			
			if(eventTime<50)
				path = 0;
			else{
				if(type==0)
					path = 1;
				else if(type==3)
					path = 8;
				else if(type==2){
					if(src<0)
						path = 6;
					else
						path = 7;
				}else{
					if(dest<0)
						path = 2;
					else if(state!=1){
						path = 5;
					}else{
						if(p==0||tag==9999)
							path = 3;
						else
							path = 4;
					}
				}
			}
		}
		if(func_num == 4)
		{
			int Direction = x[0];
			String map1 = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			String map2 = String.valueOf((char)x[4])+String.valueOf((char)x[5])+String.valueOf((char)x[6]);
			
			if(Direction==1){
				if(map1.equals("001")||map1.equals("002")||map1.equals("003")){
					if(map2.equals("004")||map2.equals("005")||map2.equals("006"))
						path = 0;
					else
						path = 1;
				}else{
					if(map2.equals("004")||map2.equals("005")||map2.equals("006"))
						path = 2;
					else
						path = 3;
				}
			}
			else path = 4;
		}
		if(func_num == 5)
		{
			boolean isFinished;
			boolean cloudletCompleted;
			if(x[0]==0) isFinished=true;
			else isFinished=false;
			if(x[R-1]==0) cloudletCompleted=true;
			else cloudletCompleted=false;
			String cl = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			
			if(isFinished)
			{
				if(cl.equals("001")||cl.equals("002")||cl.equals("003"))
					if(cloudletCompleted)
						path = 0;
					else
						path = 1;
				else
					if(cloudletCompleted)
						path = 2;
					else
						path = 3;
			}else
				if(cloudletCompleted)
					path = 4;
				else
					path = 5;
		}
		if(func_num == 6)
		{
			String edge = String.valueOf((char)x[0])+String.valueOf((char)x[1])+String.valueOf((char)x[2]); 
			String pair = String.valueOf((char)x[3])+String.valueOf((char)x[4])+String.valueOf((char)x[5]); 
			boolean canSelect;
			if(x[6]==0) canSelect = true;
			else canSelect = false;
			
			if(edge.equals("mod"))
			{
				if(pair.equals("001")||pair.equals("002")||pair.equals("003")){
					if(canSelect){
						if(x[7]==2)
							path = 0;
						else
							path = 1;
					}else
						path = 2;
				}else{
					if(canSelect){
						if(x[7]==2)
							path = 3;
						else
							path = 4;
					}else
						path = 5;
				}			
			}else
				path = 6;
		}
		
		return path;
	}
	
	public static double benchmarkfunction (int[] x , int func_num, int path_num)
	{
		double[] f = new double[NODENUM] ;  //f[k]表示节点k的Yes分支如果未被覆盖时，测试用例执行该分支的惩罚值
		double[] F = new double[NODENUM] ;  //F[k]表示节点k的No分支如果未被覆盖时，测试用例执行该分支的惩罚值
		double[] fit = new double[NODENUM] ;  //fit[k]表示测试用例经过节点k时，在该节点的适应值
		double Fitness = 0 ;    //测试用例的适应值
		
		if(func_num == 1)
		{
			char tuple[]= new char[R];
			for(int i=0;i<R;i++)
				tuple[i] = (char) x[i];
			
			double v1=0;
			
			if(tuple[0]=='O'&&tuple[1]=='l'&&tuple[2]=='d')
				v1 = 0;
			else
				v1 = Math.abs(tuple[0]-'O')+K+ Math.abs(tuple[1]-'l')+K + Math.abs(tuple[2]-'d')+K;
			f[0] = v1;
			
			if(tuple[0]!='O'||tuple[1]!='l'||tuple[2]!='d')
				v1 = 0;
			else{
				v1 = Math.min(Math.abs(tuple[0]-'O'), Math.abs(tuple[1]-'l'));
				v1 = Math.min(v1, Math.abs(tuple[2]-'d'));
			}
			F[0] = v1;
		}
		if(func_num == 2)
		{
			int entityId = x[0];
			double delay = x[1];
			
			double v1=0,v2=0;
			
			//分支节点1
			if(entityId<0) v1=0;
			else v1 = entityId+K;
			f[0] = v1;
			
			if(entityId>=0) v2=0;
			else v2 = 0-entityId+K;
			F[0] = v2;
			
			//分支节点2
			if(delay<0) {v1=0;delay=0;}
			else v1 = delay+K;
			f[1] = v1;
			
			if(delay>=0) v2=0;
			else v2 = -delay+K;
			F[1] = v2;
				
			//分支节点3
			if(delay>=100) v1=0;
			else v1 = 100-delay+K;
			f[2] = v1;
			
			if(delay<100) v2=0;
			else v2 = delay-100+K;
			F[2] = v2;
			
			//分支节点4
			if(entityId<0) v1=0;
			else v1 = entityId+K;
			f[3] = v1;
			
			if(entityId>=0) v2 = 0;
			else v2 = 0-entityId+K;
			F[3] = v2;
			
			//分支节点5
			if(entityId!=1) v1=0;
			else v1 = K;
			f[4] = v1;
			
			if(entityId==1) v2 = 0;
			else v2 = Math.abs(entityId+1)+K;
			F[4] = v2;
		}
		if(func_num == 3)
		{
			int eventTime = x[0];
			int type = x[1];
			int dest = x[2];
			int state = x[3];
			int p = x[4];
			int tag = x[5];
			int src = x[6];
			
			double v1=0;
			//分支节点0
			if(eventTime<50) v1=0;
			else v1 = eventTime-50+K;
			f[0] = v1;
			
			if(eventTime>=50) v1=0;
			else v1 = 50-eventTime+K;
			F[0] = v1;
			//分支节点1
			if(type==0) {f[1] = 0;F[1] = K;}
			else if(type==1){f[1] = 0;F[1] = K;}
			else if(type==2){f[1] = 0;F[1] = K;}
			else {f[1] = K; F[1] =0;}
			//分支节点2
			if(dest<0) v1=0;
			else v1 = dest+K;
			f[2] = v1;
			
			if(dest>=0) v1=0;
			else v1=-dest+K;
			F[2] = v1;
			//分支节点3
			if(src<0) v1=0;
			else v1=src+K;
			f[3] =0;
			
			if(src>=0) v1=0;
			else v1= -src+K;
			F[3] = 0;
			//分支节点4
			if(state==1)v1=0;
			else v1=Math.abs(state-1)+K;
			f[4]= v1;
			
			if(state==0) v1=0;
			else v1= Math.abs(state)+K;
			F[4] = v1;
			//分支节点5
			if(p==0||tag==9999)v1=0;
			else v1=Math.min(Math.abs(p-0)+K,Math.abs(tag-9999)+K);
			f[5] = 0;
			
			if(p!=0&&tag!=9999)v1=0;
			else v1= 2*K;
		}
		if(func_num == 4)
		{
			int Direction = x[0];
			String map1 = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			String map2 = String.valueOf((char)x[4])+String.valueOf((char)x[5])+String.valueOf((char)x[6]);
			
			double v1=0;
			
			//分支节点1
			if(Direction==1) v1=0;
			else v1 = Math.abs(Direction-1)+K;
			f[0] = v1;
			
			if(Direction!=1) v1=0;
			else v1 = K;
			F[0] = v1;
			
			//分支节点2
			if(map1.equals("001")||map1.equals("002")||map1.equals("003"))
				v1=0;
			else
//				v1 = Math.abs((char)x[1]-'0') + Math.abs((char)x[2]-'0') + Math.min(Math.min((char)x[3]-'1', (char)x[3]-'2'), (char)x[3]-'3');
			{
				v1 = Math.min(Math.abs((char)x[3]-'1')+K, Math.abs((char)x[3]-'2')+K);
				v1 = Math.min(v1, Math.abs((char)x[3]-'3')+K);
				v1 = Math.abs((char)x[1]-'0') + Math.abs((char)x[2]-'0') + v1;
			}
			f[1] = v1;
			
			if(!map1.equals("001")&&!map1.equals("002")&&!map1.equals("003"))
				v1 = 0;
			else
				v1 = 3*K;
			F[1] = v1;
			
			//分支节点3
			if(map2.equals("004")||map2.equals("005")||map2.equals("006"))
				v1=0;
			else
//				v1 = Math.abs((char)x[4]-'0') + Math.abs((char)x[5]-'0') + Math.min(Math.min((char)x[6]-'4', (char)x[6]-'5'), (char)x[6]-'6');
			{
				v1 = Math.min(Math.abs((char)x[6]-'4')+K, Math.abs((char)x[6]-'5')+K);
				v1 = Math.min(v1, Math.abs((char)x[6]-'6')+K);
				v1 = Math.abs((char)x[4]-'0') + Math.abs((char)x[5]-'0') + v1;
			}
			f[2] = v1;
			
			if(!map2.equals("004")&&!map2.equals("005")&&!map2.equals("006"))
				v1 = 0;
			else
				v1 = 3*K;
			F[2] = v1;
		}
		if(func_num == 5)
		{
			String cl = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			
			double v1=0;
			//分支节点1
			if(x[0]==0) v1=0;
			else v1=Math.abs(x[0])+K;
			f[0] = v1;
			
			if(x[0]==1) v1=0;
			else v1 = Math.abs(x[0]-1)+K;
			F[0] = v1;
			
			//分支节点2
			if(cl.equals("001")||cl.equals("002")||cl.equals("003"))
				v1=0;
			else
//				v1=Math.abs((char)x[1]-'0') + Math.abs((char)x[2]-'0') + Math.min(Math.min((char)x[3]-'1', (char)x[3]-'2'), (char)x[3]-'3');
			{
				v1 = Math.min(Math.abs((char)x[3]-'1')+K, Math.abs((char)x[3]-'2')+K);
				v1 = Math.min(v1, Math.abs((char)x[3]-'3')+K);
				v1 = Math.abs((char)x[1]-'0') + Math.abs((char)x[2]-'0') + v1;
			}
			f[1] = v1;
			
			if(!cl.equals("001")&&!cl.equals("002")&&!cl.equals("003"))
				v1 = 0;
			else
				v1 = 3*K;
			F[1] = v1;
			
			//分支节点3
			if(x[R-1]==0) v1=0;
			else v1=Math.abs(x[R-1])+K;
			f[2] = v1;
			
			if(x[R-1]==1) v1=0;
			else v1 = Math.abs(x[R-1]-1)+K;
			F[2] = v1;
		}
		if(func_num == 6)
		{
			String edge = String.valueOf((char)x[0])+String.valueOf((char)x[1])+String.valueOf((char)x[2]); 
			String pair = String.valueOf((char)x[3])+String.valueOf((char)x[4])+String.valueOf((char)x[5]); 
			
			double v1=0;
			//分支节点1
			if(edge.equals("mod")) v1=0;
			else v1 = Math.abs((char)x[0]-'m')+Math.abs((char)x[1]-'o')+Math.abs((char)x[2]-'d')+3*K;
			f[0] = v1;
			
			if(!edge.equals("mod")) v1=0;
			else v1 = K;
			F[0] = v1;
			
			//分支节点2
			if(pair.equals("001")||pair.equals("002")||pair.equals("003"))
				v1 = 0;
			else
//				v1 = Math.abs((char)x[3]-'0') + Math.abs((char)x[4]-'0') + Math.min(Math.min((char)x[5]-'1', (char)x[5]-'2'), (char)x[5]-'3');
			{
				v1 = Math.min(Math.abs((char)x[5]-'1')+K, Math.abs((char)x[5]-'2')+K);
				v1 = Math.min(v1, Math.abs((char)x[5]-'3')+K);
				v1 = Math.abs((char)x[3]-'0') + Math.abs((char)x[4]-'0') + v1;
			}
			f[1] = v1;
			
			if(!pair.equals("001")&&!pair.equals("002")||!pair.equals("003"))
				v1 = 0;
			else
				v1 = 3*K;
			F[1] = v1;
			//分支节点3
			if(x[6]==0) v1 = 0;
			else v1 = Math.abs(x[6])+K;
			f[2] = v1;
			
			if(x[6]==1) v1 =0;
			else v1 = Math.abs(x[6]-1)+K;
			F[2] = v1;
			//分支节点4
			if(x[7]==2) v1=0;
			else v1 = Math.abs(x[7]-2)+K;
			f[3] = v1;
			
			if(x[7]!=2) v1=0;
			else v1 = K;
			F[3] = K;
		}
		if(path_num == -1)          //没有目标路径的情况
			for(int k = 0 ; k < NODENUM ; k++)
	   		{
		   		if(visit[k][0] && visit[k][1])
		   			fit[k] = 0 ;
		   		else if(visit[k][0] && (!visit[k][1]))
		   			fit[k] = 1/(F[k] + alpha) ;
		   		else if((!visit[k][0]) && visit[k][1])
		   			fit[k] = 1/(f[k] + alpha) ;
		   		else
		   			fit[k] = 1/alpha ;
	   		}
		else{                        //存在目标路径的情况
			for(int k=0;k<NODENUM;k++){
				if(PATH[path_num].charAt(k)=='0')
					fit[k] = 1/(f[k] + alpha);
				else if(PATH[path_num].charAt(k)=='1')
					fit[k] = 1/(F[k] + alpha);
				if(PATH[path_num].charAt(k)==' ')
					fit[k] = 0;
			}
		}
		
		for(int i=0;i<NODENUM;i++)
			Fitness += fit[i];
		
		return Fitness;
	}
	
	/*返回适应值从高到低排序对应的序号*/
	  public static int[] selectsort (double[] fitness)
	  {
			  int[] sortnum = new int[fitness.length] ;
			  for(int i =0 ; i < fitness.length ; i++)
				  sortnum[i]=i;
			  int max = 0;
			  double tmp = 0;
			  int tmp2 = 0 ;
			  for(int i=0;i<fitness.length;i++)
			  {
			       max = i;
			       /**查找第 i大的数，直到记下第 i大数的位置***/
			       for(int j=i+1;j<fitness.length;j++)
			       {
			            if(fitness[max]<fitness[j]) 
			            max = j;//记下较大数位置，再次比较，直到最大
			       }
			        /***如果第 i大数的位置不在 i,则交换****/
			        if(i!=max)
			        {
					    tmp = fitness[i];
					    fitness[i] = fitness[max];
					    fitness[max] = tmp;
					    
					    tmp2 = sortnum[i];
					    sortnum[i] = sortnum[max];
					    sortnum[max] = tmp2;
			        }
			  }
			  return sortnum ;
	  }
	 
	//获取平均值
	  static double getAverage(int[] array , int num){
	      int sum = 0;
	      for(int i = 0;i < num;i++){
	          sum += array[i];
	      }
	      return (double)(sum / num);
	  }
	 
	  //标准差
	  static double getStandardDevition(int[] array , int num){
	      double sum = 0;
	      for(int i = 0;i < num;i++){
	          sum += Math.sqrt(((double)array[i] -getAverage(array, num)) * (array[i] -getAverage(array, num)));
	      }
	      return (sum / (num - 1));
	  } 
	  //返回最大值下标
	  static int getBestIndex(double[]array)
	  {
		  int index;
		  index = 0;
		  for(int i=1;i<array.length;i++)
			  if(array[i]>array[index])
				  index = i;
		  return index;
	  }
	  static int[] getIndex(int lb, int ub, int point, int step) {
			int[] index = new int[10];
			int temp = -1;
			if (point + 5 * step <= ub && point - 4 * step >= lb) {
				for (int i = 0; i < 10; i++)
					index[i] = step * (i - 4) + point;
			} else {
				for (int i = 1; i < 10; i++)
					if (point - i * step < lb) {
						temp = i;
						break;
					}
				if (temp == -1) {
					for (int i = 0; i < 10; i++)
						index[i] = point - step * (9 - i);
				} else {
					for (int i = 0; i < 10; i++)
						index[i] = point + step * (-temp + i + 1);
				}
			}

			return index;
		}
	  static void Xover (int one , int two)
	  {
	  	int k ;
	  	int point ;
	  	 
	  	if(R >1)
	  	{
	  		if(R == 2)
	  			point = 1 ;
	  		else
	  			point = (int) (( Math.random() % (R - 1) ) +1) ;
	  		
	  		for(k = 0 ; k < point ; k++)
	  		{
	  			int temp ;
	  			temp = x[one][k] ;
	  			x[one][k] = x[two][k] ;
	  			x[two][k] = temp ;
	  		}       		
	  	}
	  }
}
