package main;

import geneticalgorithm.GeneticAlgorithm;
import geneticalgorithm.Individual;
import geneticalgorithm.Population;
import randomtree.RandomGraph;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * GA
 * @author Afereva Ilona
 * @version 1.0
 */

public class Main {

    public static void main(String[] args) {

        ParserDimacs parser = new ParserDimacs();
        parser.parse("files/Taxicab_4096.txt");

        ArrayList<String> listCoordinatesVertex = parser.coordinatesVertex();
        int countVertex = getCountVertex(parser.countVertex);
        System.out.println(countVertex);
        System.out.println(listCoordinatesVertex);
        Matrix matrix = new Matrix(countVertex, listCoordinatesVertex);
        matrix.printMatrix();

        Random rnd = new Random(1);
        for (int step = 0; step < 1000; step++) {
            int V = rnd.nextInt(50) + 2;
            checkGraph(V, V - 1, rnd);
            checkGraph(V, V * (V - 1) / 2, rnd);
            checkGraph(V, rnd.nextInt(V * (V - 1) / 2 - (V - 1) + 1) + V - 1, rnd);
        }

        int numberGeneration = 1;
        int numberPopulation = 50; // размер популяции
        Population population =  new Population();

        // Значения листьев у индивидума буду брать из массива rangeLeaves, числа в диапазоне [0, 63]
        List<Integer> rangeLeaves = IntStream.range(0, matrix.getCountVerteces()).boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(rangeLeaves);
        int indexRandomCountLeaves = 0;
        // формируем начальную популяцию
        for (int i = 0; i < numberPopulation; i++) {
            int countLeaves = rangeLeaves.get(indexRandomCountLeaves);
            Individual newInd = new Individual(matrix, countLeaves);
            population.addChomosome(newInd);
            indexRandomCountLeaves++;
            if (indexRandomCountLeaves == rangeLeaves.size()){
                indexRandomCountLeaves = 0;
            }
        }
        System.out.println(population.convertRoutesToString(matrix));
        System.out.println();
        int k = 0;
        while(k < numberGeneration) {
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(numberPopulation, population, matrix);
            geneticAlgorithm.choiceParents();
            geneticAlgorithm.crossing();
            geneticAlgorithm.mutation();
            geneticAlgorithm.updateFitnessFunction();
            geneticAlgorithm.selection();
            k++;
        }
        System.out.println(population.convertRoutesToString(matrix));
        Individual bestIndivual = population.getAtIndex(0);
        HashMap<Integer, Integer> edges = getEdgesDMST(bestIndivual.getChromomeStructure());
        try(FileWriter writer = new FileWriter("Arefeva.txt", false))
        {
            // запись всей строки
            String text = "c Вес дерева = "+bestIndivual.getWeightTree()+", диаметр = "+bestIndivual.getDiameter();
            writer.write(text);
            // запись по символам
            writer.append('\n');
            String countVertexandEdges = "p edge "+matrix.getCountVerteces()+" "+(edges.size());
            writer.write(countVertexandEdges);
            writer.append('\n');
            for (Map.Entry<Integer, Integer> entry : edges.entrySet()) {
                writer.write( "e " + (entry.getValue() + 1) + " " + (entry.getKey() + 1));
                writer.append('\n');
            }

            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
}

    static HashMap<Integer, Integer> getEdgesDMST(int [] code) {
        HashMap<Integer, Integer> edges = new HashMap<>();
        ArrayList<Integer> vertices = new ArrayList<>();
        List<Integer> codePrufer = Arrays.stream(code)
                .boxed()
                .collect(Collectors.toList());
        // заполняю массив всех вершин по возрастанию номеров
        for (int i = 0; i < code.length+2; i++) {
            vertices.add(i);
        }

        while (!codePrufer.isEmpty()) {
            int curCode = codePrufer.get(0);
            for (int j = 0; j < vertices.size(); j++) {
                if (!isExistVertexInCode((ArrayList<Integer>) codePrufer, vertices.get(j))) {
                    edges.put(vertices.get(j), curCode);
                    vertices.remove(j);
                    codePrufer.remove(0);
                    break;
                }
            }
        }
        // две оставшиеся вершиные образуют последнее ребро
        edges.put(vertices.get(0), vertices.get(1));
        edges = edges.entrySet().stream()
                .sorted((k1, k2) -> -k2.getValue().compareTo(k1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return edges;
    }

    // ищу в коде Прюфера вершину requiredVertex
    static boolean isExistVertexInCode(ArrayList<Integer> list, int requiredVertex) {
        for (int i=0; i < list.size(); i++) {
            if(list.get(i) == requiredVertex) {
                return true;
            }
        }
        return false;
    }

    static void checkGraph(int V, int E, Random rnd) {
        List<Integer>[] g = RandomGraph.getRandomUndirectedConnectedGraph(V, E, rnd);
        int n = g.length;
        int[][] a = new int[n][n];
        int edges = 0;
        for (int i = 0; i < n; i++) {
            for (int j : g[i]) {
                ++a[i][j];
                ++edges;
            }
        }
        if (edges != 2 * E) {
            throw new RuntimeException();
        }
        for (int i = 0; i < n; i++) {
            if (a[i][i] != 0) {
                throw new RuntimeException();
            }
            for (int j = 0; j < n; j++) {
                if (a[i][j] != a[j][i] || a[i][j] != 0 && a[i][j] != 1) {
                    throw new RuntimeException();
                }
            }
        }
    }


    private static int getCountVertex(String countVers) {
        return Integer.parseInt(countVers.split("\\s+")[2]);
    }

    private void getMaximalOccurencesOfAVariable(Set<String> variables, ArrayList<String> clauses) {
        int[] occurences = new int[variables.size() + 1];
        for (String clause : clauses) {
            String[] literals = clause.replace("-", "").split("\\s+");
            for (String literal : literals) {
                occurences[Integer.parseInt(literal)]++;
            }
        }
    }
}