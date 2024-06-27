package it.polimi.ingsw.model;
import com.google.gson.*;
import java.lang.reflect.Type;
/**
 * A custom instance creator for the Strategy interface
 * This class creates instances of specific strategy implementations based on a given type.
 */
public class StrategyInstanceCreator implements InstanceCreator<Strategy> {
    /**
     * The type of strategy to be created.
     */
    private final String strategyType;
    /**
     * Constructs a new StrategyInstanceCreator with the specified strategy type.
     *
     * @param strategyType the type of strategy to be created
     */
    public StrategyInstanceCreator(String strategyType) {
        this.strategyType = strategyType;
    }
    /**
     * Creates an instance of a strategy based on the specified type.
     *
     * @param type the type of the strategy to be created
     * @return an instance of a specific strategy implementation, or null if the type is not supported
     */
    @Override
    public Strategy createInstance(Type type) {
        switch(strategyType) {
            case "ConcreteStrategyDiagonal":
                return new ConcreteStrategyDiagonal();
            case "ConcreteStrategyItem":
                return new ConcreteStrategyItem();
            case "ConcreteStrategyLshape":
                return new ConcreteStrategyLshape();
            case "ConcreteStrategyMixed":
                return new ConcreteStrategyMixed();
            case "ConcreteStrategyResource":
                return new ConcreteStrategyResource();
            default:
                //Strategy type not supported
                return null;
        }
    }

}
