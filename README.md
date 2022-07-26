# Lady-Lius-Latin
Scansion of Latin Dactylic Hexameter with Manual Adjustment


PROJECT TITLE: Scansion of Latin Dactylic Hexameter with Manual Adjustment
PURPOSE OF PROJECT: Scans a line of Latin Dactylic Hexameter
DATE: Summer 2020
HOW TO START THIS PROJECT: Run Main and input a line of Latin Dactylic Hexameter
AUTHORS: Sydney Liu
----------------------------------------------------------------------------------------------
ABOUT DACTYLIC HEXAMETER:
This program scans a single line of Latin dactylic hexameter. Dactylic hexameter is a poetic meter, found in epics such as Vergil's Aeneid. The program determines the "quantity" of each syllable/vowel sound in the line, marking them as either long (-) or short (U) based on a set of specific rules. This is somewhat analogous to scanning Shakespeare's iambic pentameter, in which syllables are marked stressed or unstressed, although dactylic hexameter is a quantitative meter (based on syllable length) whereas iambic pentameter is a qualitative meter (based on syllable stress).
ABOUT SCANNING DACTYLIC HEXAMETER: https://www.youtube.com/watch?v=cGF47JT0hPA
----------------------------------------------------------------------------------------------
PROGRAM PURPOSE:
Starting from the assumption that the line is in perfect dactylic hexameter, even without access to a dictionary or data sets, this program can deduce, with good accuracy, how to scan the line. It uses meter constraints (Dactyls and spondees, number of feet) and vowel length rules (e.g. a vowel is long if it's followed by two consonants, diphthongs, elision rules, etc.) in order to scan the line in a process similar to what Latin students would do on a test.

However, while this method is quite effective, it is not 100% accurate, which is why the program allows for manual correction. The program may be forced to guess, if not enough information can be deduced based on meter constraints and vowel length rules alone, in which case more information can be provided with use of a dictionary. The program may also make incorrect assumptions, especially in cases where the poet intentionally chooses to disobey vowel length/elision rules.

Manual correction gives users the abilities to override incorrect assumptions, to fill in where the program is forced to guess, and to account for exceptions. With the manual adjustment, the program will take in this new information and re-scan the line with such in mind.
----------------------------------------------------------------------------------------------
USER INSTRUCTIONS:
Video Walkthrough of program: https://www.youtube.com/watch?time_continue=516&v=QDKHmHmHzuU&feature=emb_logo

1. Input a line of Latin Dactylic Hexameter.
-----
2. The program will produce outputs I, II, III, IV... For now, ONLY look at I and II.

Output I. - Known Information Without Using Meter Constraints
  •Establishes which characters are vowel sounds.
  •Marks any vowel sounds as long by nature or by position if possible without a dictionary.
  •Takes into account manual correction (see Menu III. and Menu IV.)

Output II. - Scanned Line, Based On Meter Constraints Using Info From Step I.
  •Using the information from output I, output II scans the rest of the line using meter rules: the line can only be comprised of dactyls (-UU) or spondees (--), with the exception of the last foot being long anceps (-X).

  •First, the last foot is marked as -X, and the first vowel sound as -.
  •Any known foot breaks are then established.

  •Finally, the program reads from right to left, trying to make a dactyl (-UU) each time but making a spondee (--) if the attempt fails. It does this right to left since the penultimate foot is usually a dactyl.

Output II is the program's best guess in scanning the line. The goal of the program is to get this output to be correct!
-----
3. Consider outputs I and II. Do the assumptions (vowel lengths) in output I make sense? Does the final scanned line in output II make sense?

If yes...
  •The scanned line in output II is your scanned line. YOU'RE DONE WITH THE LINE. The rest of instructions is now irrelevant for the line you finished.
  •Feel free to scan another line.

•If no...
  •move onto manual adjustment, step 4.
-----
4. Manual adjustment
•Uh-oh, you've realized that Output I and/or Output II doesn't look quite right. Let's take a look at outputs III and IV so we can either override assumptions about the quantities in step I or add new information.

Use Menu III if you need to make any changes to the quantities of vowel sounds already identified.
Use Menu IV if you need to change which letters are considered vowel sounds.

Menu III.
•Input OVERRIDE
•Input the capital letter corresponding to the quantity that needs to be changed.
•From there, you can pick one of the following options...
  A) reassign the quantity to a different one by.
  B) elide/unelide the quantity
  C) remove an assumed diphthong.
•The line is then rescanned with this information.

Menu IV.
To add a character as a vowel sound...
•Input ADD.
•Then, input the lowercase letter corresponding to the character you'd like to turn into a vowel sound.

To remove a character as a vowel sound...
•Input REMOVE.
•Then, input the capital letter corresponding to the character you'd like to remove as a vowel sound.

The line is then rescanned with this information.

Once the line has been rescanned, go back to step 2. of USER INSTRUCTIONS in this ReadMe with your new outputs in mind.
