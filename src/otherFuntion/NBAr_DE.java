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

public class NBAr_DE {

	private static final int RUN = 30;                       //���д���
	private static final int pop_num = 50;                   //��Ⱥ��С
	
	/*��Ӧֵ�������*/
	private static final int K = 10;               
	private static final double alpha = 0.001;     
	
	/*DE�㷨����*/
	private static final double Pc = 0.2;
	private static final double F = 0.5;
	
	private static final int fun_num = 1;
	private static final int R = 3;
	private static final int PATHNUM = 4;
	private static final int NODENUM = 4;
	
	private static boolean[][] visit = new boolean[NODENUM][4];
	
	/* infection��һ��n*m�ľ���C
	 * ����C�����¼���������ĵ�i��ά�ȣ��Խڵ�j��Ӱ�����*/
	private static int[][]infection = new int[R][NODENUM];
	/* record�����¼��Ⱥÿ�����壨������������Ӧ�Ľڵ㸲�����*/
	private static int[] record = new int[pop_num];
	/*PATH��¼��ͬ·���ķ�֧�ڵ�����*/
	private static String[] PATH = new String[PATHNUM];
	/*��������*/
	private static final int step_length = 4;
	
	//�ڵ��֧�浵
	private static int[][][] NBArchives = new int[NODENUM][R][10];	
	
	private static int[][] convergence  = new int[RUN][PATHNUM];	
	
	/*��ʼ����ʱ�䡢����ʱ�䡢�����ʡ�ѭ������������������Ŀ*/
    static double start;                                             
    static double finish;
    static double[] runtime = new double[RUN];
    static double[] coverage = new double[RUN];
    static int[] case_num = new int[RUN];
    static int[] num1 = new int[RUN];
    static int[] num2 = new int[RUN];
    static int[] num3 = new int[RUN];
    static int[] Cycle = new int[RUN];

	
	private static final int MCN = 300000;                  //����������
	private static final int col = 1;
	
	public static void main(String[] args){
		/*��������������½�*/
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
		
		/*����RUN���ظ�����*/
		for(int run=0;run<RUN;run++)
		{
			//��ʼ��visit
			for(int i=0;i<NODENUM;i++)
				for(int j=0;j<4;j++)
					visit[i][j] = false;
			//��ʼ��infection��COVER
			for(int i=0;i<R;i++)
				for(int j=0;j<NODENUM;j++){
					infection[i][j] = 0;
				}
			for(int i=0;i<NODENUM;i++) {
				for(int j=0;j<R;j++){
					for(int k=0;k<5;k++){
						NBArchives[i][j][k] = Integer.MIN_VALUE;
					}
				}
			}			
			
//			for(int i=0;i<PATHNUM;i++)
//				COVER[i] = false;
			
			/*·������״̬��״̬��ʼ��*/
			int[][] x = new int[pop_num][R];
			int[][] v = new int[pop_num][R];
			double[] fitness_x = new double[pop_num];
			double[] fitness_v = new double[pop_num];
			boolean[] status = new boolean[PATHNUM];  //to mark whether the path has been covered.
			double[][] solution = new double[PATHNUM][R];
			int path ;
			int obj = 0 ;
			
			Date mydate = new Date();
			start = mydate.getTime();
			
			//Initialize��ʼ����Ⱥ
			for(int i=0;i<pop_num;i++)
			{
				for(int j=0;j<R;j++)
				{
					if(fun_num == 6 && j == 3)
					{
						if((x[i][2] == 4) || (x[i][2] == 6) || (x[i][2] == 9) || (x[i][2] == 11))
							ub[3] = 30 ;
						else if( (x[i][2] == 2) && (isRun(x[i][1])) )
							ub[3] = 29 ;
						else if( (x[i][2] == 2) && (!isRun(x[i][1])) )
							ub[3] = 28 ;
						else
							ub[3] = 31 ;
					}
					x[i][j] =(int)(lb[j] + Math.random() * (ub[j] - lb[j]));
				}
				
				if(obj == PATHNUM)
					break;
				
				
				path = pathnum(x[i],fun_num);                        //��ȡ�����������ǵ�·��
				record[i] = path;                                    //��¼�����������ǵ�·�����
//				if(!COVER[path])//��¼·������
//					COVER[path] = true;	
				
				if(!status[path])
				{
					for(int j=0;j<R;j++)
						solution[path][j] = x[i][j];                 //��¼·����һ��
					status[path] = true;                             //���·��Path�Ƿ����ҵ�������������
					convergence[run][obj] = case_num[run] + 1;
					obj++;                                           //�Ѹ��ǵ�·����
					nodeiscoverage(x[i],fun_num);                    //����ѱ����ǵķ�֧
//					System.out.println(case_num[run]);
				}
				
				fitness_x[i] = benchmarkfunction(x[i],fun_num,-1);      //����				
				case_num[run] = case_num[run]+1;             //������������
			}
			
			Cycle[run] = 1;
			
			int cycle=0;
			while(case_num[run] <= MCN && obj < PATHNUM)         //ѭ����ֱ���ҵ����Ž�������������ù�
			{
				/*��ֱ������*/
				for(int i=0;i<pop_num;i++)
				{
					int  k1 = (int) Math.floor( Math.random() * pop_num ) ;
					while(k1 == i)
						k1 = (int) Math.floor( Math.random() * pop_num ) ;
					int  k2 = (int) Math.floor( Math.random() * pop_num ) ;
					while(k2 == i || k2 == k1)
						k2 = (int) Math.floor( Math.random() * pop_num ) ;
					int jrand = (int) (Math.random() * R) ;
					for (int j = 0 ; j < R ; j++ )		
					{						
						v[i][j] = (int)(x[i][j] +  F * (x[k1][j]-x[k2][j]));
						if(Math.random() > Pc  && j!=jrand)
							v[i][j] = x[i][j] ;
						if(fun_num == 6 && j == 3)
						{
							if((v[i][2] == 4) || (v[i][2] == 6) || (v[i][2] == 9) || (v[i][2] == 11))
								ub[3] = 30 ;
							else if( (v[i][2] == 2) && (isRun(v[i][1])) )
								ub[3] = 29 ;
							else if( (v[i][2] == 2) && (!isRun(v[i][1])) )
								ub[3] = 28 ;
							else
								ub[3] = 31 ;
						}
						if(v[i][j] > ub[j] || v[i][j] < lb[j]) 
						{
						   double r01 = Math.random() ;
						   v[i][j] = (int)( lb[j] + r01 * ( ub[j] - lb[j]) );
						}	
					}
					case_num[run] = case_num[run] + 1 ;
					
					path = pathnum(v[i] , fun_num) ;
					record[i] = path;
					if( !status[path] )
					{
						for(int j = 0 ; j < R ; j++)
							solution[path][j] = v[i][j];
						status[path] = true ;               
						convergence[run][obj] = case_num[run] + 1;
						obj++ ;                            
						nodeiscoverage(v[i] , fun_num) ;   
					}
					
					if(obj == PATHNUM)
						break ;					
				}
				
				for(int i = 0 ; i < pop_num ; i++ )
				{
					if(obj == PATHNUM)
					{
						Cycle[run]++ ;
						break ;
					}
					fitness_x[i] = benchmarkfunction(x[i] , fun_num, -1) ;
					fitness_v[i] = benchmarkfunction(v[i] , fun_num, -1);
					//case_num[run] = case_num[run] + 2;           //������������
					
					if(fitness_v[i] > fitness_x[i] )		   //step 6���Ƚϸ��²�������
					{
						for( int j = 0; j < R; j++)
							x[i][j] = v[i][j] ;		
						fitness_x[i] = fitness_v[i];
					}
				}
				
				cycle++;
				
				/*�������ӿ�ܲ�����
				 * �ȸ������в�������ά��һ��n*m�ľ��󣬸þ����У�i,j����ÿһ��������ά��i��ȡֵ�����j���ڵ���ֶ�Ӧ��������·���Ĵ���
				 * Ŀǰ������������˳���֧�ṹ�����������лᾭ��ÿһ���жϷ�֧������Ϊn���жϽڵ�ĳ���·�����ó���Ϊn�Ķ���������ʾ*/
				
				if(obj == PATHNUM)
					break;                                   //�ж�·���Ƿ�ȫ�����ǣ����ȫ���������˳�ѭ��
				
				for(int i=0;i < pop_num;i++)					//�������и���
				{
					/* ���ݸ��帲��·����ʣ��·�����Ƴ̶ȣ���������ͬ�ڵ���Ŀ�����������̶ĵ���ʽѡ��һ��·����ΪĿ�ꡣ
					 * ������RP�������̾���ʹ���Ż����壬����ѡ���Ŀ��·����������*/
					int target_path = random_UncoverPath(status,record[i]);		//����Ŀ��·��
					
					for(int node=0;node<PATH[target_path].length();node++){
						if(PATH[target_path].charAt(node)!=PATH[record[i]].charAt(node)){
							//�����Ҫ���ǵ�·���Ͳ���������ǰ���ǵ�·���ķ�֧�ڵ�����ԱȲ�һ�������Ӱ��ڵ�����ά�Ƚ�������
							int tempare = 0;
							for(int r=0;r<R;r++)
								tempare += infection[r][node];
							
							int firstPath;  //��¼��ʼ�����������ǵ�·��
							
							if(tempare==0)
							{//���û��ά��Ӱ�죬��ʱֻ�����
								int j = (int)(Math.random()*R);                      //�����ȡ����ı����±�
								
								int best;                                      //��¼���������Ÿ����ֵ
								double[] fitness_temp = new double[step_length+1];              //�ݴ��������и�����Ӧֵ
								
								int step;//��ʼ������
								best = x[i][j];//��ʼ���������Ÿ���ֵ
								firstPath = pathnum(x[i],fun_num);//��¼����ǰ���ǵ�·��
								
								step = (ub[j]-lb[j])/step_length;
								
								while(step>=1){//����������Ŀ����step_length���
									int[] temp = getIndex(lb[j],ub[j],best,step);//temp�ݴ���������ֵ
									
									for(int k=0;k<step_length+1;k++){
										x[i][j] = temp[k]; //�滻��������ֵ
										if(x[i][j]>ub[j]||x[i][j]<lb[j])         //������Χ����
										{
											double r3 = Math.random();
											x[i][j] = (int)(lb[j] + r3 * (ub[j]-lb[j]));
										}
										path = pathnum(x[i], fun_num);                       //��ȡ���ǵ�·��
									
										update_Infection(firstPath,path,j, x[i][j]);
										
										if(!status[path])
										{
											for(int t=0;t<R;t++)
												solution[path][t] = x[i][t];                 //��¼·����һ��
											status[path] = true;                             //���·��Path�Ƿ����ҵ�������������
											convergence[run][obj] = case_num[run] + 1;
											obj++;                                           //�Ѹ��ǵ�·����
											nodeiscoverage(x[i],fun_num);                    //����ѱ����ǵķ�֧
										}
										
										if(obj == PATHNUM)
											break;
										
										if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
											target_path = random_UncoverPath(status,path);
										
										fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path);     //��������
										case_num[run] = case_num[run] + 1;           //������������
										num1[run] = num1[run] + 1;
									}
									int best_index = getBestIndex(fitness_temp);
									x[i][j] = temp[best_index];
									best = temp[best_index];
									fitness_x[i] = fitness_temp[best_index];
									
									step = step/step_length;                                  //step�½�����
									
									if(PATH[target_path].charAt(node)==PATH[record[i]].charAt(node))
										break;
								}
								//����������ĿС��step_length���
								int[] temp = getIndex(lb[j],ub[j],best,step);
								for(int k=0;k<step_length+1;k++){
									x[i][j] = temp[k]; // �滻��������ֵ
									if(x[i][j]>ub[j]||x[i][j]<lb[j])         //������Χ����
									{
										double r3 = Math.random();
										x[i][j] = (int)(lb[j] + r3 * (ub[j]-lb[j]));
									}
									path = pathnum(x[i], fun_num); // ��ȡ���ǵ�·��
									
									/* �ж���ά�������������㷨�Ƿ��и��ǵ�һ����ͬ��·����
									 * ����У���˵����ά�ȵĸı�Խڵ��������Ӱ��*/
									update_Infection(firstPath,path,j, x[i][j]);
									
									if (!status[path]) {
										for (int t = 0; t < R; t++)
											solution[path][t] = x[i][t]; // ��¼·����һ��
										status[path] = true; // ���·��Path�Ƿ����ҵ�������������
										convergence[run][obj] = case_num[run] + 1;
										obj++; // �Ѹ��ǵ�·����
										nodeiscoverage(x[i], fun_num); // ����ѱ����ǵķ�֧
									}
									if (obj == PATHNUM)
										break;
									
									if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
										target_path = random_UncoverPath(status,path);

									fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path); // ��������
									case_num[run] = case_num[run] + 1; // ������������
									num1[run] = num1[run] + 1;
								}
								
								int best_index = getBestIndex(fitness_temp);
								x[i][j] = temp[best_index];
								fitness_x[i] = fitness_temp[best_index];
								
							}else{//�в��ֻ���ȫ��ά����Ӱ�죬��������̶�ѡ��ά���Ż�
								double rand[] = new double[R];
								for(int r=0;r<R;r++)
									rand[r] = ((double)infection[r][node]+1)/(tempare+R);
								//����ضȣ����̶�ѡ��ά��
								double random = Math.random();
								int index =0;
								double bound[] = new double[R];
								for(int r=0;r<R;r++)
									for(int j=0;j<=r;j++)
										bound[r] += rand[j]; //�������̶Ŀ̶�
								for(int r=0;r<R;r++)
									if(random<bound[r]){
										index = r;           //��ȡ���̶ĶĲ��õ��Ŀ̶ȣ�ѡ��ά��index�����Ż�
										break;
									}
								int j = index;
								
								firstPath = pathnum(x[i],fun_num);//��¼����ǰ���ǵ�·��
								///���ýڵ��Ƿ��з�֧�浵		
								// PATH[target_path].charAt(node)!= 32 && archives[node][j][PATH[target_path].charAt(node) - '0'] != 0
								if(PATH[target_path].charAt(node)!= 32 && NBArchives[node][j][PATH[target_path].charAt(node) - '0'] != Integer.MIN_VALUE) {
									x[i][j] = NBArchives[node][j][PATH[target_path].charAt(node) - '0'];	
									path = pathnum(x[i], fun_num);//��ȡ���ǵ�·��
									record[i] = path;
									update_Infection(firstPath,path,j,x[i][j]);
									
									if(!status[path])
									{
										for(int t=0;t<R;t++)
											solution[path][t] = x[i][t];                 //��¼·����һ��
										status[path] = true;                             //���·��Path�Ƿ����ҵ�������������
										convergence[run][obj] = case_num[run] + 1;
										obj++;                                           //�Ѹ��ǵ�·����
										nodeiscoverage(x[i],fun_num);                    //����ѱ����ǵķ�֧
									}
									
									if(obj == PATHNUM)
										break;
									
									if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
										break;
									fitness_x[i] = benchmarkfunction(x[i], fun_num, target_path);     //��������
									case_num[run] = case_num[run] + 1;           //������������
									num2[run] = num2[run] + 1;
								}else{								
									/* ��ά���������ȴ󲽳�����С���� */
									int best; // ��¼���������Ÿ����ֵ
									double[] fitness_temp = new double[step_length+1]; // �ݴ��������и�����Ӧֵ
	
									int step;// ��ʼ������
									best = x[i][index];// ��ʼ���������Ÿ���ֵ
									step = (ub[index]-lb[index])/step_length;
									
									while(step>=1){//����������Ŀ����step_length���
										int[] temp = getIndex(lb[j],ub[j],best,step);//temp�ݴ���������ֵ
										
										for(int k=0;k<step_length+1;k++){
											x[i][j] = temp[k]; //�滻��������ֵ
											if(x[i][j]>ub[j]||x[i][j]<lb[j])         //������Χ����
											{
												double r3 = Math.random();
												x[i][j] = (int)(lb[j] + r3 * (ub[j]-lb[j]));
											}
											path = pathnum(x[i], fun_num);                       //��ȡ���ǵ�·��
										
											update_Infection(firstPath,path,j, x[i][j]);
											
											if(!status[path])
											{
												for(int t=0;t<R;t++)
													solution[path][t] = x[i][t];                 //��¼·����һ��
												status[path] = true;                             //���·��Path�Ƿ����ҵ�������������
												convergence[run][obj] = case_num[run] + 1;
												obj++;                                           //�Ѹ��ǵ�·����
												nodeiscoverage(x[i],fun_num);                    //����ѱ����ǵķ�֧
											}
											
											if(obj == PATHNUM)
												break;
											
											if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
												target_path = random_UncoverPath(status,path);
											
											fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path);     //��������
											case_num[run] = case_num[run] + 1;           //������������
											num3[run] = num3[run] + 1;
										}
										int best_index = getBestIndex(fitness_temp);
										x[i][j] = temp[best_index];
										best = temp[best_index];
										fitness_x[i] = fitness_temp[best_index];
										
										step = step/step_length;                                  //step�½�����
										
										if(PATH[target_path].charAt(node)==PATH[record[i]].charAt(node))
											break;
									}
									//����������ĿС��step_length���
									int[] temp = getIndex(lb[j],ub[j],best,step);
									for(int k=0;k<step_length+1;k++){
										x[i][j] = temp[k]; // �滻��������ֵ
										if(x[i][j]>ub[j]||x[i][j]<lb[j])         //������Χ����
										{
											double r3 = Math.random();
											x[i][j] = (int)(lb[j] + r3 * (ub[j]-lb[j]));
										}
										path = pathnum(x[i], fun_num); // ��ȡ���ǵ�·��
										
										/* �ж���ά�������������㷨�Ƿ��и��ǵ�һ����ͬ��·����
										 * ����У���˵����ά�ȵĸı�Խڵ��������Ӱ��*/
										update_Infection(firstPath,path,j, x[i][j]);
										
										if (!status[path]) {
											for (int t = 0; t < R; t++)
												solution[path][t] = x[i][t]; // ��¼·����һ��
											status[path] = true; // ���·��Path�Ƿ����ҵ�������������
											convergence[run][obj] = case_num[run] + 1;
											obj++; // �Ѹ��ǵ�·����
											nodeiscoverage(x[i], fun_num); // ����ѱ����ǵķ�֧
											System.out.println(case_num[run]);
										}
										if (obj == PATHNUM)
											break;
										
										if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
											target_path = random_UncoverPath(status,path);
	
										fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path); // ��������
										case_num[run] = case_num[run] + 1; // ������������
										num3[run] = num3[run] + 1;
									}
									
									int best_index = getBestIndex(fitness_temp);
									x[i][j] = temp[best_index];
									fitness_x[i] = fitness_temp[best_index];
								}
							}
						}
					}
					if(obj == PATHNUM)
						break;                                   //�ж�·���Ƿ�ȫ�����ǣ����ȫ���������˳�ѭ��
				}
			}
			
			Date mydate2 = new Date();
			finish = mydate2.getTime();
			runtime[run] = finish - start;
			System.out.println();
			System.out.println("����ʱ��="+runtime[run]+"ms");             //�������ʱ��
			System.out.println("NO. of cycles=" + (Cycle[run]-1));        //���Number of Cycle 
  			coverage[run] = obj*100/PATHNUM;
			System.out.println("·��������=" + coverage[run] +"%");
			System.out.println("���Ž�Ϊ��");
			 
			for( int k = 0; k < PATHNUM ; k++ )     //���·���������������·���Ĳ��������Լ�δ���ǵ�·��
			{
				if(status[k])
				{
				    System.out.print("path"+k+":"); 
				    for(int j = 0; j < R ; j++ )
					     System.out.print((int)solution[k][j]+" ");
                    System.out.println();
				}
				else
					System.out.println("path"+k+"û������."); 
			}
			System.out.println("case_num["+run+"] = " + case_num[run] ); 	
			System.out.println("num1["+run+"] = " + num1[run] ); 	
			System.out.println("num2["+run+"] = " + num2[run] ); 	
			System.out.println("num3["+run+"] = " + num3[run] ); 
			
			//���infection����
			System.out.println("\n"+"infection����:");
			for(int r=0;r<R;r++){
				for(int n=0;n<NODENUM;n++)
					System.out.print(infection[r][n]+"  ");
				System.out.println();
			}
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
		System.out.println("\n"+"�����ٶ�:");
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
		
		try  //�����ݵ�����Excel�ĵ���
		{
			File file = new java.io.File("D:\\Desktop Files\\Postgraduate\\��������\\Experimental\\�ڵ��֧�浵 VS RP-DE\\otherFuntion" + fun_num +".xls");
			Workbook book = Workbook.getWorkbook(file);
			WritableWorkbook wbook = Workbook.createWorkbook(file,book);
			WritableSheet sheet = wbook.getSheet(0);       //д������sheet
			
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
	
	/*������i�������ά��j,�Ƚϸ��ǵ��¾�����·�������·�������ı䣬˵��ά��j�Խڵ��������Ӱ�죬����infection����*/
//	public static void update_Infection(int firstPath, int path, int j)
//	{
//		if(firstPath!=path){
//			for(int length=0;length<NODENUM;length++)
//				if(PATH[firstPath].charAt(length)!=PATH[path].charAt(length)){
//					infection[j][length]++;
//				}
//		}
//	}
	public static void update_Infection(int firstPath, int path, int j, int value)
	{
		if(firstPath!=path){
			for(int length=0;length<NODENUM;length++)
				if(PATH[firstPath].charAt(length)!=PATH[path].charAt(length)){
					if(PATH[firstPath].charAt(length)!=' '&&PATH[path].charAt(length)!=' ') {
						infection[j][length]++;
						NBArchives[length][j][PATH[path].charAt(length) - '0'] = value;
					}
				}
		}
	}

	// ����status·��������������̶�ѡ��һ��һ��δ���ǵ�·������Ϊ���������Ż�Ŀ��
	public static int random_UncoverPath(boolean[] status,int path) {

		int[] similar = new int[PATHNUM];
		for(int i=0;i<PATHNUM;i++)
		{
			for(int j=0;j<NODENUM;j++)
			{
				if(PATH[path].charAt(j)==PATH[i].charAt(j))
					similar[i]++;			//ͳ��path������·��֮��Ĳ���̶�
			}
		}
		int[] possible = new int[PATHNUM];
		for(int i=0;i<PATHNUM;i++)
			if(!status[i])
				possible[i] = similar[i];
			else possible[i] = 0;
		//����possible[]�������̶�ѡ��Ŀ��·��
		int temp=0;
		for(int i=0;i<PATHNUM;i++)
			temp+=possible[i];	//ͳ������
		double rand[] = new double[PATHNUM];
		for(int i=0;i<PATHNUM;i++)
			rand[i]=((double)possible[i])/temp;		//ͳ�Ƹ��Եĸ���
		double random = Math.random();
		int index=0;
		double bound[] = new double[PATHNUM];
		for(int i=0;i<PATHNUM;i++)
			for(int j=0;j<=i;j++)
				bound[i] += rand[j];	//�������̶Ŀ̶�
		for(int i=0;i<PATHNUM;i++)
			if(random<bound[i]){
				index = i;
				break;
			}
		return index;
	}
	
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
	
	public static int checksum(String ISBN){
		int sum=0; int k=0;
		for(int i=0;i<ISBN.length();i++){
			if(ISBN.charAt(i)-'0'>=0&&ISBN.charAt(i)-'0'<=9){
				k++;
				sum+=(ISBN.charAt(i)-'0')*k;
			}
			if(ISBN.charAt(i)=='X'||ISBN.charAt(i)=='x')
			{
				k++;
				sum+=10*k;
			}
		}
		return sum;
	}
	
	public static int pathnum(int[] x , int func_num)
	{

		int path = -1;
		if(func_num == 1) //Triangle
		{
			int a = x[0] ;
			int b = x[1] ;
			int c = x[2] ;		
					
			if((a<(b+c)) && (b<(a+c)) && (c<(a+b)))	//������	//�ڵ� 0
			{	
    			if ( ((a==b) && (a!=c))	 || ((a==c)&&(a!=b)) || ((b==c)&&(b!=a)) )	//����������	//�ڵ� 1  
    				path = 0 ;
    			if ( (a==b) && (a==c) )	//�ȱ�������	//�ڵ� 1	  
    				path = 1  ;
    			if ( (a!=b) && (a!=c) && (b!=c) )	//һ��������	//�ڵ� 2	  
    				path = 2 ;
    		}
			else	//��������
			{
				path = 3 ;  
			}
		}
		
		if(func_num == 2) //Factorial �׳�
		{
			int a = x[0] ;
			
			if(a==1)	//�ڵ� 0
				path = 0 ;
			else
				path = 1 ;
		}
		
		if(func_num == 3)  //bubble sorting ð������
		{
			int i1,j1;
			int[] a = new int[R];
		   	for(i1 = 0; i1 < R; i1++) {
		   		a[i1] = x[i1];
		   	}
		   	for(j1 = 0;j1 <= R-1; j1++) {
		   		 for (i1 = 0; i1< R-1-j1; i1++){
		   			 if(a[i1] > a[i1+1]){	//�ڵ� 0
		   				 path = 0 ; 
		   				 break ;
		   			}
		   		 }
		   		 if(path == 0) break;	   		  
		   	}
		   	if(path != 0)
		   		path = 1 ;
		}
		
		if(func_num == 4)   //GCD ���Լ��
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
		   	
		   	if(d[0] && d[1])	//�ڵ� 0
		   		path = 0 ;
		   	else if(d[0] && (!d[1]))	//�ڵ� 1
		   		path = 1 ;
		   	else if((!d[0]) && d[1])	//�ڵ� 2
		   		path = 2 ;
		   	else
		   		path = 3 ;
		}
		
		if(func_num == 5)  //Middle ��λ��
		{
			  int a = x[0] ;
		      int b = x[1] ;
		   	  int c = x[2] ;
		   	  		  		
	   		  if( ( (a < b) && (b < c) ) || ((c < b) && (b < a)) )	//b Ϊ Middle	//�ڵ� 0
	   			  path = 0 ;	   		    
	   		  else if ( ( (a < c) && (c < b) ) || ((b < c) && (c < a)) )	//c Ϊ Middle	//�ڵ�1
	   			  path = 1 ; 
	   		  else if ( ( (b < a) && (a < c) ) || ((c < a) && (a < b)) )	//a Ϊ Middle	//�ڵ� 2
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
      	    
      	    if (Day == 7)	//�ڵ� 0
      	    {
      	     	if (Month == 12 && Date == 31)	//�ڵ� 1
      	     		path = 0 ;
    	   	    else if(Month == 2 && Date == 28)	//�ڵ� 2
    	   	    {
    	   	    	if(isRun(Year))	//�ڵ� 3
    	   	    		path = 1 ;
    	   	    	else
    	   	    		path = 2 ;
    	   	    }
    	   	    	
    	   	    else if((Month != 12  && Date == 31) || (Month == 2 && Date == 29)	//�ڵ� 4
    	   	    		|| ((Month == 4 || Month == 6 || Month == 9 || Month == 11) && Date == 30))
    	   	    	path = 3 ;
    	   	    else 
    	   	    	path = 4 ;
      	    }
      	    else
      	    {
      	    	if (Month == 12 && Date == 31)	//�ڵ� 1
      	     		path = 5 ;
    	   	    else if(Month == 2 && Date == 28)	//�ڵ� 2
    	   	    {
    	   	    	if(isRun(Year))	//�ڵ� 3
    	   	    		path = 6 ;
    	   	    	else
    	   	    		path = 7 ;
    	   	    }
    	   	    	
    	   	    else if((Month != 12  && Date == 31) || (Month == 2 && Date == 29)	//�ڵ� 4
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

			if(sales > 1800.0)	//�ڵ� 0
			  path = 0;
			else if(sales > 500.0)	//�ڵ� 1
			  path = 1 ;
			else
	          path = 2 ;
		}
		if(func_num == 8) //premium
		{
			int  driverage = x[0] ;
			int  points = x[1] ;
			
			if(driverage >=16 && driverage < 20)		//�ڵ� 0
			{			 
			    if(points <= 1)	//�ڵ� 1
			      path = 0 ;
			    else
			      path = 1 ;
			}
			else if(driverage >= 20 && driverage < 25)	//�ڵ� 2	
			{
			    if(points < 3)	//�ڵ� 3
			       path = 2 ; 
			    else
			       path = 3 ; 
			}
			else if(driverage >= 25 && driverage < 45)		//�ڵ� 4	
			{
			    if(points < 5)	//�ڵ� 5
			       path = 4 ; 
			    else
			       path = 5 ; 
			}
			else if(driverage >= 45 && driverage < 60)		//�ڵ� 6
			{
			    if(points < 7)	//�ڵ� 7
			    	path = 6  ;
			    else 
			    	path = 7 ;
			}
			else if(driverage >= 60 && driverage < 100)		//�ڵ� 8
			{
			    if(points < 5)	//�ڵ� 9
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
		double[] f = new double[NODENUM] ;  //f[k]��ʾ�ڵ�k��Yes��֧���δ������ʱ����������ִ�и÷�֧�ĳͷ�ֵ
		double[] F = new double[NODENUM] ;  //F[k]��ʾ�ڵ�k��No��֧���δ������ʱ����������ִ�и÷�֧�ĳͷ�ֵ
		double[] fit = new double[NODENUM] ;  //fit[k]��ʾ�������������ڵ�kʱ���ڸýڵ����Ӧֵ
		double Fitness = 0 ;    //������������Ӧֵ
		
		if(func_num == 1)   //Triangle
		{
			int a = x[0] ;
			int b = x[1] ;
			int c = x[2] ;		
					
    		double v1,v2,v3,v4,v5,v6,v7 ;	   		   	   		
	   		
    		if(a<(b+c)) v1 = 0;        //��������ִ�е�һ���ڵ��Yes��֧ʱ�ĳɱ�ֵf[0]��
	   		else v1 = a-(b+c)+K ;
	   		if(b<(a+c)) v2 = 0 ;
	   		else v2 = b-(a+c) + K ;
	   		if(c<(a+b)) v3 = 0;
	   		else v3 = c-(a+b) + K ;	  		
	   		f[0] = v1 + v2 + v3 ;  
	   		
	   		if(a==b) v1 = 0 ;      //��������ִ�еڶ����ڵ��Yes��֧ʱ�ĳɱ�ֵf[1]��
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
	   		
	   		if(a==b) v1 = 0 ;     //��������ִ�е������ڵ��Yes��֧ʱ�ĳɱ�ֵf[2]��
	   		else v1 = Math.abs(a-b)+K ;
	   		if(a==c) v2 = 0;
	   		else v2 = Math.abs(a-c)+K ;
	   		f[2] = v1 + v2 ;
	   		
	   		if(a!=b) v1 = 0 ;   //��������ִ�е��ĸ��ڵ��Yes��֧ʱ�ĳɱ�ֵf[3]��
	   		else v1 = K ;
	   		if(a!=c) v2 = 0 ;
	   		else v2 = K ;
	   		if(b!=c) v3 = 0 ;
	   		else v3 = K ;
	   		f[3] = v1 + v2 + v3 ;

	   		if(a>=(b+c)) v1 = 0 ;   //��������ִ�е�һ���ڵ��No��֧ʱ�ĳɱ�ֵF[0]��
	   		else v1 = (b+c)-a+K ;
	   		if(b>=(a+c)) v2 = 0;
	   		else v2 = (a+c)-b+K ;
	   		if(c>=(a+b)) v3 = 0 ;
	   		else v3 = (a+b)-c+K ;
	   		v4 = Math.min(v1, v2);
	   		F[0] = Math.min(v4, v3);
   		
	   		if(a!=b) v1 = 0 ;       //��������ִ�еڶ����ڵ��No��֧ʱ�ĳɱ�ֵF[1]��
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
  		
	   		if(a!=b) v1 = 0 ;     //��������ִ�е������ڵ��No��֧ʱ�ĳɱ�ֵF[2]��
	   		else v1 = K ;
	   		if(a!=c) v2 = 0 ;
	   		else v2 = K ;
	   		F[2] = Math.min(v1, v2);
   		
	   		if(a==b) v1 = 0 ;    //��������ִ�е��ĸ��ڵ��No��֧ʱ�ĳɱ�ֵF[3]��
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
//    			if ( ((a==b) && (a!=c))	 || ((a==c)&&(a!=b)) || ((b==c)&&(b!=a)) )	 //���������� 
//    				Fitness = fit[0] + fit[1] + fit[2] + fit[3] ;
//    			if ( (a==b) && (a==c) )	//�ȱ�������
//    				Fitness = fit[0] + fit[1] + fit[2] + fit[3] ;
//    			if ( (a!=b) && (a!=c) && (b!=c) )	//һ��������
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
	 	if(path_num == -1)          //û��Ŀ��·�������
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
		else{                        //����Ŀ��·�������
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
	
	/*������Ӧֵ�Ӹߵ��������Ӧ�����*/
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
			       /**���ҵ� i�������ֱ�����µ� i������λ��***/
			       for(int j=i+1;j<fitness.length;j++)
			       {
			            if(fitness[max]<fitness[j]) 
			            max = j;//���½ϴ���λ�ã��ٴαȽϣ�ֱ�����
			       }
			        /***����� i������λ�ò��� i,�򽻻�****/
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
	 
	//��ȡƽ��ֵ
	  static double getAverage(int[] array , int num){
	      int sum = 0;
	      for(int i = 0;i < num;i++){
	          sum += array[i];
	      }
	      return (double)(sum / num);
	  }
	 
	  //��׼��
	  static double getStandardDevition(int[] array , int num){
	      double sum = 0;
	      for(int i = 0;i < num;i++){
	          sum += Math.sqrt(((double)array[i] -getAverage(array, num)) * (array[i] -getAverage(array, num)));
	      }
	      return (sum / (num - 1));
	  } 
	  //�������ֵ�±�
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
			int[] index = new int[5];
			int temp = -1;
			if (point + 2 * step <= ub && point - 2 * step >= lb) {
				for (int i = 0; i < 5; i++)
					index[i] = step * (i - 2) + point;
			} else {
				for (int i = 1; i < 5; i++)
					if (point - i * step < lb) {
						temp = i;
						break;
					}
				if (temp == -1) {
					for (int i = 0; i < 5; i++)
						index[i] = point - step * (4 - i);
				} else {
					for (int i = 0; i < 5; i++)
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
