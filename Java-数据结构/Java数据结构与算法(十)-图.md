###1.什么是图
* 图是一种和树相像的数据结构，通常有一个固定的形状，这是有物理或者抽象的问题来决定的。
###2.邻接
* 如果两个定点被同一条便连接，就称这两个定点是邻接的。
###3.路径
*  路径是从一个定点到另一个定点经过的边的序列。
###4. 连通图和非连通图
* 至少有一挑路径可以连接所有的定点，那么这个图就是连通的，否则是非连通的。
###5.有向图和无向图
* 有向图的边是有方向的，入只能从A到B，不能从B到A。
* 无向图的边是没有方向的，可以从A到B，也可以从B到A。
###6.带权图
* 在有些图中，边被富裕了一个权值，权值是一个数字，他可以代表如两个顶点的物理距离，或者是一个顶点到另一个顶点的时间等等，这样的图叫做带权图。
###7.程序实现
```
#include <iostream>

#define  MAXVEX 100      //最大顶点数
#define INFINITY 65535   //最大权值

typedef int EdgeType;    //权值类型自己定义
typedef char VertexType;  //顶点类型自己定义
#pragma once

#pragma region 邻接矩阵结构体
typedef struct 
{
    VertexType vex[MAXVEX];   //顶点表
    EdgeType arg[MAXVEX][MAXVEX];  ///权值表-邻接矩阵
    int numVertexes,numEdges;  //图中的边数和顶点数
}GraphArray;

#pragma endregion

#pragma region 邻接表结构体
//边表结点
typedef struct EdgeNode
{
    int nNodevex;     //邻接点的点表中结点的坐标
    EdgeType nNodeWeight;   //用于网图中边的权值
    EdgeNode* next;       //链域，指向下一个邻接点
}EdgeNode,*pEdgeNode;
//顶点表结点
typedef struct VertexNode
{
    VertexType nNodeData;   //顶点表中存储的数据
    pEdgeNode pFirstNode;   //顶点表和边表中关联指针，指向边表头指针

}VertexNode,pVertexNode,VertexList[MAXVEX];
//图结构
typedef struct
{
    VertexList vertexList;
    int numVertess,numEdges;
}GraphList;

#pragma endregion

class GraphData
{
public:
    GraphData(void);
    ~GraphData(void);
    #pragma region 创建邻接矩阵
    void CreateGraphArray(GraphArray* pGraphArray,int numVer,int numEdegs);
    int GetGraphLocation(GraphArray* pGraphArrray,char chpoint);
    #pragma endregion

    #pragma region 创建邻接表
    void CreateGraphList(GraphList* pList,int numVer,int numEdegs);
    int GetGraphListLocation(GraphList* pList,char chpoint);
    #pragma endregion

};
```
```
#include "GraphData.h"


GraphData::GraphData(void)
{
}


GraphData::~GraphData(void)
{
}

int GraphData::GetGraphLocation(GraphArray* pGraphArrray,char chpoint)
{
    int i = 0;
    for (i = 0;i< pGraphArrray->numVertexes;i++)
    {
        if (pGraphArrray->vex[i] == chpoint)
        {
            break;;
        }
    }
    if (i >= pGraphArrray->numVertexes)
    {
        return -1;
    }
    return i;
}
/// <summary>
/// 创建邻接矩阵
/// </summary>        
void GraphData::CreateGraphArray(GraphArray* pGraphArray,int numVer,int numEdegs)
{
    int weight = 0;
    pGraphArray->numVertexes = numVer;
    pGraphArray->numEdges = numEdegs;
    
    //创建顶点表
    for (int i= 0; i < numVer;i++)
    {
        pGraphArray->vex[i] = getchar();
        while(pGraphArray->vex[i] == '\n')
        {
            pGraphArray->vex[i] = getchar();
        }
    }

    //创建邻接表的边矩阵
    for (int i = 0; i < numEdegs; i++)
    {
        for (int j = 0;j < numEdegs ; j++)
        {
            pGraphArray->arg[i][j] = INFINITY;
        }        
    }
    for(int k = 0; k < pGraphArray->numEdges; k++)
    {
        char p, q;
        printf("输入边(vi,vj)上的下标i，下标j和权值:\n");

        p = getchar();
        while(p == '\n')
        {
            p = getchar();
        }
        q = getchar();
        while(q == '\n')
        {
            q = getchar();
        }
        scanf("%d", &weight);    

        int m = -1;
        int n = -1;
        m = GetGraphLocation(pGraphArray, p);
        n = GetGraphLocation(pGraphArray, q);
        if(n == -1 || m == -1)
        {
            fprintf(stderr, "there is no this vertex.\n");
            return;
        }
        //getchar();
        pGraphArray->arg[m][n] = weight;
        pGraphArray->arg[n][m] = weight;  //因为是无向图，矩阵对称
    }
    
}

#pragma region
void GraphData::CreateGraphList(GraphList* pList,int numVer,int numEdegs)
{
    int weight = 0;
    GraphList *pGraphList = pList;
    pGraphList->numVertess = numVer;
    pGraphList->numEdges = numEdegs;
    EdgeNode* firstNode,*secondNode;
    //创建顶点表
    for (int i= 0; i < numVer;i++)
    {
        pGraphList->vertexList[i].nNodeData = getchar();
        pGraphList->vertexList[i].pFirstNode = NULL;
        while(pGraphList->vertexList[i].nNodeData == '\n')
        {
            pGraphList->vertexList[i].nNodeData = getchar();
        }
    }

    //创建边表    
    for(int k = 0; k < pGraphList->numEdges; k++)
    {
        char p, q;
        printf("输入边(vi,vj)上的下标i，下标j和权值:\n");

        p = getchar();
        while(p == '\n')
        {
            p = getchar();
        }
        q = getchar();
        while(q == '\n')
        {
            q = getchar();
        }
        scanf("%d", &weight);    

        int m = -1;
        int n = -1;
        m = GetGraphListLocation(pGraphList, p);
        n = GetGraphListLocation(pGraphList, q);
        if(n == -1 || m == -1)
        {
            fprintf(stderr, "there is no this vertex.\n");
            return;
        }
        //getchar();
        //字符p在顶点表的坐标为m，与坐标n的结点建立联系权重为weight
        firstNode = new EdgeNode();
        firstNode->nNodevex = n;
        firstNode->next = pGraphList->vertexList[m].pFirstNode;
        firstNode->nNodeWeight = weight;
        pGraphList->vertexList[m].pFirstNode = firstNode;

        //第二个字符second
        secondNode = new EdgeNode();
        secondNode->nNodevex = m;
        secondNode->next = pGraphList->vertexList[n].pFirstNode;
        secondNode->nNodeWeight = weight;
        pGraphList->vertexList[n].pFirstNode = secondNode;

    }
}

int GraphData::GetGraphListLocation(GraphList* pList,char chpoint)
{
    GraphList *pGraphList = pList;
    int i = 0;
    for (i = 0;i< pGraphList->numVertess;i++)
    {
        if (pGraphList->vertexList[i].nNodeData == chpoint)
        {
            break;;
        }
    }
    if (i >= pGraphList->numVertess)
    {
        return -1;
    }
    return i;
}

#pragma endregion
```

```
#include <iostream>
#include "GraphData.h"
using namespace std;
//

void PrintGrgph(GraphList *pGraphList)
{
    int i =0;
    while(pGraphList->vertexList[i].pFirstNode != NULL && i<MAXVEX)
    {
        printf("顶点:%c  ",pGraphList->vertexList[i].nNodeData);
        EdgeNode *e = NULL;
        e = pGraphList->vertexList[i].pFirstNode;
        while(e != NULL)
        {
            printf("%d  ", e->nNodevex);
            e = e->next;
        }
        i++;
        printf("\n");
    }
    
}
int main()
{
    int numVexs,numEdges;
    GraphData* pTestGraph = new GraphData();
    GraphArray graphArray;
    GraphArray* pGraphArray = &graphArray;
    GraphList* pGgraphList = new GraphList();
    
    cout<<"输入顶点数和边数"<<endl;
    cin>>numVexs>>numEdges;
    cout<<"顶点数和边数为："<<numVexs<<numEdges<<endl;

    /*pTestGraph->CreateGraphArray(pGraphArray,numVexs,numEdges);
    for(int i = 0; i< numEdges;i++)
    {
        for (int j = 0;j< numEdges;j++)
        {
            cout<<pGraphArray->arg[i][j];
        }
        cout<<endl;
    }*/
    pTestGraph->CreateGraphList(pGgraphList,numVexs,numEdges);
    PrintGrgph(pGgraphList);
    system("pause");
}
```

借鉴文章[不归路](http://www.cnblogs.com/polly333/)
