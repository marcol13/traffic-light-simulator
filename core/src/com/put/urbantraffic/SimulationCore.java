package com.put.urbantraffic;

import lombok.val;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class SimulationCore {
    City city;
    int epochs;
    int numberOfChildren;
    int numberOfCrossings;
    int mutationScale;
    int initialDeltaRange;
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
            System.out.println("Epoka: " + epoch);

            val executorService = Executors.newFixedThreadPool(children.length);

            try {
                executorService.invokeAll(Arrays.stream(children).map(Executors::callable).collect(Collectors.toList()));
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
        Random r = new Random();
        int index;
        int index2;
        for(int child=0; child<numberOfChildren;child++){
            index = (int)(abs(r.nextGaussian()) * numberOfChildren / 5);
            do{
                index2 = (int)(abs(r.nextGaussian()) * numberOfChildren / 5);
            }while(index == index2);
            newChildren[child] = new SimulatorChild();
            newChildren[child].lightDeltas = makeNewGenotype(individuals[index].lightDeltas,individuals[index2].lightDeltas);
        }

        for(int parent=0; parent<numberOfChildren;parent++){
            newParents[parent] = individuals[parent];
        }
        return new SimulatorChild[][]{newParents, newChildren};
    }

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
