package iFogSimFunction;

import java.io.File;
import java.util.Date;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * 6��benchmark������1)transmit ;2)send ;3)processTupleArrival ;4)executeTuple ;5)checkCloudletCompletion ;6)getRsultantTuples
 * (������ţ��������ά�ȣ�����·����Ŀ������·����Ŀ)
 * transmit(1,3,2,1) 100%; send(2,2,9,5)66%; processEvent(3,7,9,6)100%; executeTuple(4,7,5,3)100%; 
 * checkCloudletCompletion(5,5,6,3)100%; getResultantTuple(6,8,7,4)100%
 */

public class RP_DE {

	private static final int RUN = 30;                       //���д���
	private static final int pop_num = 50;                   //��Ⱥ��С
	
	/*��Ӧֵ�������*/
	private static final int K = 10;               
	private static final double alpha = 0.001;     
	
	/*DE�㷨����*/
	private static final double Pc = 0.2;
	private static final double F = 0.5;
	
	private static final int fun_num = 3;
	private static final int R = 7;
	private static final int PATHNUM = 9;
	private static final int NODENUM = 6;
	
	private static boolean[][] visit = new boolean[NODENUM][7];
	
	/* infection��һ��n*m�ľ���C
	 * ����C�����¼���������ĵ�i��ά�ȣ��Խڵ�j��Ӱ�����*/
	private static int[][]infection = new int[R][NODENUM];
	/* record�����¼��Ⱥÿ�����壨������������Ӧ�Ľڵ㸲�����*/
	private static int[] record = new int[pop_num];
	/*PATH��¼��ͬ·���ķ�֧�ڵ�����*/
	private static String[] PATH = new String[PATHNUM];
	/*��������*/
	private static final int step_length = 4;
	
	/*��ʼ����ʱ�䡢����ʱ�䡢�����ʡ�ѭ������������������Ŀ*/
    static double start;                                             
    static double finish;
    static double[] runtime = new double[RUN];
    static double[] coverage = new double[RUN];
    static int[] case_num = new int[RUN];
    static int[] Cycle = new int[RUN];

	
	private static final int MCN = 300000;                  //����������
	private static final int col = 3;
	
	public static void main(String[] args){
		/*��������������½�*/
		int[] lb = new int[R];
		int[] ub = new int[R];
		
		// transmit(1,3,2,1) 100%;   
		if(fun_num==1){
			for(int i=0;i<R;i++)
			{
				lb[i] = 0;    //��ʼ�����½�
				ub[i] = 255;
			}
		}//send(2,2,9,5)66%;
		if(fun_num==2){
			for(int i=0;i<R;i++)
			{
				lb[i] = -1000000;    //��ʼ�����½�
				ub[i] = 1000000;
			}
		}//processEvent(3,7,9,6)100%;
		if(fun_num==3){
			lb[0]=0;ub[0]=100;
			lb[1]=0;ub[1]=4;
			lb[2]=-2;ub[2]=10000;
			lb[3]=0;ub[3]=2;
			lb[4]=0;ub[4]=10000;
			lb[5]=0;ub[5]=10000;
			lb[6]=-2;ub[6]=100000;
		}//executeTuple(4,7,5,3)100%; 
		if(fun_num==4){
			lb[0]=1;ub[0]=3;
			for(int i=1;i<R;i++)
			{
				lb[i] = 0;    //��ʼ�����½�
				ub[i] = 255;
			}
		}//checkCloudletCompletion(5,5,6,3)100%;
		if(fun_num==5){
			lb[0]=0;ub[0]=2;
			for(int i=1;i<R-1;i++)
			{
				lb[i] = 0;    //��ʼ�����½�
				ub[i] = 255;
			}
			lb[R-1]=0;ub[R-1]=2;
		}//  getResultantTuple(6,8,7,4)100%*/
		if(fun_num==6){
			for(int i=0;i<6;i++)
			{
				lb[i] = 0;    //��ʼ�����½�
				ub[i] = 255;
			}
			lb[6]=0;ub[6]=2;
			lb[7]=1;lb[7]=4;
		}//getPhysicalTopology(7,110,7,6)
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
		case 7:
			PATH[0] = "0     ";
			PATH[1] = "100   ";
			PATH[2] = "1010  ";
			PATH[3] = "10110 ";
			PATH[4] = "10111 ";
			PATH[5] = "11   0";
			PATH[6] = "11   1";
			break;
		case 8:
			PATH[0] = "000  ";
			PATH[1] = "001  ";
			PATH[2] = "01 0 ";
			PATH[3] = "01 10";
			PATH[4] = "01 11";
			PATH[5] = "1    ";
			break;
		case 9:
			PATH[0] = "0  ";
			PATH[1] = "100";
			PATH[2] = "101";
			PATH[3] = "102";
			PATH[4] = "103";
			PATH[5] = "104";
			PATH[6] = "105";
			PATH[7] = "106";
			PATH[8] = "110";
			PATH[9] = "111";
			PATH[10] = "112";
			PATH[11] = "113";
			PATH[12] = "114";
			PATH[13] = "115";
			PATH[14] = "116";
			break;
		case 10:
		case 11:
			PATH[0] = "0    ";
			PATH[1] = "10   ";
			PATH[2] = "1100 ";
			PATH[3] = "1101 ";
			PATH[4] = "111  ";
			PATH[5] = "2   0";
			PATH[6] = "2   1";
			break;
		case 12:
			PATH[0] = "0  ";
			PATH[1] = "100";
			PATH[2] = "101";
			PATH[3] = "102";
			PATH[4] = "103";
			PATH[5] = "104";
			PATH[6] = "105";
			PATH[7] = "106";
			PATH[8] = "110";
			PATH[9] = "111";
			PATH[10] = "112";
			PATH[11] = "113";
			PATH[12] = "114";
			PATH[13] = "115";
			PATH[14] = "116";
			break;
		case 13:
			PATH[0] = "0      ";
			PATH[1] = "10     ";
			PATH[2] = "110    ";
			PATH[3] = "1110   ";
			PATH[4] = "11110  ";
			PATH[5] = "111110 ";
			PATH[6] = "1111110";
			PATH[7] = "1111111";
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
						if(v[i][j] > ub[j] || v[i][j] < lb[j]) 
						{
						   double r01 = Math.random() ;
						   v[i][j] = (int)( lb[j] + r01 * ( ub[j] - lb[j]) );
						}	
					}
					
					path = pathnum(v[i] , fun_num) ;
					record[i] = path;
					if( !status[path] )
					{
						for(int j = 0 ; j < R ; j++)
							solution[path][j] = v[i][j];
						status[path] = true ;               
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
					case_num[run] = case_num[run] + 2;           //������������
					
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
				
				for(int i=0;i<pop_num;i++)					//�������и���
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
									
										update_Infection(firstPath,path,j);
										
										if(!status[path])
										{
											for(int t=0;t<R;t++)
												solution[path][t] = x[i][t];                 //��¼·����һ��
											status[path] = true;                             //���·��Path�Ƿ����ҵ�������������
											obj++;                                           //�Ѹ��ǵ�·����
											nodeiscoverage(x[i],fun_num);                    //����ѱ����ǵķ�֧
										}
										
										if(obj == PATHNUM)
											break;
										
										if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
											target_path = random_UncoverPath(status,path);
										
										fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path);     //��������
										case_num[run] = case_num[run] + 1;           //������������
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
									update_Infection(firstPath,path,j);
									
									if (!status[path]) {
										for (int t = 0; t < R; t++)
											solution[path][t] = x[i][t]; // ��¼·����һ��
										status[path] = true; // ���·��Path�Ƿ����ҵ�������������
										obj++; // �Ѹ��ǵ�·����
										nodeiscoverage(x[i], fun_num); // ����ѱ����ǵķ�֧
									}
									if (obj == PATHNUM)
										break;
									
									if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
										target_path = random_UncoverPath(status,path);

									fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path); // ��������
									case_num[run] = case_num[run] + 1; // ������������
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
									
										update_Infection(firstPath,path,j);
										
										if(!status[path])
										{
											for(int t=0;t<R;t++)
												solution[path][t] = x[i][t];                 //��¼·����һ��
											status[path] = true;                             //���·��Path�Ƿ����ҵ�������������
											obj++;                                           //�Ѹ��ǵ�·����
											nodeiscoverage(x[i],fun_num);                    //����ѱ����ǵķ�֧
										}
										
										if(obj == PATHNUM)
											break;
										
										if(status[target_path])					//���ѡ���·���Ѿ������ǣ���������Ŀ��·��
											target_path = random_UncoverPath(status,path);
										
										fitness_temp[k] = benchmarkfunction(x[i], fun_num, target_path);     //��������
										case_num[run] = case_num[run] + 1;           //������������
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
									update_Infection(firstPath,path,j);
									
									if (!status[path]) {
										for (int t = 0; t < R; t++)
											solution[path][t] = x[i][t]; // ��¼·����һ��
										status[path] = true; // ���·��Path�Ƿ����ҵ�������������
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
								}
								
								int best_index = getBestIndex(fitness_temp);
								x[i][j] = temp[best_index];
								fitness_x[i] = fitness_temp[best_index];
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

			//���infection����
			System.out.println("\n"+"infection����:");
			for(int r=0;r<R;r++){
				for(int n=0;n<NODENUM;n++)
					System.out.print(infection[r][n]+"  ");
				System.out.println();
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
			File file = new java.io.File("D:\\Desktop Files\\Postgraduate\\��������\\Experimental\\�ڵ��֧�浵 VS RP-DE\\iFogSimFunction" + fun_num +".xls");
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
			
			 		
			wbook.write(); 	
			wbook.close();
			
		}catch(Exception e)
		{
			System.out.println(e);
		}	
	}
	
	public static void update_Infection(int firstPath, int path, int j)
	{
		if(firstPath!=path){
			for(int length=0;length<NODENUM;length++)
				if(PATH[firstPath].charAt(length)!=PATH[path].charAt(length)){
					if(PATH[firstPath].charAt(length)!=' '&&PATH[path].charAt(length)!=' ')
						infection[j][length]++;
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
			int entityId = (int)x[0];
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
			if(entityId>=999999)
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
			int eventTime = (int)x[0];
			int type = (int)x[1];
			int dest = (int)x[2];
			int state = (int)x[3];
			int p = (int)x[4];
			int tag = (int)x[5];
			int src = (int)x[6];
			
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
			int Direction = (int)x[0];
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
			if((int)x[0]==0) isFinished=true;
			else isFinished=false;
			if((int)x[R-1]==0) cloudletCompleted=true;
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
			if((int)x[6]==0) canSelect = true;
			else canSelect = false;
			
			if(edge.equals("mod"))
				visit[0][0] = true;
			else visit[0][1] = true;
			
			if(pair.equals("001")||pair.equals("002")||pair.equals("003"))
				visit[1][0] = true;
			else visit[1][1] = true;
			
			if(canSelect) visit[2][0] = true;
			else visit[2][1] = true;
			
			if((int)x[7]==2) visit[3][0] = true;
			else visit[3][1] = true;
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
		
		if(func_num == 1)
		{
			char tuple[]= new char[R];
			for(int i=0;i<R;i++)
				tuple[i] = (char) x[i];
			
			if(tuple[0]=='O'&&tuple[1]=='l'&&tuple[2]=='d')	//�ڵ� 0
				path = 0;
			else
				path = 1;
		}
		if(func_num == 2)
		{
			int entityId = (int)x[0];
			double delay = x[1];
			
			if(entityId<0)	//�ڵ� 0
			{
				path = 0;
			}else{
				if(delay<0) //�ڵ� 1
				{
					delay = 0;
					if(delay>=999999) //�ڵ� 2
						path = 1;
					else if(entityId<0)	//�ڵ� 3
						path = 2;
					else if(entityId!=1)	//�ڵ� 4
						path = 3;
					else 
						path = 4;
						
				}
				else{
					if(delay>=999999)	//�ڵ� 2
						path = 5;
					else if(entityId<0)	//�ڵ� 3
						path = 6;
					else if(entityId!=1)	//�ڵ� 4
						path = 7;
					else 
						path = 8;
				}
			}
		}
		if(func_num == 3)
		{
			int eventTime = (int) x[0];
			int type = (int)x[1];
			int dest = (int)x[2];
			int state = (int)x[3];
			int p = (int)x[4];
			int tag = (int)x[5];
			int src = (int)x[6];
			
			if(eventTime<50)	//�ڵ� 0
				path = 0;
			else{
				if(type==0)	//�ڵ� 1
					path = 1;
				else if(type==3)	//�ڵ� 1
					path = 8;
				else if(type==2){	//�ڵ� 1
					if(src<0)	//�ڵ� 3
						path = 6;
					else
						path = 7;
				}else{
					if(dest<0)	//�ڵ� 2
						path = 2;
					else if(state==1){	//�ڵ� 4
						path = 5;
					}else{
						if(p==0||tag==9999)	//�ڵ� 5
							path = 3;
						else
							path = 4;
					}
				}
			}
		}
		if(func_num == 4)
		{
			int Direction = (int)x[0];
			String map1 = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			String map2 = String.valueOf((char)x[4])+String.valueOf((char)x[5])+String.valueOf((char)x[6]);
			
			if(Direction==1){	//�ڵ� 0
				if(map1.equals("001")||map1.equals("002")||map1.equals("003")){	//�ڵ� 1
					if(map2.equals("004")||map2.equals("005")||map2.equals("006"))	//�ڵ� 2
						path = 0;
					else
						path = 1;
				}else{
					if(map2.equals("004")||map2.equals("005")||map2.equals("006"))	//�ڵ� 2
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
			if((int)x[0]==0) isFinished=true;
			else isFinished=false;
			if((int)x[R-1]==0) cloudletCompleted=true;
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
			if((int)x[6]==0) canSelect = true;
			else canSelect = false;
			
			if(edge.equals("mod"))
			{
				if(pair.equals("001")||pair.equals("002")||pair.equals("003")){
					if(canSelect){
						if((int)x[7]==2)
							path = 0;
						else
							path = 1;
					}else
						path = 2;
				}else{
					if(canSelect){
						if((int)x[7]==2)
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
		double[] f = new double[NODENUM] ;  //f[k]��ʾ�ڵ�k��Yes��֧���δ������ʱ����������ִ�и÷�֧�ĳͷ�ֵ
		double[] F = new double[NODENUM] ;  //F[k]��ʾ�ڵ�k��No��֧���δ������ʱ����������ִ�и÷�֧�ĳͷ�ֵ
		double[] fit = new double[NODENUM] ;  //fit[k]��ʾ�������������ڵ�kʱ���ڸýڵ����Ӧֵ
		double Fitness = 0 ;    //������������Ӧֵ
		
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
			int entityId = (int)x[0];
			double delay = x[1];
			
			double v1=0,v2=0;
			
			//��֧�ڵ�1
			if(entityId<0) v1=0;
			else v1 = entityId+K;
			f[0] = v1;
			
			if(entityId>=0) v2=0;
			else v2 = 0-entityId+K;
			F[0] = v2;
			
			//��֧�ڵ�2
			if(delay<0) {v1=0;delay=0;}
			else v1 = delay+K;
			f[1] = v1;
			
			if(delay>=0) v2=0;
			else v2 = -delay+K;
			F[1] = v2;
				
			//��֧�ڵ�3
			if(delay>=999999) v1=0;
			else v1 = 999999-delay+K;
			f[2] = v1;
			
			if(delay<999999) v2=0;
			else v2 = delay-999999+K;
			F[2] = v2;
			
			//��֧�ڵ�4
			if(entityId<0) v1=0;
			else v1 = entityId+K;
			f[3] = v1;
			
			if(entityId>=0) v2 = 0;
			else v2 = 0-entityId+K;
			F[3] = v2;
			
			//��֧�ڵ�5
			if(entityId!=1) v1=0;
			else v1 = K;
			f[4] = v1;
			
			if(entityId==1) v2 = 0;
			else v2 = Math.abs(entityId+1)+K;
			F[4] = v2;
		}
		if(func_num == 3)
		{
			int eventTime = (int)x[0];
			int type = (int)x[1];
			int dest = (int)x[2];
			int state = (int)x[3];
			int p = (int)x[4];
			int tag = (int)x[5];
			int src = (int)x[6];
			
			double v1=0;
			//��֧�ڵ�0
			if(eventTime<50) v1=0;
			else v1 = eventTime-50+K;
			f[0] = v1;
			
			if(eventTime>=50) v1=0;
			else v1 = 50-eventTime+K;
			F[0] = v1;
			//��֧�ڵ�1
			if(type==0) {f[1] = 0;F[1] = K;}
			else if(type==1){f[1] = 0;F[1] = K;}
			else if(type==2){f[1] = 0;F[1] = K;}
			else {f[1] = K; F[1] =0;}
			//��֧�ڵ�2
			if(dest<0) v1=0;
			else v1 = dest+K;
			f[2] = v1;
			
			if(dest>=0) v1=0;
			else v1=-dest+K;
			F[2] = v1;
			//��֧�ڵ�3
			if(src<0) v1=0;
			else v1=src+K;
			f[3] =0;
			
			if(src>=0) v1=0;
			else v1= -src+K;
			F[3] = 0;
			//��֧�ڵ�4
			if(state==1)v1=0;
			else v1=Math.abs(state-1)+K;
			f[4]= v1;
			
			if(state==0) v1=0;
			else v1= Math.abs(state)+K;
			F[4] = v1;
			//��֧�ڵ�5
			if(p==0||tag==9999)v1=0;
			else v1=Math.min(Math.abs(p-0)+K,Math.abs(tag-9999)+K);
			f[5] = 0;
			
			if(p!=0&&tag!=9999)v1=0;
			else v1= 2*K;
		}
		if(func_num == 4)
		{
			int Direction = (int)x[0];
			String map1 = String.valueOf((char)x[1])+String.valueOf((char)x[2])+String.valueOf((char)x[3]);
			String map2 = String.valueOf((char)x[4])+String.valueOf((char)x[5])+String.valueOf((char)x[6]);
			
			double v1=0;
			
			//��֧�ڵ�1
			if(Direction==1) v1=0;
			else v1 = Math.abs(Direction-1)+K;
			f[0] = v1;
			
			if(Direction!=1) v1=0;
			else v1 = K;
			F[0] = v1;
			
			//��֧�ڵ�2
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
			
			//��֧�ڵ�3
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
			int p1 = (int)x[0], p2 = (int)x[R-1];
			
			double v1=0;
			//��֧�ڵ�1
			if(p1==0) v1=0;
			else v1=Math.abs(p1)+K;
			f[0] = v1;
			
			if(p1==1) v1=0;
			else v1 = Math.abs(p1-1)+K;
			F[0] = v1;
			
			//��֧�ڵ�2
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
			
			//��֧�ڵ�3
			if(p2==0) v1=0;
			else v1=Math.abs(p2)+K;
			f[2] = v1;
			
			if(p2==1) v1=0;
			else v1 = Math.abs(p2-1)+K;
			F[2] = v1;
		}
		if(func_num == 6)
		{
			String edge = String.valueOf((char)x[0])+String.valueOf((char)x[1])+String.valueOf((char)x[2]); 
			String pair = String.valueOf((char)x[3])+String.valueOf((char)x[4])+String.valueOf((char)x[5]); 
			
			double v1=0;
			//��֧�ڵ�1
			if(edge.equals("mod")) v1=0;
			else v1 = Math.abs((char)x[0]-'m')+Math.abs((char)x[1]-'o')+Math.abs((char)x[2]-'d')+3*K;
			f[0] = v1;
			
			if(!edge.equals("mod")) v1=0;
			else v1 = K;
			F[0] = v1;
			
			//��֧�ڵ�2
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
			//��֧�ڵ�3
			if((int)x[6]==0) v1 = 0;
			else v1 = Math.abs((int)x[6])+K;
			f[2] = v1;
			
			if((int)x[6]==1) v1 =0;
			else v1 = Math.abs((int)x[6]-1)+K;
			F[2] = v1;
			//��֧�ڵ�4
			if((int)x[7]==2) v1=0;
			else v1 = Math.abs((int)x[7]-2)+K;
			f[3] = v1;
			
			if((int)x[7]!=2) v1=0;
			else v1 = K;
			F[3] = K;
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
}
