package com.put.urbantraffic;

import java.util.*;

public class SimulationCore {
    public long seed;
    int epochs;
    int population;
    int mutationScale;
    int initialDeltaRange;
    int tournamentSelectionContestants;
    SimulatorChild[] individuals;
    Random rand;

    public SimulationCore(Random rand) {
        this.rand = rand;
    }

    City worst;
    City best;
    public void startSimulation() {
        individuals = new SimulatorChild[population];
        for (int i = 0; i < population; i++) {
            individuals[i] = new SimulatorChild(seed, null);
        }
        simulateChildren(individuals);
//        Ascending order!
        Arrays.sort(individuals, Comparator.comparing(p -> p.valueOfGoalFunction));
        worst = individuals[individuals.length - 1].city;

        for (int epoch = 0; epoch < epochs; epoch++) {


            createNewIndividuals(individuals);
//            Uncomment to see the best individual in this epoch
            System.out.println("Best in Epoch " + epoch + " : " + individuals[0].valueOfGoalFunction);
        }
        best = individuals[0].city;
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
            children[child] = new SimulatorChild(seed, makeNewGenotype(individuals[index].trafficLightsSettingsList, individuals[index2].trafficLightsSettingsList));
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

    private List<TrafficLightsSettings> makeNewGenotype(List<TrafficLightsSettings> genotypeMother, List<TrafficLightsSettings> genotypeFather) {
        List<TrafficLightsSettings> newGenotype = new ArrayList<>();
        int border = (int) (rand.nextFloat() * genotypeMother.size());

        newGenotype.addAll(genotypeMother.subList(0, border));
        newGenotype.addAll(genotypeFather.subList(border, genotypeFather.size()));

        int whichDeltaMutated = (int) (rand.nextFloat() * genotypeMother.size());
        int mutatedRed, mutatedGreen, mutatedOffset;
        TrafficLightsSettings settingsToBeMutated = newGenotype.get(whichDeltaMutated);
        while (true) {
            mutatedRed = (int) (rand.nextFloat() * mutationScale - mutationScale / 2) + settingsToBeMutated.getRedDuration();
            mutatedGreen = (int) (rand.nextFloat() * mutationScale - mutationScale / 2) + settingsToBeMutated.getGreenDuration();
            mutatedOffset = (int) (rand.nextFloat() * mutationScale - mutationScale / 2) + settingsToBeMutated.getOffset();
            boolean isGreenInRange = isInRange(mutatedGreen);
            boolean isRedInRange = isInRange(mutatedRed);
//            boolean isOffsetInRange = isInRange(mutatedOffset);
            if (isGreenInRange && isRedInRange) {
                break;
            }
        }
        newGenotype.set(whichDeltaMutated, new TrafficLightsSettings(mutatedGreen, mutatedRed, mutatedOffset));

        return newGenotype;
    }

    boolean isInRange(float duration) {
        boolean isOverZero = duration > TrafficLightsSupervisor.YELLOW_DURATION * 2;
        boolean isUnderInitialDeltaRange = duration < initialDeltaRange;
        return isOverZero && isUnderInitialDeltaRange;
    }
}
