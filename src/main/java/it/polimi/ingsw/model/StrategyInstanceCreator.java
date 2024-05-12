package it.polimi.ingsw.model;
import com.google.gson.*;
import java.lang.reflect.Type;

public class StrategyInstanceCreator implements InstanceCreator<Strategy> {

    private final String strategyType;

    public StrategyInstanceCreator(String strategyType) {
        this.strategyType = strategyType;
    }

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
                throw new IllegalArgumentException("Strategy type not supported: " + strategyType);
        }
    }

}
