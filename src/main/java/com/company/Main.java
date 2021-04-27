package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Main {

    private static List<Instruction> createInstructionsFromFile(String path){
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

            // Won't catch all errors e.g. [R30 in LDR and STR
            for (int k = 1; k < insStringSplit.length; k++) {
                if (insStringSplit[k].compareTo("R30") == 0) {
                    throw new java.lang.Error("Cannot directly use PC around line " + i);
                }
            }

//            System.out.println(instructionStringsWithoutLabels.get(i));
            if (insStringSplit[0].compareTo("BR") == 0) {
                instructions.add(new Instruction(insStringSplit[0], Integer.parseInt(insStringSplit[1].substring(1)), 0, 0));
            } else if (insStringSplit[0].compareTo("MOVPC") == 0) {
                instructions.add(new Instruction(insStringSplit[0], Integer.parseInt(insStringSplit[1].substring(1)), 0, 0));
            } else if (insStringSplit[0].charAt(0) == 'B') {
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
                    instructions.add(new Instruction(insStringSplit[0].toUpperCase(), 0,0,0));
                } else if (insStringSplit.length == 2) {
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    instructions.add(new Instruction(insStringSplit[0].toUpperCase(), operand1,0,0));
                } else if (insStringSplit.length == 3) {
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(1));
                    instructions.add(new Instruction(insStringSplit[0].toUpperCase(), operand1,operand2,0));
                } else if (insStringSplit.length == 4) {
                    operand1 = Integer.parseInt(insStringSplit[1].substring(1));
                    operand2 = Integer.parseInt(insStringSplit[2].substring(1));
                    operand3 = Integer.parseInt(insStringSplit[3].substring(1));
                    instructions.add(new Instruction(insStringSplit[0].toUpperCase(), operand1,operand2,operand3));
                }
            }



        }

//        instructionStringsWithoutLabels.forEach(System.out::println);
//        System.out.println(labelToAddr.toString());
        instructions.forEach(System.out::println);

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
        List<Instruction> instructions = createInstructionsFromFile("programs/" + args[0] + "/program.txt");
        List<Integer> memory = readMemoryFile("programs/" + args[0] + "/memory.txt");

        BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
        byte[] srcPixels = new byte[1];

        if (args.length > 1) {
            try {
                image = ImageIO.read(new File(args[1].toString()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
                BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                tmp.getGraphics().drawImage(image, 0, 0, null);
                image = tmp;
            }

            srcPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            // If image file is given, contents of memory file is ignored
            memory.clear();

            for (int i = 0; i < srcPixels.length; i++) {
                //Remove byte sign when converting to int
                memory.add(srcPixels[i] & 0xFF);
            }
        }

        //execute program
        Processor p = new Processor(instructions, memory);

        if (args.length > 2) {

            int[] outPixelsInt = new int[srcPixels.length];
            for (int i = 0; i < srcPixels.length; i++) {
                // New image will be placed directly next to loaded image in memory
                outPixelsInt[i] = Math.max(0, Math.min(255, Math.abs(p.MEM.get(srcPixels.length + i))));
                System.out.print(outPixelsInt[i] + ",");
                if ((i + 1) % 45 == 0) {
                    System.out.println();
                }
            }

            BufferedImage endImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = (WritableRaster) endImage.getData();
            raster.setPixels(0,0,endImage.getWidth(), endImage.getHeight(), outPixelsInt);
            endImage.setData(raster);

//            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//            byte [] newData = ((DataBufferByte) newImage.getRaster().getDataBuffer()).getData();
//
//            for (int i = 0; i < srcPixels.length; i++) {
//                // New image will be placed directly next to loaded image in memory
//                newData[i] = p.MEM.get(srcPixels.length + i).byteValue(); //Math.max(0, Math.min(256, p.MEM.get(srcPixels.length + i)));
////                System.out.print(outPixelsInt[i] + ",");
////                if ((i + 1) % 45 == 0) {
////                    System.out.println();
////                }
//            }

            File file = new File(args[2].toString());
            try {
                ImageIO.write(endImage, "bmp", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
