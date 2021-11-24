package geneticalgorithm;


import java.util.*;
import main.Matrix;
import randomtree.RandomGraph;

/**
 * @author Arefeva Ilona
 */
public class Individual {

    private int [] chromosome;//1 хромосома
    private int weightTree;
    private int diameter;
    private int countLeaves;

    public Individual(Matrix matrix, int countRandomCountLeaves) {
        int [] codePrufer = new int [matrix.getCountVerteces() - 2];
        Random randomArr = new Random();
        Integer[] arr = new Integer[matrix.getCountVerteces()];
        for (int i = 0; i < arr.length; i++) {
            double probability2 = Math.random();
            if (probability2 <= 0.03) {
                arr[i] = randomArr.nextInt((matrix.getCountVerteces() - countRandomCountLeaves));
            } else {
                arr[i] = i;
            }
        }
       Collections.shuffle(Arrays.asList(arr));
        double probability = Math.random();
        if (probability <= 0.5) {
            for (int j = 0; j < codePrufer.length; j++) {
                    codePrufer[j] = arr[j];
            }
        } else {
            Random random = new Random();
            for (int j = 0; j < codePrufer.length; j++) {
                // nextInt(n) - случ. зн. в диапазоне [0, n)
                codePrufer[j] = random.nextInt((matrix.getCountVerteces() - countRandomCountLeaves));
            }
        }
        List<Integer>[] tree = RandomGraph.pruferCode2Tree(codePrufer);
        int [] tupleMaxDistanceFromStartVertex =  bfs(tree, 0, matrix);
        int[] maximumDistanceBetweenTwoVertices = bfs(tree, tupleMaxDistanceFromStartVertex[0], matrix);

        this.chromosome = codePrufer;
        this.weightTree = maximumDistanceBetweenTwoVertices[2];
        this.diameter = maximumDistanceBetweenTwoVertices[1];
        this.countLeaves = maximumDistanceBetweenTwoVertices[3];
    }

    //Конструктор для создания копии объекта
    public Individual(Individual indCopy) {
        this.chromosome =  Arrays.copyOf(indCopy.getChromomeStructure(), indCopy.getSizeChromosome());;
        this.weightTree = indCopy.getWeightTree();
        this.diameter = indCopy.getDiameter();
        this.countLeaves = indCopy.getCountLeaves();
    }

    //Обход дерева в ширину, получаем хэш расстояний от вершины start до всех остальных
    // и возвращаю соотвественно: tupleMaxDistance[0] - номер вершины с максимальным расстоянием до стартовой
    // tupleMaxDistance[1] - значение расстояния в ребрах
    // tupleMaxDistance[2] - вес остовного дерева
    // tupleMaxDistance[3] - число листьев
    public int [] bfs(List<Integer>[] tree, int startVertex, Matrix matrix)
    {
        HashMap<Integer, Integer> distance = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        int farthestVertex = startVertex;// номер дальней вершины
        int weightTree = 0;
        queue.add(startVertex); // Добавить start в качестве начальной вершины в очередь
        distance.put(startVertex, 0);
        int countLeaves = 0;
        // начинаем с листа
        if(tree[startVertex].size() == 1) {
            countLeaves++;
        }
        while(!queue.isEmpty())
        {
            int top = queue.poll(); // Вынимаем из очереди первый узел
            int childNodeDistance = distance.get(top) + 1; // Расстояние вокруг узлов, которые еще не были посещены
            for (Integer childNodes: tree[top]) {
                if (!distance.containsKey(childNodes)) // Посещали ли ранее эту вершину? Нет в distance => не посещали
                {
                    distance.put(childNodes, childNodeDistance);
                    weightTree += matrix.getWeight(top, childNodes);
                    queue.add(childNodes);
                    farthestVertex = childNodes;
                } else if (tree[top].size() == 1){
                    countLeaves++;
                }
            }
        }
        int [] tupleMaxDistance = new int[4];
        tupleMaxDistance[0] = farthestVertex;
        tupleMaxDistance[1] = distance.get(farthestVertex);
        tupleMaxDistance[2] = weightTree;
        tupleMaxDistance[3] = countLeaves;
        return tupleMaxDistance;
    }

    public void recalculateFitnessFunction(Matrix matrix) {
        int [] codePrufer = this.getChromomeStructure();
        List<Integer>[] tree = RandomGraph.pruferCode2Tree(codePrufer);
        int [] tupleMaxDistanceFromStartVertex =  bfs(tree, 0, matrix);
        int[] maximumDistanceBetweenTwoVertices = bfs(tree, tupleMaxDistanceFromStartVertex[0], matrix);

        this.weightTree = maximumDistanceBetweenTwoVertices[2];
        this.diameter = maximumDistanceBetweenTwoVertices[1];
        this.countLeaves = maximumDistanceBetweenTwoVertices[3];
    }

    //Заменить фрагмент хромосомы начиная с индекса indexBegin до indexEnd.
    public void changeChromosome(int[] otherChromosome, int indexBegin, int indexEnd) {
        if (indexEnd + 1 - indexBegin >= 0)
            System.arraycopy(otherChromosome, indexBegin, chromosome, indexBegin, indexEnd + 1 - indexBegin);
    }

    //Хромосомы совпадают?
    public boolean equalsChromosome(Individual ind2) {
        String str1 = Arrays.toString(this.getChromomeStructure());
        String str2 = Arrays.toString(ind2.getChromomeStructure());
        return str1.equals(str2);
    }

    // Мутация в гене, заменить на случайную вершину с вероятностью mutationProbability.
    public void mutation(Matrix matrix, double mutationProbability) {
            int randomVertex;
            int positionChromosome;
                double random = Math.random();
                if (random <= mutationProbability) {
                        positionChromosome = (int)(Math.random()*((matrix.getCountVerteces() - 3) + 1));
                        randomVertex = (int)(Math.random()*((matrix.getCountVerteces() - 1) + 1));
                        chromosome[positionChromosome] = randomVertex;
                        //a + (int)(Math.random()*((b - a) + 1))
                }
    }

    public int[] getChromomeStructure() {
        return chromosome;
    }

    public int getSizeChromosome() {
        return chromosome.length;
    }

    public int getWeightTree() {
        return this.weightTree;
    }

    public int getDiameter() {
        return this.diameter;
    }

    public int getCountLeaves() {
        return this.countLeaves;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Integer ch : chromosome) {
            str.append(ch).append(" ");
        }
        return str.toString();
    }

}
