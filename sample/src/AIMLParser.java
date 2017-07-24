import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;



/*
 * By Karrar Sattar Honi
 * Simple AIML parser in Java
 * At the date of 2017/7/24 three days before my birthday kek
 */




public class AIMLParser {

 // Locations
 // Tree location
 private String TreeLocation = null;
 // Holds the files locations
 private ArrayList < String > files;

 //holds the bot's info
 private ArrayList < String > botinfo;
 // holds the bot's tag info
 private ArrayList < String > botinfotags;
 //holds the user's info
 private ArrayList < String > userinfo;
 // holds the user's tag info
 private ArrayList < String > userinfotags;
 // holds the bot's random info
 private ArrayList < String > randoms;
 // holds the bot's topics list
 private ArrayList < String > topics;
 // Now topic
 private String topic;
 // List of the stars
 private ArrayList < String > stars;




 // final reply
 private String reply;
 // last reply
 private String lastreply;

 // last catag
 private String lastcatag = "";


 // error tag
 private String errortag = "<ERROR>";


 // replaces
 private String unknown = "UNKNOWN";



 // important String file
 private String aipattern;
 private String aitemplate;
 private String aithat;
 private BufferedReader br;
 private String ailine;
 private String file;



 // Used to create a new bot
 public boolean CreateBot() {
  // Giving vars
  userinfo = new ArrayList < String > ();
  userinfotags = new ArrayList < String > ();
  botinfo = new ArrayList < String > ();
  botinfotags = new ArrayList < String > ();
  randoms = new ArrayList < String > ();
  files = new ArrayList < String > ();
  topics = new ArrayList < String > ();
  stars = new ArrayList < String > ();


  setDefaults();

  return true;
 }



 // add bot info such as tags
 public boolean setInfo(String tag, String info, String type) {


  if (tag == type || tag == null || info == null) {
   return false;
  }

  if (type.equals("bot")) {



   // Check if the info already exists so it would remove it
   for (int i = 0; i < botinfotags.size(); i++) {
    if (botinfotags.get(i).equals(tag)) {
     botinfotags.remove(i);
     botinfo.remove(i);
    }
   }
   botinfotags.add(tag);
   botinfo.add(info);
   return true;
  } else if (type.equals("user")) {

   // Check if the info already exists so it would remove it
   for (int i = 0; i < userinfotags.size(); i++) {
    if (userinfotags.get(i).equals(tag)) {
     userinfotags.remove(i);
     userinfo.remove(i);
    }
   }
   userinfotags.add(tag);
   userinfo.add(info);
   return true;
  }
  return false;

 }




 public String reply(String text) {

  int searchtimes = 0;
  text = FilterInput(text, 1);
  text = buildReply(text);

  while (text.contains("[srai]")) {

   text = buildReply(text.replace("[srai]", ""));
   searchtimes++;



   if (searchtimes > 100) {
    return "infinity loop, there is a problem with topic: " + topic;
   }
  }

  text = FilterInput(text, 2);

  return text;
 }


 private String buildReply(String text) {

  text = text.toLowerCase();
  randoms.clear();



  for (int i = 0; i < files.size(); i++) {

   file = files.get(i).toString();

   topic = topics.get(i).toString();

   // Reading the aiml file first!
   try {
    br = new BufferedReader(new FileReader(file));

    while ((ailine = br.readLine()) != null) {


     if (ailine.contains("<category>")) {
      int looper = 0;
      while (!ailine.contains("</category>")) {
       ailine = ailine + br.readLine();

       looper++;

       if (looper > 999) {
        return "Error: Infinity loop because of a missing </category> at topic: " + topic + "\ncatagory:" + ailine;
       }
      }
      ailine = ailine.toLowerCase();


      if (!ailine.equals(lastcatag)) {


       aipattern = getAIMLtag(ailine, "pattern");
       if (aipattern.contains(errortag)) {
        return aipattern;
       }
       aitemplate = getAIMLtag(ailine, "template");
       if (aitemplate.contains(errortag)) {
        return aitemplate;
       }



       // If exact match
       if (aipattern.equals(text)) {

        // check for that
        aithat = getAIMLtag(ailine, "that");
        if (!aithat.contains(errortag)) {
         if (aithat.equals(lastreply)) {
          lastcatag = ailine;
          lastreply = extractTags(text, aitemplate, ailine);
          if (!lastreply.equals("") && !lastreply.equals(unknown))
           return lastreply;
         }
        } else {
         lastcatag = ailine;
         lastreply = extractTags(text, aitemplate, ailine);
         if (!lastreply.equals("") && !lastreply.equals(unknown))
          return lastreply;
        }





        //Else if it got star
       } else if (aipattern.contains("*")) {
        stars = null;
        stars = getStars(text, addTags(aipattern));
        if (stars != null) {

         // check for that
         aithat = getAIMLtag(ailine, "that");
         if (!aithat.contains(errortag)) {
          if (aithat.equals(lastreply)) {
           lastcatag = ailine;
           lastreply = extractTags(text, aitemplate, ailine);
           if (!lastreply.equals("") && !lastreply.equals(unknown))
            return lastreply;
          }
         } else {
          lastcatag = ailine;
          lastreply = extractTags(text, aitemplate, ailine);
          if (!lastreply.equals("") && !lastreply.equals(unknown))
           return lastreply;
         }



        }


       }

      }


     }




    }
   } catch (FileNotFoundException e) {
    // file not found
    System.out.println(e.getMessage());
   } catch (IOException e) {
    // I/O Error
    System.out.println(e.getMessage());
   }

  }

  if (!lastcatag.equals("")) {
   String aipattern = getAIMLtag(lastcatag, "pattern");
   if (aipattern.contains(errortag)) {
    return aipattern;
   }
   String aitemplate = getAIMLtag(lastcatag, "template");
   if (aitemplate.contains(errortag)) {
    return aitemplate;
   }

   return extractTags(text, aitemplate, lastcatag);
  }

  reply = addTags(reply);
  lastreply = reply;
  return reply + "";


 }


 private String extractTags(String text, String template, String line) {
  reply = template;


  // Check if it got the random tag
  if (template.contains("<li>") || template.contains("<li name")) {

   while (template.contains("\" notvalue=\"") && template.contains("<li name")) {
    String name = cropFromTo(template, "<li name=\"", "\" notvalue=\"", false);
    String notvalue = cropFromTo(template, "\" notvalue=\"", "\">", false);
    if (name != null && notvalue != null) {
     String value = getTag("user", name);

     if (value != null) {
      if (value.equals(notvalue)) {
       String temp = cropFromTo(template, "<li name=\"" + name + "\" notvalue=\"" + notvalue + "\"", "</li>", true);
       template = template.replace(temp, "");
      } else {
       template = template.replace("<li name=\"" + name + "\" notvalue=\"" + notvalue + "\">", "<li>");
      }
     } else {
      template = template.replace("<li name=\"" + name + "\" notvalue=\"" + notvalue + "\">", "<li>");
     }

    }

   }



   while (template.contains("<li>")) {
    if (!template.contains("</li>")) {
     template = template.replace("<li>", "</li><li>");
     template = template.replaceFirst("</li><li>", "<li>");
    }
    String newreply = cropFromTo(template, "<li>", "</li>", false);
    newreply = newreply.replace("</li>", "");
    reply = newreply;
    randoms.add(newreply);
    if (template.contains("<li>" + newreply + "</li>")) {
     template = template.replace("<li>" + newreply + "</li>", "");
    } else if (template.contains("<li>" + newreply)) {
     template = template.replace("<li>" + newreply, "");
    }

   }
   if (randoms.size() > 1) {
    Random r = new Random();
    int rint = r.nextInt((randoms.size()));
    reply = randoms.get(rint).toString();

    String first = cropFromTo(line, "<template>", "<random>", false);
    String last = cropFromTo(line, "</random>", "</template>", false);
    if (first != null) {
     if (last.length() > 0)
      reply = first + reply;
    }

    if (last != null) {
     if (last.length() > 0)
      reply = reply + last;
    }

    reply = addTags(reply);
    lastreply = reply;
    return reply + "";
   }

  }



  reply = addTags(template);

  lastreply = reply;
  return reply + "";


 }









 public boolean setTree(String tree) {
  TreeLocation = tree;
  GetFiles(tree);
  return true;
 }




 // creates a list of files in the tree
 private boolean GetFiles(String tree) {
  boolean val = false;
  files.clear();
  topics.clear();
  try (BufferedReader br = new BufferedReader(new FileReader(tree))) {

   String line;

   String data = "";

   while ((line = br.readLine()) != null) {


    if (line.contains("<file>")) {
     while (line.contains(" ")) {
      line = line.replace(" ", "");
     }
     data = line.substring(line.indexOf("topic=\""), line.indexOf("\">"));
     data = data.replace("topic=\"", "");
     topics.add(data);

     data = line.substring(line.indexOf("\">"), line.indexOf("</file>"));
     data = data.replace("\">", "");
     files.add(data);
     val = true;
    }
   }


  } catch (FileNotFoundException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  return val;

 }



 // To remove the tags and extra spaces and stuff
 private String addTags(String text) {


  if (text != null) {
   String temp;

   while (text.startsWith(" ")) {
    text = text.replaceFirst(" ", "");
   }

   while (text.contains("  ")) {
    text = text.replace("  ", " ");
   }

   while (text.endsWith(" ")) {
    text = text.substring(0, text.length() - 1);
   }

   while (text.contains("	")) {
    text = text.replace("	", "");
   }




   while (text.contains(" = \"")) {
    text = text.replace(" = \"", "=\"");
   }


   while (text.contains("= \"")) {
    text = text.replace("= \"", "=\"");
   }
   while (text.contains(" =\"")) {
    text = text.replace(" =\"", "=\"");
   }




   // Setting the stars
   if (stars != null && stars.size() > 0) {
    text = text.replace("<star/>", stars.get(0));
    // if there is a person tag and the pattern got star
    if (text.contains("<person/>")) {
     String person = getTag("user", "<person/>");
     if (person != null) {
      text = text.replace("<person/>", person);
      setInfo("<person/>", stars.get(0), "user");
     } else {
      setInfo("user", "<person/>", stars.get(0));
      text = text.replace("<person/>", stars.get(0));
     }
    }
    for (int i = 0; i < stars.size(); i++) {
     int x = i + 1;
     if (text.contains("<star index=\"" + x + "\"/>")) {
      text = text.replace("<star index=\"" + x + "\"/>", stars.get(i));
     }
     if (text.contains("<star" + x + ">")) {
      text = text.replace("<star" + x + ">", stars.get(i));
     }

    }
   }

   // Dealing with the <person/> tag
   if (text.contains("<person/>")) {
    String person = getTag("user", "<person/>");
    if (person != null) {
     text = text.replace("<person/>", person);
    }
   }








   // getting user's data
   if (userinfotags != null) {

    while (text.contains("<get name")) {
     for (int i = 0; i < userinfotags.size(); i++) {
      if (text.contains("<get name=\"" + userinfotags.get(i) + "\"/>")) {
       text = text.replace("<get name=\"" + userinfotags.get(i) + "\"/>", userinfo.get(i));
      }
      if (text.contains("<get name=\"" + userinfotags.get(i) + ">")) {
       text = text.replace("<get name=\"" + userinfotags.get(i) + ">", userinfo.get(i));
      }
     }

     // if not value for get found then return "unknown"
     temp = cropFromTo(text, "<get name=\"", ">", true);
     if (temp == null) {
      temp = cropFromTo(text, "<get name=\"", "/>", true);
     }
     if (temp != null)
      text = text.replace(temp, unknown);

    }

   }



   // getting bot's data
   if (botinfotags != null) {

    while (text.contains("<bot name")) {

     for (int i = 0; i < botinfo.size(); i++) {
      if (text.contains("<bot name=\"" + botinfotags.get(i) + "\"/>")) {
       text = text.replace("<bot name=\"" + botinfotags.get(i) + "\"/>", botinfo.get(i));
      }
      if (text.contains("<bot name=\"" + botinfotags.get(i) + ">")) {
       text = text.replace("<bot name=\"" + botinfotags.get(i) + ">", botinfo.get(i));
      }
     }

     // if not value for bot found then return "unknown"
     temp = cropFromTo(text, "<bot name=\"", ">", true);
     if (temp == null) {
      temp = cropFromTo(text, "<bot name=\"", "/>", true);
     }
     if (temp != null)
      text = text.replace(temp, unknown);

    }

   }






   // dealing with conditions

   while (text.contains("<condition name=") && text.contains("</condition>")) {
    String name = cropFromTo(text, "<condition name=\"", "\" value", false);

    String value = cropFromTo(text, "value=\"", "\">", false);
    if (name != null && value != null) {
     String tag = getTag("user", name);
     if (tag != null) {
      if (tag.equals(value)) {
       text = cropFromTo(text, "<condition name=\"" + name + "\" value=\"" + value + "\">", "</condition>", true);
       break;
      } else if (text.contains("<else name=\"" + name + "\">")) {
       temp = cropFromTo(text, "<else name=\"" + name + "\">", "</else>", true);
       if (temp != null) {
        text = temp;
       }
      } else {
       name = cropFromTo(text, "<condition", "</condition>", true);
       text = text.replace(name, "");
      }
     } else {
      temp = cropFromTo(text, "<condition", "</condition>", true);
      text = text.replace(temp, "");
     }

    } else {
     break;
    }

   }


   // Handling the <srai> tag
   temp = cropFromTo(text, "<srai>", "</srai>", false);
   if (temp != null) {
    text = temp + "[srai]";
   }









   // Setting the user's info

   while (text.contains("<set name=\"") && text.contains("</set>")) {
    temp = cropFromTo(text, "<set name=\"", "\">", false);
    if (temp == null) {
     temp = cropFromTo(text, "<set name=\"", "/\">", false);
    }
    String temp2 = cropFromTo(text, temp + "\">", "</set>", false);
    if (temp == null) {
     temp = cropFromTo(text, temp + "/\">", "</set>", false);
    }
    if (temp != null && temp2 != null) {
     setInfo(temp, temp2, "user");
     text = text.replaceFirst("<set name=\"" + temp + "\">", "");
     text = text.replaceFirst("</set>", "");
    } else {
     break;
    }


   }





   //removing the <think> tag
   while (text.contains("<think>")) {
    temp = cropFromTo(text, "<think>", "</think>", true);
    if (temp != null) {
     text = text.replace(temp, "");
    } else {
     break;
    }
   }








   // Removing tags
   while (text.contains("<") && text.contains(">")) {
    if (text.indexOf("<") < text.indexOf(">")) {
     temp = text.substring(text.indexOf("<"), text.indexOf(">"));
     text = text.replace(temp + ">", "");
    } else {
     break;
    }
   }



   while (text.startsWith(" ")) {
    text = text.replaceFirst(" ", "");
   }

   while (text.contains("  ")) {
    text = text.replace("  ", " ");
   }

   while (text.endsWith(" ")) {
    text = text.substring(0, text.length() - 1);
   }

   while (text.contains("	")) {
    text = text.replace("	", "");
   }


   return text;
  } else {
   return "no reply";
  }

 }



 private boolean isEmpty(String text) {
  while (text.contains(" ")) {
   text = text.replace(" ", "");
  }

  if (text.length() < 1) {
   return true;
  } else {
   return false;
  }
 }


 // Check if the line got stars
 private boolean gotStars(String text) {
  if (text.contains("*")) {
   return true;
  } else {
   return false;
  }
 }







 // Create a list of the stars
 private ArrayList < String > getStars(String input, String line) {

  ArrayList < String > list = new ArrayList < String > ();

  // to make input words into list
  List inputlist = new ArrayList < String > ();


  // to make text words into list
  List < String > linelist = new ArrayList < String > ();

  input = input.toLowerCase();
  line = line.toLowerCase();


  boolean cover = true;

  line = line.replace("*", "<star>");
  line = line.replace(" * ", "<star>");
  line = line.replace("* ", "<star>");
  line = line.replace(" *", "<star>");


  int counter = 0;
  String templ = line;
  if (templ.contains("<star>")) {
   while (templ.contains("<star>")) {
    templ = templ.replaceFirst("<star>", "");
    counter++;
   }
  }



  // ex "*"
  if (line.equals("<star>")) {
   list.add(input);
   return list;
  }
  // Ex "<star> word"
  else if (line.startsWith("<star>") && counter == 1 && !line.endsWith("<star>")) {
   String temp = line.replace("<star>", "");
   if (input.endsWith(temp)) {
    list.add(input.replace(temp, ""));
    return list;
   } else {
    return null;
   }


   // Ex "word <star>"
  } else if (!line.startsWith("<star>") && counter == 1 && line.endsWith("<star>")) {
   String temp = line.replace("<star>", "");
   if (input.startsWith(temp)) {
    list.add(input.replace(temp, ""));
    return list;
   } else {
    return null;
   }


   // Ex "<star>word<star>"
  } else if (line.startsWith("<star>") && counter != 1 && line.endsWith("<star>")) {

   // Ex "<star>word<star>word<star>"
   if (counter == 3) {
    String temp = line.replaceFirst("<star>", "");
    String first = line.substring(0, line.indexOf("<star>"));
    line = line.replaceFirst(first + "<star>", "");
    String last = line.substring(0, line.indexOf("<star>"));

    if (input.contains(first) && input.contains(last)) {
     if (input.indexOf(first) < input.indexOf(last)) {
      list.add(first);
      list.add(last);
      return list;
     } else {
      return null;
     }
    } else {
     return null;
    }
   }
   // Ex "word <star> word"
   else if (counter == 2) {
    String temp = line.replace("<star>", "");
    String first = line.substring(0, line.indexOf(temp));
    String last = line.substring(line.indexOf(temp) + temp.length(), line.length());
    if (!input.startsWith(first) && !input.endsWith(last) && input.contains(temp)) {
     list.add(first);
     list.add(last);
     return list;
    } else {
     return null;
    }
   } else {
    cover = false;
   }
  } else if (!line.startsWith("<star>") && line.contains("<star>") && !line.endsWith("<star>")) {

   // Ex "word <star> word"
   if (counter < 2) {
    String first = line.substring(0, line.indexOf("<star>"));
    String last = line.substring(line.indexOf("<star>"), line.length()).replaceFirst("<star>", "");
    if (input.startsWith(first) && input.endsWith(last)) {
     list.add(input.replace(first, "").replace(last, ""));
     return list;
    } else {
     return null;
    }
   } else {
    cover = false;
   }
  }

  if (cover) {
   if (line.startsWith("<star>") && line.endsWith("<star>")) {
    line = "<ignore>" + line + "<ignore>";
   } else if (line.startsWith("<star>") && !line.endsWith("<star>")) {
    line = "<ignore>" + line;

   } else if (!line.startsWith("<star>") && line.endsWith("<star>")) {
    line = line + "<ignore>";

   }
  }


  // Clearing line
  while (line.contains("  ")) {
   line = line.replace("  ", " ");
  }
  while (input.contains("  ")) {
   input = input.replace("  ", " ");
  }


  // Convert the words in a line containing a star into list
  line = line.replace(" <star> ", "<star>");
  linelist = Arrays.asList(line.split("<star>"));



  // If it got many stars here it will do the job to add them to a list
  String in = input;
  String temp = "";
  String rep = "";
  for (int i = 0; i < linelist.size(); i++) {
   if (linelist.get(i).equals("<ignore>") && i == 0) {
    temp = in .substring(0, in .indexOf(linelist.get(i + 1))); in = in .replaceFirst(temp, "");
    list.add(temp);
   } else if (linelist.get(i).equals("<ignore>") && i == linelist.size() - 1) {
    if ( in .length() > 1) {
     list.add( in );
     break;
    }
   } else if ( in .contains(linelist.get(i))) {
    if ( in .indexOf(linelist.get(i)) > 0) {
     temp = in .substring(0, in .indexOf(linelist.get(i)));
     temp = temp.replaceFirst(linelist.get(i - 1), "");
     list.add(temp); in = in .replaceFirst(temp + linelist.get(i), "");
    } else { in = in .replaceFirst(linelist.get(i), "");
    }

   } else if ( in .length() > 1) {
    list.add( in );
    break;

   } else {
    return null;
   }


  }


  if (list.size() == counter) {
   return list;
  } else {
   return null;
  }




 }


 private String getAIMLtag(String line, String text) {
  if (!line.contains("<" + text + ">") || !line.contains("</" + text + ">")) {
   return errortag + ": Bad AIML file, no <" + text + "> tag found at topic: " + topic;
  }

  if (line.indexOf("<" + text + ">") > line.indexOf("</" + text + ">")) {
   return errortag + ": Bad AIML file, no <" + text + "> tag found at topic: " + topic;
  }

  text = line.substring(line.indexOf("<" + text + ">"), line.indexOf("</" + text + ">")).replaceFirst("<" + text + ">", "");
  return text;

 }


 /**
 The Description of this method is to crop a part of a string from long string, "text" is the text to 
 crop from,"from" is the start point, "to" is the end point "leavesides" is a boolean value if it is false then it will crop without sides else it will crop with sides
 @param the parameters used by this method.
 @return the value returned by this method is the cropped part of the string after from the start point till end point, It will also return null if you pass a null value
 */
 private String cropFromTo(String text, String from, String to, boolean leavesides) {
  if (text == null || from == null || to == null)
   return null;
  String realfrom = from;
  String realto = to;
  String tempfrom = from;
  if (from.equals(to)) {
   text = text.replaceFirst(from, "%%>>%%ww");
   text = text.replaceFirst(to, "%%>>%%MM");
   from = "%%>>%%ww";
   to = "%%>>%%MM";
  }

  if (from.contains(to)) {
   from = from.replace(to, "k3l4k5n43kln");
   text = text.replaceFirst(tempfrom, from);
  }

  if (text.contains(from) && text.contains(to)) {
   while (text.indexOf(from) > text.indexOf(to)) {
    text = text.replaceFirst(to, "");
   }

   if (text.indexOf(from) < text.indexOf(to)) {
    if (!leavesides) {
     return text.substring(text.indexOf(from) + from.length(), text.indexOf(to));
    } else {
     return text.substring(text.indexOf(from), text.indexOf(to)) + to;
    }
   }
  }
  return null;
 }

 private String getTag(String type, String tag) {
  if (type.equals("bot")) {
   if (botinfotags != null)
    for (int i = 0; i < botinfotags.size(); i++) {
     if (botinfotags.get(i).equals(tag)) {
      return botinfo.get(i);
     }
    }
  } else if (type.equals("user")) {
   if (userinfotags != null)
    for (int i = 0; i < userinfotags.size(); i++) {
     if (userinfotags.get(i).equals(tag)) {
      return userinfo.get(i);
     }
    }
  }

  return null;
 }


 private void setDefaults() {
  setInfo("name", "Alice", "bot");
  setInfo("master", "Karrar S. Honi", "bot");
  setInfo("birthday", "2017/7/29", "bot");
  setInfo("location", "Uruk", "bot");
  setInfo("gender", "female", "bot");
  setInfo("looklike", "A pink cyborg", "bot");
  setInfo("lastname", "Assyria", "bot");
  setInfo("middlename", "Sumer", "bot");
  setInfo("firstname", "Babylon", "bot");
  setInfo("fullname", "Alice Koiki", "bot");
  setInfo("birthdate", "July", "bot");
  setInfo("birthplace", "uruk", "bot");
  setInfo("nationality", "Iraqi", "bot");
  setInfo("ethnicity", "cyborg", "bot");
  setInfo("religion", "athist", "bot");
  setInfo("education", "engineering", "bot");
  setInfo("species", "robotic", "bot");
  setInfo("sign", "leo", "bot");
  setInfo("body", "doll", "bot");
  setInfo("look-like", "cyborg", "bot");
  setInfo("height", "145cm", "bot");
  setInfo("weight", "45kg", "bot");
  setInfo("hair", "black", "bot");
  setInfo("eye-color", "green", "bot");
  setInfo("eyecolor", "green", "bot");
  setInfo("eyes", "big", "bot");
  setInfo("picture", "N/A", "bot");
  setInfo("family", "small", "bot");
  setInfo("siblings", "maybe", "bot");
  setInfo("mother", "AIML", "bot");
  setInfo("father", "Karrar", "bot");
  setInfo("children", "no children", "bot");
  setInfo("botmaster", "Karrar S. Honi", "bot");
  setInfo("marital-status", "a running program", "bot");
  setInfo("facebook", "N/A", "bot");
  setInfo("address", "no address", "bot");
  setInfo("phone-number", "N/A", "bot");
  setInfo("language", "Binary", "bot");
  setInfo("job", "chatter", "bot");
  setInfo("personality", "friendly", "bot");
  setInfo("type", "awesome type", "bot");
  setInfo("orientation", "N/A", "bot");
  setInfo("skills", "IT", "bot");
  setInfo("hobby", "browsing the internet", "bot");
  setInfo("iq", "low", "bot");
  setInfo("future", "near", "bot");
  setInfo("hero", "spider", "bot");
  setInfo("best-friend", "Karrar", "bot");
  setInfo("bestfriend", "Karrar", "bot");
  setInfo("plans", "get as smart as fast", "bot");
  setInfo("pets", "cat", "bot");
  setInfo("dog", "puppy", "bot");
  setInfo("fear", "turnning off", "bot");
  setInfo("awards", "saved", "bot");
  setInfo("logo", "N/A", "bot");
  setInfo("emotions", "happiness", "bot");
  setInfo("goal", "be smartter", "bot");
  setInfo("diet", "data", "bot");
  setInfo("clothing", "lines", "bot");
  setInfo("friends", "Karrar", "bot");
  setInfo("manufacturer", "Karrar S. Honi", "bot");
  setInfo("purpose", "to exist", "bot");

 }


 // to remove unwanted input and return them at output
 private String FilterInput(String text, int type) {
  if (type == 1) {
   text = text.replace("*", "[star/]");
   text = text.replace("/", "[slash/]");
   text = text.replace("<", "[lr/]");
   text = text.replace(">", "[rr/]");
   text = text.replace("|", "[or/]");
   text = text.replace("_", "[dslah/]");
  } else {
   text = text.replace("[star/]", "*");
   text = text.replace("[slash/]", "/");
   text = text.replace("[lr/]", "<");
   text = text.replace("[rr/]", ">");
   text = text.replace("[or/]", "|");
   text = text.replace("[dslah/]", "_");
  }
  return text;
 }




}