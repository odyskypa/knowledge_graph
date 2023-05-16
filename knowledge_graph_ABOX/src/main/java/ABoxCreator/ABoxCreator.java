package ABoxCreator;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.io.*;

public class ABoxCreator {
    public static OntModel loadTBoxFromResource(String resourcePath) {

        // Get the input stream for the resource file
        InputStream in = ABoxCreator.class.getResourceAsStream(resourcePath);

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

    public static void loadABoxData(OntModel aboxModel, String resourcePath, String baseURL) {
        // Load the input stream for the resource file
        InputStream in = ABoxCreator.class.getResourceAsStream(resourcePath);

        // Process the CSV data and create individuals in the ABox
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;

            // Read each line of the CSV file
            while ((line = reader.readLine()) != null) {
                // Skip the first line (header)
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Split the line into columns
                String[] columns = line.split(",");

                // Extract relevant data from the columns
                String id = columns[0].trim();
                String author = columns[1].trim();
                String paper = columns[2].trim();

                // Create an individual in the ABox for each row of data
                Resource individual = aboxModel.createResource(baseURL + "/Person" + "#" + id)
                        .addProperty(aboxModel.createProperty(baseURL + "author"), author)
                        .addProperty(aboxModel.createProperty(baseURL + "paper"), paper);

                // Add more properties as needed based on the CSV columns
                // For example:
                // String paperType = columns[3].trim();
                // individual.addProperty(aboxModel.createProperty("http://example.com/property/paperType"), paperType);
                // ...

                // Process the rest of the columns and add properties accordingly
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveABoxOntology(OntModel aboxModel, String outputPath) {
        try (OutputStream out = new FileOutputStream(outputPath)) {
            // Set the RDF/XML format for serialization
            aboxModel.setNsPrefix("ex", "http://example.com/ontology/");
            aboxModel.write(out, "RDF/XML");
            System.out.println("ABox ontology saved to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getBaseURL(OntModel model) {
        // Get the document manager
        OntDocumentManager docManager = model.getDocumentManager();

        // Get the base URI of the model
        String baseURI = model.getBaseModel().getGraph().getPrefixMapping().getNsPrefixURI("");

        // Determine the effective base URI
        String effectiveBaseURI = baseURI != null ? baseURI : "";

        // Extract the base URL from the effective base URI
        String baseURL = extractBaseURL(effectiveBaseURI);

        return baseURL;
    }

    private static String extractBaseURL(String uri) {
        // Find the last occurrence of '/'
        int lastIndex = uri.lastIndexOf('/');

        // Extract the substring from the start to the last '/'
        String baseURL = uri.substring(0, lastIndex + 1);

        return baseURL;
    }
}
