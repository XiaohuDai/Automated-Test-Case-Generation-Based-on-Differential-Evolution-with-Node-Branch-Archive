# Automated-Test-Case-Generation-Based-on-Differential-Evolution-with-Node-Branch-Archive

## 1. Introduce

​We proposeed a node branch archive strategy, which can record the relationship between node branch direction and the value of test case variables, and cover more paths through this driven search-based algorithms. The experimental results show that compared with other state-of-the-art algorithms, the differential evolution with node branch archive (NBAr-DE) can significantly reduce the number of redundant test cases and the time consumption.

## 2. Code instructions

The instructions for using the code are as follows: 

You can select the corresponding search based algorithm to verify the experimental results. 

The specific functions are as follows:

| Benchmark functions             | R    | PATHNUM | NODENUM |
| ------------------------------- | ---- | ------- | ------- |
| Transmit                        | 3    | 2       | 30      |
| Send                            | 2    | 9       | 47      |
| ProcessEvent                    | 7    | 9       | 67      |
| ExecuteTuple                    | 7    | 5       | 41      |
| CheckCloudletCompletion         | 5    | 6       | 43      |
| GetResultantTuple               | 8    | 7       | 73      |
| Triangle Classification Problem | 3    | 4       | 16      |
| Factorial                       | 1    | 2       | 6       |
| Bubble Sorting                  | 10   | 2       | 15      |
| GCD                             | 2    | 4       | 12      |
| Middle                          | 3    | 4       | 15      |
| Tomorrow                        | 4    | 10      | 31      |
| Commission                      | 3    | 3       | 42      |
| Premium                         | 2    | 11      | 35      |

 The selection part of function settings is as follows:

```java
	private static final int fun_num = 1; //Test Case ID
	private static final int R = 3;// input variable dimension
	private static final int PATHNUM = 2;//number of existing paths
	private static final int NODENUM = 1;//Number of nodes
```
