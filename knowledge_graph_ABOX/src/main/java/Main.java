import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;

import ABoxCreator.ABoxCreator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        // OLD IMPLEMENTATION
        // Create an empty Ontology model
        //OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);

        // Load the TBox ontology from the resources folder
        // OntModel tboxModel = ABoxCreator.loadTBoxFromResource("/final_TBOX.rdf", model);

        // Create the ABox ontology
        // OntModel aboxModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, tboxModel);

        String owlFile = "src/main/resources/final_TBOX.rdf";
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        FileManager.get().readModel(model, owlFile);

        // Create the reasoner
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();

        // Bind the reasoner to the model
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        // Get the Tbox and Abox models
        OntModel tboxModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, infModel);
        OntModel aboxModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, infModel);

        // Retrieve the base URL from the TBox ontology
        String baseURL = ABoxCreator.getBaseURL(tboxModel);
        System.out.println("Base URL: " + baseURL);

        // Check the Elements included in the TBOX model
        ABoxCreator.printTBoxElements(tboxModel);

        // Load data from instances.csv and create individuals in the ABox
        aboxModel = ABoxCreator.loadABoxData(aboxModel, "/abox_data.csv", baseURL, tboxModel);

        // Save the ABox ontology to a file
        ABoxCreator.saveABoxOntology(aboxModel, "src/main/resources/abox.rdf");

        System.out.println("We have aboxModel!");
    }
}