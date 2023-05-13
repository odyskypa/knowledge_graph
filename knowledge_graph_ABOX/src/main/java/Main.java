import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello, world!");

        // Load the TBox ontology from the resources folder
        //OntModel tboxModel = loadTBoxFromResource("/design_with_defined_classes.owl");
        OntModel tboxModel = loadTBoxFromResource("/tbox.rdf");

        // Create the ABox ontology
        OntModel aboxModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, tboxModel);

        System.out.println("We have aboxModel!");


        // Add some individuals and statements to the ABox ontology
        // ...

        // Save the ABox ontology to a file
        // ...
    }

    public static OntModel loadTBoxFromResource(String resourcePath) {
        // Get the input stream for the resource file
        InputStream in = Main.class.getResourceAsStream(resourcePath);

        // Create the ontology model
        OntModel tboxModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        // Load the ontology from the input stream
        tboxModel.read(in, null);

        // Close the input stream
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tboxModel;
    }

}