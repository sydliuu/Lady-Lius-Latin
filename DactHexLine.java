import java.util.*; 
/**
 * The DactLine class represents a line of Latin dactylic poetry. 
 *  
 * @author Sydney Liu
 * @version 5/29/2020
 */
public class DactHexLine{
    private String origLine;
    private String line; 
    //private int n; //n is number of feet; eg. dactylic hexameter is  6

    //these are parallel ArrayLists
    public ArrayList<String> vowelSounds = new ArrayList<String>(); //holds strings of each vowel sound to be marked in line
    public ArrayList<Integer> vowelSoundsIdx = new ArrayList<Integer>(); 
    //holds the indexes of the first character of each vowel sound to be marked in line

    public ArrayList<Integer> stresses = new ArrayList<Integer>(); 
    //1 = U; 2 = -; 3 = X; -1 short, elided; -2: long, elided; 5 = unknown; -5 = elided unknown

    //private ArrayList<Character> punct = new ArrayList<Character>();
    private ArrayList<Integer> punctIdx = new ArrayList<Integer>();
    private int N; //number of syllables

    private ArrayList<Integer> elidedIdx = new ArrayList<Integer>();

    private ArrayList<String> letters = new ArrayList<String>(); //Cap letters
    private ArrayList<String> addLetters = new ArrayList<String>(); //lowercase letters
    private ArrayList<Integer> addIdx = new ArrayList<Integer>(); //indexes within line of vowel sounds not to be marked

    /** 
     * Formats printing a DactHexLine object, with I. knowns, II. guess by meter, III. override/manual input.
     * 
     * (Originally I made a toString method but I realized I needed a param)
     * 
     * @return This is the String representation of a DactHexLine object. 
     */
    public String scanLineFormat(boolean overridden){ 
        String toRet = "";
        if (!overridden){
            determineVowelSounds(); 
        }

        toRet += "I. Knowns without meter. Long by position; diphthongs assumed; no hiatus assumed; final 'o' assumed to be long (unless overridden):";
        toRet += "\n" + this.format(this.stresses, 1);

        toRet += "\n\nII. Guess by working backwards with meter constraints: \n     If it's clearly wrong, try overriding or adding/removing vowel sounds!";
        toRet += "\n" + this.format(this.meterGuess(), 2);

        toRet += "\n\nIII. OVERRIDE—unelide, break a diphthong, or reassign a quantity:";
        toRet +="\n    •Number of syllables calculated: " + N + " (Meter rules: [12, 17])\n";
        toRet += "\n" + this.format(this.stresses, 3); 

        toRet += "\n\nIV. ADD (lowercase) or REMOVE (uppercase) vowel sounds:";
        toRet +="\n    •Number of syllables calculated: " + N + " (Meter rules: [12, 17])\n";
        toRet += "\n" + this.format(this.stresses, 4); 

        return toRet;
    }

    /** 
     * 1 arg constructor
     * 
     * Precondition: origLine is in perfect dactylic hexameter 
     * 
     * @param line This is the line's text.
     */
    public DactHexLine(String origLine){
        this.origLine = origLine; 
        line =  removePunct(origLine); //also goes to lowercase
    }  

    /**
     * Creates a deep copy of a DactHexLine object. 
     * @return DactHexLine Returns a deep copy clone of this object.
     */
    public DactHexLine cloneDHL(){
        DactHexLine ln = new DactHexLine(this.origLine);
        ln.vowelSounds = cloneStrList(this.vowelSounds);
        ln.vowelSoundsIdx = cloneIntList(this.vowelSoundsIdx);
        ln.stresses = cloneIntList(this.stresses);
        ln.punctIdx = cloneIntList(this.punctIdx);
        ln.N = this.N;
        ln.elidedIdx = cloneIntList(this.elidedIdx);

        //Recalculated when format is called:
        //ln.letters = cloneStrList(this.letters); 
        //ln.addLetters = cloneStrList(this.addLetters);
        //ln.addIdx = cloneIntList(this.addIdx);

        return ln;
    }

    private ArrayList<String> cloneStrList(ArrayList<String> list){
        ArrayList<String> toRet = new ArrayList<String>();
        for (String str : list){
            toRet.add(str);
        }
        return toRet;
    }

    private ArrayList<Integer> cloneIntList(ArrayList<Integer> list){ 
        ArrayList<Integer> toRet = new ArrayList<Integer>();
        for (Integer i : list){
            toRet.add(i);
        } 
        return toRet;
    }

    /** 
     * @param line whose punctuation should be removed.
     * @return orig, stripped of punctuation except spaces, and all lowercase. 
     */
    private String removePunct(String orig){
        orig = orig.toLowerCase();
        String ret = "";
        char c; 
        for (int i=0; i<orig.length(); i++){
            c = orig.charAt(i);
            if (97 <= (int)c && (int)c <= 122 || (int)c == 32){ //lowercase letter or space
                ret += c;
            } else { //punctuation
                //punct.add(c);
                punctIdx.add(i);
            }
        }
        return ret;
    }

    /** 
     * Formats the DactHexLine object.
     * 
     * Scansion on top, original line on bottom... e.g. 
     *      —  U  U —   U  U —   — ——   —   — U  U  — X 
     *      Arma virumque cano Troiae qui primus ab oris
     * 
     * Console must have monospace font in order for it to align properly!!
     * 
     * @param ArrayList<Integer> This is the arrayList of stresses to be formatted.
     * @return Nothing.
     */
    public String format(ArrayList<Integer> stressesP, int step){ 
        String toRet = "";
        StringBuilder stressLn = new StringBuilder(this.origLine.length());
        StringBuilder letterLn= new StringBuilder(this.origLine.length()); 

        int j=0;

        int alphabet = 65; //A
        int addAlpha = 97; //a
        for (int i=0; i<this.line.length(); i++){
            if (j<vowelSoundsIdx.size() && vowelSoundsIdx.get(j) == i){
                for (int r=0; r<vowelSounds.get(j).length(); r++){
                    switch (stressesP.get(j)){ 
                        case 1: stressLn.append("U"); break;
                        case 2: stressLn.append("—"); break;
                        case 3: stressLn.append("X"); break;
                        case 5: stressLn.append("?"); break;
                        default: stressLn.append("'"); break; //elision if negative
                    } //end switch
                }
                if (step == 3 || step == 4){
                    letterLn.append((char)alphabet);
                    letters.add(""+ (char)alphabet);
                    if (vowelSounds.get(j).length() == 2){
                        letterLn.append(" ");
                    }
                    alphabet++;
                }
                i += vowelSounds.get(j).length()-1;
                j++;                
            } else { //not a vowel sound to be marked
                stressLn.append(" ");
                if (step == 3){
                    letterLn.append(" ");
                } else if (step == 4){
                    if (line.charAt(i) == 'a' || line.charAt(i) == 'e' || line.charAt(i) == 'i' || line.charAt(i) == 'o'
                    || line.charAt(i) == 'u' || line.charAt(i) == 'y'){
                        this.addLetters.add("" + (char)addAlpha);
                        letterLn.append((char)addAlpha);
                        addIdx.add(i);
                        addAlpha++;
                    } else {
                        letterLn.append(" ");
                    }
                }
            }
        }
        for (Integer k : punctIdx){ //account for punctuation, which affects spacing
            stressLn.insert(k.intValue()," ");
            if (step == 3 || step == 4){
                letterLn.insert(k.intValue()," ");
            } 
        }
        if (step == 3 || step == 4){
            toRet +="\t" + letterLn;
        } 
        toRet += "\n\t" + stressLn;
        toRet +="\n\t" + origLine;
        return toRet;
    }

    /** 
     * Removes a vowel sound, used in REMOVE/Main's removeVS(). 
     * Updates the parallel arrayLists stresses, vowelSounds, and vowelSoundsIdx accordingly.
     * @param idxToChange This is the index of the parallel arrayList that should be removed
     * @return DactHexLine This returns a new DactHexLine object with the change.
     */
    public DactHexLine removeVowelSound(int idxToChange){
        DactHexLine toRet = this.cloneDHL();
        toRet.stresses.remove(idxToChange);
        toRet.vowelSounds.remove(idxToChange);
        toRet.vowelSoundsIdx.remove(idxToChange);
        return toRet;
    }

    /** 
     * Adds a vowel sound, used in ADD/Main's addVS(). 
     * Updates the parallel arrayLists stresses, vowelSounds, and vowelSoundsIdx accordingly.
     * It's stress is determined using long by position, and is assumed not to be a diphthong or elision.
     * 
     * @param idxToChange This is the index of the parallel arrayList that should be removed
     * @return DactHexLine This returns a new DactHexLine object with the change.
     */
    public DactHexLine addVowelSound(int idxInAdd){
        DactHexLine toRet = this.cloneDHL();
        int vsIdx = addIdx.get(idxInAdd);
        int offset = 0; //offset will be index 0 if the for loop never enters
        for (int i=0; i<vowelSoundsIdx.size(); i++){
            if (vsIdx > vowelSoundsIdx.get(i)){
                offset = i+1; 
            }
        }

        toRet.stresses.add(offset, longByPos(vsIdx));
        toRet.vowelSoundsIdx.add(offset, vsIdx);
        toRet.vowelSounds.add(offset, "" + line.charAt(vsIdx));

        return toRet;
    }

    /** 
     * Reassigns the indicated stress. The line's stresses are then recalculated. Used in OVERRIDE/Main's manualOverride().
     * @param idxToChange This is the index in ArrayList stresses of the stress that should be changed.
     * @param stress This is the stress that the indicated stress should be changed to.
     * @return DactHexLine Returns a new DactHexLine object with its stress changed and stresses recalculated.
     */ 
    public DactHexLine reassignStress(int idxToChange, int stress){
        DactHexLine toRet = this.cloneDHL();
        toRet.stresses.set(idxToChange, stress);
        return toRet; 
    }

    /**
     * Unelides the indicated vowel sound, and calculates its stress. The line's stresses are then recalculated. Used in OVERRIDE.
     * @param idxToChange This is the index in ArrayList stresses of the elided vowel sound that should be unelided.
     * @return DactHexLine Returns a new DactHexLine object with the indicated vowel sound unelided and stresses recalculated.
     */ 
    public DactHexLine hiatus(int idxToChange){ 
        DactHexLine toRet = this.cloneDHL();
        toRet.stresses.set(idxToChange, -this.stresses.get(idxToChange));
        return toRet;
    }

    /**
     * Breaks apart the indicated diphthong into two separate vowel sounds, whose individual stresses are recalculated. 
     * The line's stresses are then recalculated. Used in OVERRIDE.
     * 
     * @param idxToChange This is the index in ArrayList stresses of the diphthong sound that should be broken.
     * @return DactHexLine Returns a new DactHexLine object with the diphthong broken into separate vowel sounds and stresses recalculated.
     */
    public DactHexLine breakDip(int idxToChange){
        DactHexLine toRet = this.cloneDHL();
        String diphthong = toRet.getVowelSounds().get(idxToChange); 
        String a = "" + diphthong.charAt(0);
        String b = "" + diphthong.charAt(1);

        toRet.vowelSounds.set(idxToChange, b);
        toRet.vowelSounds.add(idxToChange, a);

        toRet.vowelSoundsIdx.add(idxToChange+1, toRet.vowelSoundsIdx.get(idxToChange)+1);

        toRet.stresses.set(idxToChange, longByPos(vowelSoundsIdx.get(idxToChange)));
        toRet.stresses.add(idxToChange+1, longByPos(toRet.vowelSoundsIdx.get(idxToChange+1)));

        return toRet;
    } 

    /**
     * Scans the line by guessing the remaining unknown stresses based on meter.
     * It first calls knownByMeter() and feetBreaker(). After the feet have been determined, it scans the line for remaining unknown 
     * stresses. Once an unknown stress is found, it determines the nearest known footbreak to the right and left of said stress, 
     * determining what "chunk" of the line it's in. The "chunk" is then scanned by guessChunk(). After guessChunk(),
     * starting from the end of the last chunk, the program determines whether there are any more unknown stresses. If there are,
     * the cycle repeats: determine it's "chunk" and call guessChunk() accordingly.
     * 
     * The number of syllables in the line, N, is also computed here.
     * 
     * @return This is the arrayList of stresses completed after guessing by meter.
     */
    private ArrayList<Integer> meterGuess(){
        ArrayList<Integer> toRet = stripElided(this.stresses);
        N = toRet.size(); //number of syllables

        //First VS is marked long (-)
        toRet.set(0, 2);
        //Last foot is marked long, anceps (— X)
        toRet.set(toRet.size()-2, 2);
        toRet.set(toRet.size()-1, 3);

        toRet = knownByMeter(toRet);

        ArrayList<Integer> footbreaks = feetBreaker(toRet); //ascending order
        //System.out.println(footbreaks);

        int i=0; 
        int s=0; //starting as not to waste time
        //int unknownIdx;
        while (i<toRet.size()-2){
            if (toRet.get(i) == 5){
                for (int j=s; j<footbreaks.size(); j++){
                    if (footbreaks.get(j) > i){  
                        int end = footbreaks.get(j); //find end of chunk )
                        int start = footbreaks.get(j-1); //find start of chunk [
                        toRet = guessChunk(toRet, start, end);
                        s=j+1;
                        i=end; 
                        break;
                    }
                }
            } else {
                i++;
            }
        }

        return reinsertElided(toRet);
    }

    /**
     * Scans the inputted "chunk" of the line [start, end), guessing by meter right to left.
     * 
     * Let's first call the last three indexes within the chunk a, b, and c respectively. 
     *          E.g. |??????|
     *               |???abc|
     *               
     * A) If abc can be made into a dactyl, i.e. if a is NOT U and b is NOT long and c is NOT long, then make abc a dactyl and reassign 
     *    abc where c is to the left of where a was before.
     *          E.g. |???|
     *               |abc|
     *               |-UU|
     *               
     * B) However, if any of the following are true: a is short, b is long, or c is short, abc can't be a dactyl. Thus, make bc into a spondee, 
     *    and reassign abc where c is where a was.
     *          E.g. |????U??|
     *               |????abc|
     *               |??abc--|
     * 
     * A) and B) keep repeating until there isn't enough room for a dactyl... see C)
     * 
     * C) Once there is not enough room for a dactyl, mark the rest of the unknown stresses as long (spondees). The program
     *    figures this out in a few ways: 
     *      i. The earlier step was a dactyl that went in cleanly, meaning there were THREE spaces, and there are now ZERO leftover spaces.
     *              E.g. |???|
     *                   |-UU| //all done!
     *                   
     *      ii. There are only TWO leftover spaces... 
     *              E.g. |???-|
     *                   |?abc| //make bc into a spondee
     *                   |??--| 
     *                   |----| //Mark the remaining unknowns as long, hence, a spondee.
     *                   
     *      iii. If we have ONE leftover space, however, we've got problems because we can't fit either a spondee or a dactyl in one space: 
     *       instead of making a dactyl and have one leftover space, we need to make two spondees. In this scenario, we have FOUR spaces left, 
     *       in which case I can't keep making dactyls, and should mark the rest as long.
     *              E.g. |????|
     *                   |?abc| //I can't make abc a dactyl because there'd only be one space left!
     *                   |----| //Therefore, mark every remaining stress as long (make two spondees).
     *       
     *       -Computing the number of remaining spaces is quite simple: N-(D*3)-(S*2)
     *              Where N is the number of vowel sounds in the chunk, D is the number of dactyls in the chunk, and S is the number of 
     *              spondees in the chunk. 
     *              
     * @param stressesP This is the original arrayList of stresses to be modified. It's a parameter, not the instance var.    
     * @param chunkStart This is the index of where the chunk starts, inclusive.
     * @param chunkEnd This is the index of where the chunk ends, exclusive.
     * @return This is the inputted arrayList of stresses modified after guessChunk was used.
     */
    private static ArrayList<Integer> guessChunk(ArrayList<Integer> stressesP, int chunkStart, int chunkEnd){ //[start, end) of chunk
        int len = chunkEnd-chunkStart; //length of chunk
        int numD = 0;
        int numS = 0;

        int i = chunkEnd-1; //starting index while going backwards
        while (i-2 >= chunkStart && len-3*(numD)-2*numS!=4){
            int a = stressesP.get(i-2); 
            int b =stressesP.get(i-1); 
            int c = stressesP.get(i); 
            if (a != 1 && b != 2 && c != 2 && i!=3){ //mark as dactyl if it can be a dactyl
                stressesP.set(i-2, 2);
                stressesP.set(i-1, 1);
                stressesP.set(i, 1);
                numD++;
                i = i-3; 
            } else { //if not, then mark as a spondee
                numS++;
                stressesP.set(i-1, 2);
                stressesP.set(i, 2);
                i = i-2;
            }
        }

        for (int k=chunkEnd-1; k >= chunkStart; k--){ //make everything left unknown in the chunk as a spondee
            if (stressesP.get(k) == 5){
                stressesP.set(k, 2);
            }
        }
        return stressesP;
    }

    /**
     * Fills in stresses known by meter! Should be run after determineVowelSounds and before feetbreaker and meterGuess.
     * 
     * So far, we know stresses based on long by position, long by diphthong, and based on OVERRIDE input. Based on these knowns,
     * we can go another step further by filling out our knowns by meter. Assuming our knowns so far are correct and perfect dactylic
     * hexameter, the following conditions must be true; there's no guessing here since we know a dactyl is -UU and a spondee is --:
     * 
     * 1. -_- > ---
     * 2. -_U > -UU
     * 3. -U_ > -UU
     * 4. __U_U_ > -UU-UU 
     * 5. __U- > -UU 
     * 6. _UU > -UU 
     * 
     * It iterates through each character in the line left to right and until one of these conditions are met , editing the 
     * stresses as necessary. 
     * 
     * It also has to go back and check if the last three (4, 5, 6) can trigger 1 and 5, which end in a long, because 4/5/6 fill in a long 
     * in the beginning.
     *              Eg.  -??UU
     *                   -?-UU //condition 6 (_UU)
     *                   ---UU //condition 1 (-?-) got triggered even though we passed the character within -?? earlier.
     *                   
     * @param stressesP This is the arrayList of stresses to be modified. It's a parameter, not the instance variable.
     * @return Returns the modified arrayList with knowns by meter filled out.
     */
    private static ArrayList<Integer> knownByMeter(ArrayList<Integer> stressesP){
        int i=0;
        while (i<stressesP.size()-2){
            int longMark = -1; 
            if (stressesP.get(i) == 2 && stressesP.get(i+2) == 2){ //-_- --> ---
                stressesP.set(i+1, 2);
                i+=3;
                continue;
            } else if (stressesP.get(i) == 2 && stressesP.get(i+2) == 1){ //-_U
                stressesP.set(i+1, 1);
                i+=3;
                continue;
            } else if (stressesP.get(i) == 2 && stressesP.get(i+1) == 1){ //-U_
                stressesP.set(i+2, 1);
                i+=3;
                continue;
            } else if (i-2>=0 && stressesP.get(i) == 1 && stressesP.get(i+2) == 1){ //__U_U_ --> -UU-UU
                stressesP.set(i-2, 2); 
                stressesP.set(i-1, 1); 
                stressesP.set(i+1, 2); 
                stressesP.set(i+3, 1); 

                longMark = i-2;
                i+=2; // i+=3
            } else if (i-2>=0 && stressesP.get(i) == 1 && stressesP.get(i+1) == 2){ //__U- --> -UU-
                stressesP.set(i-2, 2);
                stressesP.set(i-1, 1);

                longMark = i-2;
                i++; //i+=2
            } else if (i-1 >=0 && stressesP.get(i) == 1 && stressesP.get(i+1) == 1){ //_UU --> -UU
                stressesP.set(i-1, 2);

                longMark = i-1;
                i++; //i+=2
            } 

            if (longMark != -1){ //check if filled in longMark could trigger -_- or __U-
                if (longMark-2>=0 && stressesP.get(longMark-2) == 2){ //-_-
                    stressesP.set(longMark-1, 2);
                } else if (longMark-3 >=0 && stressesP.get(longMark-1)==1){ //__U-
                    stressesP.set(longMark-3, 2);
                    stressesP.set(longMark-2, 1);
                } 
            }
            i++;
        }
        return stressesP;
    }

    /** 
     * Determines if any footbreaks can be determined based on known stresses.
     * 
     * The program first marks a footbreak at the very front of the line and right before the anceps (|-X). It then determines footbreaks 
     * based on known dactyls, putting a footbreak before and after each known dactyl, taking care not to repeat footbreaks. |-UU|
     * 
     * Based on known those footbreaks, we then can determine more footbreaks...
     * 1. |-- > |--|  
     * 2. --| > |--|
     * 3. -?| > |-?| //the ? stresses will be marked later in meterGuess()
     * 4. ?-| > |-?|
     * 5. |??U > |??U|
     * 6. ??U| > |??U|
     * 7. |?U?| > |?U?|
     * 
     * EDIT: I've removed the ones that help find the leading foot break (2, 3, 4, 6, 8) since the program is 
     * scanning from back to front and the quantities would still align in step E, so long as the following
     * footbreak is correct. Those are therefore redundant
     *
     * @param stressesP This is the arrayList of stresses whose footbreaks should be found.
     * @return ArrayList<Integer> Returns the footbreaks arrayList
     */
    private static ArrayList<Integer> feetBreaker(ArrayList<Integer> stressesP){
        ArrayList<Integer> footbreaks = new ArrayList<Integer>(); 
        //footbreak is [ , )

        //System.out.println(footbreaks);
        footbreaks.add(0);
        int i =0;
        while (i<stressesP.size()-2){ //goes through dactyls
            if (stressesP.get(i) == 1 && i-1>=0 && i+1<stressesP.size() && stressesP.get(i-1) == 2 && stressesP.get(i+1) == 1){ //-U
                if (footbreaks.indexOf(i-1) == -1){ //if the leading footbreak isn't already added
                    footbreaks.add(i-1);
                }
                footbreaks.add(i+2); //add following footbreak
                i+=2;
            } else{
                i++;
            }
        }
        
        //footbreak before anceps |-X
        if (footbreaks.indexOf(stressesP.size()-2) == -1){
            footbreaks.add(stressesP.size()-2);
        }

        //mark known spondees' footbreaks and known dactyls using footbreaks
        int j=0;
        while (j<stressesP.size()-2){  
            if (stressesP.get(j) == 2){ //long
                //|--| with either foot known (j would be the first -)
                if (j+1<stressesP.size()-2 && stressesP.get(j+1) == 2){ //--
                    if (footbreaks.indexOf(j) != -1){ //leading foot is known |--
                        //if following foot is unknown, add the following foot to footbreaks |--|
                        if (footbreaks.indexOf(j+2) == -1){
                            footbreaks.add(j+2);
                        }
                        //or if they're both known
                        j+=2;
                        continue;
                    } /*else if (footbreaks.indexOf(j+2) != -1){ //if leading is unknown AND following is known
                            //add leading foot
                            footbreaks.add(j);
                            j+=2;
                            continue;
                    } //else, if neither footbreak is known, then can't be certain of a spondee, go to j++ */
                }
                /*if (stressesP.get(j+1) == 5 && footbreaks.indexOf(j+2) > -1){ // -?| --> known spondee
                    if (footbreaks.indexOf(j) == -1){ //if leading footbreak is unknown
                        footbreaks.add(j);
                    }
                    j+=2;
                    continue;
                }*/
                if (j-1>=0 && stressesP.get(j-1) == 5 && footbreaks.indexOf(j-1) > -1){ //|?- --> known spondee
                    if (footbreaks.indexOf(j+1) == -1){ //if following footbreak is unknown
                        footbreaks.add(j+1);
                    }
                    //j++; continue; 
                }
                //|__| both footbreaks known will be taken care of by MeterGuess()
            } else if (stressesP.get(j) == 1){ //__U
                //|__U| either footbreak known
                if (j-2>=0 && footbreaks.indexOf(j-2)>-1){ //|__U
                    if (footbreaks.indexOf(j+1) == -1){ //if following footbreak unknown, add it
                        footbreaks.add(j+1);
                    }
                    //j++; continue;
                } 
                /*else if (j-2>=0 && footbreaks.indexOf(j+1)>-1){ //__U|
                    if (footbreaks.indexOf(j-2) == -1){ //if preceding footbreak unknown, add it
                        footbreaks.add(j-2);
                    }
                    //j++; continue;
                } */
                else if (j-1>=0 && footbreaks.indexOf(j-1)>-1) { //|_U_
                    if (footbreaks.indexOf(j+2) == -1){ //add following footbreak
                        footbreaks.add(j+2);
                    }
                } 
                /*else if (footbreaks.indexOf(j+2) > -1){ 
                  //_U_| 
                  if (footbreaks.indexOf(j-1) == -1){
                    //add leading
                    footbreaks.add(j-1);
                  }
                } */
                //|___| both footbreaks known will be taken care of in meterGuess()
            }
            j++;
        }
        Collections.sort(footbreaks); 
        return footbreaks;
    }

    /**
     * Strips elided values from the ArrayList passed in, keeping track of their indicies in elidedIdx.
     * @param stressList This is the ArrayList to be stripped of elided values.
     * @return ArrayList<Integer> This is stressList stripped of elided values. 
     */
    private ArrayList<Integer> stripElided(ArrayList<Integer> stressList){ 
        //Copy stresses into new ArrayList without elided vowel sounds
        ArrayList<Integer> toRet = new ArrayList<Integer>();
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int s=0; s<stressList.size(); s++){
            if (stressList.get(s) > 0){
                toRet.add(stressList.get(s));
            } else {
                temp.add(s);
            } 
        }
        elidedIdx = temp;
        //temp was made so that stripElided could be used more than once but I ended up not doing that.
        return toRet;
    }

    /**
     * Reinserts elided values into the ArrayList passed in after stripElided() was used.
     * @param stressList This is the ArrayList to be stripped of elided values.
     * @return ArrayList<Integer> This is stressList stripped of elided values.
     */
    private ArrayList<Integer> reinsertElided(ArrayList<Integer> list){
        //Reinsert elided
        for (Integer n : elidedIdx){
            list.add(n.intValue(), -5); //-5 is elided placeholder
        }
        return list; 
    }

    private void determineVowelSounds(){ 
        boolean diphthong = false;
        boolean cui = false;
        int elisionLen = 0;

        //for every character in line..
        for (int i=0; i<line.length(); i++){
            if (elisionLen>0 && diphthong){
                elisionLen--;
                diphthong = false;
                continue;
            } else if (diphthong){
                diphthong = false;
                continue;
            } else if (elisionLen > 0){
                elisionLen--;
                continue;
            }

            //if line.charAt(i) is not a vowel, skip to next iteration
            if (!isVowel(i)){
                continue;
            } 

            String toCheck = line.charAt(i) + "";

            if (isDiphthong(i)){ //REMINDER: if param overrideDip, overrideIdx && isDiphthong(i) 
                diphthong = true;
                toCheck = line.substring(i, i+2);
                if (toCheck.equals("ui") && i-1>=0 && line.charAt(i-1) == 'c'){ 
                    if (i-2<0 || i-2>=0 && line.charAt(i-2) == ' '){
                        if (i+2>= line.length() || i+2 < line.length() && line.charAt(i+2) == ' '){
                            toCheck = "i";
                            cui = true;
                            diphthong = false; //technically it is a diphthong but I only want to mark the i
                            continue;
                        }
                    }
                } 
            } 

            if (!elided(i).equals("")){ //if toCheck is in a prod/elided unit

              int endIdx = (line.indexOf(" ", line.indexOf(" ", i)+1) == -1) ? line.length()-1 : line.indexOf(" ", line.indexOf(" ", i)+1);

              String word2word = line.substring(line.indexOf(" ", i)+1, endIdx);
              //System.out.println(word2word);

                //REMINDER: check for overrideElide
                //maybe edit so that it adds both vowel sounds, but the stress is a different number, meaning elided?
                String elidedUnit = elided(i);
                //System.out.println(elidedUnit);
                elisionLen = elidedUnit.length();

                String word1 = elidedUnit.substring(0, elided(i).indexOf(" "));
                String word2 = elidedUnit.substring(elided(i).indexOf(" ")+1);

                if (word1.endsWith("m")){
                    vowelSounds.add(word1.substring(0, elided(i).indexOf("m")));
                } else {
                    vowelSounds.add(word1);
                }

                vowelSoundsIdx.add(i);

                int word2Idx = i+elidedUnit.indexOf(" ")+1; //first character of word2's vowel sound. Index in line 
                if (word2.charAt(0) == 'h'){
                    word2Idx++;
                    vowelSounds.add(word2.substring(1));
                } else{
                    vowelSounds.add(word2);
                }
                vowelSoundsIdx.add(word2Idx);

                

                int p = (word2word.equals("es") || word2word.equals("est")) ? 1 : -1;
                //System.out.println(word2 + "=" + p);
                //if prodelided; p = 1, if normally elided, p = -1

                //first word vowel sound
                if (cui){
                    stresses.add(1*p);
                    cui = false;
                } else if (diphthong){ 
                    stresses.add(2*p);
                } else {
                    stresses.add(longByPos(i)*p);
                }

                //second word vowel sound
                if (isDiphthong(word2Idx)){ //2nd VS is diphthong... 2nd word can't be cui or else won't be prod/elided
                    stresses.add(2*-p);
                } else {
                    stresses.add(longByPos(word2Idx)*-p);
                }
            } else { //not prod/elided
                vowelSounds.add(toCheck);
                vowelSoundsIdx.add(i);
                if (cui){
                    stresses.add(1);
                    cui = false;

                } else if(diphthong){
                    stresses.add(2);
                    //continue;
                } else {
                    stresses.add(longByPos(i));
                }
            }
        }
    }

    /**
     * @param i This is the index of the character within this.line.
     * @return This returns whether the indicated character is acting as a vowel. 
     */
    private boolean isVowel(int i){
        char c = line.charAt(i);
        //if toCheck is an "i" is acting like a consonant or a "u" within the digraph "qu"--> not acting as a vowel
        if (c == 'i' && isIConsonant(i) || c == 'u' && i-1 >= 0 && line.charAt(i-1) == 'q'){  
            return false;
        } else {
            return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y'; 
        }
    }

    /** 
     * Helper method to determineVowelSounds that checks to see if "i" is acting like a consonant. 
     * 
     * Precondition: the character at iIdx is "i".
     * 
     * "i" acts like a consonant whenever it's...
     *  A) between two vowels (e.g. “Troiae”)
     *  B) the first letter of a word and the second letter is a vowel (“iacta”)
     * 
     * @param iIdx This represents idx of the "i" to be checked within String line.
     * @return boolean This returns whether the "i" acts like a consonant or not.
     */
    private boolean isIConsonant(int iIdx){
        if (iIdx+1 < line.length()-1 && isVowel(iIdx+1)){ //if there is a following letter & it's is a vowel 
            if (iIdx-1 < 0 || line.charAt(iIdx-1) == ' '){ //if there is no preceding letter, or the preceding letter is a space
                //i.e. if it's the first letter of the word
                return true;
            } else if (isVowel(iIdx-1)){ //preceding is a vowel too 
                //must be else if to avoid out of bounds error
                return true;
            }
        }
        return false;
    }

    /** 
     * Helper method to determineVowelSounds that checks to see if the vowel is part of a diphthong to be treated as one unit.
     * 
     * Precondition: The character at i is either the first letter of the diphthong or not in a diphthong at all.
     * 
     * The Latin diphthongs are: ae, oe, ei, ui, au, eu
     *      This does NOT include "qui".
     *      
     * @param idx This represents index of the vowel to be checked within String line.
     * @return boolean Returns whether the vowel is part of a diphthong or not.
     */
    private boolean isDiphthong(int idx){ 
        if (idx+1 < line.length()){
            if (idx-1>=0 && line.substring(idx-1, idx+2).equals("qui")){
                return false;
            }
            String s = line.substring(idx, idx+2);
            return (s.equals("ae") || s.equals("oe") || s.equals("ei") || s.equals("ui") || s.equals("au") || s.equals("eu"));
        }
        return false;
    }

    /** 
     * Helper method to determineVowelSounds that checks to see if the vowel is part of an elided unit. 
     * 
     * There is elision if...
     * A) A word ends with a vowel and the next word begins with a vowel or an h<vowel>-.
     * B) A word ends with a -<vowel>m and the next word begins with a vowel or an h<vowel>-.
     * 
     * Make sure to check if the either vowel sound is a diphthong!
     * 
     * Punctuation does not affect elision. (This is taken care of since line is stripped of punctuation.)
     * 
     * @param idx This represents index of the vowel to be checked within String line.
     * @param diphthong takes into account whether toCheck (the FIRST word's vowel sound) is a diphthong.
     * @return String Returns "" if there's no elision; returns the elided unit if there is.
     */
    private String elided(int idx){
        if (isVowel(idx) && line.indexOf(" ", idx) != -1){ 
            String toRet = line.charAt(idx) + "";
            int a = 0;
            if (isDiphthong(idx)){ 
                toRet += line.charAt(idx+1);
                a = 1; 
            }
            if (line.charAt(idx+a+1) == 'm'){
                toRet += "m";
                a++;
            }
            if (line.charAt(idx+a+1) == ' '){
                toRet += " ";
                if (idx+a+2 < line.length() && line.charAt(idx+a+2) == 'h'){
                    a++;
                    toRet += "h";
                }
                if (idx+a+2 < line.length() && isVowel(idx+a+2)){
                    toRet += line.charAt(idx+a+2) + "";
                    if (isDiphthong(idx+a+2)){
                        toRet += line.charAt(idx+a+3);
                        a++;
                    }
                    return toRet;
                }  
            }
        }
        return "";
    }

    /** 
     * Helper method to determineVowelSounds that checks to see whether the unit to be marked is long by position.
     * 
     * Ways to be long by position (this ignores all whitespace):
     *      A) The next letter is a double-length consonant (x = cs, z = sd)
     *      B) The next two letters are both consonants
     *          This may NOT include...
     *              i. Combos w/ "h" (e.g. "ch", "ph", "th")
     *              ii. Mute-liquid consonant pairs
     *                  This means: Mute first letter (b,c,d,f,g,p,t) & liquid second letter (l,r)
     *              iii. Mute-nasal consonant pairs
     *                  This means: Mute first letter (b,c,d,f,g,p,t) & nasal second letter (m, n)
     *      C) A final "o" is almost always long!
     *                  
     * N.B. meterGuess() takes into account that the first vowel sound is -, last foot is -X
     * 
     * @return int Returns 2 if the unit is long by position or is final o; returns 0 if it isn't/can't be determined
     * @param int This is the index of the unit to be checked within the String line.
     */
    private int longByPos(int idx){
        //final O
        if (line.charAt(idx) == 'o' && (idx==line.length()-1 || idx+1<line.length() && line.charAt(idx+1) == ' ')){
            return 2;
        }

        boolean mute = false;
        int consonants = 0;

        for (int i=idx+1; i < line.length(); i++){ 
            char c = line.charAt(i);
            if (c == ' '){ //whitespace doesn't count
                continue;
            } 
            if (consonants == 0 && (c == 'x' || c == 'z')){
                return 2;
            }
            if (c == 'b' || c == 'c' || c == 'd' || c == 'f' || c == 'g' || c == 'p' || c == 't'){ //is mute
                if (mute){ //2 mutes in a row--> 2 consonants
                    return 2; //this avoids having mute, mute, liquid/nasal and returning 2
                } 
                mute = true;
                consonants++;
            } else if (c == 'l' || c == 'r' || c == 'm' || c == 'n'){ //is liquid or nasal
                if (mute){ //mute-liquid or mute-nasal
                    return 5;
                } 
                consonants++; 
            } else if (c == 'h'){
                if (consonants == 1){
                    return 5;
                }
                consonants++;
            } else if (!isVowel(i) && !(c == 'u' && line.charAt(i-1) == 'q')){
                consonants++;
            } else {
                return 5;
            }

            if (consonants == 2){
                return 2;
            }
        }
        return 5;
    }

    public ArrayList<Integer> getStresses(){ 
        return this.stresses;
    }

    public String getOrigLine(){ 
        return this.origLine; 
    }

    public ArrayList<String> getLetters(){ 
        return this.letters;
    }

    public ArrayList<String> getVowelSounds(){
        return this.vowelSounds;
    } 

    public ArrayList<String> getAddLetters(){
        return this.addLetters;
    }

}
