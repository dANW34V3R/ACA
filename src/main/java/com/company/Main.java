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
            System.out.println("An error occurred when reading file" + path);
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static List<Instruction> stringListToInstructionList(List<String> rawInstructions) {

        //Intermediate list storing raw instructions without labels and dictionary from labels to corresponding addresses
        List<String> instructionStringsWithoutLabels = new ArrayList<>();
        HashMap<String, Integer> labelToAddr = new HashMap<>();

        //List of compiled instructions
        List<Instruction> instructions = new ArrayList<>();

        Integer operand1 = 0;
        Integer operand2 = 0;
        Integer operand3 = 0;

        // populate labelToAddr
        for (int i = 0; i < rawInstructions.size(); i++) {
            String insString = rawInstructions.get(i);
            if (insString.substring(insString.length() - 1).charAt(0) == ':') {
                // if label
                labelToAddr.put(insString.substring(0, insString.length() - 1), i - labelToAddr.size());
            } else {
                instructionStringsWithoutLabels.add(insString);
            }
        }

        //Convert instruction strings to objects
        for (int i = 0; i <instructionStringsWithoutLabels.size(); i++) {
            String[] insStringSplit = instructionStringsWithoutLabels.get(i).split(" ");

//            System.out.println(instructionStringsWithoutLabels.get(i));

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

                if (insStringSplit[3].charAt(0) == '#') {
                    //immediate
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(2));
                    operand3 = Integer.parseInt(insStringSplit[3].substring(1, insStringSplit[3].length() - 1));
//                    System.out.println(insStringSplit[0] + "i:" + operand1 + ":" + operand2 + ":" + operand3);
                    instructions.add(new Instruction(insStringSplit[0] + "i", operand1, operand2, operand3));
                } else {
                    //register index
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(2));
                    operand3 = Integer.parseInt(insStringSplit[3].substring(1, insStringSplit[3].length() - 1));
//                    System.out.println(insStringSplit[0] + ":" + operand1 + ":" + operand2 + ":" + operand3);
                    instructions.add(new Instruction(insStringSplit[0] , operand1, operand2, operand3));
                }


            } else if (insStringSplit[0].compareTo("ADD") == 0 || insStringSplit[0].compareTo("SUB") == 0 || insStringSplit[0].compareTo("MOV") == 0) {
                // Instructions allowing immediates
                if (insStringSplit[2].charAt(0) == '#') {
                    //immediate
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(1));
//                    System.out.println(insStringSplit[0] + "i:" + operand1 + ":" + operand2);
                    instructions.add(new Instruction(insStringSplit[0] + "i", operand1, operand2, 0));
                } else {
                    //register index
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(1));
                    if (insStringSplit.length > 3) {
                        operand3 = Integer.parseInt(insStringSplit[3].substring(1));
                    }
//                    System.out.println(insStringSplit[0] + ":" + operand1 + ":" + operand2 + ":" + operand3);
                    instructions.add(new Instruction(insStringSplit[0], operand1, operand2, operand3));
                }

            } else {
                // All other instructions (this isn't very nice but works)
                if (insStringSplit.length == 1) {
                    instructions.add(new Instruction(insStringSplit[0], 0,0,0));
                } else if (insStringSplit.length == 2) {
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    instructions.add(new Instruction(insStringSplit[0], operand1,0,0));
                } else if (insStringSplit.length == 3) {
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(1));
                    instructions.add(new Instruction(insStringSplit[0], operand1,operand2,0));
                } else if (insStringSplit.length == 4) {
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(1));
                    operand3 = Integer.parseInt(insStringSplit[3].substring(1));
                    instructions.add(new Instruction(insStringSplit[0], operand1,operand2,operand3));
                }
            }



        }

//        instructionStringsWithoutLabels.forEach(System.out::println);
//        System.out.println(labelToAddr.toString());
//        instructions.forEach(System.out::println);

        return instructions;
    }

    private static List<Integer> readMemoryFile(String path) {
        try {

            List<Integer> memoryVals = new ArrayList<>();

            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                memoryVals.add(Integer.parseInt(data));
//                System.out.println(data);
            }
            myReader.close();
            return memoryVals;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred when reading file" + path);
            e.printStackTrace();
        }

        return Collections.emptyList();
    }



    public static void main(String[] args) {

        //read file
        //compile file
        List<Instruction> instructions = createInstructinFromFile("programs/" + args[0] + "/program.txt");
        List<Integer> memory = readMemoryFile("programs/" + args[0] + "/memory.txt");

        //execute file
        Processor p = new Processor(instructions, memory);

    }

}
