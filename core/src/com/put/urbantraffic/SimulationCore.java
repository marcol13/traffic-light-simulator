package com.put.urbantraffic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class SimulationCore {
    City city;
    int epochs;
    int numberOfChildren;
    int population;
    int numberOfCrossings;
    int mutationScale;
    int initialDeltaRange;
    int tournamentSelectionContestants;
    SimulatorChild[] children;
    SimulatorChild[] parents;


    public void startSimulation(){
        children = new SimulatorChild[numberOfChildren];
        parents = new SimulatorChild[numberOfChildren];
        int initialParentCounter;
        for(int i=0; i<numberOfChildren; i++) {
            int[] lightDeltas = new int[numberOfCrossings];
            for (int j = 0; j < numberOfCrossings; j++) {
                lightDeltas[j] = (int) (Math.random() * initialDeltaRange);
            }

            children[i] = new SimulatorChild();
            children[i].lightDeltas = lightDeltas;

        }
        for(int i=0; i<numberOfChildren; i++) {
            initialParentCounter=0;
            int[] lightDeltas = new int[numberOfCrossings];
            for(int j=0; j<numberOfCrossings; j++){
                lightDeltas[j] = (int)(Math.random()*initialDeltaRange);
                initialParentCounter+=lightDeltas[j];
            }
            parents[i] = new SimulatorChild();
            parents[i].lightDeltas = lightDeltas;
            parents[i].valueOfGoalFunction = initialParentCounter;
        }


        for(int epoch=0; epoch < epochs; epoch++){
            System.out.println();
            System.out.println("Epoch: " + epoch);
            for(int child=0; child<numberOfChildren;child++){
                children[child].start();
            }





            try {
                for(int child=0; child<numberOfChildren;child++){
                    children[child].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SimulatorChild[] temp= new SimulatorChild[2*numberOfChildren];
            for(int child=0; child<numberOfChildren;child++){
                temp[2*child] = parents[child];
                temp[2*child+1] = children[child];
            }
            SimulatorChild[][] parentsAndChildren = createChildren(temp);
            parents = parentsAndChildren[0];
            children = parentsAndChildren[1];
        }
    }


    private SimulatorChild[][] createChildren(SimulatorChild[] individuals) {
//        Ascending order!
        Arrays.sort(individuals, Comparator.comparing(p -> p.valueOfGoalFunction));
        System.out.println("Individuals (parents + children):");
        for (SimulatorChild individual : individuals) {
            System.out.print(individual.valueOfGoalFunction + " ");
        }

        SimulatorChild[] newChildren = new SimulatorChild[numberOfChildren];
        SimulatorChild[] newParents = new SimulatorChild[numberOfChildren];
        int index;
        int index2;
        for(int child=0; child<numberOfChildren;child++){
//            index = returnGaussian();
//            index2 = returnGaussian();
            index = returnTournamentSelection(individuals);
            index2 = returnTournamentSelection(individuals);
            newChildren[child] = new SimulatorChild();
            newChildren[child].lightDeltas = makeNewGenotype(individuals[index].lightDeltas,individuals[index2].lightDeltas);
        }

        for(int parent=0; parent<numberOfChildren;parent++){
            newParents[parent] = individuals[parent];
        }
        return new SimulatorChild[][]{newParents, newChildren};
    }

    private int returnTournamentSelection(SimulatorChild[] individuals) {
        int[] indexes = new int[tournamentSelectionContestants];
        Arrays.fill(indexes, -1);
        int tryIndex;
        boolean flag;
        for(int index=0; index<tournamentSelectionContestants; index++){
            do{
                flag=false;
                tryIndex = (int) (Math.random() * individuals.length / 2);
                for(int i=0; i<index; i++){
                    if(indexes[i] == tryIndex){
                        flag = true;
                        break;
                    }
                }
            }while(flag);
            indexes[index] = tryIndex;

        }

        float min = individuals[indexes[0]].valueOfGoalFunction;
        int index = indexes[0];
        for (int i=1; i<tournamentSelectionContestants; i++){
            if(individuals[indexes[i]].valueOfGoalFunction < min){
                min = individuals[indexes[i]].valueOfGoalFunction;
                index = indexes[i];
            }
        }
        System.out.println("Child:");
        for(int i=0; i<tournamentSelectionContestants; i++){
            System.out.println(indexes[i] + " " + individuals[indexes[i]].valueOfGoalFunction);
        }
        System.out.println("Wybrano:" + index + " " + min);
        return index;
    }


//    private int returnGaussian() {
//        Random r = new Random();
//        int gaussNumber =(int) (abs(r.nextGaussian()) / 3 * population / 2);
//        while (gaussNumber >= population/2) {
//            gaussNumber = (int) (abs(r.nextGaussian()) / 3 * population / 2);
//        }
//        return gaussNumber;
//    }

    private int[] makeNewGenotype(int[] genotypeMother, int[] genotypeFather) {
        int[] newGenotype = new int[genotypeMother.length];
        double mutation;
        for(int numberDelta=0; numberDelta<genotypeMother.length; numberDelta++){
            mutation = Math.random()*mutationScale - mutationScale/2.;
            if(Math.random() < .5){
                newGenotype[numberDelta] = max((int)(genotypeMother[numberDelta] + mutation), 0);
            }
            else{
                newGenotype[numberDelta] = max((int)(genotypeFather[numberDelta] + mutation), 0);
            }
        }
        return newGenotype;
    }


}
