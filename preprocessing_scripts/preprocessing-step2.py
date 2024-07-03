import ast
import pandas as pd
from os.path import join
from ast import literal_eval
from random import choice
import random
import string
from faker import Faker

def main():
    # Data Loading
    data_dir_path = "./"
    print("Data Collected")
    input_file_path = join(data_dir_path, "csvfile.csv")
    output_file_path = join(data_dir_path, "abox_data.csv")

    df = pd.read_csv(input_file_path)
    cols = ['id', 'title', 'authors', 'keywords', 'venue', 'year', 'volume', 'abstract']
    df = df[cols]
    df = pd.DataFrame(df)

    print("Starting Preprocessing Publication Data...")
    df['authors_name'] = df['authors'].apply(literal_eval).apply(lambda x: [d['name'] for d in x])
    df['authors'] = df['authors'].apply(literal_eval).apply(lambda x: [d['id'] for d in x])
    df['venue'] = df['venue'].apply(literal_eval).apply(lambda x: x['raw'])

    # Data Processing
    data = []
    paper_types = ['Full Paper', 'Short Paper', 'Demo Paper']
    conference_types = ['Workshop', 'Symposium', 'Expert Group', 'Regular Conference']
    document_types = ['Conference', 'Journal']
    decision_types = ['Accepted', 'Rejected']
    persons_list = [item for sublist in df['authors'] for item in sublist]
    persons_list = list(set(persons_list))

    for _, row in df.iterrows():
        submission_id = row['id']
        authors = row['authors']
        author_names = row['authors_name']
        paper = row['title']
        areas = row['keywords']
        areas = ast.literal_eval(areas)
        areas = areas[0]
        source = row['venue']
        year = row['year']
        volume = row['volume']
        abstract = row['abstract']

        document_type = choice(document_types)
        is_conference = document_type == 'Conference'

        paper_type = choice(list(paper_types + ['Poster'])) if is_conference else choice(paper_types)
        conference_type = choice(conference_types) if is_conference else None

        #reviewer_1, reviewer_2 = str(choice(persons_list)), str(choice(persons_list))
        fake = Faker()
        reviewer_1, reviewer_2 = fake.name(), fake.name()
        handler = ''.join(random.choice(string.ascii_lowercase) for i in range(10))


        areas = areas.replace(", ", ",") if isinstance(areas, str) else None
        decision = choice(decision_types)

        for author, author_name in zip(authors,author_names):
            data_dict = dict()
            data_dict['Paper_ID'] = str(submission_id)
            data_dict['Paper'] = str(paper)
            data_dict['Author_ID'] = str(author)
            data_dict['Author_name'] = str(author_name)
            data_dict['Paper_Type'] = str(paper_type)
            data_dict['Conference_Type'] = str(conference_type)
            data_dict['Source_ID'] = None
            data_dict['Source'] = str(source)
            data_dict['Year'] = str(year)
            data_dict['Document_Type'] = str(document_type)
            data_dict['Volume_Proceeding'] = str(volume)
            data_dict['Reviewer_1_ID'] = None
            data_dict['Reviewer_1'] = reviewer_1
            data_dict['Reviewer_2_ID'] = None
            data_dict['Reviewer_2'] = reviewer_2
            data_dict['Handler_ID'] = None
            data_dict['Handler'] = handler
            data_dict['Handler_Type'] = 'Chair' if data_dict['Document_Type'] == 'Conference' else 'Editor'
            data_dict['Area_ID'] = None
            data_dict['Areas'] = str(areas)
            data_dict['Review_ID'] = None
            data_dict['Reviewer_Decision'] = str(decision)
            data_dict['Reviewer_Text'] = str(abstract).replace("\n", "")
            data_dict['Venue_plus_Volume_Proceeding'] = "{} {}".format(str(source), str(volume)) if data_dict['Reviewer_Decision'] == 'Accepted' else 'No'
            data_dict['Proceeding_Volume_ID'] = None
            data.append(data_dict)

    data_df = pd.DataFrame(data)
    data_df = data_df.drop_duplicates()
    #data_df['Author_ID'] = pd.factorize(data_df['Author'])[0]
    print("End Preprocess")

    #Assign UNIQUE IDs
    id_mapping = {}
    def generate_id(letter, number):
        return '{}{}'.format(letter, random.randint(int('{}0000'.format(number)), int('{}9999'.format(number))))

    def assign_id(value, letter, number):
        # Assign an ID to a value or return the already assigned ID if it exists
        if value not in id_mapping:
            id_mapping[value] = generate_id(letter, number)
        return id_mapping[value]

    ids = [
        #('Author', 'Author_ID', 'A', 1),
        ('Source', 'Source_ID', 'S', 2),
        ('Handler', 'Handler_ID', 'H', 3),
        ('Areas', 'Area_ID', 'AR', 4),
        ('Reviewer_Text', 'Review_ID', 'R', 5),
        ('Venue_plus_Volume_Proceeding', 'Proceeding_Volume_ID', 'VP', 8)
    ]

    for column_name, id_column, letter, number in ids:
        data_df[id_column] = data_df[column_name].apply(lambda x: assign_id(x, letter, number))

    letter = 'RV'
    number = 6
    data_df['Reviewer_1_ID'] = data_df['Reviewer_1'].apply(lambda x: assign_id(x, letter, number))
    number = 7
    data_df['Reviewer_2_ID'] = data_df['Reviewer_2'].apply(lambda x: assign_id(x, letter, number))
    letter = "VP"
    number = 8
    data_df['Proceeding_Volume_ID'] = data_df['Venue_plus_Volume_Proceeding'].apply(lambda x: assign_id(x, letter, number) if x !="No" else "No")


    data_df.to_csv(output_file_path, index=False) #Write the final CSV
    print("CSV File Written")

if __name__ == "__main__":
    main()