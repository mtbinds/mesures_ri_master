import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceEdge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//La classe principale de tests des deux TPS (Mesures et Propagation)

public class MesuresRI {

    //chemin de fichier "data/com-dblp.ungraph.txt"

    private static final String CHEMIN_FICHIER = "data/com-dblp.ungraph.txt";

    //chemin d'export (.dat) "data/com-dblp.ungraph.txt"

    private static final String EXPORT_DATA = "data/dd_dblp.dat";



    public static void main(String... args) throws IOException {


//------------------------------------------------------------TP Mesures----------------------------------------------------------------------------------------------------------------------------

        Graph graph = new DefaultGraph("g");

        graph.addAttribute("ui.quality");

        graph.addAttribute("ui.antialias");

        graph.addAttribute("ui.stylesheet", "url('styles/style.css')");

        FileSourceEdge fs = new FileSourceEdge();

        try {

            fs.addSink(graph);

            fs.readAll(CHEMIN_FICHIER);

            int nodeCount = graph.getNodeCount();

            int[] dd = Toolkit.degreeDistribution(graph);

            double avgDegree = Toolkit.averageDegree(graph);

            for (int k = 0; k < dd.length; k++) {

                if (dd[k] != 0) {

                    generateData(EXPORT_DATA, String.format(Locale.US, "%6d%20.8f%n", k, (double) dd[k] / graph.getNodeCount()));

                }

            }

            //------------------------------------------------Affichage TP Mesures-----------------------------------------------------------------------

            // Les graphes Random et Barbasi Albert

            Graph randomGraph = generateRandomGraph("random-graph", nodeCount, avgDegree);
            Graph bbaGraph = generateBarbasiAlbert("barbasi-albert", nodeCount, avgDegree);

            System.out.println("Noeud du réseau aléatoire = " + randomGraph.getNodeCount());
            System.out.println("Noeud du réseau Barabasi-Albert = " + bbaGraph.getNodeCount());

            System.out.println("Liens du réseau aléatoire = " + randomGraph.getEdgeCount());
            System.out.println("Liens du réseau Barabasi-Albert = " + bbaGraph.getEdgeCount());

            System.out.println("Degré moyen du réseau aleatoire= " + Toolkit.averageDegree(randomGraph));
            System.out.println("Degré moyen du reseau Barabasi-Albert= " + Toolkit.averageDegree(bbaGraph));

            System.out.println("Coefficient de clustering du réseau aléatoire : " + Toolkit.averageClusteringCoefficient(randomGraph));
            System.out.println("Coefficient de clustering du réseau Barabasi-Albert : " + Toolkit.averageClusteringCoefficient(bbaGraph));

            System.out.println("Le réseau aleatoire" + ((Toolkit.isConnected(randomGraph)) ? " est" : " n'est pas") + " connexe");
            System.out.println("Le réseau Barabasi-Albert" + ((Toolkit.isConnected(bbaGraph)) ? " est" : " n'est pas") + " connexe");

            System.out.println("La distance moyenne dans le réseau aléatoire = " + Math.log(randomGraph.getNodeCount()) / Math.log(Toolkit.averageDegree(randomGraph)));
            System.out.println("La distance moyenne dans le réseau de Barabasi-Albert  = " + Math.log(bbaGraph.getNodeCount()) / Math.log(Math.log(bbaGraph.getNodeCount())));

            //Affichage de la méthode variante de la méthode de copie (node 100,degree 4,p 0.9)

            System.out.println("Coeficient de clustering (La méthode variante de la méthode de copie (node 100,degree 4,p 0.9) ) =  " + Toolkit.averageClusteringCoefficient(variantFn(100, 4, 0.9)));
            double avgDistRandomGraph = computeAverageDistance(randomGraph, 100);
            System.out.println("La distance moyenne de graphe aléatoire (randomGraph, 100) = "+avgDistRandomGraph);


 //---------------------------------------------------------TP2 Propagation dans les réseaux--------------------------------------------------------------------------------------------------------------------------------------------------------------


            // Propagation dans le réseau
            double beta = 1.0 / 7.0;
            double mu = 1.0 / 14.0;
            System.out.println("Le taux de propagation est :" + beta / mu);

            Propagation propagation = new Propagation();
            Graph propGraph = generateRandomGraph("propagation-graph", nodeCount, avgDegree);

            System.out.println("Le seuil épidémique du réseau de collaboration = " + avgDegree / Propagation.degVariance(graph));
            System.out.println("Le seuil épidémique du réseau aléatoire = " + Toolkit.averageDegree(propGraph) / Propagation.degVariance(propGraph));

            //--------------------------------------------------- Simulation 1 case-------------------------------------------------------------------------------------------------------------------------

            propagation.withoutInfection(graph);
            propagation.infectRandomNode(graph);
            propagation.spread(graph, 1.0 / 7.0, 1.0 / 14.0);
            propagation.generateData("data/epidemie_first_case.dat");

            //----------------------------------------------------Simulation 2nd case-----------------------------------------------------------------------------------------------------------------------

            propagation.infectRandomNode(graph);
            Propagation.randomImmunition(graph);
            Graph s2Graph = propagation.spread(graph, 1.0 / 7.0, 1.0 / 14.0);

            //propagation.generateData("src/main/resources/deuxiemeCas");
            Graph gNewAleaatoire = Propagation.removeInfected(s2Graph);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation aléatoire = " + Toolkit.averageDegree(gNewAleaatoire) / Propagation.degVariance(gNewAleaatoire));
            Propagation.reset();

            //----------------------------------------------------Simulation 3rd case-----------------------------------------------------------------------------------------------------------------------

            propagation.infectRandomNode(graph);
            Propagation.selectiveImmunition(graph);
            Graph s3Graph = propagation.spread(graph, 1.0 / 7.0, 1.0 / 14.0);
            //propagation.generateData("src/main/resources/troisiemeCas");
            Graph g4 = Propagation.removeInfected(s3Graph);
            System.out.println("Le seuil épidémique du réseau avec stratégies d'immunisation séclective = " + Toolkit.averageDegree(g4) / Propagation.degVariance(g4));

        } catch (IOException e) {
            e.printStackTrace();
            fs.end();
        } finally {
            fs.removeSink(graph);
        }
    }

//-------------------------------------------------------Méthodes et fonctions de TP Mesures----------------------------------------------------------------------------------------------------

    //La méthode variante de la méthode de copie
    public static Graph variantFn(int node, int degree, double p) {
        Graph graph = new SingleGraph("graph");
        Generator g = new WattsStrogatzGenerator(node, degree, p);
        g.addSink(graph);
        g.begin();
        while (g.nextEvents()) ;
        g.end();
        graph.display(false);
        return graph;
    }

    //Le calcul des distances moyennes
    private static double computeAverageDistance(Graph graph, int size) {
        double distance = 0, nb = 0;
        List<Node> randomNodeSet = Toolkit.randomNodeSet(graph, size);
        for (Node currNode : randomNodeSet) {
            BreadthFirstIterator<Node> bfi = new BreadthFirstIterator<>(currNode);
            while (bfi.hasNext()) {
                Node opNode = bfi.next();
                Integer key = bfi.getDepthOf(opNode);
                distance += bfi.getDepthOf(opNode);
                nb++;
            }
        }
        return distance / nb;
    }


    //Ecriture des données générées dans un fichier
    public static void generateData(String fileName, String line) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        try {
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }

    //La normalisation
    public static double normalize(Map<Integer, Double> map) {
        return map
                .values()
                .stream()
                .reduce(0.0, Double::sum)
                .doubleValue();
    }

    //Génération de graphe aléatoire
    public static Graph generateRandomGraph(String nomGraphe, int taille, double degre) {
        Graph graph = new SingleGraph(nomGraphe);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        Generator g = new RandomGenerator(degre);
        g.addSink(graph);
        g.begin();
        for (int i = 0; i < taille; i++)
            g.nextEvents();
        g.end();
        return graph;
    }

    //Génération de graphe avec la méthode de BarbasiAlbert
    public static Graph generateBarbasiAlbert(String nomGraphe, int taille, double degre) {
        Graph graph = new SingleGraph(nomGraphe);
        Generator g = new BarabasiAlbertGenerator((int) degre);
        g.addSink(graph);
        g.begin();
        for (int i = 0; i < taille; i++) {
            g.nextEvents();
        }
        g.end();
        return graph;
    }
}


