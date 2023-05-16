package ABoxCreator;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static void loadABoxData(OntModel aboxModel, String resourcePath, String baseURL, OntModel tboxModel) {
        // Load the input stream for the resource file
        InputStream in = ABoxCreator.class.getResourceAsStream(resourcePath);

        // Process the CSV data and create individuals in the ABox
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;

            boolean isFirstLine = true;
            String previousPaperID = "xxxxx";

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
                String paperID = prepare_str(columns[0].trim());
                String paperTitle = prepare_str(columns[1].trim());
                String authorID = prepare_str(columns[2].trim());
                String authorName = prepare_str(columns[3].trim());
                String paperType = prepare_str(columns[4].trim());
                String conferenceType = prepare_str(columns[5].trim());
                String sourceID = prepare_str(columns[6].trim());
                String source = prepare_str(columns[7].trim());
                int year = Integer.parseInt(columns[8].trim());
                String documentType = prepare_str(columns[9].trim());
                String volumeProceeding = prepare_str(columns[10].trim());
                String reviewer1ID = prepare_str(columns[11].trim());
                String reviewer1 = prepare_str(columns[12].trim());
                String reviewer2ID = prepare_str(columns[13].trim());
                String reviewer2 = prepare_str(columns[14].trim());
                String handlerID = prepare_str(columns[15].trim());
                String handler = prepare_str(columns[16].trim());
                String handlerType = prepare_str(columns[17].trim());
                String areaID = prepare_str(columns[18].trim());
                String area = prepare_str(columns[19].trim());
                String reviewID = prepare_str(columns[20].trim());
                String reviewerDecision = prepare_str(columns[21].trim());
                String reviewerText = columns[22].trim();
                String proceedingVolume = prepare_str(columns[23].trim());
                String proceedingVolumeID = prepare_str(columns[24].trim());

                Boolean isConference = documentType.equals("Conference");
                Boolean isAccepted = reviewerDecision.equals("Accepted");

                Resource paper;
                if (paperType.equals("Full_Paper")) {
                    paper = tboxModel.getResource(baseURL.concat(TBoxVariables.FULL_PAPER));
                } else if (paperType.equals("Short_Paper")) {
                    paper = tboxModel.getResource(baseURL.concat(TBoxVariables.SHORT_PAPER));
                } else if (paperType.equals("Demo_Paper")) {
                    paper = tboxModel.getResource(baseURL.concat(TBoxVariables.DEMO_PAPER));
                } else {
                    paper = tboxModel.getResource(baseURL.concat(TBoxVariables.POSTER));
                }

                // This block refers to all the rows of the same paper.
                if (paperID.equals(previousPaperID)) {






                    previousPaperID = paperID; // change previousPaperID to show the one that inserted now

                } else {

                    previousPaperID = paperID;


                }
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

    public static void saveABoxOntology(OntModel aboxModel, String outputPath, String baseURL) {
        try (OutputStream out = new FileOutputStream(outputPath)) {
            // Set the base URL for serialization
            aboxModel.setNsPrefix("", baseURL);
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

    public static String prepare_str(String str) {
        return str
                .replace("; ", ";")
                .replace(".", "_")
                .replace(",", "_")
                .replace(": ", ":")
                .replace("'s", "_s")
                .replace(" ", "_")
                .replace("-", "_")
                .replace("’", "")
                .replace("©", "")
                .replace("›", "")
                .replace("‹", "")
                .replace("°", "")
                .replace("%", "")
                .replace("$", "")
                .replace("@", "")
                .replace("!", "")
                .replace("&", "")
                .replace("~", "")
                .replace("'", "")
                .replace("+", "")
                .replace("ν", "")
                .replace("η", "")
                .replace("?", "")
                .replace(">", "")
                .replace("<", "")
                .replace("μ", "")
                .replace("\"", "")
                .replace("\\", "")
                .replace("^", "")
                .replace("#", "")
                .replaceAll("[\\p{Ps}\\p{Pe}]", "") // To remove all opening & closing brackets (https://stackoverflow.com/a/25853119/6390175)
                .replaceAll("[^A-Za-z0-9] ","")
                ;
    }

    public static void printTBoxElements(Model tboxModel) {
        Set<Property> objectPropertySet = getObjectProperties(tboxModel);
        Set<Property> dataPropertySet = getDataProperties(tboxModel);
        Set<Resource> classSet = getClasses(tboxModel);

        // Print the retrieved object properties
        System.out.println("Object Properties:");
        for (Property objectProperty : objectPropertySet) {
            System.out.println(objectProperty.getURI());
        }

        // Print the retrieved data properties
        System.out.println("Data Properties:");
        for (Property dataProperty : dataPropertySet) {
            System.out.println(dataProperty.getURI());
        }

        // Print the retrieved classes
        System.out.println("Classes:");
        for (Resource classResource : classSet) {
            System.out.println(classResource.getURI());
        }
    }

    private static Set<Property> getObjectProperties(Model tboxModel) {
        StmtIterator objectProperties = tboxModel.listStatements(null, RDF.type, OWL.ObjectProperty);
        Set<Property> objectPropertySet = new HashSet<>();
        while (objectProperties.hasNext()) {
            Statement statement = objectProperties.next();
            objectPropertySet.add(statement.getSubject().as(Property.class));
        }
        return objectPropertySet;
    }

    private static Set<Property> getDataProperties(Model tboxModel) {
        StmtIterator dataProperties = tboxModel.listStatements(null, RDF.type, OWL.DatatypeProperty);
        Set<Property> dataPropertySet = new HashSet<>();
        while (dataProperties.hasNext()) {
            Statement statement = dataProperties.next();
            dataPropertySet.add(statement.getSubject().as(Property.class));
        }
        return dataPropertySet;
    }

    private static Set<Resource> getClasses(Model tboxModel) {
        StmtIterator classes = tboxModel.listStatements(null, RDF.type, OWL.Class);
        Set<Resource> classSet = new HashSet<>();
        while (classes.hasNext()) {
            Statement statement = classes.next();
            classSet.add(statement.getSubject());
        }
        return classSet;
    }

    public class TBoxVariables {
        // Object Properties
        public static final String HANDLED_BY_EDITOR = "handledByEditor";
        public static final String HANDLES_CONFERENCE = "handlesConference";
        public static final String INCLUDED_IN_JOURNAL_VOLUME = "includedInJournalVolume";
        public static final String INCLUDED_IN = "includedIn";
        public static final String HANDLED_BY_CHAIR = "handledByChair";
        public static final String HANDLES_JOURNAL = "handlesJournal";
        public static final String ASSIGNS_REVIEWER = "assignsReviewer";
        public static final String HAS_SUBMITTED = "hasSubmitted";
        public static final String HAS_POSITIVE_REVIEW_SUBMITTED = "hasPositiveReviewSubmitted";
        public static final String HAS_AUTHOR = "hasAuthor";
        public static final String HAS_NEGATIVE_REVIEW_SUBMITTED = "hasNegativeReviewSubmitted";
        public static final String CHAIR_ASSIGNS_REVIEWER = "chairAssignsReviewer";
        public static final String SUBMITS_POSITIVE_REVIEW = "submitsPositiveReview";
        public static final String VENUE_IS_RELATED_TO = "venueIsRelatedTo";
        public static final String IS_SUBMITTED = "isSubmitted";
        public static final String HAS_REVIEWER = "hasReviewer";
        public static final String HAS_REVIEW_SUBMITTED = "hasReviewSubmitted";
        public static final String HANDLES = "handles";
        public static final String BEING_HANDLED_BY = "beingHandledBy";
        public static final String EDITOR_ASSIGNS_REVIEWER = "editorAssignsReviewer";
        public static final String SUBMITS_NEGATIVE_REVIEW = "submitsNegativeReview";
        public static final String SUBMITS_REVIEW = "submitsReview";
        public static final String INCLUDED_IN_CONFERENCE_PROCEEDINGS = "includedInConferenceProceedings";
        public static final String IS_RELATED_TO = "isRelatedTo";
        public static final String PAPER_IS_RELATED_TO = "paperIsRelatedTo";

        // Data Properties
        public static final String HAS_POSITIVE_DECISION = "hasPositiveDecision";
        public static final String JOURNAL_VOLUME_HAS_YEAR = "journalVolumeHasYear";
        public static final String CONFERENCE_PROCEEDINGS_HAS_YEAR = "conferenceProceedingsHasYear";
        public static final String AREA_HAS_NAME = "areaHasName";
        public static final String PERSON_HAS_NAME = "personHasName";
        public static final String HAS_YEAR = "hasYear";
        public static final String VENUE_HAS_NAME = "venueHasName";
        public static final String HAS_NEGATIVE_DECISION = "hasNegativeDecision";
        public static final String HAS_DECISION = "hasDecision";
        public static final String HAS_NAME = "hasName";
        public static final String HAS_TEXT = "hasText";
        public static final String HAS_TITLE = "hasTitle";

        // Classes
        public static final String DEMO_PAPER = "DemoPaper";
        public static final String CONFERENCE_PROCEEDINGS = "ConferenceProceedings";
        public static final String PAPER = "Paper";
        public static final String CHAIR = "Chair";
        public static final String WORKSHOP = "Workshop";
        public static final String EDITOR = "Editor";
        public static final String EXPERT_GROUP = "ExpertGroup";
        public static final String POSTER = "Poster";
        public static final String JOURNAL_VOLUME = "JournalVolume";
        public static final String SUBMITTED_PAPER = "SubmittedPaper";
        public static final String VENUE = "Venue";
        public static final String REGULAR_CONFERENCE = "RegularConference";
        public static final String CONFERENCE = "Conference";
        public static final String NEGATIVE_REVIEW = "NegativeReview";
        public static final String REVIEW = "Review";
        public static final String AREA = "Area";
        public static final String SHORT_PAPER = "ShortPaper";
        public static final String AUTHOR = "Author";
        public static final String FULL_PAPER = "FullPaper";
        public static final String POSITIVE_REVIEW = "PositiveReview";
        public static final String ACCEPTED_PAPER = "AcceptedPaper";
        public static final String JOURNAL = "Journal";
        public static final String SYMPOSIUM = "Symposium";
        public static final String PERSON = "Person";
        public static final String REVIEWER = "Reviewer";
    }
}
