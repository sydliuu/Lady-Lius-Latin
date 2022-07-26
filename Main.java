import java.util.*;
/**
 * Driver class of DactHexLine. 
 *
 * @author Sydney Liu
 * @version 6/16/2020
 */
public class Main{
    private static ArrayList<DactHexLine> lines = new ArrayList<DactHexLine>();
    private static ArrayList<String> lineString = new ArrayList<String>();
    public static void main(String[] args){
        //System.out.println();
        System.out.println("LADY LIU'S LATIN: SCANSION OF LATIN DACTYLIC HEXAMETER WITH MANUAL CORRECTION");
        System.out.println("\tBy Sydney A. Liu, summer of 2020.\n");

        takeInput();
    }

    public static void takeInput(){
        System.out.println();
        if (lines.size() == 1){
            System.out.print("(OVERRIDE/REMOVE/ADD | Line to be scanned | QUIT): ");
        } else if(lines.size() > 1){
            System.out.print("(OVERRIDE/REMOVE/ADD/RESET/BACK | Line to be scanned | QUIT): ");
        }else {
            System.out.print("(Line to be scanned | QUIT): ");
        }
        Scanner scnr = new Scanner(System.in);
        String input = scnr.nextLine();
        switch (input.toUpperCase()){
            case "OVERRIDE": 
            if (lines.size() > 0){
                manualOverride(); 
            } else {
                System.out.println("Please enter a line to be scanned first.");
                takeInput();
            }
            break;
            case "REMOVE":
            if (lines.size() > 0){
                removeVS(); 
            } else {
                System.out.println("Please enter a line to be scanned first.");
                takeInput();
            }
            break;
            case "ADD":
            if (lines.size() > 0){
                addVS(); 
            } else {
                System.out.println("Please enter a line to be scanned first.");
                takeInput();
            }
            break;
            case "RESET":
            if (lines.size() > 0){
                scanLine(lines.get(lines.size()-1).getOrigLine()); 
            } else {
                System.out.println("Please enter a line to be scanned first.");
                takeInput();
            }
            case "BACK":
            if (lines.size() > 1){
                lines.remove(lines.size()-1); 
                lineString.remove(lineString.size()-1);

                System.out.println(lineString.get(lineString.size()-1));

                takeInput();
            } else {
                System.out.println("You haven't scanned more than one line yet...");
                takeInput();
            }
            break;
            case "QUIT": quit(); break;
            default: 
            try {
                scanLine(input);
            } catch(Exception e){
                System.out.print("Please enter a valid line to be scanned! ");
                if (input.length() == 1 && 65 <= input.charAt(0) && input.charAt(0) <= 90 && lines.size() > 0){
                    System.out.print("Remember to input OVERRIDE before entering a letter.");
                }
                System.out.println();
                takeInput();
            }
            break; 
        }
    }

    public static void scanLine(String origLine){
        DactHexLine ln = new DactHexLine(origLine);
        String temp = ln.scanLineFormat(false);
        System.out.println(temp);
        lineString.add(temp);
        lines.add(ln);

        takeInput();
    }

    public static void scanLine(DactHexLine ln){ //manual input happens here
        String temp = ln.scanLineFormat(true);
        System.out.println(temp);
        lineString.add(temp);
        lines.add(ln);

        takeInput();
    }

    public static void manualOverride(){
        DactHexLine toOverride = lines.get(lines.size()-1);

        Scanner scnr = new Scanner(System.in);
        System.out.print("(Valid CAPITAL LETTER input | BACK | QUIT): ");
        String input = scnr.nextLine();

        int idxToChange=0;
        if (toOverride.getLetters().indexOf(input) != -1){ //valid letter
            idxToChange = (int)input.charAt(0)-65; //this is the idx within the ArrayList stresses and the other parallel arrayLists
            String input2="";
            //check if elided
            if (toOverride.getStresses().get(idxToChange) < 0){ //elided
                System.out.print("Unelide? (YES/BACK/QUIT): ");
                input2 = scnr.nextLine();

                switch (input2.toUpperCase()){
                    case "YES":
                    scanLine(toOverride.hiatus(idxToChange)); break;
                    case "BACK": 
                    manualOverride(); break;
                    case "QUIT":
                    quit(); break;
                    default: 
                    System.out.println("Invalid input... BACK"); manualOverride(); break;
                }
            } else if (toOverride.getVowelSounds().get(idxToChange).length() == 2){ //diphthong
                System.out.print("Break diphthong? (YES/BACK/QUIT): ");
                input2 = scnr.nextLine();

                switch (input2.toUpperCase()){
                    case "YES":
                    scanLine(toOverride.breakDip(idxToChange));
                    case "BACK": 
                    manualOverride(); break;
                    case "QUIT":
                    quit(); break;
                    default: System.out.println("Invalid input... BACK"); manualOverride(); break;
                }
            } else { //normal vowel sound
                System.out.print("Reassign the quantity at " + input + " as (LONG/SHORT/UNKNOWN)? (or BACK/QUIT): ");
                input2 = scnr.nextLine();
                switch (input2.toUpperCase()){
                    case "LONG": case "—": case "-":
                    scanLine(toOverride.reassignStress(idxToChange, 2)); break;
                    case "SHORT": case "U": 
                    scanLine(toOverride.reassignStress(idxToChange, 1)); break;
                    case "UNKNOWN": case "?":
                    scanLine(toOverride.reassignStress(idxToChange, 5)); break;
                    case "BACK": 
                    manualOverride(); break;
                    case "QUIT":
                    quit(); break;
                    default: System.out.println("Invalid input... BACK"); manualOverride(); break;
                }
            }
            //check if diphthong
            switch (input2.toUpperCase()){
                case "BACK": manualOverride(); break;
                case "QUIT": quit(); break;
                default: break;
            }
        } else if (input.toUpperCase().equals("BACK")){
            takeInput();
        } else if (input.toUpperCase().equals("QUIT")){
            quit();
        } else { //invalid input
            manualOverride();
        }
    }

    public static void addVS(){
        DactHexLine toOverride = lines.get(lines.size()-1);

        if (toOverride.getAddLetters().size() == 0){
            System.out.println("There are no other unmarked vowel sounds that can be added! Going back...");
            takeInput();
        } else {
            Scanner scnr = new Scanner(System.in);
            System.out.print("(Valid LOWERCASE LETTER input | BACK | QUIT): "); //reminder to implement back and quit
            String input = scnr.nextLine();

            if (toOverride.getAddLetters().indexOf(input) != -1){ //valid letter
                int idxInAdd = (int)input.charAt(0)-97; //this is the idx within the ArrayList addIdx

                DactHexLine toRet = toOverride.addVowelSound(idxInAdd);

                scanLine(toRet);
            } else if (input.toUpperCase().equals("BACK")){
                takeInput();
            } else if (input.toUpperCase().equals("QUIT")){
                quit();
            } else {
                addVS();
            }
        }
    }

    public static void removeVS(){
        DactHexLine toOverride = lines.get(lines.size()-1);

        Scanner scnr = new Scanner(System.in);
        System.out.print("(Valid CAPITAL LETTER input | BACK | QUIT): ");
        String input = scnr.nextLine();

        if (toOverride.getLetters().indexOf(input) != -1){ //valid letter
            int idxToChange = (int)input.charAt(0)-65; //this is the idx within the ArrayList stresses and the other parallel arrayLists
            scanLine(toOverride.removeVowelSound(idxToChange));
        } else if (input.toUpperCase().equals("BACK")){
            takeInput();
        } else if (input.toUpperCase().equals("QUIT")){
            quit();
        } else {
            removeVS();
        }
    }



    public static void quit(){

        System.out.println("You have quit the program. Hail Caesar!\n");
        System.exit(0);
    }


    


    

    
}
    