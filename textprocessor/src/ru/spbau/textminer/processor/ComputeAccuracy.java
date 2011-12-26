package ru.spbau.textminer.processor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ComputeAccuracy {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java [-cp ...] ru.spbau.textminer.processor.ComputeAccuracy result-file test-file");
            return;
        }
        BufferedReader resultReader = null;
        BufferedReader testReader = null;

        try {
            resultReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "cp1251"));
            testReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "cp1251"));
            List<Relation> resultRelations = readRelations(resultReader);
            List<Relation> testRelations = readRelations(testReader);

            int posCounter = 0;
            for (Relation relation : resultRelations) {
                double accuracy = computeAccuracy(relation, testRelations);
                if (accuracy > 0.5) {
                    posCounter++;
                }
            }
            System.out.println("precision: \t" + posCounter + "/" + resultRelations.size() + " = " + (((double) posCounter )/ resultRelations.size()));
            System.out.println("recall: \t" + posCounter + "/" + testRelations.size() + " = " + (((double) posCounter) / testRelations.size()));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (resultReader != null) {
                try {
                    resultReader.close();
                } catch (IOException ex) {}
            }
            if (testReader != null) {
                try {
                    testReader.close();
                } catch (IOException ex) {}
            }
        }
    }

    private static double computeAccuracy(Relation relation, List<Relation> testRelations) {
        double maxAccuracy = 0;
        for (Relation testRelation : testRelations) {
            double curAccuracy = computeAccuracy(relation, testRelation);
            if (curAccuracy > maxAccuracy) {
                maxAccuracy = curAccuracy;
            }
        }
        return maxAccuracy;
    }

    private static double computeAccuracy(Relation relation, Relation testRelation) {
        double jLeft = computeJaccard(relation.getLeftArgument(), testRelation.getLeftArgument());
        double jRel = computeJaccard(relation.getRelation(), testRelation.getRelation());
        double jRight = computeJaccard(relation.getRightArgument(), testRelation.getRightArgument());
        return (jLeft + jRel + jRight) / 3;
    }

    private static double computeJaccard(List<String> first, List<String> second) {
        List<String> intersected = new ArrayList<String>(first);
        intersected.removeAll(second);
        return ((double)(first.size() - intersected.size())) / (intersected.size()  + second.size());
    }

    private static List<Relation> readRelations(BufferedReader reader) throws IOException {
        String line;
        List<Relation> relations = new ArrayList<Relation>();
        while ((line = reader.readLine()) != null) {
            String left = line.substring(0, line.indexOf('['));
            String rel = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
            String right = line.substring(line.indexOf(']') + 1);

            List<String> leftArgument = readWords(left);
            List<String> relation = readWords(rel);
            List<String> rightArgument = readWords(right);

            relations.add(new Relation(leftArgument, relation, rightArgument));
        }
        return relations;
    }

    private static List<String> readWords(String line) {
        Scanner scanner = new Scanner(line);
        List<String> words = new ArrayList<String>();
        while (scanner.hasNext()) {
            words.add(scanner.next().toLowerCase().trim());
        }
        return words;
    }

    private static class Relation {
        private List<String> leftArgument;
        private List<String> relation;
        private List<String> rightArgument;

        public Relation(List<String> leftArgument, List<String> relation, List<String> rightArgument) {
            this.leftArgument = leftArgument;
            this.relation = relation;
            this.rightArgument = rightArgument;
        }

        public List<String> getLeftArgument() {
            return Collections.unmodifiableList(leftArgument);
        }

        public List<String> getRelation() {
            return Collections.unmodifiableList(relation);
        }

        public List<String> getRightArgument() {
            return Collections.unmodifiableList(rightArgument);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String arg : leftArgument) {
                sb.append(arg);
                sb.append(" ");
            }
            sb.append('[');
            for (int i = 0; i < relation.size(); i++) {
                sb.append(relation.get(i));
                if (i < relation.size() - 1) {
                    sb.append(" ");
                }
            }
            sb.append("] ");
            for (int i = 0; i < rightArgument.size(); i++) {
                sb.append(rightArgument.get(i));
                if (i < rightArgument.size() - 1) {
                    sb.append(" ");
                }
            }

            return sb.toString();
        }
    }
}
