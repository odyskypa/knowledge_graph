package ABoxCreator;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                String[] columns = splitCSVLine(line);

                // Extract relevant data from the columns
                String paperID = columns[0].trim();
                String paperTitle = columns[1].trim();
                String authorID = columns[2].trim();
                String authorName = columns[3].trim();
                String paperType = columns[4].trim();
                String conferenceType = columns[5].trim();
                String sourceID = columns[6].trim();
                String source = columns[7].trim();
                int year = Integer.parseInt(columns[8].trim());
                String documentType = columns[9].trim();
                String volumeProceeding = columns[10].trim();
                String reviewer1ID = columns[11].trim();
                String reviewer1 = columns[12].trim();
                String reviewer2ID = columns[13].trim();
                String reviewer2 = columns[14].trim();
                String handlerID = columns[15].trim();
                String handler = columns[16].trim();
                String handlerType = columns[17].trim();
                String areaID = columns[18].trim();
                String area = columns[19].trim();
                String reviewID = columns[20].trim();
                String reviewerDecision = columns[21].trim();
                String reviewerText = columns[22].trim();
                String proceedingVolume = columns[23].trim();
                String proceedingVolumeID = columns[24].trim();

                // proceedingVolume = "No"  -> Review Decision = "Rejected"
                // proceedingVolume != "No" -> proceedingVolume contains Conference Proceeding or Journal Volume
                // and proceedingVolumeID the ID of Conference Proceeding or Journal Volume respectively
                if (proceedingVolume.equals("No")) {
                    System.out.println("Publication is 'No'");
                } else {
                    System.out.println("Publication is not 'No'");
                }

                if (reviewerDecision.equals("Rejected")) {
                    System.out.println("Submitted Paper is Rejected");
                } else {
                    System.out.println("Publication is not 'No'");
                }



                if (!resourceExist(aboxModel, baseURL, "Paper", paperID)) {
                    System.out.println("Resource does not exists!");

                    Resource paperIndividual = aboxModel.createResource(baseURL + "/Paper" + "#" + paperID)
                            .addProperty(aboxModel.createProperty(baseURL, "hasTitle"), paperTitle)
                            .addProperty(aboxModel.createProperty(baseURL, "hasAuthor"), authorID)
                            .addProperty(aboxModel.createProperty(baseURL, "conferenceType"), conferenceType)
                            .addProperty(aboxModel.createProperty(baseURL, "sourceID"), sourceID)
                            .addProperty(aboxModel.createProperty(baseURL, "source"), source)
                            .addProperty(aboxModel.createProperty(baseURL, "year"), Integer.toString(year))
                            .addProperty(aboxModel.createProperty(baseURL, "documentType"), documentType)
                            .addProperty(aboxModel.createProperty(baseURL, "volumeProceeding"), volumeProceeding);

                    // Create an individual for the author and link it to the paper
                    Resource authorIndividual = aboxModel.createResource(baseURL + "#" + authorID)
                            .addProperty(aboxModel.createProperty(baseURL, "authorName"), authorName);

                    // Link the author to the paper
                    paperIndividual.addProperty(aboxModel.createProperty(baseURL, "hasAuthor"), authorIndividual);

                    paperIndividual.addProperty(aboxModel.createProperty(baseURL, "reviewer1"), reviewer1)
                            .addProperty(aboxModel.createProperty(baseURL, "reviewer2"), reviewer2)
                            .addProperty(aboxModel.createProperty(baseURL, "handlerID"), handlerID)
                            .addProperty(aboxModel.createProperty(baseURL, "handler"), handler)
                            .addProperty(aboxModel.createProperty(baseURL, "handlerType"), handlerType)
                            .addProperty(aboxModel.createProperty(baseURL, "areaID"), areaID)
                            .addProperty(aboxModel.createProperty(baseURL, "areas"), area)
                            .addProperty(aboxModel.createProperty(baseURL, "reviewID"), reviewID)
                            .addProperty(aboxModel.createProperty(baseURL, "reviewerDecision"), reviewerDecision)
                            .addProperty(aboxModel.createProperty(baseURL, "reviewerText"), reviewerText);

                } else {
                    System.out.println("Resource exist!");
                }
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

    public static String[] splitCSVLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes; // Toggle insideQuotes flag
            } else if (c == ',' && !insideQuotes) {
                columns.add(sb.toString().trim());
                sb.setLength(0); // Reset StringBuilder
            } else {
                sb.append(c);
            }
        }

        // Add the last column
        columns.add(sb.toString().trim());

        return columns.toArray(new String[0]);
    }

    public static boolean resourceExist(OntModel aboxModel, String baseURL, String className, String id) {
        String resourceURI = baseURL + "/" + className + "#" + id;
        return aboxModel.containsResource(aboxModel.getResource(resourceURI));
    }
}
