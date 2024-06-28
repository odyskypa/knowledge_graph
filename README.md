# Semantic Data Management
Project of Semantic Data Management (SDM) Course for the `Master in Data Science` Program of Universitat Politècnica de Catalunya (UPC)
***
## Knowledge Graph of Scientific Research Papers 
This laboratory session focuses on knowledge graphs using the GraphDB database. It is recommended that participants familiarize themselves with RDF Schema, SPARQL specification, and the GraphDB manual before attending.

### Setup Instructions
1. **Download and Install GraphDB**:
   - Download from [GraphDB Free](https://www.ontotext.com/products/graphdb/graphdb-free/).
   - Follow installation steps without altering default settings.
   
2. **Launch GraphDB**:
   - Start the server and access the GraphDB interface through `http://localhost:7200/`.
   
3. **Configure Settings**:
   - Navigate to `Settings...` and set custom java properties:
     - `graphdb.workbench.maxUploadSize=40000000000`
     - `Xmx=2000m`
   - Save and restart GraphDB.
   
4. **Load DBpedia Data**:
   - Download and unzip TBOX and ABOX data files from DBpedia.
       - [http://downloads.dbpedia.org/2014/dbpedia_2014.owl.bz2](http://downloads.dbpedia.org/2014/dbpedia_2014.owl.bz2)
       - [http://downloads.dbpedia.org/2014/en/instance_types_en.nt.bz2](http://downloads.dbpedia.org/2014/en/instance_types_en.nt.bz2)
   - Create a repository with inference set to `RDFS-Plus (Optimized)`.
   - Import data into GraphDB using the specified base IRI: `http://dbpedia.org/resource/`.
   - Click on `Setup` and activate `Autocomplete`.

### Session Activities
#### A. Exploring DBpedia
- Use GraphDB’s visual interface to explore the classes and properties within DBpedia.
- Perform SPARQL queries to understand the structure and relationships within the dataset.

#### B. Ontology Creation
1. **TBOX Definition**:
   - Define a TBOX for the research publication domain.
   - Utilize tools like Protege or VocBench to create the ontology, providing graphical representations and files as required.

2. **ABOX Definition**:
   - Align the created ABOX with the defined TBOX, using tools such as Jena API or RDFLib to manage data transformations and triple creation.

3. **Final Ontology Setup**:
   - Import TBOX and ABOX into GraphDB.
   - Ensure rdf:type links are correctly set between TBOX and ABOX, using SPARQL CONSTRUCT queries or programmatically with Jena/RDFLib.

4. **Querying the Ontology**:
   - Execute SPARQL queries to interact with the ontology, focusing on authorship and publication data within academic databases.

### Testing and Autocompletion
- Ensure the autocomplete feature is enabled in GraphDB to assist with query construction.
- Test the setup by running predefined SPARQL queries to confirm data connectivity and schema accuracy.
