import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import ABoxCreator.ABoxCreator;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        // Load the TBox ontology from the resources folder
        OntModel tboxModel = ABoxCreator.loadTBoxFromResource("/final_TBOX.rdf");

        // Retrieve the base URL from the TBox ontology
        String baseURL = ABoxCreator.getBaseURL(tboxModel);
        System.out.println("Base URL: " + baseURL);

        // Create the ABox ontology
         OntModel aboxModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, tboxModel);

        // Load data from instances.csv and create individuals in the ABox
        ABoxCreator.loadABoxData(aboxModel, "/abox_data.csv", baseURL);

        // Save the ABox ontology to a file
//        String namespaceURI = tboxModel.getNsPrefixURI("ex");  // Retrieve the namespace URI from the TBox model

        // Save the ABox ontology to a file
//        ABoxCreator.saveABoxOntology(aboxModel, "abox.rdf");

        System.out.println("We have aboxModel!");
    }
}