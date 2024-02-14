/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student.dijkstra;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author Andrea
 */
public class Dijkstra {
    
    public static class AdjListNode {
        int vertex, weight;
 
        public AdjListNode(int v, int w)
        {
            vertex = v;
            weight = w;
        }
        int getVertex() { return vertex; }
        int getWeight() { return weight; }
    }
    
    public static ArrayList<int[]> dijkstra(int V, ArrayList<ArrayList<AdjListNode> > graph, int src)
    {
        int[] distance = new int[V];
        for (int i = 0; i < V; i++)
            distance[i] = Integer.MAX_VALUE;
        distance[src] = 0;
 
        PriorityQueue<AdjListNode> pq = new PriorityQueue<>((v1, v2) -> v1.getWeight() - v2.getWeight());
        pq.add(new AdjListNode(src, 0));
        
        int[] parents = new int[V];
        parents[src] = -1;
        
        while (pq.size() > 0) {
            AdjListNode current = pq.poll();
            
            for (AdjListNode n : graph.get(current.getVertex())) {
                if (distance[current.getVertex()] + n.getWeight() < distance[n.getVertex()]) {
                    distance[n.getVertex()] = n.getWeight() + distance[current.getVertex()];
                    parents[n.getVertex()] = current.getVertex();
                    pq.add(new AdjListNode( n.getVertex(), distance[n.getVertex()]));
                }
            }
        }
        //printSolution(src, distance, parents);
        
        ArrayList<int[]> ret = new ArrayList<>();
        ret.add(distance);
        ret.add(parents);
        
        return ret;
    }
    
    public static void getPath(int currentVertex, int[] parents, ArrayList<Integer> path){
        if (currentVertex == -1)
        {
            return;
        }
        getPath(parents[currentVertex], parents, path);
        path.add(currentVertex);
    }
     
    private void printSolution(int startVertex, int[] distances, int[] parents)
    {
        int nVertices = distances.length;
        System.out.print("Vertex\t Distance\tPath");
         
        for (int vertexIndex = 0;  vertexIndex < nVertices; vertexIndex++)
        {
            if (vertexIndex != startVertex)
            {
                System.out.print("\n" + startVertex + " -> ");
                System.out.print(vertexIndex + " \t\t ");
                System.out.print(distances[vertexIndex] + "\t\t");
                printPath(vertexIndex, parents);
            }
        }
    }
     
    private void printPath(int currentVertex, int[] parents)
    {
        if (currentVertex == -1)
        {
            return;
        }
        printPath(parents[currentVertex], parents);
        System.out.print(currentVertex + " ");
    }

    
    /*public static void main(String[] args) {
        int V = 7;
        ArrayList<ArrayList<AdjListNode> > graph  = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }
        int source = 0;
        graph.get(0).add(new AdjListNode(1, 8));
        graph.get(0).add(new AdjListNode(2, 2));
        graph.get(3).add(new AdjListNode(1, 10));
        graph.get(2).add(new AdjListNode(3, 15));
        graph.get(3).add(new AdjListNode(4, 3));
        graph.get(3).add(new AdjListNode(5, 3));
        graph.get(4).add(new AdjListNode(6, 2));
        graph.get(5).add(new AdjListNode(6, 1));

        int[] distance = dijkstra(V, graph, source);
        // Printing the Output
        System.out.println("Vertex    Distance from Source");
        for (int i = 0; i < V; i++) {
            System.out.println(i + "             "+ distance[i]);
        }
        
        int[] distance = dijkstra(V, graph, source);
    }*/
    
}
