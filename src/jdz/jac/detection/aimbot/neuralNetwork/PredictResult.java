
package jdz.jac.detection.aimbot.neuralNetwork;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class PredictResult {
	@Getter String bestMatched;
	@Getter double distance;

	public double getConfidence() {
		return distance;
	}
}
