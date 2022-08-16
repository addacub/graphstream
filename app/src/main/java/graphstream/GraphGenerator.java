package graphstream;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkSVG;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.images.Resolution;
import org.graphstream.stream.file.images.Resolutions;

public class GraphGenerator {

    private final Graph graph;
    private final int[] degrees;
    private final FileSinkImages fsi;

    public GraphGenerator(String graph_id, int[] degrees) {
        this.graph = new SingleGraph(graph_id);
        this.degrees = degrees;

        System.setProperty("org.graphstream.ui", "swing");

        // Graph attributes
        graph.setAttribute("ui.stylesheet",
                "url('file:///home/cubea/Documents/repos/java/graphstream/app/src/main/java/graphstream/stylesheet.css')");
        graph.setAttribute("ui.quality");
        graph.setAttribute("layout.quality", 4);
        graph.setAttribute("layout.stabilization-limit", 0.95);
        graph.setAttribute("layout.force", 1.2);

        OutputPolicy outputPolicy = OutputPolicy.NONE;
        OutputType type = OutputType.PNG;
        Resolution resolution = Resolutions.HD1080;

        fsi = FileSinkImages.createDefault();
        fsi.setQuality(Quality.HIGH);
        fsi.setResolution(resolution);
        fsi.setOutputType(type);
        fsi.setOutputPolicy(outputPolicy);
        fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);

        fsi.setStyleSheet(
                "url('file:///home/cubea/Documents/repos/java/graphstream/app/src/main/java/graphstream/stylesheet.css')");

        // Image production
        graph.addSink(fsi);

    }

    public void generate() {
        new NodeExtended(null, 0, 0, graph);

        String prefix = "/home/cubea/Documents/repos/java/graphstream/images/treeGraph_";

        try {
            fsi.begin(prefix);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        find_solutions();

        try {
            fsi.end();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void display() {
        graph.display();

    }

    public Graph get_graph() {
        return graph;
    }

    public void find_solutions() {
        fsi.outputNewImage();

        Iterator<? extends Node> k = graph.getNode("L0-0").getDepthFirstIterator();
        Node just_visited = null;
        int just_visited_level = 0;
        int L1_counter = 0;
        int L2_counter = 0;
        int L3_counter = 0;
        List<Integer> node_list = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 7, 8));

        String just_visited_classes;

        while (k.hasNext()) {
            // Node Prunning
            if (just_visited != null) {
                if (just_visited_level == 1 && L1_counter % 2 == 0) {
                    prune_node(just_visited);
                } else if (just_visited_level == 2 && L2_counter % 2 == 0) {
                    prune_node(just_visited);
                } else if (just_visited_level == 3 && node_list.contains(L3_counter)) {
                    prune_node(just_visited);
                }
            }

            // Visit node
            Node visiting = k.next();
            String visiting_classes = (String) visiting.getAttribute("ui.class");

            // Record level type of node being visited.
            just_visited_level = extract_level(visiting_classes);
            if (just_visited_level == 1) {
                L1_counter += 1;
            } else if (just_visited_level == 2) {
                L2_counter += 1;
                L3_counter = 0;
            } else if (just_visited_level == 3) {
                L3_counter += 1;
            }

            // Check if node has been pruned
            if (visiting_classes.contains("pruned")) {
                continue;
            }
            visiting.setAttribute("ui.class", "visiting, " + visiting_classes);

            // Set the previously visited node
            if (just_visited != null) {
                just_visited_classes = (String) just_visited.getAttribute("ui.class");
                if (!just_visited_classes.contains("pruned")) {
                    just_visited.setAttribute("ui.class", "visited, " + just_visited_classes);
                }

            }

            just_visited = visiting;
            fsi.outputNewImage();
            // sleep(extract_level(visiting_classes));
        }

        // Mark the last node as visited
        just_visited_classes = (String) just_visited.getAttribute("ui.class");
        just_visited.setAttribute("ui.class", "visited");
        fsi.outputNewImage();

    }

    private void prune_node(Node source) {
        int counter = 0;
        int level = extract_level((String) source.getAttribute("ui.class"));
        Iterator<? extends Node> k = source.getDepthFirstIterator();

        while (k.hasNext()) {
            Node node = k.next();
            String classes = (String) node.getAttribute("ui.class");

            // Only prune nodes below source node
            if (extract_level(classes) < level) {
                break;
            }

            // Source node is visited and not pruned
            if (counter > 0) {
                node.setAttribute("ui.class", "pruned, " + classes);
            }

            counter += 1;

        }

    }

    private int extract_level(String classes) {
        String[] parts = classes.split(", ");
        return Character.getNumericValue(parts[parts.length - 1].charAt(1));
    }

    private void sleep(int level) {
        try {
            Thread.sleep(750 * 5 - 750 * level);
        } catch (Exception e) {
        }
    }

    private class NodeExtended {

        private final int level;
        private final String id;
        private final Graph graph;
        private final ArrayList<NodeExtended> children_nodes = new ArrayList<NodeExtended>();

        public NodeExtended(String parent_id, int level, int child_number, Graph graph) {
            this.level = level;
            if (parent_id != null) {
                this.id = MessageFormat.format("{0}_L{1}-{2}", parent_id, level, child_number);
            } else {
                this.id = MessageFormat.format("L{1}-{2}", parent_id, level, child_number);
            }

            this.graph = graph;

            Node node = graph.addNode(this.id);

            node.setAttribute("ui.class", MessageFormat.format("L{0}", this.level));
            node.setAttribute("layout.weight", 5 - level);

            if (level < 3) {
                node.setAttribute("ui.label", MessageFormat.format("L{0}", this.level));
            } else if (child_number % 4 == 0 && level < 4) {
                node.setAttribute("ui.label", MessageFormat.format("L{0}", this.level));
            } else if ((parent_id.contains("L3-0") || parent_id.contains("L3-5") || parent_id.contains("L3-10"))
                    && child_number == 0) {
                node.setAttribute("ui.label", MessageFormat.format("L{0}", this.level));
            }

            for (int i = 0; i < degrees[this.level]; i++) {
                children_nodes.add(new NodeExtended(this.id, this.level + 1, i, this.graph));
            }

            create_edges();

        }

        private void create_edges() {
            for (NodeExtended child_node : children_nodes) {
                graph.addEdge(id + child_node.id, id, child_node.id);
            }
        }

    }

}
