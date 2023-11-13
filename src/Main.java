import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        try {

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "output-" + sdf1.format(new Date()) + ".gv.csv";
            FileWriter file = new FileWriter(filename);
            CSVWriter myWriter = new CSVWriter(file);

            String[] header = {"Number of Nodes", "Edge to Node Ratio", "Prim Completion Time", "Kruskal Completion Time"};

            myWriter.writeNext(header);

            for(int i = 0; i < 5; i++) {
                Graph graph = new Graph();
                int[][] KruskalOutput = graph.KruskalAlgorithm();
                int[][] PrimOutput = graph.PrimAlgorithm();
                String[] data = {String.valueOf(graph.getNumberOfNodes()), graph.getEdgeNodeRatio(), graph.getPrimCompletionTime(), graph.getKruskalCompletionTime()};

                myWriter.writeNext(data);

            }
            myWriter.close();
        }
        catch (IOException e){}


    }
}