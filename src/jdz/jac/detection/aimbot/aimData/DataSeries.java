
package jdz.jac.detection.aimbot.aimData;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class DataSeries {
	@Getter private List<Float> angleSeries = new ArrayList<Float>();
	@Getter private int hits = 0;

	public void add(float angle, boolean hit) {
		angleSeries.add(angle);
		if (hit)
			hits++;
	}

	public void clear() {
		angleSeries.clear();
		hits = 0;
	}

	public double getClicksPerSec(long trainTimeLength) {
		return Math.sqrt(getLength());
	}

	public double getStddev() {
		float stddev = 0;
		for (float singleData : angleSeries)
			stddev += Math.pow(singleData - getMean(), 2);
		return Math.sqrt(stddev / getLength());
	}

	public double getMean() {
		return getSum() / getLength();
	}

	public double getDeltaStddev() {
		double delta_stddev = 0;
		for (double delta : getDeltas())
			delta_stddev += Math.pow(delta - getDeltaMean(), 2);
		return Math.sqrt(delta_stddev / getDeltaLength());
	}

	public double getDeltaMean() {
		return (getDeltaSum() / getDeltaLength());
	}

	public double getDeltaSum() {
		double delta_sum = 0;
		double[] deltas = getDeltas();
		for (double delta_line : deltas)
			delta_sum += delta_line;
		return delta_sum;
	}

	public double getAccuraccy() {
		return (double) hits / (double) getLength();
	}

	public double[] getDeltas() {
		if (getDeltaLength() < 0)
			return new double[0];

		double[] deltas = new double[getDeltaLength()];
		for (int i = 0; i <= getDeltaLength() - 1; i++)
			deltas[i] = Math.abs(getAngleSeries().get(i + 1) - getAngleSeries().get(i));
		return deltas;
	}

	public int getDeltaLength() {
		return getLength() - 1;
	}

	public int getLength() {
		return angleSeries.size();
	}

	public float getSum() {
		float sum = 0;
		for (float singleData : getAngleSeries())
			sum += singleData;
		return sum;
	}

	public Double[] getAllDump() {
		return new Double[] { getStddev(), getDeltaStddev(), getMean(), getDeltaMean(), getAccuraccy() };
	}

	public void save(String filename) throws Exception {
		DataIO.saveCapturedData(filename, this);
	}
}
