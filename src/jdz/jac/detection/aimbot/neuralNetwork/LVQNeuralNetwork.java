/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jdz.jac.detection.aimbot.neuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import lombok.Getter;

/**
 * A java implementation of learning-vector-quantization neural network
 * 
 * References: T. Kohonen, "Improved Versions of Learning Vector
 * Quantization", International Joint Conference on Neural Networks (IJCNN),
 * 1990.
 * 
 * @author Nova41
 */
public class LVQNeuralNetwork {
	private double stepAlpha;
	private double stepAlphaDelRate;

	@Getter private int inputFeatures;

	private List<DataSet> inputDataSets = new ArrayList<DataSet>();
	private List<DataSet> outputLayer = new ArrayList<DataSet>();
	private Map<Double, DataSet> distances = new HashMap<Double, DataSet>();
	private List<Double> inputMins = new ArrayList<Double>();
	private List<Double> inputMaxes = new ArrayList<Double>();

	public LVQNeuralNetwork(double stepAlpha, double stepAlphaDelRate) {
		this.stepAlpha = stepAlpha;
		this.stepAlphaDelRate = stepAlphaDelRate;
	}

	public void reset() {
		inputDataSets.clear();
		outputLayer.clear();
		distances.clear();
		inputMins.clear();
		inputMaxes.clear();
	}

	public void initialize() {
		// initialize output layers according to the number of egories and features
		Set<String> set = new HashSet<String>();
		for (DataSet entry : inputDataSets)
			set.add(entry.category);

		for (String category : set) {
			Double[] randOutput = new Double[inputFeatures];
			for (int i = 0; i <= inputFeatures - 1; i++)
				randOutput[i] = randdouble();
			outputLayer.add(new DataSet(category, randOutput));
		}
	}

	public void normalize() {
		List<Double> featureColumn = new ArrayList<Double>();
		for (int i = 0; i <= inputFeatures - 1; i++) {
			for (DataSet entry : inputDataSets)
				featureColumn.add(entry.data[i]);

			double min = getMin(featureColumn);
			double max = getMax(featureColumn);
			inputMins.add(min);
			inputMaxes.add(max);
			
			for (DataSet entry : inputDataSets)
				entry.data[i] = (entry.data[i] - min) / (max - min);
			
			featureColumn.clear();
		}
	}

	public Double[] normalizeInput(Double[] input) {
		Double[] normalized = input.clone();
		for (int i = 0; i <= inputFeatures - 1; i++) {
			double min = inputMins.get(i);
			double max = inputMaxes.get(i);
			normalized[i] = (normalized[i] - min) / (max - min);
		}
		return normalized;
	}

	public PredictResult predict(Double[] input) {
		String matchedCat = outputLayer.get(getWinner(normalizeInput(input))).category;
		Double matchedDis = getWinnerDistance(normalizeInput(input));
		PredictResult result = new PredictResult(matchedCat, matchedDis);
		return result;
	}

	public void printPredictResult(Double[] input) {
		printInputLayers();
		printOutputLayers();
		System.out.println("Input:" + Arrays.asList(input));
		System.out.println("Normalized input: " + Arrays.asList(normalizeInput(input)));
	}

	public void input(String category, Double[] data) {
		inputDataSets.add(new DataSet(category, data));
		inputFeatures = data.length;
	}

	public void input(DataSet DataSet) {
		inputDataSets.add(DataSet);
		inputFeatures = DataSet.data.length;
	}

	public int getWinner(Double[] input) {
		// Winner is the neuron nearest to the input
		// return the index of the winner in the outputLayer
		distances.clear();
		for (DataSet entry : outputLayer) {
			double distance = 0;
			Double[] output = entry.data;
			for (int i = 0; i <= input.length - 1; i++)
				distance += Math.pow(input[i] - output[i], 2);
			// I don't open root here as it's unnecessary
			distances.put(distance, entry);
		}
		return outputLayer.indexOf(distances.get(getMinKey(distances)));
	}

	public double getWinnerDistance(Double[] input) {
		distances.clear();
		for (DataSet entry : outputLayer) {
			double distance = 0;
			Double[] output = entry.data;
			for (int i = 0; i <= input.length - 1; i++)
				distance += Math.pow(input[i] - output[i], 2);
			distances.put(distance, entry);
		}
		return getMinKey(distances);
	}

	public void move(DataSet input, int outputIndex) {
		DataSet output = outputLayer.get(outputIndex);
		if (input.category.equals(output.category)) {
			for (int i = 0; i <= inputFeatures - 1; i++)
				outputLayer.get(outputIndex).data[i] += stepAlpha * (input.data[i] - output.data[i]);
		}
		else {
			for (int i = 0; i <= inputFeatures - 1; i++)
				outputLayer.get(outputIndex).data[i] -= stepAlpha * (input.data[i] - output.data[i]);
		}
	}

	public void train() {
		for (DataSet input : inputDataSets) {
			move(input, getWinner(input.data));
		}
	}

	public int trainUntil(double maxEpoch) {
		int generate = 0;
		while (stepAlpha >= maxEpoch) {
			train();
			generate++;
			stepAlpha *= stepAlphaDelRate;
		}
		return generate;
	}

	public DataSet getInput(int index) {
		return inputDataSets.get(index);
	}

	public void printInputLayers() {
		System.out.println("Input layers: " + inputDataSets.size() + " category(s)");
		for (DataSet input : inputDataSets) {
			System.out.println("  " + input.category + " " + Arrays.asList(input.data));
		}
	}

	public void printOutputLayers() {
		System.out.println("Output layers: " + outputLayer.size() + " category(s)");
		for (DataSet output : outputLayer) {
			System.out.println("  " + output.category + " " + Arrays.asList(output.data));
		}
	}

	public int getInputLayerSize() {
		return inputDataSets.size();
	}

	public int getOutputLayerSize() {
		return outputLayer.size();
	}

	private double randdouble() {
		return new Random().nextDouble();
	}

	private Double getMinKey(Map<Double, DataSet> map) {
		if (map == null)
			return null;
		Set<Double> set = map.keySet();
		Object[] obj = set.toArray();
		Arrays.sort(obj);
		return (Double) obj[0];
	}

	private Double getMin(List<Double> list) {
		Collections.sort(list);
		return list.get(0);
	}

	private Double getMax(List<Double> list) {
		Collections.sort(list);
		Collections.reverse(list);
		return list.get(0);
	}
}
