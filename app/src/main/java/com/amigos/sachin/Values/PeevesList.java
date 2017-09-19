package com.amigos.sachin.Values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Sachin on 8/1/2017.
 */

public class PeevesList {
    public final static String[] petPeeves = new String[]{"People",
            "Crowd",
            "Politics",
            "Philosophy",
            "Smoking",
            "Procrastination",
            "Overly Extroverts",
            "Overly Introverts",
            "Lying",
            "Hashtags",
            "Biting nails",
            "Slow walkers",
            "Misspelled signs",
            "Misspelling my name",
            "Fitness freaks",
            "K",
            "Hmm",
            "Social media",
            "PDA",
            "Pets",
            "Noisy Eaters",
            "Driving slow",
            "Sober people",
            "Drunk people",
            "Boss",
            "Fat people",
            "Kids",
            "Selfies",
            "Distraction",
            "Loud people",
            "No sense of parking",
            "No sense of public property",
            "Dating apps",
            "Pedestrians",
            "Animals",
            "Vegetarians",
            "Unpunctuality",
            "Hard-work",
            "Speed Breakers",
            "Overperfumed",
            "Clingy people",
            "Animal Cruelty",
            "Messy Toilets",
            "Personal Space",
            "Whiny people",
            "Bad Body Odour",
            "Bad Grammar",
            "Duck face"};

    public static ArrayList<String> getAllInterests(){
        HashMap<String,ArrayList<String>> prefTags = new HashMap<String, ArrayList<String>>();

        ArrayList<String> business = new ArrayList<String>(Arrays.asList("Entrepreneurship", "Management","Marketing","Strategy"));
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Painting", "Sketching","Writing","Photography"));
        ArrayList<String> entertainment = new ArrayList<String>(Arrays.asList("Music", "Theatre","Dance","Storytelling","Movies","Comedy"));
        ArrayList<String> sports = new ArrayList<String>(Arrays.asList("Cricket", "Tennis","Football","Chess","Athletics"));
        ArrayList<String> music = new ArrayList<String>(Arrays.asList("EDM", "Rock","Hip Hop","Country Music","Pop Music","Rap","Instrumental"));
        ArrayList<String> lifestyle = new ArrayList<String>(Arrays.asList("Books", "Fashion","Food","Travel","Adventure"));
        ArrayList<String> psychology = new ArrayList<String>(Arrays.asList("Philosophy", "Psychoanalysis","Cognition","Sociology"));
        ArrayList<String> science = new ArrayList<String>(Arrays.asList("Astronomy", "Geoscience","Biology"));
        ArrayList<String> technology = new ArrayList<String>(Arrays.asList("Artificial Intelligence", "Cryptocurrency","Programming"));

        prefTags.put("business",business);
        prefTags.put("arts",arts);
        prefTags.put("entertainment",entertainment);
        prefTags.put("sports",sports);
        prefTags.put("music",music);
        prefTags.put("lifestyle",lifestyle);
        prefTags.put("psychology",psychology);
        prefTags.put("science",science);
        prefTags.put("technology",technology);

        ArrayList<String> interests = new ArrayList<String>();

        Iterator it = prefTags.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            for (String s:(ArrayList<String>)pair.getValue()){
                interests.add(s);
            }
            it.remove();
        }

        return interests;
    }

    public static ArrayList<String> getAllLifestyleInterests(){
        ArrayList<String> lifestyle = new ArrayList<String>(Arrays.asList("Fashion",
                "Food",
                "Travel",
                "Adventure",
                "TV series",
                "Anime and cartoons",
                "Astrology",
                "Chess",
                "Gaming",
                "Poker",
                "Psychology",
                "Tattoo"));
        return lifestyle;
    }

    public static ArrayList<String> getAllArtsInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Painting",
                "Sketching",
                "Writing",
                "Photography",
                "Drawing",
                "Poetry"));
        return arts;
    }

    public static ArrayList<String> getAllEntertainmentInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Music",
                "Theatre",
                "Dance",
                "Storytelling",
                "Movies",
                "Comedy"));
        return arts;
    }

    public static ArrayList<String> getAllBusinessInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Entrepreneurship",
                "Management",
                "Marketing",
                "Strategy"));
        return arts;
    }

    public static ArrayList<String> getAllSportsInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Cricket",
                "Tennis",
                "Football",
                "Badminton",
                "Athletics",
                "Basketball",
                "Bowling",
                "Golf",
                "Skating" ,
                "Vollyeball"));
        return arts;
    }

    public static ArrayList<String> getAllMusicInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("EDM",
                "Rock",
                "Hip Hop",
                "Pop",
                "Rap",
                "Instrumental"));
        return arts;
    }

    public static ArrayList<String> getAllTechnologyInterests(){
        ArrayList<String> arts = new ArrayList<String>(Arrays.asList("Artificial Intelligence",
                "Cryptocurrency",
                "Programming" ,
                "Blockchain",
                "Space travel",
                "Astronomy"));
        return arts;
    }

    public static ArrayList<String> getAllNewInterestsTopics(){
        ArrayList<String> topics = new ArrayList<String>(Arrays.asList("Health and Medicine",
                "Health",
                "Healthy Eating",
                "Medicine and Healthcare",
                "Exercise",
                "Healthy Living",
                "Nutrition",
                "Mental Health",
                "History, Philosophy, Religion, and Humanities",
                "History",
                "World History",
                "History of the United States of America",
                "Indian History",
                "Military History and Wars",
                "World War II",
                "Philosophy",
                "Religion",
                "Philosophy of Science",
                "Literature, Languages, and Communication",
                "Books",
                "Writing",
                "Literature",
                "Book Recommendations",
                "Novels",
                "Reading",
                "Fiction",
                "Literary Fiction",
                "Science, Engineering, and Technology",
                "Technology",
                "Science",
                "Computer Science",
                "Physics",
                "Psychology",
                "Economics",
                "Mathematics",
                "Technology Trends",
                "Computer Programming",
                "Software Engineering",
                "Machine Learning",
                "Social Psychology",
                "The Universe",
                "Algorithms",
                "Neuroscience",
                "Programming Languages",
                "Statistics (academic discipline)",
                "Probability (statistics)",
                "Startups",
                "Lean Startups",
                "Startup Founders and Entrepreneurs",
                "Startup Strategy",
                "Venture Capital",
                "Business, Work, and Careers",
                "Business",
                "Entrepreneurship",
                "Finance",
                "Business Strategy",
                "Marketing",
                "Investing",
                "Business Models",
                "Stock Markets",
                "Career Advice",
                "Personal Finance",
                "Money",
                "Art, Design, and Style",
                "Design",
                "Photography",
                "Fashion and Style",
                "Fine Art",
                "Web Design",
                "User Interfaces",
                "Digital Photography",
                "Product Design of Physical Goods",
                "Clothing and Apparel",
                "Recreation, Sports, Travel, and Activities",
                "Visiting and Travel",
                "International Travel",
                "Vacations",
                "Tourism",
                "Travel Hacks",
                "Sports",
                "Football (Soccer)",
                "Cricket (sport)",
                "Education, Schools, and Learning",
                "Education",
                "Scientific Research",
                "Higher Education",
                "Learning",
                "TED",
                "Graduate School Education",
                "The College and University Experience",
                "News, Entertainment, and Pop Culture",
                "Journalism",
                "Entertainment",
                "Hollywood",
                "Movies",
                "Music",
                "Rock Music",
                "Musicians",
                "Television",
                "Television Series",
                "Game of Thrones (TV series)",
                "How I Met Your Mother (TV series)",
                "Life, Relationships, and Self",
                "Dating and Relationships",
                "Life and Living",
                "Love",
                "Humor",
                "Self-Improvement",
                "Social Advice",
                "Tips and Hacks for Everyday Life",
                "Interpersonal Interaction",
                "Life Lessons",
                "Friendship",
                "Comedy",
                "Life Advice",
                "Marriage",
                "Dating Advice",
                "Politics, Law, Government, and Judiciary",
                "Politics",
                "Government",
                "International Relations",
                "Food, Cuisines, and Cooking",
                "Food",
                "Cooking",
                "Recipes",
                "Restaurants",
                "Social Media",
                "YouTube",
                "Google (company)",
                "Facebook (product)",
                "Quora",
                "Adventure",
                "TV series",
                "Anime and Cartoons",
                "Astrology",
                "Fashion",
                "Adventure",
                "TV series",
                "Anime and cartoons",
                "Astrology",
                "Chess",
                "Gaming",
                "Poker",
                "Tattoo",
                "Painting",
                "Sketching",
                "Drawing",
                "Poetry",
                "Theatre",
                "Dance",
                "Storytelling",
                "Management",
                "Marketing",
                "Strategy",
                "Cricket",
                "Tennis",
                "Badminton",
                "Athletics",
                "Basketball",
                "Bowling",
                "Golf",
                "Skating" ,
                "Vollyeball",
                "EDM",
                "Rock",
                "Hip Hop",
                "Pop",
                "Rap",
                "Instrumental",
                "Artificial Intelligence",
                "Cryptocurrency",
                "Blockchain",
                "Space travel",
                "Astronomy"));
        return topics;
    }
}
