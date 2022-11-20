package com.put.urbantraffic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class SimulationCore {
    City city;
    int epochs;
    int population;
    int numberOfCrossings;
    int mutationScale;
    int initialDeltaRange;
    int tournamentSelectionContestants;
    SimulatorChild[] individuals;
    Random rand;

    public SimulationCore(Random rand) {
        this.rand = rand;
    }

    public void startSimulation() {
        individuals = new SimulatorChild[population];
        for (int i = 0; i < population; i++) {
            int[] lightDeltas = new int[numberOfCrossings];
            for (int j = 0; j < numberOfCrossings; j++) {
                lightDeltas[j] = (int) (rand.nextFloat() * initialDeltaRange);
            }

            individuals[i] = new SimulatorChild();
            individuals[i].lightDeltas = lightDeltas;
        }
        simulateChildren(individuals);
//        Ascending order!
        Arrays.sort(individuals, Comparator.comparing(p -> p.valueOfGoalFunction));


        for (int epoch = 0; epoch < epochs; epoch++) {


            createNewIndividuals(individuals);
            System.out.println("Best in Epoch " + epoch + " : " + individuals[0].valueOfGoalFunction);
        }
    }


    private void createNewIndividuals(SimulatorChild[] individuals) {
        SimulatorChild[] children = new SimulatorChild[population / 2];
        int index;
        int index2;
        for (int child = 0; child < population / 2; child++) {
//            index = returnGaussian();
//            index2 = returnGaussian();
            index = returnTournamentSelection(individuals);
            index2 = returnTournamentSelection(individuals);
            children[child] = new SimulatorChild();
            children[child].lightDeltas = makeNewGenotype(individuals[index].lightDeltas, individuals[index2].lightDeltas);
        }
        simulateChildren(children);

        System.arraycopy(children, 0, individuals, population / 2, population / 2);

//        Ascending order!
        Arrays.sort(individuals, Comparator.comparing(p -> p.valueOfGoalFunction));


    }

    private void simulateChildren(SimulatorChild[] children) {

        for (SimulatorChild child : children) {
            child.start();
        }
        try {
            for (SimulatorChild child : children) {
                child.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int returnTournamentSelection(SimulatorChild[] individuals) {
        int[] indexes = new int[tournamentSelectionContestants];
        Arrays.fill(indexes, Integer.MAX_VALUE);
        int tryIndex;
        boolean flag;
        float min = Integer.MAX_VALUE;
        int finalIndex = -10;
        for (int index = 0; index < tournamentSelectionContestants; index++) {
            do {
                flag = false;
                tryIndex = (int) (rand.nextFloat() * individuals.length / 2);
                for (int i = 0; i < index; i++) {
                    if (indexes[i] == tryIndex) {
                        flag = true;
                        break;
                    }
                }
            } while (flag);
            indexes[index] = tryIndex;
            if (individuals[tryIndex].valueOfGoalFunction < min) {
                min = individuals[tryIndex].valueOfGoalFunction;
                finalIndex = tryIndex;
            }

        }
        return finalIndex;
    }


//    private int returnGaussian() {
//        Random r = rand;
//        int gaussNumber =(int) (abs(r.nextGaussian()) / 3 * population / 2);
//        while (gaussNumber >= population/2) {
//            gaussNumber = (int) (abs(r.nextGaussian()) / 3 * population / 2);
//        }
//        return gaussNumber;
//    }

    private int[] makeNewGenotype(int[] genotypeMother, int[] genotypeFather) {
        int[] newGenotype = new int[genotypeMother.length];
        int border = (int) (rand.nextFloat() * genotypeMother.length);

        System.arraycopy(genotypeMother, 0, newGenotype, 0, border);
        System.arraycopy(genotypeFather, border, newGenotype, border, genotypeMother.length - border);

        int whichDeltaMutated = (int) (rand.nextFloat() * genotypeMother.length);
        int mutation;
        do {
            mutation = (int) (rand.nextFloat() * mutationScale - mutationScale / 2);
        } while (newGenotype[whichDeltaMutated] + mutation < 0 || newGenotype[whichDeltaMutated] + mutation > initialDeltaRange);

        newGenotype[whichDeltaMutated] += mutation;

        return newGenotype;
    }


}
