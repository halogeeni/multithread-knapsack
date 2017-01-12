multithread-knapsack
====================

Knapsack problem solver using multithreaded bruteforcing written in Java.
Reads knapsack files in following format:
```
33
    1   835   735
    2  1670  1470
    3  3340  2940
    4  1087   987
    5  1087   987
    6   517   417
    7  1034   834
    8  2068  1668
    9  1034   834
   10   630   530
   11  1260  1060
   12  1260  1060
   13  1071   971
   14   165    65
   15   330   130
   16   495   195
   17   176    76
   18   663   563
   19  1326  1126
   20  1326  1126
   21   984   884
   22  1968  1768
   23  2952  2652
   24   829   729
   25  1658  1458
   26  3316  2916
   27   829   729
   28   663   563
   29  1326  1126
   30  1989  1689
   31  1086   986
   32  1086   986
   33   639   539
18586
```

First line denotes total number of items. Last line is the total carrying capacity of the knapsack.
Items are separated on individual lines; first the item id, then value and weight.

