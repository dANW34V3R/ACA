package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    private static List<Instruction> createInstructinFromFile(String path){
        try {

            List<String> rawInstructions = new ArrayList<>();

            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                rawInstructions.add(data);
//                System.out.println(data);
            }
            myReader.close();
            return stringListToInstructionList(rawInstructions);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static List<Instruction> stringListToInstructionList(List<String> rawInstructions) {

        List<String> instructionStringsWithoutLabels = new ArrayList<>();
        List<Instruction> instructions = new ArrayList<>();
        HashMap<String, Integer> labelToAddr = new HashMap<>();

        String opcode;
        Integer operand1 = 0;
        Integer operand2 = 0;
        Integer operand3 = 0;

        // populate labelToAddr
        for (int i = 0; i < rawInstructions.size(); i++) {
            // if label
            String insString = rawInstructions.get(i);
            if (insString.substring(insString.length() - 1).charAt(0) == ':') {
                labelToAddr.put(insString.substring(0, insString.length() - 1), i - labelToAddr.size());
            } else {
                instructionStringsWithoutLabels.add(insString);
            }
        }

        for (int i = 0; i <instructionStringsWithoutLabels.size(); i++) {
            String[] insStringSplit = instructionStringsWithoutLabels.get(i).split(" ");

            if (insStringSplit[0].charAt(0) == 'B') {
                // branch instruction
                operand1 = labelToAddr.get(insStringSplit[1]);
                if (operand1 == null) {
                    System.out.println("label error around " + i);
                    return Collections.emptyList();
                }
                instructions.add(new Instruction(insStringSplit[0], operand1, 0 ,0));
            } else if (insStringSplit[0].compareTo("LDR") == 0 || insStringSplit[0].compareTo("STR") == 0) {
                // Instructions using []

            } else if (insStringSplit[0].compareTo("ADD") == 0 || insStringSplit[0].compareTo("SUB") == 0 || insStringSplit[0].compareTo("MOV") == 0) {
                // Instructions allowing immediates

            } else {
                // All other instructions
                operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                operand2 = Integer.parseInt(insStringSplit[2].substring(1));
                operand3 = Integer.parseInt(insStringSplit[3].substring(1));

                instructions.add(new Instruction(insStringSplit[0], operand1, operand2 ,operand3));
            }



        }

        instructionStringsWithoutLabels.forEach(System.out::println);
        System.out.println(labelToAddr.toString());

        return instructions;
    }


    public static void main(String[] args) {
        //read file
        //compile file
        //execute file

        List<Instruction> instructions = createInstructinFromFile("programs/livermore3.txt");



    }

}
