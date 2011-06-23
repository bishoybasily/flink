package eu.stratosphere.sopremo.base;

import java.util.Iterator;
import java.util.List;

import eu.stratosphere.pact.common.contract.CoGroupContract;
import eu.stratosphere.pact.common.contract.Contract;
import eu.stratosphere.pact.common.plan.PactModule;
import eu.stratosphere.pact.common.stub.Collector;
import eu.stratosphere.sopremo.ElementaryOperator;
import eu.stratosphere.sopremo.EvaluationContext;
import eu.stratosphere.sopremo.JsonStream;
import eu.stratosphere.sopremo.Operator;
import eu.stratosphere.sopremo.base.Intersection.TwoInputIntersection.Implementation;
import eu.stratosphere.sopremo.expressions.EvaluableExpression;
import eu.stratosphere.sopremo.pact.PactJsonObject;
import eu.stratosphere.sopremo.pact.SopremoCoGroup;

public class Difference extends MultiSourceOperator {

	public Difference(List<? extends JsonStream> inputs) {
		super(inputs);

		setDefaultKeyProjection(EvaluableExpression.SAME_VALUE);
	}

	public Difference(JsonStream... inputs) {
		super(inputs);

		setDefaultKeyProjection(EvaluableExpression.SAME_VALUE);
	}

	protected Operator createElementaryOperations(List<Operator> inputs) {
		if (inputs.size() <= 1)
			return inputs.get(0);

		Operator leftInput = inputs.get(0);
		for (int index = 1; index < inputs.size(); index++)
			leftInput = new TwoInputDifference(leftInput, inputs.get(index));

		return leftInput;
	}

	public static class TwoInputDifference extends ElementaryOperator {
		public TwoInputDifference(JsonStream input1, JsonStream input2) {
			super(input1, input2);
		}

		// @Override
		// public PactModule asPactModule(EvaluationContext context) {
		// CoGroupContract<PactJsonObject.Key, PactJsonObject, PactJsonObject, PactJsonObject.Key, PactJsonObject>
		// difference =
		// new CoGroupContract<PactJsonObject.Key, PactJsonObject, PactJsonObject, PactJsonObject.Key, PactJsonObject>(
		// Implementation.class);
		// return PactModule.valueOf(toString(), difference);
		// }

		public static class Implementation extends
				SopremoCoGroup<PactJsonObject.Key, PactJsonObject, PactJsonObject, PactJsonObject.Key, PactJsonObject> {
			@Override
			public void coGroup(PactJsonObject.Key key, Iterator<PactJsonObject> values1,
					Iterator<PactJsonObject> values2, Collector<PactJsonObject.Key, PactJsonObject> out) {
				if (values1.hasNext() && !values2.hasNext())
					out.collect(key, values1.next());
			}
		}
	}
}
