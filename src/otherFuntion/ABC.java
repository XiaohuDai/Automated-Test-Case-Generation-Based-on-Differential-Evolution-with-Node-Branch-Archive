package otherFuntion;

import java.io.File;
import java.util.Date;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * 1 TRIANGLE		(1,3,4,4)
 * 2 FACTORIAL		(2,1,2,1)
 * 3 BUBBLE SORT	(3,10,2,1)
 * 4 GCD			(4,2,4,3)
 * 5 MIDDLE			(5,3,4,3)
 * 6 TOMORROW		(6,4,10,5)
 * 7 COMISSION		(7,3,3,2)
 * 8 PREMIUN		(8,2,11,10)
 */

public class ABC {

	private static final int RUN = 30;    //运行次数
	private static final int limit = 2;
	private static final int MIN = 0;
	private static final int MAX = 1;
	private static final int bee_num = 50;  // 侦查蜂数目
	private static final int pop_num = 50;
	/*适应值计算参数*/
	private static final int K = 10;               
	private static final double alpha = 0.001;   
	
	private static final int fun_num = 7;
	private static final int R = 3;
	private static final int PATHNUM = 3;
	private static final int NODENUM = 2;
	
	private static boolean[][] visit = new boolean[NODENUM][4];
	private static final int MCN = 300000;  //最大迭代次数
	private static final int col = 7;
			
	/* infection是一个n*m的矩阵C
	 * 矩阵C负责记录测试用例的第i个维度，对节点j的影响次数*/
	private static int[][]infection = new int[R][NODENUM];
	/* record负责记录种群每个个体（测试用例）对应的节点覆盖情况*/
	private static int[] record = new int[pop_num];
	/*PATH记录不同路径的分支节点走向*/
	private static String[] PATH = new String[PATHNUM];
	
	private static int[][] convergence  = new int[RUN][PATHNUM];

	//	static int[] visit ;
	private static int obj;
 	static double[] runtime = new double[RUN];
 	static int[] case_num = new int[RUN];
	static double start;            //运行开始时间，完成时间
	static double finish;
	static float[] coverage = new float[RUN];
	static int[] Cycle = new int[RUN];
	
	public static void main(String[] args){
		/*输入变量参数上下界*/
		int[] lb = new int[R];
		int[] ub = new int[R];
		
		if( (fun_num == 1) || (fun_num == 2) || (fun_num == 3) || (fun_num == 4) || (fun_num == 5) || (fun_num == 7))
			for(int j = 0 ; j < R ; j++ )
		    {
			    lb[j] = 1 ;
			    ub[j] = 100000;
		    }
		else if(fun_num == 6)
		{
			lb[0] = 1 ;
			ub[0] = 7 ;
			lb[1] = 1900;
			ub[1] = 2000 ;
			lb[2] = 1 ;
			ub[2] = 12 ;
			lb[3] = 1 ;
			ub[3] = 31;
		}
		else if(fun_num == 8)
		{
			lb[0] = 1 ;
			ub[0] = 100 ;
			lb[1] = 1 ;
			ub[1] = 12 ;
		}
		
		switch(fun_num){
		case 1:
			PATH[0] = "0011";
			PATH[1] = "0101";
			PATH[2] = "0110";
			PATH[3] = "1   ";
			break;
		case 2:
			PATH[0] = "0";
			PATH[1] = "1  ";
			break;
		case 3:
			PATH[0] = "0";
			PATH[1] = "1";
			break;
		case 4:
			PATH[0] = "0  ";
			PATH[1] = "10 ";
			PATH[2] = "110";
			PATH[3] = "111";
			break;
		case 5:
			PATH[0] = "0  ";
			PATH[1] = "10 ";
			PATH[2] = "110";
			PATH[3] = "111";
			break;
		case 6:
			PATH[0] = "00   ";
			PATH[1] = "0100 ";
			PATH[2] = "0111 ";
			PATH[3] = "011 0";
			PATH[4] = "011 1";
			PATH[5] = "10   ";
			PATH[6] = "1100 ";
			PATH[7] = "1111 ";
			PATH[8] = "111 0";
			PATH[9] = "111 1";
			break;
		case 7:
			PATH[0] = "01";
			PATH[1] = "10";
			PATH[2] = "11";
			break;
		case 8:
			PATH[0] = "00         ";
			PATH[1] = "01         ";
			PATH[2] = "1 00       ";
			PATH[3] = "1 01       ";
			PATH[4] = "1 1 00     ";
			PATH[5] = "1 1 01     ";
			PATH[6] = "1 1 1 00   ";
			PATH[7] = "1 1 1 01   ";
			PATH[8] = "1 1 1 1 00 ";
			PATH[9] = "1 1 1 1 01 ";
			PATH[10] = "1 1 1 1 1  ";
			break;	
		}
		
		/*进行RUN次重复试验*/
		for(int run=0;run<RUN;run++)
		{
			int[][] x = new int[bee_num][R];
			int[][] v = new int[bee_num][R];
			double[] fitness_x = new double[bee_num];
			double[] fitness_v = new double[bee_num];
			int path  ;
		
			obj = 0 ;
			int[] rep = new int[bee_num] ;
			boolean[] statu = new boolean[PATHNUM];
			int[][] solution = new int[PATHNUM][R];
			double[] p_relate = new double[bee_num];
			double[] p_accumulate = new double[bee_num];

			Date mydate = new Date();
			start= mydate.getTime();       //开始计时
			
			for(int i = 0 ; i < bee_num ; i++ )           //step 1：随机初始化种群
			{
				for(int j = 0 ; j < R ; j++ )
				{					
					double r0 = Math.random() ;
					
					x[i][j] =  (int)Math.round( lb[j] + r0 * ( ub[j] - lb[j]) ) ;				
				}
				case_num[run] = case_num[run] + 1 ;
				
	            path = pathnum (x[i] ,  fun_num) ;   //存储有效用例
				if(statu[path] == false)
				{
					for(int j = 0 ;  j < R ; j++)
						solution[path][j] = x[i][j];
					statu[path] = true ;
					convergence[run][obj] = case_num[run] + 1;
					obj++ ;
				}
				if(obj == PATHNUM)
					break;
			}						
			
			Cycle[run] = 1 ;     //step 3
			
			while(  obj < PATHNUM &&case_num[run]<=MCN)     //step 4 case_num[run] <= MCN &&  
			{				
//				System.out.println(rands);
				for(int i = 0 ; i < bee_num ; i++ )     
				{
					for (int j = 0 ; j < R ; j++ )		//step 5 :生成新的测试用例 
					{
						int k = (j + 1) % R ;
						double r1 = Math.random() * 2 - 1;
						v[i][j] =(int) Math.round(Math.abs (x[i][j] +  r1 * (x[i][j]-x[i][k]) ) ) ;
						
						if(v[i][j] > ub[j] || v[i][j] < lb[j]) 
						{
						   double r01 = Math.random() ;
						   v[i][j] = (int)Math.round( lb[j] + r01 * ( ub[j] - lb[j]) ) ;
						}						
					}
					case_num[run] = case_num[run] + 1 ;
					path = pathnum (x[i] ,  fun_num) ;
					if(statu[path] == false)
					{
						for(int j = 0 ;  j < R ; j++)
							solution[path][j] = x[i][j];
						statu[path] = true ;
						convergence[run][obj] = case_num[run] + 1;
						obj++ ;
					}
					if(obj == PATHNUM)
						break;
				}
								
				if(obj == PATHNUM)
				{
					Cycle[run]++ ;
					break ;
				}
					
				int rands = (int) ( Math.floor (Math.random()*PATHNUM ) );  //产生一个0~PATHNUM-1之间的随机整数，决定接下来的目标路径
					
				if(statu[rands])    
				{
					do                    //随机选择一条还未找到测试用例的目标路径
					{
						rands = (int) ( Math.floor (Math.random()*PATHNUM ) );
					}while(statu[rands]);				
				}				
					
				for(int i = 0 ; i < bee_num ; i++ )     
				{
					fitness_x[i] = benchmarkfunction(x[i], fun_num, rands) ;
					fitness_v[i] = benchmarkfunction(v[i], fun_num, rands );    //计算对应目标路径rands的适应值 
										
					if(fitness_v[i] > fitness_x[i] )		   //step 6：比较更新测试用例
					{
						for( int j = 0; j < R; j++)
							x[i][j] = v[i][j] ;
						fitness_x[i] = fitness_v[i] ;
						rep[i] = 0 ;		  
					}
					else
					{
						rep[i] = rep[i] +1;
						if( rep[i]>= limit )     //侦查蜂
						{
							for(int j = 0 ; j < R ; j ++)
							{
								double r2 = Math.random() ;
								x[i][j] =  (int)Math.round( lb[j] + r2 * ( ub[j] - lb[j]) )  ;
							}
							case_num[run] = case_num[run] + 1 ;
							path = pathnum (x[i] ,  fun_num) ;   //存储用例
							if(statu[path] == false)
							{
								for(int j = 0 ;  j < R ; j++)
									solution[path][j] = x[i][j];
								statu[path] = true ;
								convergence[run][obj] = case_num[run] + 1;
								obj++ ;
							}
							if(obj == PATHNUM)
								break;
							rep[i] = 0;							
						}	
					}
				}   //雇佣蜂更新完				
											
				if(obj == PATHNUM)
				{
					Cycle[run]++ ;
					break ;
				}
					
				if(statu[rands])    
				{
					do                    //随机选择一条还未找到测试用例的目标路径
					{
						rands = (int) ( Math.floor (Math.random()*PATHNUM ) );
					}while(statu[rands]);
					
					for (int i = 0 ; i < bee_num ; i++)  //重新计算适应值
						fitness_x[i] = benchmarkfunction(x[i], fun_num, rands);
				}
									
				int sumfit = 0 ;
				for(int i = 0 ; i < bee_num ; i++)
					sumfit += fitness_x[i] ;		     //计算适应值和		
				for(int i =0 ; i < bee_num ; i++)
					p_relate[i] = fitness_x[i]/sumfit ;  //计算相关概率
				p_accumulate[0] = p_relate[0] ;
				for(int i = 1 ; i < bee_num ; i++)
					p_accumulate[i] = p_accumulate[i-1] + p_relate[i] ;  //计算累积概率
				
				for(int t = 0 ; t < bee_num ; t++)
				{
					int s = 0  ;   
					double r3 = Math.random() ;
					if(r3 <= p_accumulate[0])          //step 7:轮盘选择,被选中的用例编号赋给s
						s = 0 ;
					else 					
						for(int i = 1 ; i < bee_num ; i++)
							if(r3 > p_accumulate[i-1] && r3 <= p_accumulate[i])
							{
								s=i ;
							    break ;
							}

					for (int j = 0 ; j < R ; j++ )		//step 8:基于编号为s的用例进行更新  
					{
						int k = (j + 1) % R ;
						double r1 = Math.random() * 2 - 1;
						v[t][j] = (int) Math.round(Math.abs ( x[s][j] +  r1 *	(x[s][j]-x[s][k])) ) ;
						if(v[t][j] > ub[j] || v[t][j] < lb[j]) 
						{
						   double r01 = Math.random() ;
						   v[t][j] = (int)Math.round( lb[j] + r01 * ( ub[j] - lb[j]) ) ;
						}
					}
					case_num[run] = case_num[run] + 1 ;
					path = pathnum (v[t] ,  fun_num) ;   //存储用例
					if(statu[path] == false)
					{
						for(int j = 0 ;  j < R ; j++)
							solution[path][j] = v[t][j];
						statu[path] = true ;
						convergence[run][obj] = case_num[run] + 1;
						obj++ ;
					}
					if(obj == PATHNUM)
						break;
						
					fitness_v[t] = benchmarkfunction(v[t], fun_num, rands);
					
					if(fitness_v[t] > fitness_x[s])		   //step 9						
					{
						for( int j = 0; j < R; j++)
							x[s][j] = v[t][j] ;					
						rep[s] = 0 ;		  
					}
					else
					{				
						rep[s] = rep[s] +1;
							
						if( rep[s]>= limit )     //step 10:侦查蜂
						{
							for(int j = 0 ; j < R ; j ++)
							{
								double r2 = Math.random() ;
								x[s][j] =  (int)Math.round( lb[j] + r2 * ( ub[j] - lb[j]) )  ;
							}							
							case_num[run] = case_num[run] + 1 ;
							path = pathnum (x[s] ,  fun_num) ;   //存储用例
							if(statu[path] == false)
							{
								for(int j = 0 ;  j < R ; j++)
									solution[path][j] = x[s][j];
								statu[path] = true ;
								convergence[run][obj] = case_num[run] + 1;
								obj++ ;
							}							
							rep[s] = 0; 							
						}
					}		
				}  //跟随蜂更新完											
															
				Cycle[run]++ ;    //step 12
						
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
		 System.out.println();
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
			File file = new java.io.File("D:\\Desktop Files\\Postgraduate\\科研论文\\Experimental\\节点分支存档 VS RP-DE\\otherFuntion" + fun_num +".xls");
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
	
//	/*遍历第i个个体的维度j,比较覆盖的新旧两条路径，如果路径发生改变，说明维度j对节点的走向有影响，更新infection矩阵*/
//	public static void update_Infection2(int firstPath, int path, int j)
//	{
//		if(firstPath!=path){
//			for(int length=0;length<NODENUM;length++)
//				if(PATH[firstPath].charAt(length)!=PATH[path].charAt(length)){
//					infection[j][length]++;
//				}
//		}
//	}
//
//	// 根据status路径覆盖情况，随机产生一个一个未覆盖的路径，作为接下来的优化目标
//	public static int random_UncoverPath(boolean[] status) {
//		int num = 0;
//		for (int i = 0; i < PATHNUM; i++)
//			if (!status[i])
//				num++; // 记录未覆盖的路径数目
//		int a[] = new int[num];
//		num = 0;
//		for (int i = 0; i < PATHNUM; i++)
//			if (!status[i]) {
//				a[num] = i;
//				num++;
//			}
//		return a[(int) (Math.random() * num)];// 随机获取一个未覆盖的路径，接下来所有个体围绕这条路径搜索
//	}
	
	public static void nodeiscoverage(int[] x , int func_num)  //visit[][2]:To sign whether the 'Yes' branch or the 'No' branch of each node has been covered with test case (that we have obtained).   
	{
		if(func_num == 1)   //Triangle
		{
			int a = x[0] ;
			int b = x[1] ;
			int c = x[2] ;		
					
			if((a<(b+c)) && (b<(a+c)) && (c<(a+b)))
			{	
				visit[0][0] = true ;
    			if ( ((a==b) && (a!=c))	 || ((a==c)&&(a!=b)) || ((b==c)&&(b!=a)) )	  
    				visit[1][0] = true ;
    			else
    				visit[1][1] = true ;
    			if ( (a==b) && (a==c) )
    				visit[2][0] = true  ;
    			else
    				visit[2][1] = true ;
    			if ( (a!=b) && (a!=c) && (b!=c) )
    				visit[3][0] = true ;
    			else 
    				visit[3][1] = true ;
    		}
			else
				visit[0][1] = true ;   		
		}
		
		if(func_num == 2) //Factorial
		{
			int a = x[0] ;
			
			if(a==1)
				visit[0][0] = true ;
			else
				visit[0][1] = true ;
		}
		
		if(func_num == 3)  //bubble sorting
		{
			int i1,j1;
			int[] a = new int[R];
		   	  for(i1=0;i1<R;i1++)
		   	       a[i1] = x[i1];
		   	for(j1=0;j1<=R-1;j1++) 
		   	{
		   		 for (i1=0;i1<R-1-j1;i1++)
		   		 {
		   			 if(a[i1]>a[i1+1])
		   			   { visit[0][0] = true ; break ;}
		   		 }
		   		 if(visit[0][0]) break;	   		  
		   	}
		   	if(!visit[0][0])
		   		visit[0][1] = true ;
		}
		
		if(func_num == 4)   //GCD
		{
			int m = x[0] ;
			int n = x[1] ;
			
			if (m<n)
	   		{
				visit[0][0] = true ;
	   			int t = n;
	   			n = m;
	   			m = t;
	   		}
			else
				visit[0][1] = true ;
			
			int r;
		   	r = m % n;
		    m = n;
		   	n = r;
		   	
		   	while (r != 0)
	   		{
		   		visit[1][0] = true ;
	   		    r = m % n;
	   		    m = n;
	   		    n = r;
	   		    break ;
	   	    }
		   	
		   	if(!visit[1][0])
		   		visit[1][1] = true ;
		}
		
		if(func_num == 5)  //Middle
		{
			int a = x[0] ;
			int b = x[1] ;
			int c = x[1] ;
			
			if( ( (a < b) && (b < c) ) || ((c<b) && (b<a)) )
	   			visit[0][0] = true ;
	   		else if ( ( (a < c) && (c < b) ) || ((b<c) && (c<a)) )
	   			{visit[1][0] = true ; visit[0][1] = true ;}
	   		else if ( ( (b < a) && (a < c) ) || ((c<a) && (a<b)) )
	   			{visit[2][0] = true ; visit[0][1] = true ; visit[1][1] = true ;}
	   		else
	   			{visit[0][1] = true ; visit[1][1] = true ; visit[2][1] = true ;}
		}

		if(func_num == 6)  //Tomorrow
		{
			int Day = x[0] ;
      	    int Year = x[1] ;
      	    int Month = x[2] ;
      	    int Date = x[3] ;
      	    
      	    if (Day == 7)
	   	    	visit[0][0] = true ;
	   	    else
	   	    	visit[0][1] = true ;
      	  
	      	if (Month == 12 && Date == 31)
	   	    {
	   	    	visit[1][0] = true ;
	   	    }		   	    		   	    
	   	    else if(Month == 2 && Date == 28)
	   	    {	
	   	    	visit[1][1] = true ;
	   	    	visit[2][0] = true ;
	   	    	if(isRun(Year))
	   	    		visit[3][0] = true ;
	   	    	else
	   	    		visit[3][1] = true ;
	   	    }		   	    
	   	    else if((Month != 12  && Date == 31) || (Month == 2 && Date == 29) 
	   	    		|| ((Month == 4 || Month == 6 || Month == 9 || Month == 11) && Date == 30))
	   	    {
	   	    	visit[1][1] = true ;
	   	    	visit[2][1] = true ;
	   	    	visit[4][0] = true ;
	   	    }
	   	    else 
	   	    {
	   	    	visit[1][1] = true ;
	   	    	visit[2][1] = true ;
	   	    	visit[4][1] = true ;
	   	    }
		}
		if(func_num == 7) //commission
		{
			int totallocks = x[0] ;
			int totalstocks = x[1] ;
			int totalbarrels = x[2] ;
			
			double  lockprice = 45.0 ;
			double  stockprice = 30.0 ;
			double  barrelprice = 25.0 ;
			
			double  locksales = lockprice * totallocks ;
			double  stocksales = stockprice * totalstocks ;
			double  barrelsales = barrelprice * totalbarrels ;
			double  sales = locksales + stocksales + barrelsales ;

			if(sales > 1800.0)
			  visit[0][0] = true;
			else if(sales > 500.0)
			{
				visit[0][1] = true ;
				visit[1][0] = true ;
			}
			else
			{
			  visit[0][1] = true ;
			  visit[1][1] = true ;
			}
		}
		if(func_num == 8) //premium
		{
			int  driverage = x[0] ;
			int  points = x[1] ;
			
			if(driverage >=16 && driverage < 20)
			{
			    visit[0][0] = true ;
			    if(points <= 1)
			      visit[1][0] = true ;
			    else
			    	visit[1][1] = true ;
			}
			else if(driverage >= 20 && driverage < 25)
			{
				visit[0][1] = true ;
				visit[2][0] = true ;
			    if(points < 3)
			       visit[3][0] = true ; 
			    else
			       visit[3][1] = true ; 
			}
			else if(driverage >= 25 && driverage < 45)
			{
				visit[0][1] = true ;
				visit[2][1] = true ;
				visit[4][0] = true ;
			    if(points < 5)
			       visit[5][0] = true ; 
			    else
			       visit[5][1] = true ; 
			}
			else if(driverage >= 45 && driverage < 60)
			{
				visit[0][1] = true ;
				visit[2][1] = true ;
				visit[4][1] = true ;
				visit[6][0] = true ;
			    if(points < 7)
			    	visit[7][0] = true  ;
			    else
			    	visit[7][1] = true ;
			}
			else if(driverage >= 60 && driverage < 100)
			{
				visit[0][1] = true ;
				visit[2][1] = true ;
				visit[4][1] = true ;
				visit[6][1] = true ;
				visit[8][0] = true ;
			    if(points < 5)
			    	visit[9][0] = true ;
			    else
			    	visit[9][1] = true ;
			}
			else
			{
				visit[0][1] = true ;
				visit[2][1] = true ;
				visit[4][1] = true ;
				visit[6][1] = true ;
				visit[8][1] = true ;
			}
		}

	}
	
	public static int pathnum(int[] x , int func_num)
	{

		int path = -1;
		if(func_num == 1) //Triangle
		{
			int a = x[0] ;
			int b = x[1] ;
			int c = x[2] ;		
					
			if((a<(b+c)) && (b<(a+c)) && (c<(a+b)))	//三角形	//节点 0
			{	
    			if ( ((a==b) && (a!=c))	 || ((a==c)&&(a!=b)) || ((b==c)&&(b!=a)) )	//等腰三角形	//节点 1  
    				path = 0 ;
    			if ( (a==b) && (a==c) )	//等边三角形	//节点 1	  
    				path = 1  ;
    			if ( (a!=b) && (a!=c) && (b!=c) )	//一般三角形	//节点 2	  
    				path = 2 ;
    		}
			else	//非三角形
			{
				path = 3 ;  
			}
		}
		
		if(func_num == 2) //Factorial 阶乘
		{
			int a = x[0] ;
			
			if(a==1)	//节点 0
				path = 0 ;
			else
				path = 1 ;
		}
		
		if(func_num == 3)  //bubble sorting 冒泡排序
		{
			int i1,j1;
			int[] a = new int[R];
		   	for(i1 = 0; i1 < R; i1++) {
		   		a[i1] = x[i1];
		   	}
		   	for(j1 = 0;j1 <= R-1; j1++) {
		   		 for (i1 = 0; i1< R-1-j1; i1++){
		   			 if(a[i1] > a[i1+1]){	//节点 0
		   				 path = 0 ; 
		   				 break ;
		   			}
		   		 }
		   		 if(path == 0) break;	   		  
		   	}
		   	if(path != 0)
		   		path = 1 ;
		}
		
		if(func_num == 4)   //GCD 最大公约数
		{
			int m = x[0] ;
			int n = x[1] ;
			boolean d[] =new boolean[2] ;
			
			if (m < n)
	   		{
				d[0] = true ;
	   			int t = n;
	   			n = m;
	   			m = t;
	   		}
			
			int r;
		   	r = m % n;
		    m = n;
		   	n = r;
		   	
		   	while (r != 0)
	   		{
		   		d[1] = true ;
	   		    r = m % n;
	   		    m = n;
	   		    n = r;
	   		    break ;
	   	    }
		   	
		   	if(d[0] && d[1])	//节点 0
		   		path = 0 ;
		   	else if(d[0] && (!d[1]))	//节点 1
		   		path = 1 ;
		   	else if((!d[0]) && d[1])	//节点 2
		   		path = 2 ;
		   	else
		   		path = 3 ;
		}
		
		if(func_num == 5)  //Middle 中位数
		{
			  int a = x[0] ;
		      int b = x[1] ;
		   	  int c = x[2] ;
		   	  		  		
	   		  if( ( (a < b) && (b < c) ) || ((c < b) && (b < a)) )	//b 为 Middle	//节点 0
	   			  path = 0 ;	   		    
	   		  else if ( ( (a < c) && (c < b) ) || ((b < c) && (c < a)) )	//c 为 Middle	//节点1
	   			  path = 1 ; 
	   		  else if ( ( (b < a) && (a < c) ) || ((c < a) && (a < b)) )	//a 为 Middle	//节点 2
	   			  path = 2 ;
	   		  else
	   			  path = 3 ;
		}
		
		if(func_num == 6)  //Tomorrow
		{
			int Day = x[0] ;
      	    int Year = x[1] ;
      	    int Month = x[2] ;
      	    int Date = x[3] ;
      	    
      	    if (Day == 7)	//节点 0
      	    {
      	     	if (Month == 12 && Date == 31)	//节点 1
      	     		path = 0 ;
    	   	    else if(Month == 2 && Date == 28)	//节点 2
    	   	    {
    	   	    	if(isRun(Year))	//节点 3
    	   	    		path = 1 ;
    	   	    	else
    	   	    		path = 2 ;
    	   	    }
    	   	    	
    	   	    else if((Month != 12  && Date == 31) || (Month == 2 && Date == 29)	//节点 4
    	   	    		|| ((Month == 4 || Month == 6 || Month == 9 || Month == 11) && Date == 30))
    	   	    	path = 3 ;
    	   	    else 
    	   	    	path = 4 ;
      	    }
      	    else
      	    {
      	    	if (Month == 12 && Date == 31)	//节点 1
      	     		path = 5 ;
    	   	    else if(Month == 2 && Date == 28)	//节点 2
    	   	    {
    	   	    	if(isRun(Year))	//节点 3
    	   	    		path = 6 ;
    	   	    	else
    	   	    		path = 7 ;
    	   	    }
    	   	    	
    	   	    else if((Month != 12  && Date == 31) || (Month == 2 && Date == 29)	//节点 4
    	   	    		|| ((Month == 4 || Month == 6 || Month == 9 || Month == 11) && Date == 30))
    	   	    	path = 8 ;
    	   	    else 
    	   	    	path = 9 ;
      	    }     	  
		}
		if(func_num == 7) //commission
		{
			int totallocks = x[0] ;
			int totalstocks = x[1] ;
			int totalbarrels = x[2] ;
			
			double  lockprice = 45.0 ;
			double  stockprice = 30.0 ;
			double  barrelprice = 25.0 ;
			
			double  locksales = lockprice * totallocks ;
			double  stocksales = stockprice * totalstocks ;
			double  barrelsales = barrelprice * totalbarrels ;
			double  sales = locksales + stocksales + barrelsales ;

			if(sales > 1800.0)	//节点 0
			  path = 0;
			else if(sales > 500.0)	//节点 1
			  path = 1 ;
			else
	          path = 2 ;
		}
		if(func_num == 8) //premium
		{
			int  driverage = x[0] ;
			int  points = x[1] ;
			
			if(driverage >=16 && driverage < 20)		//节点 0
			{			 
			    if(points <= 1)	//节点 1
			      path = 0 ;
			    else
			      path = 1 ;
			}
			else if(driverage >= 20 && driverage < 25)	//节点 2	
			{
			    if(points < 3)	//节点 3
			       path = 2 ; 
			    else
			       path = 3 ; 
			}
			else if(driverage >= 25 && driverage < 45)		//节点 4	
			{
			    if(points < 5)	//节点 5
			       path = 4 ; 
			    else
			       path = 5 ; 
			}
			else if(driverage >= 45 && driverage < 60)		//节点 6
			{
			    if(points < 7)	//节点 7
			    	path = 6  ;
			    else 
			    	path = 7 ;
			}
			else if(driverage >= 60 && driverage < 100)		//节点 8
			{
			    if(points < 5)	//节点 9
			    	path = 8 ;
			    else
			    	path = 9 ;
			}
			else
                path = 10 ;
		}
	
		return path ;
	
	}
	
	public static double benchmarkfunction (int[] x , int func_num, int path_num)
	{
		double[] f = new double[NODENUM] ;  //f[k]表示节点k的Yes分支如果未被覆盖时，测试用例执行该分支的惩罚值
		double[] F = new double[NODENUM] ;  //F[k]表示节点k的No分支如果未被覆盖时，测试用例执行该分支的惩罚值
		double[] fit = new double[NODENUM] ;  //fit[k]表示测试用例经过节点k时，在该节点的适应值
		double Fitness = 0 ;    //测试用例的适应值
		
		if(func_num == 1)   //Triangle
		{
			int a = x[0] ;
			int b = x[1] ;
			int c = x[2] ;		
					
    		double v1,v2,v3,v4,v5,v6,v7 ;	   		   	   		
	   		
    		if(a<(b+c)) v1 = 0;        //测试用例执行第一个节点的Yes分支时的成本值f[0]；
	   		else v1 = a-(b+c)+K ;
	   		if(b<(a+c)) v2 = 0 ;
	   		else v2 = b-(a+c) + K ;
	   		if(c<(a+b)) v3 = 0;
	   		else v3 = c-(a+b) + K ;	  		
	   		f[0] = v1 + v2 + v3 ;  
	   		
	   		if(a==b) v1 = 0 ;      //测试用例执行第二个节点的Yes分支时的成本值f[1]；
	   		else v1 = Math.abs(a-b)+K ;
	   		if(a!=c) v2 = 0 ;
	   		else v2 = K ;
	   		if(a==c) v3 = 0 ;
	   		else v3 = Math.abs(a-c)+K ;
	   		if(a!=b) v4 = 0 ;
	   		else v4 = K ;
	   		if(b==c) v5 = 0 ;
	   		else v5 = Math.abs(b-c)+K ;
	   		if(b!=a) v6 = 0 ;
	   		else v6 = K ;
	   		v7 = Math.min(v1+v2 , v3+v4);
	   		f[1] = Math.min(v7 , v5+v6);
	   		
	   		if(a==b) v1 = 0 ;     //测试用例执行第三个节点的Yes分支时的成本值f[2]；
	   		else v1 = Math.abs(a-b)+K ;
	   		if(a==c) v2 = 0;
	   		else v2 = Math.abs(a-c)+K ;
	   		f[2] = v1 + v2 ;
	   		
	   		if(a!=b) v1 = 0 ;   //测试用例执行第四个节点的Yes分支时的成本值f[3]；
	   		else v1 = K ;
	   		if(a!=c) v2 = 0 ;
	   		else v2 = K ;
	   		if(b!=c) v3 = 0 ;
	   		else v3 = K ;
	   		f[3] = v1 + v2 + v3 ;

	   		if(a>=(b+c)) v1 = 0 ;   //测试用例执行第一个节点的No分支时的成本值F[0]；
	   		else v1 = (b+c)-a+K ;
	   		if(b>=(a+c)) v2 = 0;
	   		else v2 = (a+c)-b+K ;
	   		if(c>=(a+b)) v3 = 0 ;
	   		else v3 = (a+b)-c+K ;
	   		v4 = Math.min(v1, v2);
	   		F[0] = Math.min(v4, v3);
   		
	   		if(a!=b) v1 = 0 ;       //测试用例执行第二个节点的No分支时的成本值F[1]；
	   		else v1 = K ;
	   		if(a==c) v2 = 0 ;
	   		else v2 = Math.abs(a-c)+K ;
	   		if(a!=c) v3 = 0 ;
	   		else v3 = K ;
	   		if(a==b) v4 = 0 ;
	   		else v4 = Math.abs(a-b)+K ;
	   		if(b!=c) v5 = 0 ;
	   		else v5 = K ;
	   		if(b==a) v6 = 0 ;
	   		else v6 = Math.abs(b-a)+K ;
	   		F[1] = Math.min(v1, v2) + Math.min(v3, v4) + Math.min(v5, v6) ;
  		
	   		if(a!=b) v1 = 0 ;     //测试用例执行第三个节点的No分支时的成本值F[2]；
	   		else v1 = K ;
	   		if(a!=c) v2 = 0 ;
	   		else v2 = K ;
	   		F[2] = Math.min(v1, v2);
   		
	   		if(a==b) v1 = 0 ;    //测试用例执行第四个节点的No分支时的成本值F[3]；
	   		else v1 = Math.abs(a-b)+K ;
	   		if(a==c) v2 = 0 ;
	   		else v2 = Math.abs(a-c)+K ;
	   		if(b==c) v3 = 0 ;
	   		else v3 = Math.abs(b-c)+K ;
	   		v4 = Math.min(v1, v2);
	   		F[3] = Math.min(v4, v3);	
	   		
//	   		for(int k = 0 ; k < NODENUM ; k++)
//	   		{
//		   		if(visit[k][0] && visit[k][1])
//		   			fit[k] = 0 ;
//		   		else if(visit[k][0] && (!visit[k][1]))
//		   			fit[k] = 1/(F[k] + alpha) ;
//		   		else if((!visit[k][0]) && visit[k][1])
//		   			fit[k] = 1/(f[k] + alpha) ;
//		   		else
//		   			fit[k] = 1/alpha ;
//	   		}
//	   		
//	   		if((a<(b+c)) && (b<(a+c)) && (c<(a+b)))
//			{	
//    			if ( ((a==b) && (a!=c))	 || ((a==c)&&(a!=b)) || ((b==c)&&(b!=a)) )	 //等腰三角形 
//    				Fitness = fit[0] + fit[1] + fit[2] + fit[3] ;
//    			if ( (a==b) && (a==c) )	//等边三角形
//    				Fitness = fit[0] + fit[1] + fit[2] + fit[3] ;
//    			if ( (a!=b) && (a!=c) && (b!=c) )	//一般三角形
//    				Fitness = fit[0] + fit[1] + fit[2] + fit[3] ;
//    		}
//			else
//				Fitness = fit[0] ;   	   		
		}
			  
	    if(func_num == 2)    //Factorial
	   	{
	   		int a = x[0];
	   		double v1 ;
	   		
	   		if(a==1) v1 = 0 ;
	   		else v1 = Math.abs(a-1)+K ;
	   		f[0] = v1 ;
	   		  
	   		if(a!=1) v1 = 0 ;
	   		else v1 = K ;
	   		F[0] = v1 ;
	   		  	
//	   		if(visit[0][0] && visit[0][1])
//	   			fit[0] = 0 ;
//	   		else if(visit[0][0] && (!visit[0][1]))
//	   			fit[0] = 1/(F[0] + alpha) ;
//	   		else if((!visit[0][0]) && visit[0][1])
//	   			fit[0] = 1/(f[0] + alpha) ;
//	   		else
//	   			fit[0] = 1/alpha ;
//	   		
//	   		Fitness = fit[0] ;
	   	 }
	    
	 	  if(func_num == 3)   //sorting
	   	  {
	   		  boolean d =false ;
	   		  double v1=0,v2=0 ;
		   	  int i1,j1 ;
		   	  int[] a = new int[R];
		   	  for(i1=0;i1<R;i1++)
		   	       a[i1] = x[i1];

		   	  for(j1=0;j1<=R-1;j1++) 
		   	  {
		   		  for (i1=0;i1<R-1-j1;i1++)
		   		  {
		   			  d =(a[i1]>a[i1+1]) ;
		   			  if(a[i1]>a[i1+1]){ v1 = 0 ; break ;}
		   			else v1 = a[i1+1]-a[i1]+K ;
		   			  v2 = v2 + v1 ;
		   		  }
		   		  if(d) break;	   		  
		   	  }
		   	  if(v1==0) f[0] = v1 ;
		   	  else f[0] = v2 ;
		   	 
		   	  v2 = 0 ;
			  for(j1=0;j1<=R-1;j1++) 
		   	  {
		   		  for (i1=0;i1<R-1-j1;i1++)
		   		  {
		   			  if(a[i1]<=a[i1+1]) v1 = 0 ; 
		   			else v1 = a[i1]-a[i1+1]+K ;
		   			  v2 = v2 + v1 ;
		   		  }
		   	  }
		   	  F[0] = v2 ;
		   	  	   	 	   	  
//	   		  if(visit[0][0] && visit[0][1])
//	   			 fit[0] = 0 ;
//	   		  else if(visit[0][0] && (!visit[0][1]))
//	   			 fit[0] = 1/(F[0] + alpha) ;
//	   		  else if((!visit[0][0]) && visit[0][1])
//	   			 fit[0] = 1/(f[0] + alpha) ;
//	   		  else
//	   			 fit[0] = 1/alpha ;
//	   		
//	   		  Fitness = fit[0] ;  		  	   				   		
	   	  }
	 	  
	 	  if(func_num == 4)    //GCD
	   	  {
	   		 int m = x[0] ;
	   		 int n = x[1] ;
	   		 double v1 ;
	   		 	   		
	   		 if(m<n)v1 = 0 ;
	   		 else v1 = m-n+K ;
	   		 f[0] = v1 ;
	   		
	   		 if(m>=n)v1 = 0 ;
	   		 else v1 = n-m+K ;
	   		 F[0] = v1 ;
	   		
	   	     int r;
	   	     r = m % n;
	   		 m = n;
	   		 n = r;
	   		
	   		 if(r!=0) v1 = 0 ;
	   		else v1 = K ;
	   		 f[1] = v1 ;
	   		
	   		 if(r==0)v1 = 0 ;
	   		else v1 = Math.abs(r)+K ;
	   		 F[1] = v1 ;
		   		
//	   		 for(int k = 0 ; k < NODENUM ; k++)
//	   		 {
//		   		if(visit[k][0] && visit[k][1])
//		   			fit[k] = 0 ;
//		   		else if(visit[k][0] && (!visit[k][1]))
//		   			fit[k] = 1/(F[k] + alpha) ;
//		   		else if((!visit[k][0]) && visit[k][1])
//		   			fit[k] = 1/(f[k] + alpha) ;
//		   		else
//		   			fit[k] = 1/alpha ;
//	   		 }
//	   		 
//	   		 Fitness = fit[0] + fit[1] ;
	   	  }
	 	  
	 	 if(func_num == 5)    //Middle
	     {
    	     int a = x[0] ;
	   		 int b = x[1] ;
	   		 int c = x[2] ;
	   		 double v1,v2,v3,v4 ;		   		
	   		
	   		 if(a<b) v1 = 0 ;
	   		 else v1 = a-b+K ;
	   		 if(b<c) v2 = 0 ;
	   		 else v2 = b-c+K ;
	   		 if(c<b) v3 = 0 ;
	   		 else v3 = c-b+K;
	   		 if(b<a) v4 = 0 ;
	   		 else v4 = b-a+K ;
	   		 f[0] = Math.min(v1+v2, v3+v4);
	   		
	   		 if(a>=b) v1 = 0 ;
	   		 else v1 = b-a+K ;
	   		 if(b>=c) v2 = 0 ;
	   		 else v2 = c-b+K ;
	   		 if(c>=b) v3 = 0 ;
	   		 else v3 = b-c+K;
	   		 if(b>=a) v4 = 0 ;
	   		 else v4 = a-b+K ;
	   		 F[0] = Math.min(v1, v2) + Math.min(v3, v4);
	   		
	   		 if(a<c) v1 = 0 ;
	   		 else v1 = a-c+K ;
	   		 if(c<b) v2 = 0 ;
	   		 else v2 = c-b+K ;
	   		 if(b<c) v3 = 0 ;
	   		 else v3 = b-c+K;
	   	 	 if(c<a) v4 = 0 ;
	   		 else v4 = c-a+K ;
	   		 f[1] = Math.min(v1+v2, v3+v4);
	   		
	   		 if(a>=c) v1 = 0 ;
	   		 else v1 = c-a+K ;
	   		 if(c>=b) v2 = 0 ;
	   		 else v2 = b-c+K ;
	   		 if(b>=c) v3 = 0 ;
	   		 else v3 = c-b+K;
	   		 if(c>=a) v4 = 0 ;
	   		 else v4 = a-c+K ;
	   		 F[1] = Math.min(v1, v2) + Math.min(v3, v4);
	   		
	   		 if(b<a) v1 = 0 ;
	   		 else v1 = b-a+K ;
	   		 if(a<c) v2 = 0 ;
	   		 else v2 = a-c+K ;
	   		 if(c<a) v3 = 0 ;
	   		 else v3 = c-a+K;
	   		 if(a<b) v4 = 0 ;
	   		 else v4 = a-b+K ;
	   		 f[2] = Math.min(v1+v2, v3+v4);
	   		
	   		 if(b>=a) v1 = 0 ;
	   		 else v1 = a-b+K ;
	   		 if(a>=c) v2 = 0 ;
	   		 else v2 = c-a+K ;
	   		 if(c>=a) v3 = 0 ;
	   		 else v3 = a-c+K;
	   		 if(a>=b) v4 = 0 ;
	   		 else v4 = b-a+K ;
	   		 F[2] = Math.min(v1, v2) + Math.min(v3, v4);
	   		
//	   		 for(int k = 0 ; k < NODENUM ; k++)
//	   		 {
//		   		if(visit[k][0] && visit[k][1])
//		   			fit[k] = 0 ;
//		   		else if(visit[k][0] && (!visit[k][1]))
//		   			fit[k] = 1/(F[k] + alpha) ;
//		   		else if((!visit[k][0]) && visit[k][1])
//		   			fit[k] = 1/(f[k] + alpha) ;
//		   		else
//		   			fit[k] = 1/alpha ;
//	   		 }
//	   		
//	   		 if( ( (a < b) && (b < c) ) || ((c<b) && (b<a)) )
//	   			Fitness = fit[0] ;
//	   		 else if ( ( (a < c) && (c < b) ) || ((b<c) && (c<a)) )
//	   			Fitness = fit[0] + fit[1] ;
//	   		 else if ( ( (b < a) && (a < c) ) || ((c<a) && (a<b)) )
//	   			Fitness = fit[0] + fit[1] + fit[2] ;
//	   		 else
//	   			Fitness = fit[0] + fit[1] + fit[2] ;
	     }
	 	 	 
	 	 
	 	if(func_num == 6)   //Tomorrow
      	{
      		int Day = x[0] ;
      	    int Year = x[1] ;
      	    int Month = x[2] ;
      	    int Date = x[3] ;
		   		
	   		double v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15 ;	   		
	   		   	    
	   		if(Day == 7) v1 = 0 ;
	   		else v1 = Math.abs(Day-7)+K ;
	   		f[0] = v1 ;
	   		
	   		if(Day != 7) v1 = 0 ;
	   		else v1 = K ;
	   		F[0] = v1 ;

	   	    if(Month == 12) v1 = 0 ;
	   	    else v1 = Math.abs(Month-12)+K ;
	   	    if(Date == 31) v2 = 0 ;
	   	    else v2 = Math.abs(Date-31)+K;
	   	    f[1] = v1 + v2 ;
	   	     
	   	    if(Month != 12) v1 = 0 ;
	   	    else v1 = K ;
	   	    if(Date != 31) v2 = 0 ;
	   	    else v2 = K;
	   	    F[1] = Math.min(v1 , v2) ;

	   	    if(Month == 2) v1 = 0 ;
	   	    else v1 = Math.abs(Month-2)+K ;
	   	    if(Date == 28) v2 = 0 ;
	   	    else v2 = Math.abs(Date-28)+K;
	   	    f[2] = v1 + v2 ;
	   	     
	   	    if(Month != 2) v1 = 0 ;
	   	    else v1 = K ;
	   	    if(Date != 28) v2 = 0 ;
	   	    else v2 = K;
	   	    F[2] = Math.min(v1 , v2) ;
	   	     
	   	    if(Year%4==0) v1 = 0;
		    else v1 = Math.abs(Year%4-0)+K;
	   	    if(Year%100!=0)v2 = 0 ;
	   	    else v2 = K ;
	   	    if(Year%400==0)v3 = 0 ;
	   	    else v3 = Math.abs(Year%400)+K ;
		    f[3] = Math.min(v1+v2, v3) ;
		     
		    if(Year%4!=0) v1 = 0;
		    else v1 = K;
	   	    if(Year%100==0)v2 = 0 ;
	   	    else v2 = Math.abs(Year%100)+K ;
	   	    if(Year%400!=0)v3 = 0 ;
	   	    else v3 = K ;
		    F[3] = Math.min(v1,v2)+v3 ;
	   	    
		    if(Month != 12) v1 = 0 ;
	   	    else v1 = K ;
	   	    if(Date == 31) v2 = 0 ;
	   	    else v2 = Math.abs(Date-31)+K ; 
	   	    if(Month == 2) v3 = 0 ;
	   	    else v3 = Math.abs(Month-2)+K ;
	   	    if(Date == 29) v4 = 0 ;
	   	    else v4 = Math.abs(Date-29)+K;
	   	    if(Month == 4) v5 = 0 ;
	   	    else v5 = Math.abs(Month-4)+K ;
	   	    if(Month == 6) v6 = 0 ;
	   	    else v6 = Math.abs(Month-6)+K ;
	   	    if(Month == 9) v7 = 0 ;
	   	    else v7 = Math.abs(Month-9)+K ;
	   	    if(Month == 11) v8 = 0 ;
	   	    else v8 = Math.abs(Month-11)+K ;
	   	    if(Date == 30) v9 = 0 ;
	   	    else v9 = Math.abs(Date-30)+K;
	   	    v10 = v1 + v2 ;
	   	    v11 = v3 + v4 ;
	   	    v12 = Math.min(v5, v6);
	   	    v13 = Math.min(v7, v8);
	   	    v14 = Math.min(v12, v13) + v9;
	   	    v15 = Math.min(v10, v11);
	   	    f[4] = Math.min(v15, v14) ;
	   	    
	   	    if(Month == 12) v1 = 0 ;
	   	    else v1 = Math.abs(Month-12)+K ;
	   	    if(Date != 31) v2 = 0 ;
	   	    else v2 = K ; 
	   	    if(Month != 2) v3 = 0 ;
	   	    else v3 = K ;
	   	    if(Date != 29) v4 = 0 ;
	   	    else v4 = K;
	   	    if(Month != 4) v5 = 0 ;
	   	    else v5 = K ;
	   	    if(Month != 6) v6 = 0 ;
	   	    else v6 = K ;
	   	    if(Month != 9) v7 = 0 ;
	   	    else v7 = K ;
	   	    if(Month != 11) v8 = 0 ;
	   	    else v8 = K ;
	   	    if(Date != 30) v9 = 0 ;
	   	    else v9 = K;
	   	    v10 = Math.min(v1, v2);
	   	    v11 = Math.min(v3, v4);
	   	    v12 = Math.min(v5+v6+v7+v8 , v9);
	   	    F[4] = v10 + v11 + v12 ;
	   	    	   	    
//		   	for(int k = 0 ; k < NODENUM ; k++)
//	   		{
//		   		if(visit[k][0] && visit[k][1])
//		   			fit[k] = 0 ;
//		   		else if(visit[k][0] && (!visit[k][1]))
//		   			fit[k] = 1/(F[k] + alpha) ;
//		   		else if((!visit[k][0]) && visit[k][1])
//		   			fit[k] = 1/(f[k] + alpha) ;
//		   		else
//		   			fit[k] = 1/alpha ;
//	   		}
//		   	
//		   	if (Month == 12 && Date == 31)
//	          	 Fitness = fit[0] + fit[1] ;
//	   	    else if(Month == 2 && Date == 28)
//	   	    	 Fitness = fit[0] + fit[1] + fit[2] + fit[3] ; 	   	    	
//	   	    else if((Month != 12  && Date == 31) || (Month == 2 && Date == 29) 
//	   	    		|| ((Month == 4 || Month == 6 || Month == 9 || Month == 11) && Date == 30))
//	   	    	 Fitness = fit[0] + fit[1] + fit[2] + fit[4] ; 
//	   	    else 
//	   	    	 Fitness = fit[0] + fit[1] + fit[2] + fit[4] ; 
      	}
	 	if(func_num == 7) //commission
		{
			int totallocks = x[0] ;
			int totalstocks = x[1] ;
			int totalbarrels = x[2] ;
			
			double  lockprice = 45.0 ;
			double  stockprice = 30.0 ;
			double  barrelprice = 25.0 ;
			
			double  locksales = lockprice * totallocks ;
			double  stocksales = stockprice * totalstocks ;
			double  barrelsales = barrelprice * totalbarrels ;
			double  sales = locksales + stocksales + barrelsales ;
			double v1,v2 ;

			if(sales > 1800.0) {f[0] = 0 ; F[0] = (sales - 1800.0) + K ;}
			else {f[0] = (1800.0-sales)+K ; F[0] = 0 ;}
			 
			if( sales >500.0)v1 = 0 ;
			else v1 = (500.0-sales) + K ;
			if(sales <= 1800.0)v2 = 0 ;
			else v2 = (sales-1800.0) +K ;
			f[1] = v1 + v2 ;
			
			if( sales <=500.0)v1 = 0 ;
			else v1 = (sales-500.0) + K ;
			if(sales > 1800.0)v2 = 0 ;
			else v2 = (1800.0-sales) + K ;
			F[1] = Math.min(v1, v2) ;	
			
//			for(int k = 0 ; k < NODENUM ; k++)
//	   		{
//		   		if(visit[k][0] && visit[k][1])
//		   			fit[k] = 0 ;
//		   		else if(visit[k][0] && (!visit[k][1]))
//		   			fit[k] = 1/(F[k] + alpha) ;
//		   		else if((!visit[k][0]) && visit[k][1])
//		   			fit[k] = 1/(f[k] + alpha) ;
//		   		else
//		   			fit[k] = 1/alpha ;
//	   		}
//			
//			if(sales > 1800.0)
//			   Fitness = fit[0] ;
//			else if(sales > 500.0)
//			   Fitness = fit[0] + fit[1] ;
//			else
//			   Fitness = fit[0] + fit[1] ;			
		}
	 	if(func_num == 8) //premium
		{
			int  driverage = x[0] ;
			int  points = x[1] ;
			double v1,v2,v3,v4,v5,v6,v7,v8,v9,v10 ;	
			
			if(driverage >=16)v1 = 0 ;
			else  v1 = (16-driverage)+K ;
			if(driverage < 20)v2 = 0 ;
			else v2 = (driverage-20)+K ;
			f[0] = v1 + v2 ;
			
			if(driverage < 16)v1 = 0 ;
			else  v1 = (driverage-16)+K ;
			if(driverage >= 20)v2 = 0 ;
			else v2 = (20-driverage)+K ;
			F[0] = Math.min(v1, v2) ;
			
			if(points <= 1){f[1] = 0 ; F[1] = (1-points)+K ; ;}
			else {f[1] = (points-1)+K ; F[1] = 0 ;}
			
			if(driverage >= 20)v3 = 0 ;
			else v3 = (20-driverage)+K ;
			if(driverage < 25) v4 = 0 ;
			else v4 = (driverage-25)+K ;
			f[2] = v3 + v4 ;
			
			if(driverage < 20)v3 = 0 ;
			else v3 = (driverage-20)+K ;
			if(driverage >= 25) v4 = 0 ;
			else v4 = (25-driverage)+K ;
			F[2] = Math.min(v3 , v4) ;
			
			if(points < 3){f[3] = 0 ; F[3] = (3-points)+K ;}
			else {f[3] = (points-3)+K ; F[3] = 0 ;}
			
			if(driverage >= 25)v5 = 0 ;
			else v5 = (25-driverage)+K ;
			if(driverage < 45) v6 = 0 ;
			else v6 = (driverage-45)+K ;
			f[4] = v5 + v6 ;
			
			if(driverage < 25)v5 = 0 ;
			else v5 = (driverage-25)+K ;
			if(driverage >= 45) v6 = 0 ;
			else v6 = (45-driverage)+K ;
			F[4] = Math.min(v5 , v6) ;
			
			if(points < 5){f[5] = 0 ; F[5] = (5-points)+K ;}
			else {f[5] = (points-5)+K ; F[5] = 0 ;}
			
			if(driverage >= 45)v7 = 0 ;
			else v7 = (45-driverage)+K ;
			if(driverage < 60) v8 = 0 ;
			else v8 = (driverage-60)+K ;
			f[6] = v7 + v8 ;
			
			if(driverage < 45)v7 = 0 ;
			else v7 = (driverage-45)+K ;
			if(driverage >= 60) v8 = 0 ;
			else v8 = (60-driverage)+K ;
			F[6] = Math.min(v7 , v8) ;
			
			if(points < 7){f[7] = 0 ; F[7] = (7-points)+K ;}
			else {f[7] = (points-7)+K ; F[7] = 0 ;}
			
			if(driverage >= 60)v9 = 0 ;
			else v9 = (60-driverage)+K ;
			if(driverage < 100) v10 = 0 ;
			else v10 = (driverage-100)+K ;
			f[8] = v9 + v10 ;
			
			if(driverage < 60)v9 = 0 ;
			else v9 = (driverage-60)+K ;
			if(driverage >= 100) v10 = 0 ;
			else v10 = (100-driverage)+K ;
			F[8] = Math.min(v9 , v10) ;
			
			if(points < 5){f[9] = 0; F[9] = (5-points)+K ;}
			else {f[9] = (points-5)+K ; F[9] = 0 ;}
			
//			for(int k = 0 ; k < NODENUM ; k++)
//	   		{
//		   		if(visit[k][0] && visit[k][1])
//		   			fit[k] = 0 ;
//		   		else if(visit[k][0] && (!visit[k][1]))
//		   			fit[k] = 1/(F[k] + alpha) ;
//		   		else if((!visit[k][0]) && visit[k][1])
//		   			fit[k] = 1/(f[k] + alpha) ;
//		   		else
//		   			fit[k] = 1/alpha ;
//	   		}
//			
//			if(driverage >=16 && driverage < 20)
//			   Fitness = fit[0] + fit[1] ;
//			else if(driverage >= 20 && driverage < 25)
//			   Fitness = fit[0] + fit[2] + fit[3];
//			else if(driverage >= 25 && driverage < 45)
//			   Fitness = fit[0] + fit[2] + fit[4] + fit[5];
//			else if(driverage >= 45 && driverage < 60)
//			   Fitness = fit[0] + fit[2] + fit[4] + fit[6] + fit[7];
//			else if(driverage >= 60 && driverage < 100)
//			   Fitness = fit[0] + fit[2] + fit[4] + fit[6] + fit[8]+ fit[9] ;
//			else
//			   Fitness = fit[0] + fit[2] + fit[4] + fit[6] + fit[8] ;			
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
		 static boolean isRun(int year) //To identify whether the year is bissextile.  
		 {  
		      if((year%4==0 && year%100!=0) || (year%400==0))  
		      {  
		          return true;  
		      }  
		     else  
		     {  
		        return false;  
		     }  
		 } 
}
