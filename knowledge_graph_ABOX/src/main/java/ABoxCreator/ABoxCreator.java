package ABoxCreator;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ABoxCreator {

    public static OntModel loadABoxData(OntModel aboxModel, String resourcePath, String baseURL, OntModel tboxModel) {
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

                OntClass paperClass = null;
                if (paperType.equals("Full_Paper")) {
                    paperClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.FULL_PAPER));
                } else if (paperType.equals("Short_Paper")) {
                    paperClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.SHORT_PAPER));
                } else if (paperType.equals("Demo_Paper")) {
                    paperClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.DEMO_PAPER));
                } else if (paperType.equals("Poster")) {
                    paperClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.POSTER));
                }

                // Get the Ontology classes for creating the individuals below

                OntClass authorClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.AUTHOR));
                OntClass areaClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.AREA));
                OntClass venueClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.VENUE));
                OntClass reviewerClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.REVIEWER));

                OntClass conferenceClass = null;
                OntClass editorClass = null;
                OntClass proceedingClass = null;
                OntClass journalClass = null;
                OntClass chairClass = null;
                OntClass volumeClass = null;

                if (isConference){
                    chairClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.CHAIR));
                    proceedingClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.CONFERENCE_PROCEEDINGS));
                    if (conferenceType.equals("Symposium")) {
                        conferenceClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.SYMPOSIUM));
                    } else if (conferenceType.equals("Regular_Conference")) {
                        conferenceClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.REGULAR_CONFERENCE));
                    } else if (conferenceType.equals("Expert_Group")) {
                        conferenceClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.EXPERT_GROUP));
                    } else if (conferenceType.equals("Workshop")) {
                        conferenceClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.WORKSHOP));
                    }
                } else {
                    journalClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.JOURNAL));
                    editorClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.EDITOR));
                    volumeClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.JOURNAL_VOLUME));
                }

                OntClass posRevClass = null;
                OntClass negRevClass = null;

                if (isAccepted) {
                    posRevClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.POSITIVE_REVIEW));
                } else {
                    negRevClass = tboxModel.getOntClass(baseURL.concat(TBoxVariables.NEGATIVE_REVIEW));
                }


                // All papers are considered SubmittedPaper
                // This block refers to all the rows of the same paper.
                if (paperID.equals(previousPaperID)) {
                    if (isAccepted){
                        if (isConference) {
                            // In the same papers only add values that are not repeated!
                            System.out.println("Same paper accepted conference");
                        } else{
                            System.out.println("Same paper accepted journal");
                        }

                    } else{
                        if (isConference) {
                            System.out.println("Same paper rejected conference");
                        } else{
                            System.out.println("Same paper rejected journal");
                        }
                    }
                    previousPaperID = paperID; // change previousPaperID to show the one that inserted now

                } else {
                    if (isAccepted){
                        if (isConference) {
                            System.out.println("New paper accepted conference");

                            // Create Class Individuals
                            // #########################################################################################

                            // Create Author Individual
                            Individual authorIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.AUTHOR).concat( "#").concat(authorID), authorClass);

                            // Create Area Individual
                            Individual areaIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.AREA).concat( "#").concat(areaID), areaClass);

                            // Create Positive Review Individual
                            Individual posRevIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.POSITIVE_REVIEW).concat( "#").concat(reviewID), posRevClass);

                            // Create Reviewer 1 and 2 Individuals
                            Individual rev1Individual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.REVIEWER).concat( "#").concat(reviewer1ID), reviewerClass);
                            Individual rev2Individual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.REVIEWER).concat( "#").concat(reviewer2ID), reviewerClass);

                            // Create Chair Individual
                            Individual chairIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.EDITOR).concat( "#").concat(handlerID), chairClass);

                            // Create Conference Proceeding Individual
                            Individual confProcIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.CONFERENCE_PROCEEDINGS).concat( "#").concat(proceedingVolumeID), proceedingClass);

                            // Create Conference Proceeding Individual
                            Individual conferenceIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.CONFERENCE).concat( "#").concat(sourceID), conferenceClass);

                            // Create paper Individual
                            Individual paperIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.DEMO_PAPER).concat( "#").concat(paperID), paperClass);

                            // Create Data & Object Properties
                            // #########################################################################################

                            // Create Paper Object Properties
                            paperIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_TITLE), paperTitle)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_AUTHOR), authorIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PAPER_IS_RELATED_TO), areaIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_POSITIVE_REVIEW_SUBMITTED), posRevIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.IS_SUBMITTED), conferenceIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_REVIEWER), rev1Individual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_REVIEWER), rev2Individual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.INCLUDED_IN_CONFERENCE_PROCEEDINGS), confProcIndividual);

                            authorIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), authorName)
                                            .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_SUBMITTED), paperIndividual);

                            areaIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.AREA_HAS_NAME), area);

                            posRevIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_POSITIVE_DECISION), reviewerDecision)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_TEXT), reviewerText);

                            rev1Individual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), reviewer1)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.SUBMITS_POSITIVE_REVIEW), posRevIndividual);

                            rev2Individual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), reviewer2)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.SUBMITS_POSITIVE_REVIEW), posRevIndividual);;

                            chairIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.CHAIR_ASSIGNS_REVIEWER), rev1Individual)
                                            .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.CHAIR_ASSIGNS_REVIEWER), rev2Individual)
                                            .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), handler)
                                            .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HANDLES_CONFERENCE), conferenceIndividual);

                            // EXPECTING ERROR WITH TYPE INT
                            confProcIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.CONFERENCE_PROCEEDINGS_HAS_YEAR), String.valueOf(year));
                            conferenceIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HANDLED_BY_CHAIR), chairIndividual)
                                                .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.VENUE_HAS_NAME), source)
                                                .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.VENUE_IS_RELATED_TO), areaIndividual);

                        } else{
                            System.out.println("New paper accepted journal");

                            // Create Class Individuals
                            // #########################################################################################

                            // Create Author Individual
                            Individual authorIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.AUTHOR).concat( "#").concat(authorID), authorClass);

                            // Create Area Individual
                            Individual areaIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.AREA).concat( "#").concat(areaID), areaClass);

                            // Create Positive Review Individual
                            Individual posRevIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.POSITIVE_REVIEW).concat( "#").concat(reviewID), posRevClass);

                            // Create Reviewer 1 and 2 Individuals
                            Individual rev1Individual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.REVIEWER).concat( "#").concat(reviewer1ID), reviewerClass);
                            Individual rev2Individual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.REVIEWER).concat( "#").concat(reviewer2ID), reviewerClass);

                            // Create Chair Individual
                            Individual editorIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.EDITOR).concat( "#").concat(handlerID), editorClass);

                            // Create Conference Proceeding Individual
                            Individual journalVolIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.CONFERENCE_PROCEEDINGS).concat( "#").concat(proceedingVolumeID), volumeClass);

                            // Create Conference Proceeding Individual
                            Individual journalIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.CONFERENCE).concat( "#").concat(sourceID), journalClass);

                            // Create paper Individual
                            Individual paperIndividual = aboxModel.createIndividual(baseURL.concat(TBoxVariables.DEMO_PAPER).concat( "#").concat(paperID), paperClass);

                            // Create Data & Object Properties
                            // #########################################################################################

                            // Create Paper Object Properties
                            paperIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_TITLE), paperTitle)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_AUTHOR), authorIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PAPER_IS_RELATED_TO), areaIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_POSITIVE_REVIEW_SUBMITTED), posRevIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.IS_SUBMITTED), journalIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_REVIEWER), rev1Individual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_REVIEWER), rev2Individual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.INCLUDED_IN_JOURNAL_VOLUME), journalVolIndividual);;

                            authorIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), authorName)
                                            .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_SUBMITTED), paperIndividual);

                            areaIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.AREA_HAS_NAME), area);

                            posRevIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_POSITIVE_DECISION), reviewerDecision)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HAS_TEXT), reviewerText);

                            rev1Individual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), reviewer1)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.SUBMITS_POSITIVE_REVIEW), posRevIndividual);

                            rev2Individual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), reviewer2)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.SUBMITS_POSITIVE_REVIEW), posRevIndividual);;

                            editorIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.CHAIR_ASSIGNS_REVIEWER), rev1Individual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.CHAIR_ASSIGNS_REVIEWER), rev2Individual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.PERSON_HAS_NAME), handler)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HANDLES_JOURNAL), journalIndividual);

                            // EXPECTING ERROR WITH TYPE INT
                            journalVolIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.JOURNAL_VOLUME_HAS_YEAR), String.valueOf(year));
                            journalIndividual.addProperty(aboxModel.getProperty(baseURL, TBoxVariables.HANDLED_BY_EDITOR), editorIndividual)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.VENUE_HAS_NAME), source)
                                    .addProperty(aboxModel.getProperty(baseURL, TBoxVariables.VENUE_IS_RELATED_TO), areaIndividual);

                        }

                    } else{
                        if (isConference) {
                            System.out.println("New paper rejected conference");
                        } else{
                            System.out.println("New paper rejected journal");
                        }
                    }
                    previousPaperID = paperID;
                }
            }
            return aboxModel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aboxModel;
    }

    public static void saveABoxOntology(OntModel aboxModel, String outputPath) {
        aboxModel.write(System.out, "RDF/XML-ABBREV"); // Print the Abox model to console
        try {
            // Write the Abox model to the output file
            OutputStream outputStream = new FileOutputStream(outputPath);
            aboxModel.write(outputStream, "RDF/XML-ABBREV");
            outputStream.close();
        } catch (Exception e) {
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
        public static final String REJECTED_PAPER = "RejectedPaper";
        public static final String JOURNAL = "Journal";
        public static final String SYMPOSIUM = "Symposium";
        public static final String PERSON = "Person";
        public static final String REVIEWER = "Reviewer/";
    }
}
