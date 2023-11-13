import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class Graph {
    static int MIN_GRAPH_SIZE = 4;
    static int MAX_GRAPH_SIZE = 100;
    static int MAX_EDGE_WEIGHT = 9;
    int graphSize;
    int[][] graph;
    int [] arraySet;

    int[] edgePerNodeCount;

    int edgeCount = 0;
    Stack<Integer> weights = new Stack<Integer>();
    ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
    DecimalFormat df = new DecimalFormat();
    DecimalFormat df2 = new DecimalFormat();
    String KruskalCompletionTime;
    String PrimCompletionTime;
    String edgeNodeRatio;
    public Graph(){
        df.setMaximumFractionDigits(9);
        df2.setMaximumFractionDigits(3);
        graphSize = ThreadLocalRandom.current().nextInt(MIN_GRAPH_SIZE, MAX_GRAPH_SIZE + 1);
        graph = new int[graphSize][graphSize];

        //this array is only used for data collection
        //values used to find node to edge ratio
        edgePerNodeCount = new int[graphSize];
        for(int i = 0; i < graphSize; i++){
            edgePerNodeCount[i] = 0;
        }

        //array intialized
        for(int row = 0; row < graphSize; row++){
            for(int col = 0; col < graphSize; col++){
                graph[row][col] = 0;
            }
        }

        //randomized graph created
        for(int row = 0; row < graphSize; row++){
            int edgeAmount = ThreadLocalRandom.current().nextInt(1, (int)(0.7 * graphSize));
            for(int i = 0; i < edgeAmount; i++){
                int weight = ThreadLocalRandom.current().nextInt(1, MAX_EDGE_WEIGHT + 1);
                weights.push(weight);
            }
            //a stack of weights is created for each node and the weights are randomly placed
            while(!weights.empty()) {
                for (int col = 0; col < graphSize; col++) {
                    if (row != col && !weights.empty() && (Math.random() < 0.1)){
                        //this statement is put here because the loop can overwrite edges that have already been placed
                        if(graph[row][col] == 0){
                            edgePerNodeCount[col]++;
                            edgeCount++;
                        }
                        //weight removed and added to graph
                        //weight is added to both graph[row][col] and graph[col][row] so that the graph is undirected
                        graph[row][col] = weights.pop();
                        graph[col][row] = graph[row][col];
                    }
                }

            }

            //value for edge to node ratio calculated
            int totalEdgeCount = 0;
            for(int i = 0; i < graphSize; i++){
                totalEdgeCount += edgePerNodeCount[i];
            }

            double edgeNodeRatio2 = totalEdgeCount / Double.valueOf(graphSize);
            edgeNodeRatio = df2.format(edgeNodeRatio2);

        }
    }
    public void outputGraph(){
        for(int row = 0; row < graphSize; row++){
            for (int col = 0; col < graphSize; col++) {
                System.out.print("[" + graph[row][col] + "]");
            }
            System.out.println();
        }
    }

    public int[][] getGraph(){
        return graph;
    }
    public int[][] PrimAlgorithm(){
        //array copied for return
        int[][] graph2 = new int[graphSize][graphSize];
        for(int row = 0; row < graphSize; row++){
            for (int col = 0; col < graphSize; col++) {
                graph2[row][col] = graph[row][col];
            }
        }
        //mst and visited nodes array initialized
        int mst = 0;
        visitedNodes.clear();

        int initialNode = ThreadLocalRandom.current().nextInt(0, graphSize);
        visitedNodes.add(initialNode);
        long startTime = System.nanoTime();
        while(visitedNodes.size() < graphSize){
            //minimum value and indexes initialized
            //Note: minimum is set to 1 higher than the maximum possible value for a weight
            int min = 10;
            int selectedSpot = 100;
            int selectedSpot2 = 100;
            for(int i = 0; i < visitedNodes.size(); i++){
                for(int j = 0; j < graphSize; j++){
                    int currentValue = graph2[visitedNodes.get(i)][j];
                    //minimum spot selected if it is less than past minimum and if it is not connected to a visited node
                    if(currentValue != 0 && currentValue < min && !visitedNodes.contains(j)){
                        min = currentValue;
                        selectedSpot = j;
                        selectedSpot2 = i;
                    }
                }
            }

            //edge set to 0 so that it will not be looked at further
            graph2[visitedNodes.get(selectedSpot2)][selectedSpot] = 0;
            graph2[selectedSpot][visitedNodes.get(selectedSpot2)] = 0;
            visitedNodes.add(selectedSpot);
            mst += min;
        }

        //completion time calculated
        long stopTime = System.nanoTime();
        long completionTime = stopTime - startTime;
        PrimCompletionTime = df.format(completionTime / (Math.pow(10, 9)));
        return graph2;
    }
    //The set that the index is contained in is searched
    //searchSet() function inspired from https://www.geeksforgeeks.org/kruskals-algorithm-simple-implementation-for-adjacency-matrix/
    int searchSet(int i) {
        while (arraySet[i] != i)
            i = arraySet[i];
        return i;
    }
    //the sets are combined
    //combineSets() inspired from https://www.geeksforgeeks.org/kruskals-algorithm-simple-implementation-for-adjacency-matrix/
    void combineSets(int i, int j) {
        int a = searchSet(i);
        int b = searchSet(j);
        arraySet[a] = b;
    }
    public int[][] KruskalAlgorithm(){
        arraySet = new int[graphSize];
        int[][] graph2 = new int[graphSize][graphSize];
        for(int row = 0; row < graphSize; row++){
            for (int col = 0; col < graphSize; col++) {
                graph2[row][col] = graph[row][col];
            }
        }
        //initializes each index as its own set
        for(int i = 0; i < graphSize; i++){
            arraySet[i] = i;
        }


        int searchedEdges = edgeCount;

        int mstCost = 0;
        long startTime = System.nanoTime();

        while(searchedEdges > 1){
            int min = 10;
            int selectedSpot = 100;
            int selectedSpot2 = 100;
            //the entire array is searched for the minimum edge
            for(int i = 0; i < graphSize; i++){
                for(int j = 0; j < i; j++){
                    int currentValue = graph2[i][j];
                    if(currentValue != 0 && currentValue != -1 && currentValue < min){
                        if(searchSet(i) != searchSet(j)) {
                            min = currentValue;
                            selectedSpot = j;
                            selectedSpot2 = i;
                        }
                        //the edge cannot be selected if both indexes are in the same set
                        else{
                            graph2[i][j] = -1;
                            graph2[j][i] = -1;
                            searchedEdges--;
                        }
                    }
                }
            }
            searchedEdges--;
            if(selectedSpot2 != 100) {
                graph2[selectedSpot2][selectedSpot] = 0;
                graph2[selectedSpot][selectedSpot2] = 0;
                combineSets(selectedSpot2, selectedSpot);
                mstCost += min;
            }
        }
        long stopTime = System.nanoTime();
        long completionTime = stopTime - startTime;
        KruskalCompletionTime = df.format(completionTime / (Math.pow(10, 9)));
        return graph2;
    }
    public void SortedOutput(int[][] graph2, String graphType){
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "graph-" + graphType + sdf1.format(new Date()) + ".gv.text";
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write("digraph \"Graph\" {\nnode [color=black]\nedge [color=black]\n[dir=none]\n");
            for(int row = 0; row < graphSize; row++){
                for (int col = 0; col < row; col++) {
                    if(graph[row][col] != 0) {
                        if(graph2[row][col] == 0) {
                            myWriter.write((char)(row+65) + " -> " + (char)(col+65) + " [color=\"red\" label=" + graph[row][col] + "]\n");
                        }
                        else{
                            myWriter.write((char)(row+65) + " -> " + (char)(col+65) + " [label=" + graph[row][col] + "]\n");
                        }
                    }
                }
            }
            myWriter.write("}");
            myWriter.close();
        }
        catch (IOException e){}
    }
    public String getKruskalCompletionTime(){
        return KruskalCompletionTime;
    }
    public String getPrimCompletionTime(){
        return PrimCompletionTime;
    }
    public int getNumberOfNodes(){
        return graphSize;
    }
    public String getEdgeNodeRatio(){
        return edgeNodeRatio;
    }
    public void OutputAlgorithmData(){
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "output-" + sdf1.format(new Date()) + ".gv.csv";
            FileWriter file = new FileWriter(filename);
            CSVWriter myWriter = new CSVWriter(file);

            String[] data = { String.valueOf(graphSize), df.format(edgeNodeRatio), PrimCompletionTime, KruskalCompletionTime};
            myWriter.writeNext(data);
            myWriter.close();
        }
        catch (IOException e){}
    }
}
