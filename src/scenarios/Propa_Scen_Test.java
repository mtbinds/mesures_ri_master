import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceEdge;

/**
 * <p>
 * Programme permettant de récupérer quelques mesures sur la propagation d'un
 * virus dans un réseau.
 * </p>
 *
 * Résultats : <code>
 *
 * DBLP
 *
 * <k>  = 6,622089
 * <k²> = 144,006276
 * λ    = 2,000000
 * --------- Simulation ---------
 * Scénario 3 : 10,0009%
 * Scénario 2 : 27,3877%
 * Scénario 1 : 85,4731%
 * ------------------------------
 * <k> g0 = 6,62
 * <k> g1 = 11,37
 * λc 1 = 0,045985
 * λc 2 = 0,046415
 * λc 3 = 0,094701
 *
 * Random
 *
 * <k>  = 6,622089
 * <k²> = 56,102855
 * λ    = 2,000000
 * --------- Simulation ---------
 * Scénario 2 : 40,2021%
 * Scénario 3 : 48,9754%
 * Scénario 1 : 92,0379%
 * ------------------------------
 * <k> g0 = 7,01
 * <k> g1 = 7,77
 * λc 1 = 0,124929
 * λc 2 = 0,124924
 * λc 3 = 0,133103
 *
 * Preferential
 *
 * <k>  = 6,622089
 * <k²> = 293,868198
 * λ    = 2,000000
 * --------- Simulation ---------
 * Scénario 1 :  0,0000%
 * Scénario 3 : 38,4711%
 * Scénario 2 : 37,2740%
 * ------------------------------
 * <k> g0 = 8,05
 * <k> g1 = 13,81
 * λc 1 = 0,027232
 * λc 2 = 0,028569
 * λc 3 = 0,120096
 *
 * Exec in 87 s
 * </code>
 *
 */
public class Propagation_Scen_Test {

    public static void main(String[] args) throws InterruptedException {
        long begin = System.currentTimeMillis();
        exec(GraphUtils.readResource("DBLP", "com-dblp.ungraph.txt", new FileSourceEdge()));
        exec(GraphUtils.readResource("Random", "random-graph.txt", new FileSourceDGS()));
        exec(GraphUtils.readResource("Preferential", "preferential-graph.txt", new FileSourceDGS()));
        long end = System.currentTimeMillis();
        System.out.printf("%nExec in %d s%n", (end - begin) / 1000);
    }

    public static void exec(Graph g) throws InterruptedException {
        System.out.printf("%n%s%n%n", g.getId());
        double averageDegree = Toolkit.averageDegree(g);
        System.out.printf("<k>  = %f%n", averageDegree);

        float beta = 1F / 7;
        float mu = 1F / 14;
        System.out.printf("<k²> = %f%n", degreeVariance(g));
        System.out.printf("λ    = %f%n", propagationRate(beta, mu));

        int days = 3 * 4 * 7;
        Scenario1 s1 = new Scenario1(g);
        Scenario2 s2 = new Scenario2(g);
        Scenario3 s3 = new Scenario3(g);

        System.out.println("--------- Simulation ---------");
        Thread thread1 = runScenario(s1, days, res -> {
            double prc = res.get(res.size() - 1).size() / (double) g.getNodeCount();
            System.out.printf("Scénario %d : %7.4f%%%n", 1, 100 * prc);
            writeInfectedDistribution(g, res, 1);
        });
        Thread thread2 = runScenario(s2, days, res -> {
            double prc = res.get(res.size() - 1).size() / (double) g.getNodeCount();
            System.out.printf("Scénario %d : %7.4f%%%n", 2, 100 * prc);
            writeInfectedDistribution(g, res, 2);
        });
        Thread thread3 = runScenario(s3, days, res -> {
            double prc = res.get(res.size() - 1).size() / (double) g.getNodeCount();
            System.out.printf("Scénario %d : %7.4f%%%n", 3, 100 * prc);
            writeInfectedDistribution(g, res, 3);
        });
        thread1.join();
        thread2.join();
        thread3.join();
        System.out.println("------------------------------");

        System.out.printf("<k> g0 = %.2f%n", averageDegree(s3.getGroup0())); // ~6.6
        System.out.printf("<k> g1 = %.2f%n", averageDegree(s3.getGroup1())); // ~11.4 <- Hub

        double epidemicThreshold1 = epidemicThreshold(s1.getNodes());
        double epidemicThreshold2 = epidemicThreshold(s2.getNodes());
        double epidemicThreshold3 = epidemicThreshold(s3.getNodes());
        System.out.printf("λc 1 = %f%n", epidemicThreshold1);
        System.out.printf("λc 2 = %f%n", epidemicThreshold2);
        System.out.printf("λc 3 = %f%n", epidemicThreshold3);
    }

    public static Thread runScenario(Scenario s, int days, Consumer<List<Collection<Node>>> callback) {
        Thread thread = new Thread(() -> callback.accept(s.propagation(days)));
        thread.start();
        return thread;
    }

    public static double averageDegree(Collection<Node> nodes) {
        int sum = 0;
        for (Node n : nodes)
            sum += n.getDegree();
        return (double) sum / nodes.size();
    }

    public static float propagationRate(float beta, float mu) {
        return beta / mu;
    }

    public static double degreeVariance(Graph g) {
        return degreeVariance(g.getNodeSet());
    }

    public static double degreeVariance(Collection<Node> nodes) {
        int sum = 0;
        for (Node n : nodes)
            sum += Math.pow(n.getDegree(), 2);
        return sum / (double) nodes.size();
    }

    public static double epidemicThreshold(Collection<Node> nodes) {
        return epidemicThreshold(averageDegree(nodes), degreeVariance(nodes));
    }

    public static double epidemicThreshold(double averageDegree, double degreeVariance) {
        return averageDegree / degreeVariance;
    }

    public static float randomEpidemicThreshold(double averageDegree) {
        return (float) (1 / (averageDegree + 1)); //
    }

    public static void writeInfectedDistribution(Graph graph, List<Collection<Node>> list, int id) {
        int[] distribution = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            distribution[i] = list.get(i).size();
        String fileName = String.format("./data/dinfection_%s_%d.dat", graph.getId().toLowerCase(), id);
        GraphUtils.writeDistribution(fileName, distribution, d -> (double) d / graph.getNodeCount());
    }
}
